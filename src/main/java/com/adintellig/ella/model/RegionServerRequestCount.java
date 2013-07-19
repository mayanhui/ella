package com.adintellig.ella.model;

import java.sql.Timestamp;

public class RegionServerRequestCount extends RequestCount {
	private String serverHost = null;

	public RegionServerRequestCount(String serverHost) {
		super();
		this.serverHost = serverHost;
	}

	public RegionServerRequestCount() {
		super();
	}

	public RegionServerRequestCount(long writeCount, long readCount,
			long totalCount, Timestamp updateTime, Timestamp insertTime,
			int writeTps, int readTps, int totalTps) {
		super(writeCount, readCount, totalCount, updateTime, insertTime,
				writeTps, readTps, totalTps);
	}

	public String getServerHost() {
		return serverHost;
	}

	public void setServerHost(String serverHost) {
		this.serverHost = serverHost;
	}

}
