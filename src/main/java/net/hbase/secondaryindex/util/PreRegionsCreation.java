package net.hbase.secondaryindex.util;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.regionserver.StoreFile;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.util.GenericOptionsParser;

public class PreRegionsCreation {
	static final Log LOG = LogFactory.getLog(PreRegionsCreation.class);

	public static final String NAME = "Pre-Region-Creation";

	public int NUM_REGIONSERVERS;
	public static final String DEFAULT_START_KEY = "0";
	public static final String DEFAULT_END_KEY = "zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz";

	public Configuration config;
	public HBaseAdmin admin;

	ConfigProperties cp = ConfigFactory.getInstance().getConfigProperties(
			ConfigFactory.INDEX_CONFIG_PATH);

	public PreRegionsCreation() {
		config = HBaseConfiguration.create();
		config.set(ConfigProperties.CONFIG_NAME_HBASE_MASTER,
				cp.getProperty(ConfigProperties.CONFIG_NAME_HBASE_MASTER));
		config.set(
				ConfigProperties.CONFIG_NAME_HBASE_ZOOKEEPER_PROPRERTY_CLIENTPORT,
				cp.getProperty(ConfigProperties.CONFIG_NAME_HBASE_ZOOKEEPER_PROPRERTY_CLIENTPORT));
		config.set(
				ConfigProperties.CONFIG_NAME_HBASE_ZOOKEEPER_QUORUM,
				cp.getProperty(ConfigProperties.CONFIG_NAME_HBASE_ZOOKEEPER_QUORUM));

		// double number of regionserver
		NUM_REGIONSERVERS = 2 * cp.getInt(
				ConfigProperties.CONFIG_NAME_HBASE_REGIONSERVER_NUM, 1);

		try {
			admin = new HBaseAdmin(config);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void create(String tableName, String[] columnFamily,
			String startKey, String endKey) throws IOException {
		HTableDescriptor desc = new HTableDescriptor(Bytes.toBytes(tableName));
		for (String cf : columnFamily) {
			HColumnDescriptor cfDesc = new HColumnDescriptor(cf);
			// column family attribute
			cfDesc.setValue(HConstants.VERSIONS,
					String.valueOf(Integer.MAX_VALUE));
			cfDesc.setValue(HColumnDescriptor.BLOOMFILTER,
					StoreFile.BloomType.ROW.toString());
			desc.addFamily(cfDesc);
		}
		if (!admin.tableExists(tableName)) {
			if (null != startKey && null != endKey)
				admin.createTable(desc, Bytes.toBytes(startKey),
						Bytes.toBytes(endKey), NUM_REGIONSERVERS);
		}
	}

	public void create(String tableName, String[] columnFamily)
			throws IOException {
		create(tableName, columnFamily, DEFAULT_START_KEY, DEFAULT_END_KEY);
	}

	public static void main(String[] args) throws IOException, ParseException {
		Configuration conf = HBaseConfiguration.create();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		CommandLine cmd = parseArgs(otherArgs);
		String indexTable = cmd.getOptionValue("i");
		String columns = cmd.getOptionValue("c");
		String startKey = cmd.getOptionValue("s");
		String endKey = cmd.getOptionValue("e");

		PreRegionsCreation pc = new PreRegionsCreation();
		String[] cfs = columns.split(",");
		if (null != startKey && null != endKey)
			pc.create(indexTable, cfs, startKey, endKey);
		else
			pc.create(indexTable, cfs);
	}

	private static CommandLine parseArgs(String[] args) throws ParseException {
		Options options = new Options();
		Option o = new Option("i", "indexTable", true,
				"the directory or file to read from (must exist)");
		o.setArgName("input-table-name");
		o.setRequired(true);
		options.addOption(o);

		o = new Option(
				"c",
				"column",
				true,
				"column to store row data into (must exist). Such as: cf1:age,cf2:tag,cf2:msg  or rowkey or rowkey,cf1:age. The last two usage are for 'rowkey' index building.");
		o.setArgName("family:qualifier");
		o.setRequired(true);
		options.addOption(o);

		o = new Option("s", "startKey", true,
				"the start date of data to build index(default is 19700101), such as: 20130101");
		o.setArgName("start-date");
		o.setRequired(false);
		options.addOption(o);

		o = new Option("e", "endKey", true,
				"the end date of data to build index(default is today), such as: 20130120");
		o.setArgName("end-date");
		o.setRequired(false);
		options.addOption(o);

		CommandLineParser parser = new PosixParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (Exception e) {
			System.err.println("ERROR: " + e.getMessage() + "\n");
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(NAME + " ", options, true);
			System.exit(-1);
		}
		return cmd;
	}

}
