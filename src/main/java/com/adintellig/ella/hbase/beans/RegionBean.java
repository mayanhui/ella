package com.adintellig.ella.hbase.beans;

public class RegionBean {
	
	private String rsName;
	private String namespace;
	private String tableName;
	private String regionName;
	private Long writeCount;
	private Long readCount;
	
	public String getRsName() {
		return rsName;
	}
	public void setRsName(String rsName) {
		this.rsName = rsName;
	}
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getRegionName() {
		return regionName;
	}
	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}
	public Long getWriteCount() {
		return writeCount;
	}
	public void setWriteCount(Long writeCount) {
		this.writeCount = writeCount;
	}
	public Long getReadCount() {
		return readCount;
	}
	public void setReadCount(Long readCount) {
		this.readCount = readCount;
	}
	@Override
	public String toString() {
		return "RegionBean [rsName=" + rsName + ", namespace=" + namespace + ", tableName=" + tableName
				+ ", regionName=" + regionName + ", writeCount=" + writeCount + ", readCount=" + readCount + "]";
	}
	
	
	
	
}
