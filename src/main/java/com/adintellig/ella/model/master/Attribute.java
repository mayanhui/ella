package com.adintellig.ella.model.master;

public class Attribute {
	private String hbaseVersion;
	private String hbaseCompiled;
	private String hadoopVersion;
	private String hadoopCompiled;
	private String hbaseRootDir;
	private String hbaseClusterID;
	private String loadAvg;
	private String zkQuorum;
	private String coprocessors;
	private String hmasterStartTime;
	private String hmasterActiveTime;

	public String getHbaseVersion() {
		return hbaseVersion;
	}

	public void setHbaseVersion(String hbaseVersion) {
		this.hbaseVersion = hbaseVersion;
	}

	public String getHbaseCompiled() {
		return hbaseCompiled;
	}

	public void setHbaseCompiled(String hbaseCompiled) {
		this.hbaseCompiled = hbaseCompiled;
	}

	public String getHadoopVersion() {
		return hadoopVersion;
	}

	public void setHadoopVersion(String hadoopVersion) {
		this.hadoopVersion = hadoopVersion;
	}

	public String getHadoopCompiled() {
		return hadoopCompiled;
	}

	public void setHadoopCompiled(String hadoopCompiled) {
		this.hadoopCompiled = hadoopCompiled;
	}

	public String getHbaseRootDir() {
		return hbaseRootDir;
	}

	public void setHbaseRootDir(String hbaseRootDir) {
		this.hbaseRootDir = hbaseRootDir;
	}

	public String getHbaseClusterID() {
		return hbaseClusterID;
	}

	public void setHbaseClusterID(String hbaseClusterID) {
		this.hbaseClusterID = hbaseClusterID;
	}

	public String getLoadAvg() {
		return loadAvg;
	}

	public void setLoadAvg(String loadAvg) {
		this.loadAvg = loadAvg;
	}

	public String getZkQuorum() {
		return zkQuorum;
	}

	public void setZkQuorum(String zkQuorum) {
		this.zkQuorum = zkQuorum;
	}

	public String getCoprocessors() {
		return coprocessors;
	}

	public void setCoprocessors(String coprocessors) {
		this.coprocessors = coprocessors;
	}

	public String getHmasterStartTime() {
		return hmasterStartTime;
	}

	public void setHmasterStartTime(String hmasterStartTime) {
		this.hmasterStartTime = hmasterStartTime;
	}

	public String getHmasterActiveTime() {
		return hmasterActiveTime;
	}

	public void setHmasterActiveTime(String hmasterActiveTime) {
		this.hmasterActiveTime = hmasterActiveTime;
	}

}
