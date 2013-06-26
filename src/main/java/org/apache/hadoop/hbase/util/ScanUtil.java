package org.apache.hadoop.hbase.util;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Scan;

public class ScanUtil {
	/**
	 * Parses a combined family and qualifier and adds either both or just the
	 * family in case there is not qualifier. This assumes the older colon
	 * divided notation, e.g. "data:contents" or "meta:".
	 * <p>
	 * Note: It will through an error when the colon is missing.
	 * 
	 * @param familyAndQualifier
	 *            family and qualifier
	 * @return A reference to this instance.
	 * @throws IllegalArgumentException
	 *             When the colon is missing.
	 */
	public static void addColumn(Scan scan, byte[] familyAndQualifier) {
		byte[][] fq = KeyValue.parseColumn(familyAndQualifier);
		if (fq.length > 1 && fq[1] != null && fq[1].length > 0) {
			scan.addColumn(fq[0], fq[1]);
		} else {
			scan.addFamily(fq[0]);
		}
	}

	/**
	 * Adds an array of columns specified using old format, family:qualifier.
	 * <p>
	 * Overrides previous calls to addFamily for any families in the input.
	 * 
	 * @param columns
	 *            array of columns, formatted as
	 * 
	 *            <pre>
	 * family:qualifier
	 * </pre>
	 */
	public static void addColumns(Scan scan, byte[][] columns) {
		for (byte[] column : columns) {
			addColumn(scan, column);
		}
	}
	
	
}
