package com.adintellig.ella.hbase.beans.request;

public class RegionServer {
	private String key;
	private RegionServerValue value;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public RegionServerValue getValue() {
		return value;
	}

	public void setValue(RegionServerValue value) {
		this.value = value;
	}

}
