package com.adintellig.ella.hbase.handler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adintellig.ella.dao.RegionDaoImpl;
import com.adintellig.ella.dao.RequestCountDaoImpl;
import com.adintellig.ella.dao.ServerDaoImpl;
import com.adintellig.ella.dao.TableDaoImpl;
import com.adintellig.ella.model.Region;
import com.adintellig.ella.model.RequestCount;
import com.adintellig.ella.model.Server;
import com.adintellig.ella.model.Table;
import com.adintellig.ella.model.jmxbeans.LiveRegionServerBeans;
import com.adintellig.ella.model.jmxbeans.RegionBeans;

/**
 * JMXService 直接被Quartz调用的入口类
 * 
 * @author didi
 *
 */
public class JMXHandler extends Thread {
	private static Logger logger = LoggerFactory.getLogger(JMXHandler.class);

	private static JMXHandler service;

	private RequestCountDaoImpl reqDao = null;
	private TableDaoImpl tblDao = null;
	private RegionDaoImpl regDao = null;
	private ServerDaoImpl serDao = null;

	private JMXHandler() {
		this.reqDao = new RequestCountDaoImpl();
		this.tblDao = new TableDaoImpl();
		this.regDao = new RegionDaoImpl();
		this.serDao = new ServerDaoImpl();
	}

	public static synchronized JMXHandler getInstance() {
		if (service == null)
			service = new JMXHandler();
		return service;
	}

	/**
	 * 解析live regionserver hostname
	 * 输入格式：slave,16020,1496208060829;master,16020,1496208216136
	 * 
	 * @param rsList
	 * @return
	 */
	public List<Object> parseLiveRSHostname(String rsList) {
		List<Object> hosts = new ArrayList<Object>();
		if (null != rsList && rsList.length() > 0) {
			String[] arr = rsList.split(";");
			for (String hostString : arr) {
				String[] partStringArr = hostString.split(",");
				if (partStringArr.length == 3) {
					hosts.add(partStringArr[0]);
				}
			}
		}
		return hosts;
	}

	@Override
	public void run() {

		List<Region> rgs = new ArrayList<Region>();
		List<Server> ses = new ArrayList<Server>();
		List<Table> tbs = new ArrayList<Table>();

		// 获取Master的JMX信息
		JMXHMasterHandler handler = JMXHMasterHandler.getInstance();
		LiveRegionServerBeans beans = (LiveRegionServerBeans) handler.handle();
		for (Object obj : parseLiveRSHostname(beans.getBeans()[0].getLiveRegionServers())) {
			logger.info("测试hostname：" + obj);
			/* 获取RS的JMX信息 */
			RegionBeans rbs = (RegionBeans) new JMXRegionServerHandler((String) obj).handle();

			/* 将获取RS信息转换为基础数据模型（mysql表模型），并写入数据库 */
			try {
				// region count
				List<RequestCount> list = RequestPopulator.populateRegionRequestCount(rbs);
				reqDao.batchAdd(list);
				logger.info("[INSERT] Load Region Count info into 'region_requests'. Size=" + list.size());

				// add region check
				List<Region> regions = RequestPopulator.populateRegions(list);
				rgs.addAll(regions);

				// server count
				list = RequestPopulator.populateRegionServerRequestCount(rbs);
				reqDao.batchAdd(list);
				logger.info("[INSERT] Load Server Count info into 'server_requests'. Size=" + list.size());

				// add server check
				List<Server> servers = RequestPopulator.populateServers(list);
				ses.addAll(servers);

				// table count
				list = RequestPopulator.populateTableRequestCount(rbs);
				reqDao.batchAdd(list);
				logger.info("[INSERT] Load Table Count info into 'table_requests'. Size=" + list.size());

				// add table check
				List<Table> tables = RequestPopulator.populateTables(list);
				tbs.addAll(tables);

			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/* region/table/server check */
		try {
			if (regDao.needUpdate(rgs)) {
				regDao.truncate();
				regDao.batchUpdate(rgs);
			}

			if (serDao.needUpdate(ses)) {
				serDao.truncate();
				serDao.batchUpdate(ses);
			}

			if (tblDao.needUpdate(tbs)) {
				tblDao.truncate();
				tblDao.batchUpdate(tbs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException, SQLException {
		JMXHandler service = JMXHandler.getInstance();
		Thread t = new Thread(service);
		t.start();
	}
}
