package com.adintellig.ella.hbase.handler;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.adintellig.ella.hbase.beans.RegionBean;
import com.adintellig.ella.hbase.beans.RegionBeans;
import com.adintellig.ella.model.Region;
import com.adintellig.ella.model.RegionRequestCount;
import com.adintellig.ella.model.RequestCount;
import com.adintellig.ella.model.Server;
import com.adintellig.ella.model.ServerRequestCount;
import com.adintellig.ella.model.Table;
import com.adintellig.ella.model.TableRequestCount;

public class RequestPopulator {

	/**
	 * 将RegionBeans转为RegionRequestCount
	 * @param bean
	 * @return  List<RegionRequestCount>
	 */
	public static List<RequestCount> populateRegionRequestCount(
			RegionBeans bean) {
		Timestamp insertTime = new Timestamp(System.currentTimeMillis());
		Timestamp updateTime = new Timestamp(System.currentTimeMillis());

		List<RequestCount> requests = new ArrayList<RequestCount>();

		RegionBean[] beans = bean.getBeans();

		if (null != beans && beans.length > 0) {
			for(RegionBean rb : beans){
				RegionRequestCount request = new RegionRequestCount();
				request.setRegionName(rb.getRegionName());
				request.setReadCount(rb.getReadCount());
				request.setWriteCount(rb.getWriteCount());
				request.setTotalCount(rb.getReadCount() + rb.getWriteCount());
				request.setInsertTime(insertTime);
				request.setUpdateTime(updateTime);
				requests.add(request);
			}
		}
		return requests;
	}

	/**
	 * 转换为TableRequestCount
	 * @param bean
	 * @return
	 */
	public static List<RequestCount> populateTableRequestCount(
			RegionBeans bean) {
		String tableName = null;
		Long readCount = null;
		Long writeCount = null;
		Long totalCount = null;
		Timestamp insertTime = new Timestamp(System.currentTimeMillis());
		Timestamp updateTime = new Timestamp(System.currentTimeMillis());

		List<RequestCount> requests = new ArrayList<RequestCount>();
		Map<String, TableRequestCount> tableNameMap = new HashMap<String, TableRequestCount>();

		RegionBean[] beans = bean.getBeans();

		if (null != beans && beans.length > 0) {
			for(RegionBean rb : beans){
				readCount = rb.getReadCount();
				writeCount = rb.getWriteCount();
				totalCount = rb.getReadCount() + rb.getWriteCount();
				tableName = rb.getTableName();
				
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
			RegionBeans bean) {
		String host = null;
		Long readCount = 0L;
		Long writeCount = 0L;
		Long totalCount = 0L;
		Timestamp insertTime = new Timestamp(System.currentTimeMillis());
		Timestamp updateTime = new Timestamp(System.currentTimeMillis());

		List<RequestCount> requests = new ArrayList<RequestCount>();

		RegionBean[] beans = bean.getBeans();

		if (null != beans && beans.length > 0) {
			ServerRequestCount request = new ServerRequestCount();
			for(RegionBean rb : beans){
					host = rb.getRsName();
					readCount += rb.getReadCount();
					writeCount += rb.getWriteCount();
					totalCount += rb.getReadCount() + rb.getWriteCount();
			}
			request.setServerHost(host);
			request.setReadCount(readCount);
			request.setWriteCount(writeCount);
			request.setTotalCount(totalCount);
			request.setInsertTime(insertTime);
			request.setUpdateTime(updateTime);
			requests.add(request);
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
