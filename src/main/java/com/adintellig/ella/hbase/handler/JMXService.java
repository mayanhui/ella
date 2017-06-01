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
import com.adintellig.ella.hbase.beans.LiveRegionServerBeans;
import com.adintellig.ella.hbase.beans.RegionBeans;
import com.adintellig.ella.model.Region;
import com.adintellig.ella.model.RequestCount;
import com.adintellig.ella.model.Server;
import com.adintellig.ella.model.Table;

/**
 * JMXService 直接被Quartz调用的入口类
 * @author didi
 *
 */
public class JMXService extends Thread {
	private static Logger logger = LoggerFactory
			.getLogger(JMXService.class);

	private RequestCountDaoImpl reqDao = null;
	private TableDaoImpl tblDao = null;
	private RegionDaoImpl regDao = null;
	private ServerDaoImpl serDao = null;
	
	private JMXService() {
		this.reqDao = new RequestCountDaoImpl();
		this.tblDao = new TableDaoImpl();
		this.regDao = new RegionDaoImpl();
		this.serDao = new ServerDaoImpl();
	}
	/**
	 * 解析live regionserver hostname
	 * 输入格式：slave,16020,1496208060829;master,16020,1496208216136
	 * @param rsList
	 * @return
	 */
	public List<Object> parseLiveRSHostname(String rsList){
		List<Object> hosts = new ArrayList<Object>();
		if (null != rsList && rsList.length() > 0) {
			String[] arr = rsList.split(";");
			for(String hostString : arr){
				String[] partStringArr = hostString.split(",");
				if(partStringArr.length == 3){
					hosts.add(partStringArr[0]);
				}
			}
		}
		return hosts;
	}
	
	
	@Override
	public void run() {
		//获取Master的JMX信息
		JMXHMasterHandler handler = JMXHMasterHandler.getInstance();
		LiveRegionServerBeans beans = (LiveRegionServerBeans) handler.handle();
		for(Object obj : parseLiveRSHostname(beans.getBeans()[0].getLiveRegionServers())){
			//获取RS的JMX信息
			RegionBeans rbs = (RegionBeans)JMXRegionServerHandler.getInstance((String)obj).handle();
			
			//将获取RS信息转换为基础数据模型（mysql表模型），并写入数据库
			
			//
		}
		
		
		/*
		try {
			// region count
			List<RequestCount> list = RequestPopulator
					.populateRegionRequestCount(bean);
			reqDao.batchAdd(list);
			logger.info("[INSERT] Load Region Count info into 'region_requests'. Size="
					+ list.size());

			// region check
			List<Region> regions = RequestPopulator.populateRegions(list);
			if (regDao.needUpdate(regions)) {
				regDao.truncate();
				regDao.batchUpdate(regions);
			}
			

			// server count
			list = RequestPopulator.populateRegionServerRequestCount(bean);
			reqDao.batchAdd(list);
			logger.info("[INSERT] Load Server Count info into 'server_requests'. Size="
					+ list.size());

			// server check
			List<Server> servers = RequestPopulator.populateServers(list);
			if (serDao.needUpdate(servers)) {
				serDao.truncate();
				serDao.batchUpdate(servers);
			}

			// table count
			list = RequestPopulator.populateTableRequestCount(bean);
			reqDao.batchAdd(list);
			logger.info("[INSERT] Load Table Count info into 'table_requests'. Size="
					+ list.size());

			// table check
			List<Table> tables = RequestPopulator.populateTables(list);
			if (tblDao.needUpdate(tables)) {
				tblDao.truncate();
				tblDao.batchUpdate(tables);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}*/

	}

//	public static void main(String[] args) throws JsonParseException,
//			JsonMappingException, IOException, SQLException {
//		JMXService service = JMXService.getInstance();
//		Thread t = new Thread(service);
//		t.start();
//	}
}
