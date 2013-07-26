package com.adintellig.ella.hbase.beans.request;

public class RegionsLoadValue {
	private String nameAsString;
	private Long readRequestsCount;
	private Long writeRequestsCount;
	private Long requestsCount;

	public String getNameAsString() {
		return nameAsString;
	}

	public void setNameAsString(String nameAsString) {
		this.nameAsString = nameAsString;
	}

	public Long getReadRequestsCount() {
		return readRequestsCount;
	}

	public void setReadRequestsCount(Long readRequestsCount) {
		this.readRequestsCount = readRequestsCount;
	}

	public Long getWriteRequestsCount() {
		return writeRequestsCount;
	}

	public void setWriteRequestsCount(Long writeRequestsCount) {
		this.writeRequestsCount = writeRequestsCount;
	}

	public Long getRequestsCount() {
		return requestsCount;
	}

	public void setRequestsCount(Long requestsCount) {
		this.requestsCount = requestsCount;
	}

}
