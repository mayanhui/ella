package com.adintellig.ella.hbase.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
//import java.sql.SQLException;
//import java.util.List;

//import org.codehaus.jackson.JsonParseException;
//import org.codehaus.jackson.map.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adintellig.ella.hbase.beans.attr.HBaseAttributeBean;
import com.adintellig.ella.hbase.beans.attr.HBaseAttributeBeans;
//import com.adintellig.ella.hbase.beans.request.MasterServiceBeans;
//import com.adintellig.ella.model.Region;
//import com.adintellig.ella.model.RequestCount;
//import com.adintellig.ella.model.Server;
//import com.adintellig.ella.model.Table;
//import com.adintellig.ella.mysql.RegionDaoImpl;
//import com.adintellig.ella.mysql.RequestCountDaoImpl;
//import com.adintellig.ella.mysql.ServerDaoImpl;
//import com.adintellig.ella.mysql.TableDaoImpl;
import com.adintellig.ella.util.ConfigFactory;
import com.adintellig.ella.util.ConfigProperties;
import com.alibaba.fastjson.JSON;

public class JMXClusterAttrHandler extends ServerHandler{
	private static Logger logger = LoggerFactory
			.getLogger(JMXClusterAttrHandler.class);

	static ConfigProperties config = ConfigFactory.getInstance()
			.getConfigProperties(ConfigFactory.ELLA_CONFIG_PATH);

	public static String url;

	private static JMXClusterAttrHandler service;

	private JMXClusterAttrHandler() {
		url = config.getProperty("ella.hbase.master.baseurl")
				+ config.getProperty("ella.hbase.master.jmx.attr.suburl");
	}

	public static synchronized JMXClusterAttrHandler getInstance() {
		if (service == null)
			service = new JMXClusterAttrHandler();
		return service;
	}

	private HBaseAttributeBeans parseBean(String jsonString) {
		HBaseAttributeBeans bean = null;
		if (null != jsonString && jsonString.trim().length() > 0)
			bean = JSON.parseObject(jsonString, HBaseAttributeBeans.class);
		return bean;
	}

	public HBaseAttributeBeans genBeans() {
		HBaseAttributeBeans beans = null;
		if (null != url) {
			String urlString = request(url);
			beans = parseBean(urlString);
		}
		return beans;
	}

	public static void main(String[] args) {
		JMXClusterAttrHandler attr = JMXClusterAttrHandler.getInstance();
		HBaseAttributeBeans beans = attr.genBeans();
		HBaseAttributeBean[] beanArr = beans.getBeans();
		System.out.println(beanArr[0].getHdfsUrl());
	}

	@Override
	public Object handle() {
		// TODO Auto-generated method stub
		return null;
	}
}
