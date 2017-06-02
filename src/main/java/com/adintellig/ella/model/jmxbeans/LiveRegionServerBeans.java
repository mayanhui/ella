package com.adintellig.ella.model.jmxbeans;

/**
 * 
 * @author didi
 *	HBase HMaster JMX beans, 对应live RS列表信息
 *{
  "beans" : [ {
    "name" : "Hadoop:service=HBase,name=Master,sub=Server",
    "modelerType" : "Master,sub=Server",
    "tag.liveRegionServers" : "slave,16020,1496208060829;master,16020,1496208216136",
    "tag.deadRegionServers" : "",
    "tag.zookeeperQuorum" : "master:2181",
    "tag.serverName" : "master,16000,1496208062206",
    "tag.clusterId" : "620c85ca-d69f-4682-b68c-9f5a918660c5",
    "tag.isActiveMaster" : "true",
    "tag.Context" : "master",
    "tag.Hostname" : "master",
    "masterActiveTime" : 1496208066668,
    "masterStartTime" : 1496208062206,
    "averageLoad" : 3.0,
    "numRegionServers" : 2,
    "numDeadRegionServers" : 0,
    "clusterRequests" : 24
  } ]
}
 */

public class LiveRegionServerBeans {
	private LiveRegionServerBean[] beans;

	public LiveRegionServerBean[] getBeans() {
		return beans;
	}

	public void setBeans(LiveRegionServerBean[] beans) {
		this.beans = beans;
	}

}
