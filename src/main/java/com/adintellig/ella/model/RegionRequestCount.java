package com.adintellig.ella.model;

import java.sql.Timestamp;

public class RegionRequestCount extends RequestCount {

	private String regionName = null;

	public RegionRequestCount() {
		super();
	}

	public RegionRequestCount(long writeCount, long readCount, long totalCount,
			Timestamp updateTime, Timestamp insertTime, int writeTps,
			int readTps, int totalTps) {
		super(writeCount, readCount, totalCount, updateTime, insertTime, writeTps,
				readTps, totalTps);
	}

	@Override
	public String toString() {
		return "RegionRequestCount [regionName=" + regionName
				+ ", getRegionName()=" + getRegionName() + ", getWriteCount()="
				+ getWriteCount() + ", getReadCount()=" + getReadCount()
				+ ", getTotalCount()=" + getTotalCount() + ", getUpdateTime()="
				+ getUpdateTime() + ", getInsertTime()=" + getInsertTime()
				+ ", getWriteTps()=" + getWriteTps() + ", getReadTps()="
				+ getReadTps() + ", getTotalTps()=" + getTotalTps()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
				+ ", toString()=" + super.toString() + "]";
	}

	public RegionRequestCount(String regionName) {
		super();
		this.regionName = regionName;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

}