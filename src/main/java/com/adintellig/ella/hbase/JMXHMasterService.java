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

import com.adintellig.ella.derby.DBManager;
import com.adintellig.ella.derby.model.RequestCount;
import com.adintellig.ella.derby.model.RequestDAO;
import com.adintellig.ella.hbase.beans.MasterServiceBeans;
import com.adintellig.ella.util.ConfigFactory;
import com.adintellig.ella.util.ConfigProperties;
import com.alibaba.fastjson.JSON;

public class JMXHMasterService extends Thread {
	private static Logger logger = LoggerFactory
			.getLogger(JMXHMasterService.class);

	static ConfigProperties config = ConfigFactory.getInstance()
			.getConfigProperties(ConfigFactory.ELLA_CONFIG_PATH);
	// public static String url =
	// "http://hadoop-node-20:60010/jmx?qry=hadoop:service=Master,name=Master";
	public static String url;

	private DBManager dbm = null;
	private RequestDAO rdao = null;

	// private int maxItemNumber = 0;

	public JMXHMasterService() {
		this.dbm = new DBManager();
		this.rdao = dbm.getRequestDAO();
		// this.maxItemNumber = rdao.getMaxItemNumber();
		url = config
				.getProperty(ConfigProperties.CONFIG_NAME_ELLA_HBASE_MASTER_JMX_QRY_URL);
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
			// region
			List<RequestCount> list = RequestPopulator
					.populateRegionRequestCount(bean);
			rdao.batchInsert(list);
			logger.info("Load Region info into Derby. Size=" + list.size());
			// server
			list = RequestPopulator.populateRegionServerRequestCount(bean);
			rdao.batchInsert(list);
			logger.info("Load RegionServer info into Derby. Size="
					+ list.size());
			// table
			list = RequestPopulator.populateTableRequestCount(bean);
			rdao.batchInsert(list);
			logger.info("Load Table info into Derby. Size=" + list.size());
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws JsonParseException,
			JsonMappingException, IOException, SQLException {
		JMXHMasterService service = new JMXHMasterService();
		Thread t = new Thread(service);
		t.start();
	}
}
