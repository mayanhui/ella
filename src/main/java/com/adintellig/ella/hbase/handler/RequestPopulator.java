package com.adintellig.ella.hbase.handler;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.adintellig.ella.hbase.beans.request.MasterServiceBean;
import com.adintellig.ella.hbase.beans.request.MasterServiceBeans;
import com.adintellig.ella.hbase.beans.request.RegionServer;
import com.adintellig.ella.hbase.beans.request.RegionsLoad;
import com.adintellig.ella.hbase.beans.request.RegionsLoadValue;
import com.adintellig.ella.model.Region;
import com.adintellig.ella.model.RegionRequestCount;
import com.adintellig.ella.model.Server;
import com.adintellig.ella.model.ServerRequestCount;
import com.adintellig.ella.model.RequestCount;
import com.adintellig.ella.model.TableRequestCount;
import com.adintellig.ella.model.Table;

public class RequestPopulator {

	public static List<RequestCount> populateRegionRequestCount(
			MasterServiceBeans bean) {
		String regionName = null;
		Long readCount = 0L;
		Long writeCount = 0L;
		Long totalCount = 0L;
		Timestamp insertTime = new Timestamp(System.currentTimeMillis());
		Timestamp updateTime = new Timestamp(System.currentTimeMillis());

		List<RequestCount> requests = new ArrayList<RequestCount>();

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

	public static List<RequestCount> populateTableRequestCount(
			MasterServiceBeans bean) {
		String regionName = null;
		String tableName = null;
		Long readCount = null;
		Long writeCount = null;
		Long totalCount = null;
		Timestamp insertTime = new Timestamp(System.currentTimeMillis());
		Timestamp updateTime = new Timestamp(System.currentTimeMillis());

		List<RequestCount> requests = new ArrayList<RequestCount>();
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

	public static List<RequestCount> populateRegionServerRequestCount(
			MasterServiceBeans bean) {
		String host = null;
		Long readCount = 0L;
		Long writeCount = 0L;
		Long totalCount = 0L;
		Timestamp insertTime = new Timestamp(System.currentTimeMillis());
		Timestamp updateTime = new Timestamp(System.currentTimeMillis());

		List<RequestCount> requests = new ArrayList<RequestCount>();

		MasterServiceBean[] beans = bean.getBeans();

		if (null != beans && beans.length > 0) {
			RegionServer[] rs = beans[0].getRegionServers();
			for (RegionServer r : rs) {
				host = r.getKey();
				RegionsLoad[] rl = r.getValue().getRegionsLoad();
				ServerRequestCount request = new ServerRequestCount();
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

	public static List<Table> populateTables(List<RequestCount> beans) {
		String tableName = null;
		Timestamp updateTime = new Timestamp(System.currentTimeMillis());

		List<Table> tables = new ArrayList<Table>();

		for (RequestCount req : beans) {
			if (req instanceof TableRequestCount) {
				tableName = ((TableRequestCount) req).getTableName();
				updateTime = req.getUpdateTime();
				Table t = new Table();
				t.setTableName(tableName);
				t.setUpdateTime(updateTime);
				tables.add(t);
			}
		}
		return tables;
	}

	public static List<Region> populateRegions(List<RequestCount> beans) {
		String regionName = null;
		Timestamp updateTime = new Timestamp(System.currentTimeMillis());

		List<Region> regions = new ArrayList<Region>();

		for (RequestCount req : beans) {
			if (req instanceof RegionRequestCount) {
				regionName = ((RegionRequestCount) req).getRegionName();
				updateTime = req.getUpdateTime();
				Region t = new Region();
				t.setRegionName(regionName);
				t.setUpdateTime(updateTime);
				regions.add(t);
			}
		}
		return regions;
	}

	public static List<Server> populateServers(List<RequestCount> beans) {
		String host = null;
		Timestamp updateTime = new Timestamp(System.currentTimeMillis());

		List<Server> servers = new ArrayList<Server>();

		for (RequestCount req : beans) {
			if (req instanceof ServerRequestCount) {
				host = ((ServerRequestCount) req).getServerHost();
				updateTime = req.getUpdateTime();
				Server s = new Server();
				s.setHost(host);
				s.setUpdateTime(updateTime);
				servers.add(s);
			}
		}
		return servers;
	}

}
