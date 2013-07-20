package com.adintellig.ella.hbase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adintellig.ella.hbase.beans.MasterServiceBeans;
import com.adintellig.ella.model.Region;
import com.adintellig.ella.model.RequestCount;
import com.adintellig.ella.model.Table;
import com.adintellig.ella.mysql.RegionDaoImpl;
import com.adintellig.ella.mysql.RequestCountDaoImpl;
import com.adintellig.ella.mysql.TableDaoImpl;
import com.adintellig.ella.util.ConfigFactory;
import com.adintellig.ella.util.ConfigProperties;
import com.alibaba.fastjson.JSON;

public class JMXHMasterService extends Thread {
	private static Logger logger = LoggerFactory
			.getLogger(JMXHMasterService.class);

	static ConfigProperties config = ConfigFactory.getInstance()
			.getConfigProperties(ConfigFactory.ELLA_CONFIG_PATH);

	public static String url;

	private RequestCountDaoImpl reqDao = null;
	private TableDaoImpl tblDao = null;
	private RegionDaoImpl regDao = null;

	private static JMXHMasterService service;

	private JMXHMasterService() {
		this.reqDao = new RequestCountDaoImpl();
		this.tblDao = new TableDaoImpl();
		this.regDao = new RegionDaoImpl();
		url = config
				.getProperty(ConfigProperties.CONFIG_NAME_ELLA_HBASE_MASTER_JMX_QRY_URL);
	}

	public static synchronized JMXHMasterService getInstance() {
		if (service == null)
			service = new JMXHMasterService();
		return service;
	}

	public String request(String urlString) {
		URL url = null;
		BufferedReader in = null;
		StringBuffer sb = new StringBuffer();
		try {
			url = new URL(urlString);
			in = new BufferedReader(new InputStreamReader(url.openStream(),
					"UTF-8"));
			String str = null;
			while ((str = in.readLine()) != null) {
				sb.append(str);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return sb.toString();
	}

	public MasterServiceBeans parseBean(String jsonString) {
		MasterServiceBeans bean = null;
		if (null != jsonString && jsonString.trim().length() > 0)
			bean = JSON.parseObject(jsonString, MasterServiceBeans.class);
		return bean;
	}

	@Override
	public void run() {
		String result = request(url);
		logger.info("Request URL: " + url);
		MasterServiceBeans bean = parseBean(result);
		try {
			// region count
			List<RequestCount> list = RequestPopulator
					.populateRegionRequestCount(bean);
			reqDao.batchAdd(list);
			logger.info("[INSERT] Load Region Count info into 'region_requests'. Size="
					+ list.size());
			
			// region check
			List<Region> regions = RequestPopulator.populateRegions(list);
			if(regDao.needUpdate(regions)){
				regDao.truncate();
				regDao.batchUpdate(regions);
			}
			
			// server count
			list = RequestPopulator.populateRegionServerRequestCount(bean);
			reqDao.batchAdd(list);
			logger.info("[INSERT] Load Server Count info into 'server_requests'. Size="
					+ list.size());
			
			// table count
			list = RequestPopulator.populateTableRequestCount(bean);
			reqDao.batchAdd(list);
			logger.info("[INSERT] Load Table Count info into 'table_requests'. Size=" + list.size());

			// table check
			List<Table> tables = RequestPopulator.populateTables(list);
			if(tblDao.needUpdate(tables)){
				tblDao.truncate();
				tblDao.batchUpdate(tables);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws JsonParseException,
			JsonMappingException, IOException, SQLException {
		JMXHMasterService service = JMXHMasterService.getInstance();
		Thread t = new Thread(service);
		t.start();
	}
}
