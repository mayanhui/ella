package com.adintellig.ella.action;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

public class QuartzInitListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent event) {

	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		System.out.println("============ Start Quartz Job =============");
		System.getProperties().put("org.quartz.properties", "quartz.properties");
		try {
			StdSchedulerFactory.getDefaultScheduler().start();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		System.out.println("============ Quartz Job Started! ===========");
	}
}
