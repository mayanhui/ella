package net.hbase.secondaryindex.mapred;

import java.io.IOException;

import net.hbase.secondaryindex.util.Const;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Mapper;

public class LoadMapper extends
		Mapper<LongWritable, Text, ImmutableBytesWritable, Writable> {

	private byte[] family = null;
	private byte[] qualifier = null;
	private byte[] cellValue = null;
	private byte[] rowkey = null;
	private long ts = System.currentTimeMillis();

	public Configuration config;
	public HTable table;

	@Override
	protected void setup(Context context) throws IOException,
			InterruptedException {
		config = context.getConfiguration();
		table = new HTable(config, Bytes.toBytes(config
				.get(TableOutputFormat.OUTPUT_TABLE)));
	}

	@Override
	protected void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {

		try {
			String lineString = value.toString();
			String[] arr = lineString.split("\t", -1);
			if (arr.length == 2) {
				String midTs = arr[0];
				String cfq = arr[1];
				String[] keys = midTs.split(Const.FIELD_COMMON_SEPARATOR, -1);
				if (keys.length == 2) {
					rowkey = Bytes.toBytes(keys[0]);
					ts = Long.parseLong(keys[1]);
				}

				String[] vals = cfq.split(Const.FIELD_COMMON_SEPARATOR, -1);
				if (vals.length == 3) {
					family = Bytes.toBytes(vals[0]);
					qualifier = Bytes.toBytes(vals[1]);
					cellValue = Bytes.toBytes(vals[2]);

				}

				Put put = new Put(rowkey, ts);
				put.add(family, qualifier, cellValue);

				context.write(new ImmutableBytesWritable(rowkey), put);
//				table.incrementColumnValue(rowkey, family,
//						Const.COLUMN_RK_COUNTER_BYTE, 1L);
				long cur = table.incrementColumnValue(rowkey, family,
						Const.COLUMN_RK_COUNTER_BYTE, 1L);
				System.out.println(cur);
			
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected void cleanup(Context context) throws IOException,
			InterruptedException {
		if (null != table)
			table.close();
	}
}
