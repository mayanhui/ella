package com.adintellig.ella.hbase.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adintellig.ella.hbase.beans.RegionBean;
import com.adintellig.ella.hbase.beans.RegionBeans;
import com.adintellig.ella.util.ConfigFactory;
import com.adintellig.ella.util.ConfigProperties;

public class JMXRegionServerHandler extends ServerHandler{
	private static Logger logger = LoggerFactory.getLogger(JMXRegionServerHandler.class);

	static ConfigProperties config = ConfigFactory.getInstance().getConfigProperties(ConfigFactory.ELLA_CONFIG_PATH);

	private String url;
	private String hostname;


	public JMXRegionServerHandler(String hostname) {
		this.hostname = hostname;
		url = config.getProperty("ella.hbase.rs.jmx.baseurl.prefix") + this.hostname
				+ config.getProperty("ella.hbase.rs.jmx.baseurl.suffix")
				+ config.getProperty("ella.hbase.rs.jmx.req.suburl");
	}

	
	/**
	 * 将JSON对象转换为Java Bean。
	 * 
	 * @param jsonString
	 * @return
	 */
	public RegionBeans parseBean(String jsonString) {
		RegionBeans beans = new RegionBeans();
		if (null != jsonString && jsonString.trim().length() > 0){
			String[] jsonFields = jsonString.split(",\\s+\"");
			Arrays.sort(jsonFields);
			List<RegionBean> beanList = new ArrayList<RegionBean>();
			RegionBean bean = new RegionBean();
			bean.setRsName(this.hostname);
			String prefix = null;
			
			for(String jf : jsonFields){
				//Namespace_hbase_table_meta_region_1588230740_metric_storeCount
				//Namespace_default_table_t1_region_38854d8401bfb7630bc0ed555db9e62b_metric_storeFileCount
				String[] kvs = jf.split("\"\\s+:\\s+");
				if(kvs.length == 2){
					if(kvs[0].indexOf("_") >= 0){
						//判断是否是新Bean
						String tmpPrefix = jf.substring(0, jf.indexOf("_metric_"));
						if(null != prefix && (!tmpPrefix.equals(prefix))){
							beanList.add(bean);
							bean = new RegionBean();
							bean.setRsName(this.hostname);
						}
						
						String line = kvs[0];
						String namespace = line.substring("Namespace_".length(), line.indexOf("_table_"));
						String tableName = line.substring(line.indexOf("_table_")+"_table_".length(),line.indexOf("_region_"));
						String regionName = line.substring(line.indexOf("_region_") + "_region_".length(),line.indexOf("_metric_"));
						bean.setNamespace(namespace);
						bean.setTableName(tableName);
						bean.setRegionName(tableName + "," + regionName);
						
						String action = line.substring(line.indexOf("_metric_") + "_metric_".length());
						if(action.indexOf("readRequestCount") >= 0){
							bean.setReadCount(Long.parseLong(kvs[1]));
						}
						if(action.indexOf("writeRequestCount") >=0){
							bean.setWriteCount(Long.parseLong(kvs[1]));
						}
						
						prefix = jf.substring(0, jf.indexOf("_metric_"));
					}
				}
				
			}
			
			beanList.add(bean);
			
			System.out.println(beanList);
			RegionBean[] rs = new RegionBean[beanList.size()];
			beans.setBeans(beanList.toArray(rs));
		}

		return beans;
	}

	public Object handle() {
		String result = request(url);
		logger.info("Request URL: " + url);
		
		return parseBean(result);
		
	}

}
