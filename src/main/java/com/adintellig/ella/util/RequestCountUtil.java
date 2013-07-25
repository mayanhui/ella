package com.adintellig.ella.util;

import java.util.List;

import com.adintellig.ella.model.RequestCount;

public class RequestCountUtil {

	public static final String WRITE = "WRITE";
	public static final String READ = "READ";
	public static final String TOTAL = "TOTAL";

	public static int sumTps(List<RequestCount> tables, String type) {
		int sum = 0;

		if (null != tables && tables.size() > 0) {
			for (RequestCount rc : tables) {
				if (type.equals(WRITE))
					sum += rc.getWriteTps();
				else if (type.equals(READ))
					sum += rc.getReadTps();
				else if (type.equals(TOTAL))
					sum += rc.getTotalTps();
			}
		}
		return sum;
	}

}
