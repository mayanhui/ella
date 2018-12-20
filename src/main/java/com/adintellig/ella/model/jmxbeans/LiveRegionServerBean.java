package com.adintellig.ella.model.jmxbeans;

import org.codehaus.jackson.annotate.JsonAutoDetect;

@JsonAutoDetect
public class LiveRegionServerBean {
	private String name;
	private String modelerType;
	private String liveRegionServers;
	
	private String deadRegionServers;
	private String zookeeperQuorum;
	private String serverName;
	private String clusterId;
	private String isActiveMaster;
	private String context;
	private String hostname;
	
	private String masterActiveTime;
	private String masterStartTime;
	private Float averageLoad;
    private Integer numRegionServers;
    private Integer numDeadRegionServers;
	private Long clusterRequests;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getModelerType() {
		return modelerType;
	}
	public void setModelerType(String modelerType) {
		this.modelerType = modelerType;
	}
	public String getLiveRegionServers() {
		return liveRegionServers;
	}
	public void setLiveRegionServers(String liveRegionServers) {
		this.liveRegionServers = liveRegionServers;
	}
	
	public String getDeadRegionServers() {
		return deadRegionServers;
	}
	public void setDeadRegionServers(String deadRegionServers) {
		this.deadRegionServers = deadRegionServers;
	}
	public String getZookeeperQuorum() {
		return zookeeperQuorum;
	}
	public void setZookeeperQuorum(String zookeeperQuorum) {
		this.zookeeperQuorum = zookeeperQuorum;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public String getClusterId() {
		return clusterId;
	}
	public void setClusterId(String clusterId) {
		this.clusterId = clusterId;
	}
	public String getIsActiveMaster() {
		return isActiveMaster;
	}
	public void setIsActiveMaster(String isActiveMaster) {
		this.isActiveMaster = isActiveMaster;
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public String getMasterActiveTime() {
		return masterActiveTime;
	}
	public void setMasterActiveTime(String masterActiveTime) {
		this.masterActiveTime = masterActiveTime;
	}
	public String getMasterStartTime() {
		return masterStartTime;
	}
	public void setMasterStartTime(String masterStartTime) {
		this.masterStartTime = masterStartTime;
	}
	public Float getAverageLoad() {
		return averageLoad;
	}
	public void setAverageLoad(Float averageLoad) {
		this.averageLoad = averageLoad;
	}
	public Integer getNumRegionServers() {
		return numRegionServers;
	}
	public void setNumRegionServers(Integer numRegionServers) {
		this.numRegionServers = numRegionServers;
	}
	public Integer getNumDeadRegionServers() {
		return numDeadRegionServers;
	}
	public void setNumDeadRegionServers(Integer numDeadRegionServers) {
		this.numDeadRegionServers = numDeadRegionServers;
	}
	public Long getClusterRequests() {
		return clusterRequests;
	}
	public void setClusterRequests(Long clusterRequests) {
		this.clusterRequests = clusterRequests;
	}
	
}
