package com.adintellig.ella.hbase.beans.stat;

public class MasterStat {
	private String modelerType;
	private int splitTimeNumOps;
	private int splitTimeAvgTime;
	private int splitTimeMinTime;
	private int splitTimeMaxTime;
	private long splitSizeNumOps;
	private long splitSizeAvgTime;
	private long splitSizeMinTime;
	private long splitSizeMaxTime;
	private long cluster_requests;

	public String getModelerType() {
		return modelerType;
	}

	public void setModelerType(String modelerType) {
		this.modelerType = modelerType;
	}

	public int getSplitTimeNumOps() {
		return splitTimeNumOps;
	}

	public void setSplitTimeNumOps(int splitTimeNumOps) {
		this.splitTimeNumOps = splitTimeNumOps;
	}

	public int getSplitTimeAvgTime() {
		return splitTimeAvgTime;
	}

	public void setSplitTimeAvgTime(int splitTimeAvgTime) {
		this.splitTimeAvgTime = splitTimeAvgTime;
	}

	public int getSplitTimeMinTime() {
		return splitTimeMinTime;
	}

	public void setSplitTimeMinTime(int splitTimeMinTime) {
		this.splitTimeMinTime = splitTimeMinTime;
	}

	public int getSplitTimeMaxTime() {
		return splitTimeMaxTime;
	}

	public void setSplitTimeMaxTime(int splitTimeMaxTime) {
		this.splitTimeMaxTime = splitTimeMaxTime;
	}

	public long getSplitSizeNumOps() {
		return splitSizeNumOps;
	}

	public void setSplitSizeNumOps(long splitSizeNumOps) {
		this.splitSizeNumOps = splitSizeNumOps;
	}

	public long getSplitSizeAvgTime() {
		return splitSizeAvgTime;
	}

	public void setSplitSizeAvgTime(long splitSizeAvgTime) {
		this.splitSizeAvgTime = splitSizeAvgTime;
	}

	public long getSplitSizeMinTime() {
		return splitSizeMinTime;
	}

	public void setSplitSizeMinTime(long splitSizeMinTime) {
		this.splitSizeMinTime = splitSizeMinTime;
	}

	public long getSplitSizeMaxTime() {
		return splitSizeMaxTime;
	}

	public void setSplitSizeMaxTime(long splitSizeMaxTime) {
		this.splitSizeMaxTime = splitSizeMaxTime;
	}

	public long getCluster_requests() {
		return cluster_requests;
	}

	public void setCluster_requests(long cluster_requests) {
		this.cluster_requests = cluster_requests;
	}

}
