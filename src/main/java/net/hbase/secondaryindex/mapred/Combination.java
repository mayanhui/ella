package net.hbase.secondaryindex.mapred;

import java.util.*;

import net.hbase.secondaryindex.util.Const;

@SuppressWarnings("rawtypes")
public class Combination {

	public static void main(String[] args) {
		String cols = "bhvr:vvmid,bhvr:search,attr:age";
		String[] arr = cols.split(",", -1);
		Map<String, String> colVal = new HashMap<String, String>();
		colVal.put("bhvr:vvmid", "12358");
		colVal.put("bhvr:search", "12306");
		colVal.put("attr:age", "12");

		Vector<String> source = new Vector<String>();
		for (String col : arr) {
			String cn = colVal.get(col);
			source.add(col + Const.ROWKEY_DEFAULT_SEPARATOR + cn);
		}

		Vector<Vector> comb = Combination.getLowerLimitCombinations(source, 2);
		if (null != comb && comb.size() > 0) {
			for (Vector v : comb) {
				System.out.println(v.toString().replaceAll(", ", "_")
						.replaceAll("\\[", "").replaceAll("\\]", ""));
				System.out.println(v.toString());
			}
		}

	}

	@SuppressWarnings("unchecked")
	public static Vector<Vector> getAllCombinations(Vector data) {
		Vector allCombinations = new Vector();
		for (int i = 1; i <= data.size(); i++) {
			allCombinations.addAll(getAllCombinations(data, i));
		}
		return allCombinations;
	}

	@SuppressWarnings("unchecked")
	public static Vector<Vector> getLowerLimitCombinations(Vector data,
			int lowerLimit) {
		Vector allCombinations = new Vector();
		for (int i = lowerLimit; i <= data.size(); i++) {
			allCombinations.addAll(getAllCombinations(data, i));
		}
		return allCombinations;
	}

	public static Vector getAllCombinations(Vector data, int length) {
		Vector allCombinations = new Vector();
		Vector initialCombination = new Vector();
		combination(allCombinations, data, initialCombination, length);
		return allCombinations;
	}

	@SuppressWarnings("unchecked")
	private static void combination(Vector allCombinations, Vector data,
			Vector initialCombination, int length) {
		if (length == 1) {
			for (int i = 0; i < data.size(); i++) {
				Vector newCombination = new Vector(initialCombination);
				newCombination.add(data.elementAt(i));
				allCombinations.add(newCombination);
			}
		}

		if (length > 1) {
			for (int i = 0; i < data.size(); i++) {
				Vector newCombination = new Vector(initialCombination);
				newCombination.add(data.elementAt(i));

				Vector newData = new Vector(data);
				for (int j = 0; j <= i; j++)
					newData.remove(data.elementAt(j));

				combination(allCombinations, newData, newCombination,
						length - 1);
			}
		}
	}
}