package net.hbase.secondaryindex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

import net.hbase.secondaryindex.util.Const;

public class Test {
	public static void main(String args[]) throws Exception {
		String str = "mail";
		System.out.println(str.indexOf(":"));

		List<String> list = new ArrayList<String>();
		List<String> list1 = new ArrayList<String>();
		list1.add("@@");

		for (String s : list) {
			for (String s1 : list1) {
				System.out.println(s + ":" + s1);
			}
		}

		String[] arr = new String[] { "1", "2", "3" };
		List<String> arrList = Arrays.asList(arr);
		// arrList.remove(0);
		System.out.println(arrList);

		list = new ArrayList<String>();
		for (String s : arr) {
			list.add(s);
		}
		list.remove("2");
		System.out.println(list);
		str = "[test start]";
		if (str.startsWith(Const.JSON_ARRAY_START)) {
			System.out.println(str);
		}

		String column = "rowkey";
		Scan scan = new Scan();
		arr = column.split(",", -1);
		if (column != null && !column.equals(Const.MAPPER_TYPE_ROWKEY)) {
			System.out.println(column);
			if (null != arr && arr.length > 0) {
				for (String col : arr) {
					byte[][] colkey = KeyValue.parseColumn(Bytes.toBytes(col));
					if (colkey.length > 1) {
						scan.addColumn(colkey[0], colkey[1]);
					} else {
						scan.addFamily(colkey[0]);
					}
				}
			}
		}
		scan.setTimeRange(2L, 10000000L);
		scan.setMaxVersions(1);
		/* batch and caching */
		scan.setBatch(0);
		scan.setCaching(10000);
		System.out.println(scan.toJSON());
		String s = new String(
				Bytes.toBytesBinary("attr:movt_area_\\xE6\\xAC\\xA7\\xE7\\xBE\\x8E_type_\\xE7\\x94\\xB5\\xE5\\xBD\\xB1"),
				"utf-8");
		System.out.println(s);
		s = new String(
				Bytes.toBytesBinary("attr:movt_category_\\xE4\\xBC\\xA6\\xE7\\x90\\x86_area_\\xE9\\x9F\\xA9\\xE5\\x9B\\xBD_type_\\xE7\\x94\\xB5\\xE5\\xBD\\xB1"));
		System.out.println(s);
		
	}
}
