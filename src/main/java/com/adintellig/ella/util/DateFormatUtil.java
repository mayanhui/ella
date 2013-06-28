package com.adintellig.ella.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatUtil {
	public static final String FROMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String FROMAT_2 = "yyyyMMdd";

	public static long formatStringTimeToLong(String timeLine) {
		long time = -1L;
		SimpleDateFormat format = new SimpleDateFormat(FROMAT);
		try {
			time = format.parse(timeLine).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return time;
	}
	
	public static long formatStringTimeToLong2(String timeLine) {
		long time = -1L;
		SimpleDateFormat format = new SimpleDateFormat(FROMAT_2);
		try {
			time = format.parse(timeLine).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return time;
	}

	public static String parseToStringDate(long ms) {
		SimpleDateFormat format = new SimpleDateFormat(FROMAT_2);
		String time = format.format(new Date(ms));
		return time;
	}

	public static void main(String[] args) {
		String str = "2013-01-15 17:54:29 345:asdf";
		System.out.println(str);
		System.out.println(formatStringTimeToLong(str));
		System.out.println(Integer.parseInt("20130121") - Integer.parseInt("20121121"));
		String s = parseToStringDate(formatStringTimeToLong(str));
		System.out.println(s);
		System.out.println(parseToStringDate(1358506007000L));
	}
}
