package com.adintellig.ella.hbase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adintellig.ella.hbase.beans.stat.RPCStat;
import com.adintellig.ella.hbase.beans.stat.RPCStats;
import com.adintellig.ella.util.ConfigFactory;
import com.adintellig.ella.util.ConfigProperties;
import com.alibaba.fastjson.JSON;

public class JMXRPCStatService {
	private static Logger logger = LoggerFactory
			.getLogger(JMXRPCStatService.class);

	static ConfigProperties config = ConfigFactory.getInstance()
			.getConfigProperties(ConfigFactory.ELLA_CONFIG_PATH);

	public static String url;

	private static JMXRPCStatService service;

	private JMXRPCStatService() {
		url = config.getProperty("ella.hbase.master.baseurl")
				+ config.getProperty("ella.hbase.master.jmx.stat.rpc.suburl");
	}

	public static synchronized JMXRPCStatService getInstance() {
		if (service == null)
			service = new JMXRPCStatService();
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

	private RPCStats parseBean(String jsonString) {
		RPCStats bean = null;
		if (null != jsonString && jsonString.trim().length() > 0)
			bean = JSON.parseObject(jsonString, RPCStats.class);
		return bean;
	}

	public RPCStats genBeans() {
		RPCStats beans = null;
		if (null != url) {
			String urlString = request(url);
			beans = parseBean(urlString);
		}
		return beans;
	}

	public static void main(String[] args) {
		JMXRPCStatService attr = JMXRPCStatService.getInstance();
		RPCStats beans = attr.genBeans();
		RPCStat[] beanArr = beans.getBeans();
		System.out.println(beanArr[0].getCreateTableMaxTime());
	}
}
