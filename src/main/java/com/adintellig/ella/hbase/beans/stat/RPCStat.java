package com.adintellig.ella.hbase.beans.stat;

public class RPCStat {
	private String modelerType;
	private int deleteTableMinTime;
	private int deleteTableMaxTime;
	private int getHTableDescriptorsMinTime;
	private int getHTableDescriptorsMaxTime;
	private int disableTableMinTime;
	private int disableTableMaxTime;
	private int createTableMinTime;
	private int createTableMaxTime;
	private int rpcProcessingTimeMinTime;
	private int rpcProcessingTimeMaxTime;
	private int regionServerStartupMinTime;
	private int regionServerStartupMaxTime;
	private int rpcQueueTimeMinTime;
	private int rpcQueueTimeMaxTime;
	private int regionServerReportMinTime;
	private int regionServerReportMaxTime;

	public String getModelerType() {
		return modelerType;
	}

	public void setModelerType(String modelerType) {
		this.modelerType = modelerType;
	}

	public int getDeleteTableMinTime() {
		return deleteTableMinTime;
	}

	public void setDeleteTableMinTime(int deleteTableMinTime) {
		this.deleteTableMinTime = deleteTableMinTime;
	}

	public int getDeleteTableMaxTime() {
		return deleteTableMaxTime;
	}

	public void setDeleteTableMaxTime(int deleteTableMaxTime) {
		this.deleteTableMaxTime = deleteTableMaxTime;
	}

	public int getGetHTableDescriptorsMinTime() {
		return getHTableDescriptorsMinTime;
	}

	public void setGetHTableDescriptorsMinTime(int getHTableDescriptorsMinTime) {
		this.getHTableDescriptorsMinTime = getHTableDescriptorsMinTime;
	}

	public int getGetHTableDescriptorsMaxTime() {
		return getHTableDescriptorsMaxTime;
	}

	public void setGetHTableDescriptorsMaxTime(int getHTableDescriptorsMaxTime) {
		this.getHTableDescriptorsMaxTime = getHTableDescriptorsMaxTime;
	}

	public int getDisableTableMinTime() {
		return disableTableMinTime;
	}

	public void setDisableTableMinTime(int disableTableMinTime) {
		this.disableTableMinTime = disableTableMinTime;
	}

	public int getDisableTableMaxTime() {
		return disableTableMaxTime;
	}

	public void setDisableTableMaxTime(int disableTableMaxTime) {
		this.disableTableMaxTime = disableTableMaxTime;
	}

	public int getCreateTableMinTime() {
		return createTableMinTime;
	}

	public void setCreateTableMinTime(int createTableMinTime) {
		this.createTableMinTime = createTableMinTime;
	}

	public int getCreateTableMaxTime() {
		return createTableMaxTime;
	}

	public void setCreateTableMaxTime(int createTableMaxTime) {
		this.createTableMaxTime = createTableMaxTime;
	}

	public int getRpcProcessingTimeMinTime() {
		return rpcProcessingTimeMinTime;
	}

	public void setRpcProcessingTimeMinTime(int rpcProcessingTimeMinTime) {
		this.rpcProcessingTimeMinTime = rpcProcessingTimeMinTime;
	}

	public int getRpcProcessingTimeMaxTime() {
		return rpcProcessingTimeMaxTime;
	}

	public void setRpcProcessingTimeMaxTime(int rpcProcessingTimeMaxTime) {
		this.rpcProcessingTimeMaxTime = rpcProcessingTimeMaxTime;
	}

	public int getRegionServerStartupMinTime() {
		return regionServerStartupMinTime;
	}

	public void setRegionServerStartupMinTime(int regionServerStartupMinTime) {
		this.regionServerStartupMinTime = regionServerStartupMinTime;
	}

	public int getRegionServerStartupMaxTime() {
		return regionServerStartupMaxTime;
	}

	public void setRegionServerStartupMaxTime(int regionServerStartupMaxTime) {
		this.regionServerStartupMaxTime = regionServerStartupMaxTime;
	}

	public int getRpcQueueTimeMinTime() {
		return rpcQueueTimeMinTime;
	}

	public void setRpcQueueTimeMinTime(int rpcQueueTimeMinTime) {
		this.rpcQueueTimeMinTime = rpcQueueTimeMinTime;
	}

	public int getRpcQueueTimeMaxTime() {
		return rpcQueueTimeMaxTime;
	}

	public void setRpcQueueTimeMaxTime(int rpcQueueTimeMaxTime) {
		this.rpcQueueTimeMaxTime = rpcQueueTimeMaxTime;
	}

	public int getRegionServerReportMinTime() {
		return regionServerReportMinTime;
	}

	public void setRegionServerReportMinTime(int regionServerReportMinTime) {
		this.regionServerReportMinTime = regionServerReportMinTime;
	}

	public int getRegionServerReportMaxTime() {
		return regionServerReportMaxTime;
	}

	public void setRegionServerReportMaxTime(int regionServerReportMaxTime) {
		this.regionServerReportMaxTime = regionServerReportMaxTime;
	}

}
