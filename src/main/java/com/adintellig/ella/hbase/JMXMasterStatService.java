package com.adintellig.ella.hbase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adintellig.ella.hbase.beans.stat.MasterStat;
import com.adintellig.ella.hbase.beans.stat.MasterStats;
import com.adintellig.ella.util.ConfigFactory;
import com.adintellig.ella.util.ConfigProperties;
import com.alibaba.fastjson.JSON;

public class JMXMasterStatService {
	private static Logger logger = LoggerFactory
			.getLogger(JMXMasterStatService.class);

	static ConfigProperties config = ConfigFactory.getInstance()
			.getConfigProperties(ConfigFactory.ELLA_CONFIG_PATH);

	public static String url;

	private static JMXMasterStatService service;

	private JMXMasterStatService() {
		url = config.getProperty("ella.hbase.master.baseurl")
				+ config.getProperty("ella.hbase.master.jmx.stat.master.suburl");
	}

	public static synchronized JMXMasterStatService getInstance() {
		if (service == null)
			service = new JMXMasterStatService();
		return service;
	}

	private String request(String urlString) {
		logger.info("[REQ URL] " + urlString);
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

	private MasterStats parseBean(String jsonString) {
		MasterStats bean = null;
		if (null != jsonString && jsonString.trim().length() > 0)
			bean = JSON.parseObject(jsonString, MasterStats.class);
		return bean;
	}

	public MasterStats genBeans() {
		MasterStats beans = null;
		if (null != url) {
			String urlString = request(url);
			beans = parseBean(urlString);
		}
		return beans;
	}

	public static void main(String[] args) {
		JMXMasterStatService stat = JMXMasterStatService.getInstance();
		MasterStats beans = stat.genBeans();
		MasterStat[] beanArr = beans.getBeans();
		System.out.println(beanArr[0].getModelerType());
	}
}
