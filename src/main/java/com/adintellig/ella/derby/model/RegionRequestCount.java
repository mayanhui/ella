package com.adintellig.ella.derby.model;

import java.sql.Timestamp;

public class RegionRequestCount extends RequestCount {

	private String regionName = null;

	public RegionRequestCount() {
		super();
	}

	public RegionRequestCount(long writeCount, long readCount, long totalCount,
			Timestamp updateTime, Timestamp insertTime) {
		super(writeCount, readCount, totalCount, updateTime, insertTime);
	}

	public RegionRequestCount(String regionName) {
		super();
		this.regionName = regionName;
	}

	public RegionRequestCount(long writeCount, long readCount, long totalCount,
			Timestamp updateTime, Timestamp insertTime, String regionName) {
		super(writeCount, readCount, totalCount, updateTime, insertTime);
		this.regionName = regionName;
	}

	@Override
	public String toString() {
		return "RegionRequestCount [regionName=" + regionName
				+ ", getWriteCount()=" + getWriteCount() + ", getReadCount()="
				+ getReadCount() + ", getTotalCount()=" + getTotalCount()
				+ ", getUpdateTime()=" + getUpdateTime() + ", getInsertTime()="
				+ getInsertTime() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}
	
}