package com.adintellig.ella.derby.model;

import java.sql.Timestamp;

public class Request {

	private String host = null;
	private String regionName = null;
	private String tableName = null;
	private long writeCount = 0;
	private long readCount = 0;
	private long totalCount = 0;
	private Timestamp updateTime = null;
	private Timestamp insertTime = null;

	public Request() {
		super();
	}

	public Request(String host, String regionName, String tableName,
			long writeCount, long readCount, long totalCount,
			Timestamp updateTime, Timestamp insertTime) {
		super();
		this.host = host;
		this.regionName = regionName;
		this.tableName = tableName;
		this.writeCount = writeCount;
		this.readCount = readCount;
		this.totalCount = totalCount;
		this.updateTime = updateTime;
		this.insertTime = insertTime;
	}

	@Override
	public String toString() {
		return "Request [host=" + host + ", regionName=" + regionName
				+ ", tableName=" + tableName + ", writeCount=" + writeCount
				+ ", readCount=" + readCount + ", totalCount=" + totalCount
				+ ", updateTime=" + updateTime + ", insertTime=" + insertTime
				+ "]";
	}

	public static void main(String[] args) {
		Request r = new Request(
				"hadoop-node-20",
				"lvv_uid,{0405BD52-C505-C3D6-EC89-C735F3D6DEB3},1372149624541.5ed9f894d4e640fa2260fdcada5fc59a.",
				"lvv_uid", 100L, 1000L, 1100L, new Timestamp(System
						.currentTimeMillis()), new Timestamp(System
						.currentTimeMillis()));
		System.out.println(r.toString());
	}
}