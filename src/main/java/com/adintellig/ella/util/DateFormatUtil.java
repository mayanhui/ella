package com.adintellig.ella.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatUtil {
	public static final String FROMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String FROMAT_2 = "yyyyMMdd";
	public static final String FROMAT_TIME = "HH:mm:ss";
	public static final String FORMAT_UTC_ISO8601 = "yyyy-MM-dd'T'HH:mm:ss'Z'";

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

	public static String formatToString(Date date) {
		String time = null;
		SimpleDateFormat format = new SimpleDateFormat(FROMAT);
		time = format.format(date);

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

	public static String formatToTime(Timestamp ts) {
		SimpleDateFormat format = new SimpleDateFormat(FROMAT_TIME);
		return format.format(ts);
	}
	
	public static String formatToUTC(Timestamp ts){
		SimpleDateFormat format = new SimpleDateFormat(FORMAT_UTC_ISO8601);
		return format.format(ts);
	}

	public static void main(String[] args) {
		String str = "2013-01-15 17:54:29 345:asdf";
		System.out.println(str);
		System.out.println(formatStringTimeToLong(str));
		System.out.println(Integer.parseInt("20130121")
				- Integer.parseInt("20121121"));
		String s = parseToStringDate(formatStringTimeToLong(str));
		System.out.println(s);
		System.out.println(parseToStringDate(1358506007000L));
		System.out.println(formatToTime(new Timestamp(System
				.currentTimeMillis())));

		System.out.println();
		System.out.println(new Timestamp(System.currentTimeMillis()));

		long t1 = Timestamp.valueOf("2013-07-19 18:31:39").getTime();
		long t2 = Timestamp.valueOf("2013-07-19 18:31:09").getTime();

		long timeDiff = (t1 - t2) / 1000;

		System.out.println(timeDiff);
		
		
		System.out.println(formatToString(new Date()));
		
		SimpleDateFormat format = new SimpleDateFormat(FORMAT_UTC_ISO8601);
		System.out.println(format.format(new Date()));
	}
}
