package com.adintellig.ella.hbase.handler;

import java.io.IOException;
import java.sql.SQLException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adintellig.ella.model.jmxbeans.LiveRegionServerBeans;
import com.adintellig.ella.util.ConfigFactory;
import com.adintellig.ella.util.ConfigProperties;
import com.alibaba.fastjson.JSON;

public class JMXHMasterHandler extends ServerHandler{
	private static Logger logger = LoggerFactory
			.getLogger(JMXHMasterHandler.class);

	static ConfigProperties config = ConfigFactory.getInstance()
			.getConfigProperties(ConfigFactory.ELLA_CONFIG_PATH);

	public static String url;

	private static JMXHMasterHandler service;

	private JMXHMasterHandler() {
		url = config.getProperty("ella.hbase.master.baseurl")
				+ config.getProperty("ella.hbase.master.jmx.req.suburl");
	}

	public static synchronized JMXHMasterHandler getInstance() {
		if (service == null)
			service = new JMXHMasterHandler();
		return service;
	}

	/**
	 * 将JSON对象转换为Java Bean。
	 * @param jsonString
	 * @return
	 */
	public LiveRegionServerBeans parseBean(String jsonString) {
		LiveRegionServerBeans bean = null;
		if (null != jsonString && jsonString.trim().length() > 0){
			//替换字段名称，防止名称中有句点出现
			jsonString = jsonString.replaceAll("tag.", "");
			bean = JSON.parseObject(jsonString, LiveRegionServerBeans.class);
		}
		return bean;
	}
	
	@Override
	public Object handle(){
		String result = request(url);
		logger.info("Request URL: " + url);
		return parseBean(result);
	}

	

	public static void main(String[] args) throws JsonParseException,
			JsonMappingException, IOException, SQLException {
		JMXHMasterHandler handler = JMXHMasterHandler.getInstance();
		handler.handle();
	}
}
