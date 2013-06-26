package net.hbase.secondaryindex.coprocessor;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.hbase.HBaseConfiguration;
//import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.coprocessor.BaseRegionObserver;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
//import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.regionserver.wal.WALEdit;
import org.apache.hadoop.hbase.util.Bytes;

public class IndexerDemo extends BaseRegionObserver {

	public static final String INDEX_TABLE_SUFFIX = "_index";
	public String tableName = "demo_table";
	public String inputColumn = "cf1:c1";
	public String indexColumn = "cf1:c1";
	public HTable table;

	@Override
	public void preOpen(ObserverContext<RegionCoprocessorEnvironment> e) {
		try {
			table = new HTable(HBaseConfiguration.create(), tableName
					+ INDEX_TABLE_SUFFIX);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void postPut(final ObserverContext e, final Put put,
			final WALEdit edit, final boolean writeToWAL) throws IOException {

		byte[][] colkey = KeyValue.parseColumn(Bytes.toBytes(inputColumn));
		if (colkey.length > 1) {
			List kvList = put.get(colkey[0], colkey[1]);
			Iterator kvl = kvList.iterator();

			while (kvl.hasNext()) {
				KeyValue kv = (KeyValue) kvl.next();
				Put indexPut = new Put(kv.getValue());
				colkey = KeyValue.parseColumn(Bytes.toBytes(indexColumn));
				indexPut.add(colkey[0], colkey[1], kv.getRow());
				table.put(indexPut);
			}
		}
	}

	@Override
	public void postClose(ObserverContext<RegionCoprocessorEnvironment> e,
			boolean abortRequested) {
		try {
			table.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
