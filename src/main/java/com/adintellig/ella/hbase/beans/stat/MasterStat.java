package com.adintellig.ella.hbase.beans.stat;

public class MasterStat {
	private String name;
	private String modelerType;
	private String splitTimeNumOps;
	private String splitTimeAvgTime;
	private String splitTimeMinTime;
	private String splitTimeMaxTime;
	private String splitSizeNumOps;
	private String splitSizeAvgTime;
	private String splitSizeMinTime;
	private String splitSizeMaxTime;
	private String cluster_requests;

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

	public String getSplitTimeNumOps() {
		return splitTimeNumOps;
	}

	public void setSplitTimeNumOps(String splitTimeNumOps) {
		this.splitTimeNumOps = splitTimeNumOps;
	}

	public String getSplitTimeAvgTime() {
		return splitTimeAvgTime;
	}

	public void setSplitTimeAvgTime(String splitTimeAvgTime) {
		this.splitTimeAvgTime = splitTimeAvgTime;
	}

	public String getSplitTimeMinTime() {
		return splitTimeMinTime;
	}

	public void setSplitTimeMinTime(String splitTimeMinTime) {
		this.splitTimeMinTime = splitTimeMinTime;
	}

	public String getSplitTimeMaxTime() {
		return splitTimeMaxTime;
	}

	public void setSplitTimeMaxTime(String splitTimeMaxTime) {
		this.splitTimeMaxTime = splitTimeMaxTime;
	}

	public String getSplitSizeNumOps() {
		return splitSizeNumOps;
	}

	public void setSplitSizeNumOps(String splitSizeNumOps) {
		this.splitSizeNumOps = splitSizeNumOps;
	}

	public String getSplitSizeAvgTime() {
		return splitSizeAvgTime;
	}

	public void setSplitSizeAvgTime(String splitSizeAvgTime) {
		this.splitSizeAvgTime = splitSizeAvgTime;
	}

	public String getSplitSizeMinTime() {
		return splitSizeMinTime;
	}

	public void setSplitSizeMinTime(String splitSizeMinTime) {
		this.splitSizeMinTime = splitSizeMinTime;
	}

	public String getSplitSizeMaxTime() {
		return splitSizeMaxTime;
	}

	public void setSplitSizeMaxTime(String splitSizeMaxTime) {
		this.splitSizeMaxTime = splitSizeMaxTime;
	}

	public String getCluster_requests() {
		return cluster_requests;
	}

	public void setCluster_requests(String cluster_requests) {
		this.cluster_requests = cluster_requests;
	}

}
