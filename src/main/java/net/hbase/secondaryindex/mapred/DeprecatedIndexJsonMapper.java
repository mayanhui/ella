package net.hbase.secondaryindex.mapred;

import java.io.IOException;
import java.util.*;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

import net.hbase.secondaryindex.util.Const;
import net.hbase.secondaryindex.util.JsonUtil;

/**
 * Just build index for json column.
 * 
 * @author mayanhui
 * 
 */
@Deprecated
public class DeprecatedIndexJsonMapper extends TableMapper<Writable, Writable> {

	private boolean isBuildSingleIndex;

	private String column;
	private String jsonFields;
	private Map<String, Set<String>> colNameValSetrMap;

	private JsonUtil jsonUtil = new JsonUtil();
	private long ts = System.currentTimeMillis();

	private Text k = new Text();
	private Text v = new Text();

	@Override
	protected void setup(Context context) throws IOException,
			InterruptedException {
		column = context.getConfiguration().get(Const.HBASE_CONF_COLUMN_NAME);
		jsonFields = context.getConfiguration().get(Const.HBASE_CONF_JSON_NAME);
		isBuildSingleIndex = context.getConfiguration().getBoolean(
				Const.HBASE_CONF_ISBUILDSINGLEINDEX_NAME, true);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void map(ImmutableBytesWritable row, Result columns, Context context)
			throws IOException {
		String json = null;
		// byte[] rowkey = row.get();
		// byte[] cf = Const.COLUMN_FAMILY_CF1_BYTE;
		// byte[] qualifier = Const.COLUMN_RK_BYTE;

		String rowkey = new String(row.get());
		String cf = Const.COLUMN_FAMILY_CF1_STRING;
		String qualifier = Const.COLUMN_RK_STRING;

		String[] arr = jsonFields.split(",", -1);

		if (!isBuildSingleIndex) {
			// initial column name and values map
			colNameValSetrMap = new HashMap<String, Set<String>>(arr.length);
			for (int i = 0; i < arr.length; i++) {
				colNameValSetrMap.put(arr[i], new HashSet<String>());
			}
		}

		try {
			for (KeyValue kv : columns.list()) {
				json = Bytes.toStringBinary(kv.getValue()); // json column value
				ts = kv.getTimestamp();

				/* build single column index */
				for (String jf : arr) {
					List<String> jfValueList = new ArrayList<String>();
					// json array
					if (json.startsWith(Const.JSON_ARRAY_START)) {
						jfValueList = jsonUtil.evaluateArray(json, jf);
					} else {// single json object
						String jfValue = jsonUtil.evaluate(json, "$." + jf)
								.toString();
						jfValueList.add(jfValue);
					}

					for (String jfValue : jfValueList) {
						if (null != jfValue && jfValue.trim().length() > 0) {
							// Put put = new Put(
							// Bytes.toBytes(column
							// + Const.ROWKEY_DEFAULT_SEPARATOR
							// + jf
							// + Const.ROWKEY_DEFAULT_SEPARATOR
							// + jfValue), ts);
							// put.add(cf, qualifier, rowkey);
							// context.write(row, put);

							k.set(column + Const.ROWKEY_DEFAULT_SEPARATOR + jf
									+ Const.ROWKEY_DEFAULT_SEPARATOR + jfValue
									+ Const.FIELD_COMMON_SEPARATOR + ts);
							v.set(cf + Const.FIELD_COMMON_SEPARATOR + qualifier
									+ Const.FIELD_COMMON_SEPARATOR + rowkey);

							context.write(k, v);

							if (!isBuildSingleIndex) {
								Set<String> colValSet = colNameValSetrMap
										.get(jf);
								colValSet.add(jfValue);
								colNameValSetrMap.put(jf, colValSet);
							}
						}
					}
				}

			}

			/* build combined index */
			if (!isBuildSingleIndex) {
				// remove empty columns
				Map<String, Set<String>> cleanedMap = removeEmptyEntry(colNameValSetrMap); // valid
				if (cleanedMap.size() > 1 && cleanedMap.size() < 4
						&& cleanedMap.size() <= arr.length) {
					// The existing columns of this rowkey is 3 and the input
					// 'column' is 3 too.
					if (cleanedMap.size() == 3) {
						Set<String> cn0 = cleanedMap.get(arr[0]);
						Set<String> cn1 = cleanedMap.get(arr[1]);
						Set<String> cn2 = cleanedMap.get(arr[2]);
						for (String v0 : cn0) {
							for (String v1 : cn1) {
								for (String v2 : cn2) {
									Vector<String> source = new Vector<String>();
									source.add(arr[0]
											+ Const.ROWKEY_DEFAULT_SEPARATOR
											+ v0);
									source.add(arr[1]
											+ Const.ROWKEY_DEFAULT_SEPARATOR
											+ v1);
									source.add(arr[2]
											+ Const.ROWKEY_DEFAULT_SEPARATOR
											+ v2);
									Vector<Vector> comb = Combination
											.getLowerLimitCombinations(source,
													2);
									if (null != comb && comb.size() > 0) {
										for (Vector vect : comb) {
											String indexRowkey = column
													+ Const.ROWKEY_DEFAULT_SEPARATOR
													+ vect.toString()
															.replaceAll(", ",
																	"_")
															.replaceAll("\\[",
																	"")
															.replaceAll("\\]",
																	"");
											// Put put = new Put(
											// Bytes.toBytes(indexRowkey));
											// put.add(cf, qualifier, rowkey);
											// context.write(row, put);

											k.set(indexRowkey
													+ Const.FIELD_COMMON_SEPARATOR
													+ ts);

											v.set(cf
													+ Const.FIELD_COMMON_SEPARATOR
													+ qualifier
													+ Const.FIELD_COMMON_SEPARATOR
													+ rowkey);

											context.write(k, v);
										}
									}
								}
							}
						}
						// The input 'column' is 2 or 3, and the existing
						// columns of this rowkey is 2.
					} else if (cleanedMap.size() == 2) {
						Set<String> cn0 = null;
						Set<String> cn1 = null;
						// arr convert to list, do not use Arrays.asList(), it
						// could not use remove().
						List<String> arrList = new ArrayList<String>();
						for (String s : arr) {
							arrList.add(s);
						}

						if (arr.length == 3) {
							String key = getKeyWithEmptyValue(colNameValSetrMap);
							arrList.remove(key);
							cn0 = cleanedMap.get(arrList.get(0));
							cn1 = cleanedMap.get(arrList.get(1));
						} else if (arr.length == 2) {
							cn0 = cleanedMap.get(arr[0]);
							cn1 = cleanedMap.get(arr[1]);
						}

						for (String v0 : cn0) {
							for (String v1 : cn1) {
								String indexRowkey = column
										+ Const.ROWKEY_DEFAULT_SEPARATOR
										+ arrList.get(0)
										+ Const.ROWKEY_DEFAULT_SEPARATOR + v0
										+ Const.ROWKEY_DEFAULT_SEPARATOR
										+ arrList.get(1)
										+ Const.ROWKEY_DEFAULT_SEPARATOR + v1;
								// Put put = new
								// Put(Bytes.toBytes(indexRowkey));
								// put.add(cf, qualifier, rowkey);
								// context.write(row, put);

								k.set(indexRowkey
										+ Const.FIELD_COMMON_SEPARATOR + ts);

								v.set(cf + Const.FIELD_COMMON_SEPARATOR
										+ qualifier
										+ Const.FIELD_COMMON_SEPARATOR + rowkey);

								context.write(k, v);

							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error: " + e.getMessage() + ", Row: "
					+ Bytes.toString(row.get()) + ", Value: " + json);
		}
	}

	private Map<String, Set<String>> removeEmptyEntry(
			Map<String, Set<String>> map) {
		Map<String, Set<String>> rst = new HashMap<String, Set<String>>();
		if (null != map && map.size() > 0) {
			Set<Map.Entry<String, Set<String>>> set = map.entrySet();
			for (Iterator<Map.Entry<String, Set<String>>> it = set.iterator(); it
					.hasNext();) {
				Map.Entry<String, Set<String>> entry = (Map.Entry<String, Set<String>>) it
						.next();
				Set<String> value = entry.getValue();
				if (value.size() > 0)
					rst.put(entry.getKey(), value);
			}
		}
		return rst;
	}

	private String getKeyWithEmptyValue(Map<String, Set<String>> map) {
		String rst = null;
		if (null != map && map.size() > 0) {
			Set<Map.Entry<String, Set<String>>> set = map.entrySet();
			for (Iterator<Map.Entry<String, Set<String>>> it = set.iterator(); it
					.hasNext();) {
				Map.Entry<String, Set<String>> entry = (Map.Entry<String, Set<String>>) it
						.next();
				Set<String> value = entry.getValue();
				if (value.size() == 0)
					rst = entry.getKey();
			}
		}
		return rst;
	}

}
