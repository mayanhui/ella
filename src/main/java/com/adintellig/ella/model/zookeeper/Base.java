package com.adintellig.ella.model.zookeeper;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

import org.apache.hadoop.hbase.zookeeper.ZKUtil;

public class Base {
	private String hdfsRoot;
	private String masterAddress;
	private String backupMasterAddress;
	private String regionServerHoldingRoot;
	private String regionServers;
	private Timestamp updateTime;
	
	private List<Quorum> quorums;

	public String getHdfsRoot() {
		return hdfsRoot;
	}

	public void setHdfsRoot(String hdfsRoot) {
		this.hdfsRoot = hdfsRoot;
	}

	public String getMasterAddress() {
		return masterAddress;
	}

	public void setMasterAddress(String masterAddress) {
		this.masterAddress = masterAddress;
	}

	public String getBackupMasterAddress() {
		return backupMasterAddress;
	}

	public void setBackupMasterAddress(String backupMasterAddress) {
		this.backupMasterAddress = backupMasterAddress;
	}

	public String getRegionServerHoldingRoot() {
		return regionServerHoldingRoot;
	}

	public void setRegionServerHoldingRoot(String regionServerHoldingRoot) {
		this.regionServerHoldingRoot = regionServerHoldingRoot;
	}

	public String getRegionServers() {
		return regionServers;
	}

	public void setRegionServers(String regionServers) {
		this.regionServers = regionServers;
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}
	
	public List<Quorum> getQuorums() {
		return quorums;
	}

	public void setQuorums(List<Quorum> quorums) {
		this.quorums = quorums;
	}

	
	
	@Override
	public String toString() {
		return "Base [hdfsRoot=" + hdfsRoot + ", masterAddress="
				+ masterAddress + ", backupMasterAddress="
				+ backupMasterAddress + ", regionServerHoldingRoot="
				+ regionServerHoldingRoot + ", regionServers=" + regionServers
				+ ", updateTime=" + updateTime + ", quorums=" + quorums + "]";
	}

	public static void main(String[] args) throws IOException{
		ZKUtil.getServerStats("", 0);
	}
}
