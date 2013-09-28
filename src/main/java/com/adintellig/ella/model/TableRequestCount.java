package com.adintellig.ella.model;

import java.sql.Timestamp;

public class TableRequestCount extends RequestCount {
	private String tableName = null;
	private int regionCount = 0;

	public TableRequestCount() {
		super();
	}

	public TableRequestCount(String tableName, int regionCount) {
		super();
		this.tableName = tableName;
		this.regionCount = regionCount;
	}

	public TableRequestCount(long writeCount, long readCount, long totalCount,
			Timestamp updateTime, Timestamp insertTime, int writeTps,
			int readTps, int totalTps) {
		super(writeCount, readCount, totalCount, updateTime, insertTime,
				writeTps, readTps, totalTps);
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public int getRegionCount() {
		return regionCount;
	}

	public void setRegionCount(int regionCount) {
		this.regionCount = regionCount;
	}

	@Override
	public String toString() {
		return "TableRequestCount [tableName=" + tableName
				+ ", getTableName()=" + getTableName() + ", getWriteCount()="
				+ getWriteCount() + ", getReadCount()=" + getReadCount()
				+ ", getTotalCount()=" + getTotalCount() + ", getUpdateTime()="
				+ getUpdateTime() + ", getInsertTime()=" + getInsertTime()
				+ ", getWriteTps()=" + getWriteTps() + ", getReadTps()="
				+ getReadTps() + ", getTotalTps()=" + getTotalTps()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
				+ ", toString()=" + super.toString() + "]";
	}

}
