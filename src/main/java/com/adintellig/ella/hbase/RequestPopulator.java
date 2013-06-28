package com.adintellig.ella.hbase;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.adintellig.ella.derby.model.RegionRequestCount;
import com.adintellig.ella.derby.model.RegionServerRequestCount;
import com.adintellig.ella.derby.model.TableRequestCount;
import com.adintellig.ella.hbase.beans.MasterServiceBean;
import com.adintellig.ella.hbase.beans.MasterServiceBeans;
import com.adintellig.ella.hbase.beans.RegionServer;
import com.adintellig.ella.hbase.beans.RegionsLoad;
import com.adintellig.ella.hbase.beans.RegionsLoadValue;

public class RequestPopulator {

	public static List<RegionRequestCount> populateRegionRequestCount(
			MasterServiceBeans bean) {
		String regionName = null;
		Long readCount = 0L;
		Long writeCount = 0L;
		Long totalCount = 0L;
		Timestamp insertTime = new Timestamp(System.currentTimeMillis());
		Timestamp updateTime = new Timestamp(System.currentTimeMillis());

		List<RegionRequestCount> requests = new ArrayList<RegionRequestCount>();

		MasterServiceBean[] beans = bean.getBeans();

		if (null != beans && beans.length > 0) {
			RegionServer[] rs = beans[0].getRegionServers();
			for (RegionServer r : rs) {
				RegionsLoad[] rl = r.getValue().getRegionsLoad();
				for (RegionsLoad l : rl) {
					RegionsLoadValue regionsLoadValue = l.getValue();
					regionName = regionsLoadValue.getNameAsString();
					readCount = regionsLoadValue.getReadRequestsCount();
					writeCount = regionsLoadValue.getWriteRequestsCount();
					totalCount = regionsLoadValue.getRequestsCount();

					RegionRequestCount request = new RegionRequestCount();
					request.setRegionName(regionName);
					request.setReadCount(readCount);
					request.setWriteCount(writeCount);
					request.setTotalCount(totalCount);
					request.setInsertTime(insertTime);
					request.setUpdateTime(updateTime);
					requests.add(request);
				}
			}
		}
		return requests;
	}

	public static List<TableRequestCount> populateTableRequestCount(
			MasterServiceBeans bean) {
		String regionName = null;
		String tableName = null;
		Long readCount = null;
		Long writeCount = null;
		Long totalCount = null;
		Timestamp insertTime = new Timestamp(System.currentTimeMillis());
		Timestamp updateTime = new Timestamp(System.currentTimeMillis());

		List<TableRequestCount> requests = new ArrayList<TableRequestCount>();
		Map<String, TableRequestCount> tableNameMap = new HashMap<String, TableRequestCount>();

		MasterServiceBean[] beans = bean.getBeans();

		if (null != beans && beans.length > 0) {
			RegionServer[] rs = beans[0].getRegionServers();
			for (RegionServer r : rs) {
				RegionsLoad[] rl = r.getValue().getRegionsLoad();
				for (RegionsLoad l : rl) {
					RegionsLoadValue regionsLoadValue = l.getValue();
					regionName = regionsLoadValue.getNameAsString();
					readCount = regionsLoadValue.getReadRequestsCount();
					writeCount = regionsLoadValue.getWriteRequestsCount();
					totalCount = regionsLoadValue.getRequestsCount();
					// table name
					tableName = regionName
							.substring(0, regionName.indexOf(","));
					TableRequestCount request = null;

					if (null != tableNameMap.get(tableName)) {
						request = tableNameMap.get(tableName);
						request.setReadCount(request.getReadCount() + readCount);
						request.setWriteCount(request.getWriteCount()
								+ writeCount);
						request.setTotalCount(request.getTotalCount()
								+ totalCount);
					} else {
						request = new TableRequestCount();
						request.setTableName(tableName);
						request.setReadCount(readCount);
						request.setWriteCount(writeCount);
						request.setTotalCount(totalCount);
						request.setInsertTime(insertTime);
						request.setUpdateTime(updateTime);
					}
					tableNameMap.put(tableName, request);
				}
			}
		}

		if (tableNameMap.size() > 0) {
			Set<Map.Entry<String, TableRequestCount>> set = tableNameMap
					.entrySet();
			for (Iterator<Map.Entry<String, TableRequestCount>> it = set
					.iterator(); it.hasNext();) {
				Map.Entry<String, TableRequestCount> entry = (Map.Entry<String, TableRequestCount>) it
						.next();
				requests.add(entry.getValue());
			}
		}
		return requests;
	}

	public static List<RegionServerRequestCount> populateRegionServerRequestCount(
			MasterServiceBeans bean) {
		String host = null;
		Long readCount = 0L;
		Long writeCount = 0L;
		Long totalCount = 0L;
		Timestamp insertTime = new Timestamp(System.currentTimeMillis());
		Timestamp updateTime = new Timestamp(System.currentTimeMillis());

		List<RegionServerRequestCount> requests = new ArrayList<RegionServerRequestCount>();

		MasterServiceBean[] beans = bean.getBeans();

		if (null != beans && beans.length > 0) {
			RegionServer[] rs = beans[0].getRegionServers();
			for (RegionServer r : rs) {
				host = r.getKey();
				RegionsLoad[] rl = r.getValue().getRegionsLoad();
				RegionServerRequestCount request = new RegionServerRequestCount();
				request.setServerHost(host);
				for (RegionsLoad l : rl) {
					RegionsLoadValue regionsLoadValue = l.getValue();
					readCount += regionsLoadValue.getReadRequestsCount();
					writeCount += regionsLoadValue.getWriteRequestsCount();
					totalCount += regionsLoadValue.getRequestsCount();
				}
				request.setReadCount(readCount);
				request.setWriteCount(writeCount);
				request.setTotalCount(totalCount);
				request.setInsertTime(insertTime);
				request.setUpdateTime(updateTime);
				requests.add(request);
			}
		}
		return requests;
	}

}
