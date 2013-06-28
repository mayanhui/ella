package com.adintellig.ella.hbase.beans;

public class RegionServerValue {
	
	private Integer load;
	private RegionsLoad[] regionsLoad;

	public Integer getLoad() {
		return load;
	}

	public void setLoad(Integer load) {
		this.load = load;
	}

	public RegionsLoad[] getRegionsLoad() {
		return regionsLoad;
	}

	public void setRegionsLoad(RegionsLoad[] regionsLoad) {
		this.regionsLoad = regionsLoad;
	}

}
