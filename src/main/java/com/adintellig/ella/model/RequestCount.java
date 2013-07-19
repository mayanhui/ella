package com.adintellig.ella.model;

import java.sql.Timestamp;

public class RequestCount {
	private long writeCount = 0l;
	private long readCount = 0l;
	private long totalCount = 0l;
	private Timestamp updateTime = null;
	private Timestamp insertTime = null;
	private int writeTps = 0;
	private int readTps = 0;
	private int totalTps = 0;

	public RequestCount() {
	}

	public RequestCount(long writeCount, long readCount, long totalCount,
			Timestamp updateTime, Timestamp insertTime, int writeTps,
			int readTps, int totalTps) {
		super();
		this.writeCount = writeCount;
		this.readCount = readCount;
		this.totalCount = totalCount;
		this.updateTime = updateTime;
		this.insertTime = insertTime;
		this.writeTps = writeTps;
		this.readTps = readTps;
		this.totalTps = totalTps;
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

	public int getWriteTps() {
		return writeTps;
	}

	public void setWriteTps(int writeTps) {
		this.writeTps = writeTps;
	}

	public int getReadTps() {
		return readTps;
	}

	public void setReadTps(int readTps) {
		this.readTps = readTps;
	}

	public int getTotalTps() {
		return totalTps;
	}

	public void setTotalTps(int totalTps) {
		this.totalTps = totalTps;
	}

}
