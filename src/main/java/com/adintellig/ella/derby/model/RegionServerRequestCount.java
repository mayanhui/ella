package com.adintellig.ella.derby.model;

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
			long totalCount, Timestamp updateTime, Timestamp insertTime) {
		super(writeCount, readCount, totalCount, updateTime, insertTime);
	}

	@Override
	public String toString() {
		return "RegionServerRequestCount [serverHost=" + serverHost
				+ ", getWriteCount()=" + getWriteCount() + ", getReadCount()="
				+ getReadCount() + ", getTotalCount()=" + getTotalCount()
				+ ", getUpdateTime()=" + getUpdateTime() + ", getInsertTime()="
				+ getInsertTime() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}

	public String getServerHost() {
		return serverHost;
	}

	public void setServerHost(String serverHost) {
		this.serverHost = serverHost;
	}

}
