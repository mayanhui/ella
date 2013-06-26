package net.hbase.secondaryindex;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

import net.hbase.secondaryindex.util.ConfigFactory;
import net.hbase.secondaryindex.util.ConfigProperties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.filter.ColumnPaginationFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.util.Bytes;

public class HBaseManager {
	public Configuration config;
	public HTable table;
	public HBaseAdmin admin;

	ConfigProperties cp = ConfigFactory.getInstance().getConfigProperties(
			ConfigFactory.INDEX_CONFIG_PATH);

	public HBaseManager() {
		config = HBaseConfiguration.create();
		config.set("hbase.master",
				cp.getProperty(ConfigProperties.CONFIG_NAME_HBASE_MASTER));
		config.set("hbase.zookeeper.property.clientPort", "2181");
		config.set(
				"hbase.zookeeper.quorum",
				cp.getProperty(ConfigProperties.CONFIG_NAME_HBASE_ZOOKEEPER_QUORUM));
		try {
			admin = new HBaseAdmin(config);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws Exception {
		HBaseManager m = new HBaseManager();
		// m.testScanner();
		// m.put();
		// m.testGet();
		m.testScanGet();

	}

	public void put(List<Put> puts, byte[] tableName) throws IOException {
		if (null != puts && puts.size() > 0 && null != tableName
				&& tableName.length > 0) {
			table = new HTable(config, tableName);
			table.put(puts);
			table.close();
		}
	}

	public void put(Put put, byte[] tableName) throws IOException {
		if (null != put && null != tableName && tableName.length > 0) {
			table = new HTable(config, tableName);
			table.put(put);
			table.close();
		}

	}

	public Result get(byte[] tableName, byte[] rowkey, byte[] columnFamily,
			String[] columns) throws IOException {
		table = new HTable(config, tableName);
		Get get = new Get(rowkey);
		if (null != columnFamily && null != columns && columns.length > 0) {
			for (int i = 0; i < columns.length; i++) {
				get.addColumn(columnFamily, Bytes.toBytes(columns[i]));
			}
		}
		if (null != columnFamily && (null == columns || columns.length == 0)) {
			get.addFamily(columnFamily);
		}
		return table.get(get);
	}

	public Result get(byte[] tableName, byte[] rowkey, byte[] columnFamily)
			throws IOException {
		return get(tableName, rowkey, columnFamily, null);
	}

	public Result get(byte[] tableName, byte[] rowkey) throws IOException {
		return get(tableName, rowkey, null, null);
	}

	public ResultScanner getVer(byte[] tableName, byte[] rowkey,
			byte[] columnFamily, String[] columns, int ver) throws IOException {
		table = new HTable(config, tableName);
		Get get = new Get(rowkey);
		if (null != columnFamily && null != columns && columns.length > 0) {
			for (int i = 0; i < columns.length; i++) {
				get.addColumn(columnFamily, Bytes.toBytes(columns[i]));
			}
		} else if (null != columnFamily
				&& (null == columns || columns.length == 0)) {
			get.addFamily(columnFamily);
		}

		Scan scanner = new Scan(get);
		scanner.setMaxVersions(ver);
		return table.getScanner(scanner);
	}

	public void testQueryRS() throws Exception {

		Scan scanner = new Scan();
		scanner.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("description"));
		scanner.setMaxVersions();
		ResultScanner rsScanner = table.getScanner(scanner);
		System.out.println(rsScanner.toString());
		Result rs = rsScanner.next();
		int count = 0;
		while (null != rs) {
			++count;
			System.out.println(rs.size());
			NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> nMap = rs
					.getMap();
			NavigableMap<byte[], NavigableMap<Long, byte[]>> columnMap = nMap
					.get(Bytes.toBytes("description"));
			NavigableMap<Long, byte[]> qualMap = columnMap.get(new byte[] {});

			if (qualMap.entrySet().size() > 0) {
				System.out.println("---------------------------");
				for (Map.Entry<Long, byte[]> m : qualMap.entrySet()) {
					System.out.println("Value:" + new String(m.getValue()));
				}
			}
			rs = rsScanner.next();
			if (count > 10)
				break;
		}
	}

	public void testQueryCommodity() throws Exception {

		System.out.println("Get Spin's commodity info");
		Get mathGet = new Get(new String("Spin").getBytes());
		mathGet.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("widgetname"));
		mathGet.setMaxVersions();
		Result rs = table.get(mathGet);

		NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> nMap = rs
				.getMap();
		NavigableMap<byte[], NavigableMap<Long, byte[]>> columnMap = nMap
				.get(Bytes.toBytes("widgetname"));
		NavigableMap<Long, byte[]> qualMap = columnMap.get(new byte[] {});

		if (qualMap.entrySet().size() > 0) {
			for (Map.Entry<Long, byte[]> m : qualMap.entrySet()) {
				System.out.println("Value:" + new String(m.getValue()));
				break;
			}
		}
	}

	public void testScanner() throws IOException {
		long st = System.currentTimeMillis();
		// System.out.println("Scan....");

		Scan scanner = new Scan();

		// /*version*/
		// scanner.addColumn(Bytes.toBytes("cf1"), Bytes.toBytes("mid"));
		// scanner.setTimeRange(1253997917140L, 1453997917140L);
		scanner.setMaxVersions();

		/* filter */
		// Filter filter = new RowFilter(CompareFilter.CompareOp.EQUAL, new
		// RegexStringComparator("20121.*_AF6E39E3-3174-7BBF-7EE9-02AB6528897B"));
		// scanner.setFilter(filter);

		/* batch and caching */
		// scanner.setBatch(0);
		// scanner.setCaching(100000);

		ResultScanner rsScanner = table.getScanner(scanner);

		// long en = System.currentTimeMillis();
		// System.out.println("search Time: " + (en - st) + " ms");

		int version = 0;
		for (Result res : rsScanner) {
			version++;
			final List<KeyValue> list = res.list();
			// System.out.println(getRealRowKey(list.get(0)));
			// for (final KeyValue kv : list) {
			// System.out.println(getRealRowKey(kv));
			// break;
			// }

			for (final KeyValue kv : list)
				System.out.println(kv.toString());

			version = list.size();
			// System.out.println("Time: " + System.currentTimeMillis());
			// if(version % 10000 == 0)
			// System.out.println("rows: " + version);
		}

		rsScanner.close();

		long en2 = System.currentTimeMillis();
		System.out.println("Total rows: " + version);
		System.out.println("Total Time: " + (en2 - st) + " ms");

		// long en = System.currentTimeMillis();
		// System.out.println("Time: " + (en-st) +" ms");
		//
		// Result rs = rsScanner.next();
		// for (; null != rs; rs = rsScanner.next()) {
		// System.out.println("rs.getRow()[" + new String(rs.getRow()) + "]");
		// System.out.println("["
		// + new String(rs.getValue(Bytes.toBytes("vv_log"),
		// Bytes.toBytes("aid"))) + "]");
		//
		// }
	}

	public static String getRealRowKey(KeyValue kv) {
		int rowlength = Bytes.toShort(kv.getBuffer(), kv.getOffset()
				+ KeyValue.ROW_OFFSET);
		String rowKey = Bytes.toStringBinary(kv.getBuffer(), kv.getOffset()
				+ KeyValue.ROW_OFFSET + Bytes.SIZEOF_SHORT, rowlength);
		return rowKey;
	}

	public void testGet() throws IOException {
		long st = System.currentTimeMillis();
		Get get = new Get(
				Bytes.toBytes("{1F591795-74DE-EB70-0245-0E4465C72CFA}"));
		get.addColumn(Bytes.toBytes("bhvr"), Bytes.toBytes("vvmid"));
		get.setMaxVersions(100);
		// get.setTimeRange(1354010844711L - 12000L, 1354010844711L);

		// get.setTimeStamp(1354700700000L);

		Filter filter = new ColumnPaginationFilter(1, 10);
		get.setFilter(filter);

		Result dbResult = table.get(get);

		System.out.println("result=" + dbResult.toString());

		long en2 = System.currentTimeMillis();
		System.out.println("Total Time: " + (en2 - st) + " ms");

	}

	public void testScanGet() throws IOException {
		long st = System.currentTimeMillis();

		Get get = new Get(
				Bytes.toBytes("{1F591795-74DE-EB70-0245-0E4465C72CFA}"));
		get.addColumn(Bytes.toBytes("bhvr"), Bytes.toBytes("vvmid"));

		Filter filter = new ColumnPaginationFilter(1, 10);

		Scan scanner = new Scan(get);
		scanner.setFilter(filter);
		scanner.setMaxVersions(100);
		// scanner.setCaching(100);
		// scanner.setBatch(10);

		ResultScanner rsScanner = table.getScanner(scanner);

		for (Result result : rsScanner) {
			System.out.println(result);
		}
		rsScanner.close();

		int version = 0;
		// int count = 0;
		// for (Result res : rsScanner) {
		// System.out.println("count: " + ++count);
		// final List<KeyValue> list = res.list();
		//
		// for (final KeyValue kv : list)
		// System.out.println(kv.toString());
		//
		// version = list.size();
		// }
		// rsScanner.close();

		long en2 = System.currentTimeMillis();
		System.out.println("Total rows: " + version);
		System.out.println("Total Time: " + (en2 - st) + " ms");

	}
}