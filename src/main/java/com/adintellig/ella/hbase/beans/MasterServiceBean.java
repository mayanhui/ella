package com.adintellig.ella.hbase.beans;

public class MasterServiceBean {
	private String name;
	private String ServerName;
	private Float AverageLoad;
	private RegionServer[] RegionServers;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getServerName() {
		return ServerName;
	}

	public void setServerName(String serverName) {
		ServerName = serverName;
	}

	public Float getAverageLoad() {
		return AverageLoad;
	}

	public void setAverageLoad(Float averageLoad) {
		AverageLoad = averageLoad;
	}

	public RegionServer[] getRegionServers() {
		return RegionServers;
	}

	public void setRegionServers(RegionServer[] regionServers) {
		RegionServers = regionServers;
	}

}
