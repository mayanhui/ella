package com.adintellig.ella.service.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.adintellig.ella.hbase.JMXHMasterService;

public class CronQuartzJob implements Job {

	public CronQuartzJob() {
	}

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		JMXHMasterService service = JMXHMasterService.getInstance();
		Thread t = new Thread(service);
		t.start();
	}

}