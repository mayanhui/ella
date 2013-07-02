package com.adintellig.ella.quartz;

import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

public class TestJob {

	public static void main(String[] args) {
		System.out.println("start");
		System.getProperties().put("org.quartz.properties", "quartz.properties");
		try {
			StdSchedulerFactory.getDefaultScheduler().start();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		System.out.println("end");
	}

}