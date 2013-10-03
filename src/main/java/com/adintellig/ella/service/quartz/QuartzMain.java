package com.adintellig.ella.service.quartz;

import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

public class QuartzMain {

	public static void main(String[] args) {
		System.getProperties()
				.put("org.quartz.properties", "quartz.properties");
		try {
			StdSchedulerFactory.getDefaultScheduler().start();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

}