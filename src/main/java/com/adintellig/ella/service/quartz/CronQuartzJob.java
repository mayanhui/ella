package com.adintellig.ella.service.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.adintellig.ella.hbase.handler.JMXHandler;

public class CronQuartzJob implements Job {

	public CronQuartzJob() {
	}

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		JMXHandler service = JMXHandler.getInstance();
		Thread t = new Thread(service);
		t.start();
	}

}