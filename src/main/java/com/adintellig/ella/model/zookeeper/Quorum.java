package com.adintellig.ella.model.zookeeper;

import java.sql.Timestamp;
import java.util.List;

public class Quorum {
	private String host;
	private String version;
	private int latencyMin;
	private int latencyMax;
	private int latencyAvg;
	private long received;
	private long sent;
	private int outstanding;
	private String zxid;
	private String mode;
	private int nodeCount;
	private Timestamp updateTime;

	private List<Client> clients;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getLatencyMin() {
		return latencyMin;
	}

	public void setLatencyMin(int latencyMin) {
		this.latencyMin = latencyMin;
	}

	public int getLatencyMax() {
		return latencyMax;
	}

	public void setLatencyMax(int latencyMax) {
		this.latencyMax = latencyMax;
	}

	public int getLatencyAvg() {
		return latencyAvg;
	}

	public void setLatencyAvg(int latencyAvg) {
		this.latencyAvg = latencyAvg;
	}

	public long getReceived() {
		return received;
	}

	public void setReceived(long received) {
		this.received = received;
	}

	public long getSent() {
		return sent;
	}

	public void setSent(long sent) {
		this.sent = sent;
	}

	public int getOutstanding() {
		return outstanding;
	}

	public void setOutstanding(int outstanding) {
		this.outstanding = outstanding;
	}

	public String getZxid() {
		return zxid;
	}

	public void setZxid(String zxid) {
		this.zxid = zxid;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public int getNodeCount() {
		return nodeCount;
	}

	public void setNodeCount(int nodeCount) {
		this.nodeCount = nodeCount;
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}

	public List<Client> getClients() {
		return clients;
	}

	public void setClients(List<Client> clients) {
		this.clients = clients;
	}

	@Override
	public String toString() {
		return "Quorum [host=" + host + ", version=" + version
				+ ", latencyMin=" + latencyMin + ", latencyMax=" + latencyMax
				+ ", latencyAvg=" + latencyAvg + ", received=" + received
				+ ", sent=" + sent + ", outstanding=" + outstanding + ", zxid="
				+ zxid + ", mode=" + mode + ", nodeCount=" + nodeCount
				+ ", updateTime=" + updateTime + ", clients=" + clients + "]";
	}

}
