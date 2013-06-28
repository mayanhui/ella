package com.adintellig.ella.derby.model;

import java.sql.Timestamp;

public class RegionRequestCount {

	private String regionName = null;
	private long writeCount = 0;
	private long readCount = 0;
	private long totalCount = 0;
	private Timestamp updateTime = null;
	private Timestamp insertTime = null;

	public RegionRequestCount() {
		super();
	}

	public RegionRequestCount(String regionName, long writeCount,
			long readCount, long totalCount, Timestamp updateTime,
			Timestamp insertTime) {
		super();
		this.regionName = regionName;
		this.writeCount = writeCount;
		this.readCount = readCount;
		this.totalCount = totalCount;
		this.updateTime = updateTime;
		this.insertTime = insertTime;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public long getWriteCount() {
		return writeCount;
	}

	public void setWriteCount(long writeCount) {
		this.writeCount = writeCount;
	}

	public long getReadCount() {
		return readCount;
	}

	public void setReadCount(long readCount) {
		this.readCount = readCount;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}

	public Timestamp getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(Timestamp insertTime) {
		this.insertTime = insertTime;
	}

	@Override
	public String toString() {
		return "Request [regionName=" + regionName + ", writeCount="
				+ writeCount + ", readCount=" + readCount + ", totalCount="
				+ totalCount + ", updateTime=" + updateTime + ", insertTime="
				+ insertTime + "]";
	}

	public static void main(String[] args) {
		RegionRequestCount r = new RegionRequestCount(
				"lvv_uid,{0405BD52-C505-C3D6-EC89-C735F3D6DEB3},1372149624541.5ed9f894d4e640fa2260fdcada5fc59a.",
				100L, 1000L, 1100L, new Timestamp(System.currentTimeMillis()),
				new Timestamp(System.currentTimeMillis()));
		System.out.println(r.toString());
	}
}