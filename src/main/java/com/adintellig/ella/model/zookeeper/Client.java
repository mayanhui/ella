package com.adintellig.ella.model.zookeeper;

import java.sql.Timestamp;

public class Client {
	private String host;
	private int tag;
	private int queued;
	private long recved;
	private long sent;
	private Timestamp updateTime;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public int getQueued() {
		return queued;
	}

	public void setQueued(int queued) {
		this.queued = queued;
	}

	public long getRecved() {
		return recved;
	}

	public void setRecved(long recved) {
		this.recved = recved;
	}

	public long getSent() {
		return sent;
	}

	public void setSent(long sent) {
		this.sent = sent;
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public String toString() {
		return "Client [host=" + host + ", tag=" + tag + ", queued=" + queued
				+ ", recved=" + recved + ", sent=" + sent + ", updateTime="
				+ updateTime + "]";
	}

}
