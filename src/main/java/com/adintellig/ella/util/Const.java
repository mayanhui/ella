package com.adintellig.ella.util;

import org.apache.hadoop.hbase.util.Bytes;

public class Const {
	public static final String HBASE_TABLE_NAME = "hbase.tablename";
	public static final int MAX_INPUT_SPLIT_SIZE = 50 * 1000 * 1000;// MB
	public static final int MIN_INPUT_SPLIT_SIZE = 512 * 1000 * 1000;// MB

	/* Column Family */
	public static final String COLUMN_FAMILY_CF1_STRING = "cf1";
	public static final byte[] COLUMN_FAMILY_CF1_BYTE = Bytes
			.toBytes(COLUMN_FAMILY_CF1_STRING);

	/* Column */
	public static final String COLUMN_RK_STRING = "rk";
	public static final byte[] COLUMN_RK_BYTE = Bytes.toBytes(COLUMN_RK_STRING);

	/* Counter */
	public static final String COLUMN_RK_COUNTER_STRING = "rk_cnt";
	public static final byte[] COLUMN_RK_COUNTER_BYTE = Bytes
			.toBytes(COLUMN_RK_COUNTER_STRING);

	/* hbase conf */
	public static final String HBASE_CONF_COLUMN_NAME = "conf.column";
	public static final String HBASE_CONF_ISBUILDSINGLEINDEX_NAME = "conf.isbuildsingleindex";
	public static final String HBASE_CONF_JSON_NAME = "conf.json";
	public static final String HBASE_CONF_ROWKEY_NAME = "conf.rowkey";

	public static final String ROWKEY_DEFAULT_SEPARATOR = "_";
	public static final String FAMILY_COLUMN_SEPARATOR = ":";
	public static final String FIELD_COMMON_SEPARATOR = "\u0001";

	public static final String JSON_ARRAY_START = "[";

	/* mapper type */
	public static final String MAPPER_TYPE_JSON = "json";
	public static final String MAPPER_TYPE_ROWKEY = "rowkey";

	public static final String PARAMETER_ISROWKEY = "isrowkey";

	public static final String HADOOP_MAP_SPECULATIVE_EXECUTION = "mapred.map.tasks.speculative.execution";
	public static final String HADOOP_REDUCE_SPECULATIVE_EXECUTION = "mapred.reduce.tasks.speculative.execution";

}
