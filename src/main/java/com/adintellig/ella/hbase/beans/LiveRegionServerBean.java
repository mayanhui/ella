package com.adintellig.ella.hbase.beans;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonAutoDetect
public class LiveRegionServerBean {
	private String name;
	private String modelerType;
	private String liveRegionServers;
	
	/*对JSON不规范命名，需要进行标注*/
	@JsonProperty("tag.deadRegionServers")
	private String tagDeadRegionServers;
	@JsonProperty("tag.zookeeperQuorum")
	private String tagZookeeperQuorum;
	@JsonProperty("tag.serverName")
	private String tagServerName;
	@JsonProperty("tag.clusterId")
	private String tagClusterId;
	@JsonProperty("tag.isActiveMaster")
	private String tagIsActiveMaster;
	@JsonProperty("tag.Context")
	private String tagContext;
	@JsonProperty("tag.Hostname")
	private String tagHostname;
	
	private String masterActiveTime;
	private String masterStartTime;
	private Float averageLoad;
    private Integer numRegionServers;
    private Integer numDeadRegionServers;
	private Integer clusterRequests;
	
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
	public String getTagDeadRegionServers() {
		return tagDeadRegionServers;
	}
	public void setTagDeadRegionServers(String tagDeadRegionServers) {
		this.tagDeadRegionServers = tagDeadRegionServers;
	}
	public String getTagZookeeperQuorum() {
		return tagZookeeperQuorum;
	}
	public void setTagZookeeperQuorum(String tagZookeeperQuorum) {
		this.tagZookeeperQuorum = tagZookeeperQuorum;
	}
	public String getTagServerName() {
		return tagServerName;
	}
	public void setTagServerName(String tagServerName) {
		this.tagServerName = tagServerName;
	}
	public String getTagClusterId() {
		return tagClusterId;
	}
	public void setTagClusterId(String tagClusterId) {
		this.tagClusterId = tagClusterId;
	}
	public String getTagIsActiveMaster() {
		return tagIsActiveMaster;
	}
	public void setTagIsActiveMaster(String tagIsActiveMaster) {
		this.tagIsActiveMaster = tagIsActiveMaster;
	}
	public String getTagContext() {
		return tagContext;
	}
	public void setTagContext(String tagContext) {
		this.tagContext = tagContext;
	}
	public String getTagHostname() {
		return tagHostname;
	}
	public void setTagHostname(String tagHostname) {
		this.tagHostname = tagHostname;
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
	public Integer getClusterRequests() {
		return clusterRequests;
	}
	public void setClusterRequests(Integer clusterRequests) {
		this.clusterRequests = clusterRequests;
	}
	
}
