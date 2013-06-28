package com.adintellig.ella.derby.model;

import java.sql.Timestamp;

public class TableRequestCount extends RequestCount {
	private String tableName = null;

	public TableRequestCount() {
		super();
	}

	public TableRequestCount(long writeCount, long readCount, long totalCount,
			Timestamp updateTime, Timestamp insertTime) {
		super(writeCount, readCount, totalCount, updateTime, insertTime);
	}

	public TableRequestCount(long writeCount, long readCount, long totalCount,
			Timestamp updateTime, Timestamp insertTime, String tableName) {
		super(writeCount, readCount, totalCount, updateTime, insertTime);
		this.tableName = tableName;
	}

	@Override
	public String toString() {
		return "TableRequestCount [tableName=" + tableName
				+ ", getWriteCount()=" + getWriteCount() + ", getReadCount()="
				+ getReadCount() + ", getTotalCount()=" + getTotalCount()
				+ ", getUpdateTime()=" + getUpdateTime() + ", getInsertTime()="
				+ getInsertTime() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

}
