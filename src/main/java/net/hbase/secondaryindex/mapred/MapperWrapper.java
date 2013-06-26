package net.hbase.secondaryindex.mapred;

import org.apache.hadoop.hbase.mapreduce.TableMapper;

import net.hbase.secondaryindex.util.Const;

public class MapperWrapper {

	@SuppressWarnings("rawtypes")
	public static Class<? extends TableMapper> wrap(String type) {
		Class<? extends TableMapper> c = IndexMapper.class;
		if (null != type && type.length() > 0) {
			if (type.equals(Const.MAPPER_TYPE_JSON))
				c = IndexJsonMapper.class;
			else if (type.equals(Const.MAPPER_TYPE_ROWKEY))
				c = IndexRowkeyMapper.class;
		}
		return c;
	}
}
