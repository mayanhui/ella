package com.adintellig.ella.hbase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import com.adintellig.ella.hbase.beans.MasterServiceBean;
import com.adintellig.ella.hbase.beans.MasterServiceBeans;
import com.adintellig.ella.hbase.beans.RegionServer;
import com.adintellig.ella.hbase.beans.RegionsLoad;
import com.alibaba.fastjson.JSON;

public class JMXHMasterService {

	public static void main(String[] args) throws JsonParseException,
			JsonMappingException, IOException {

		JMXHMasterService service = new JMXHMasterService();

		String urlStr = "http://hadoop-node-20:60010/jmx?qry=hadoop:service=Master,name=Master";

		URL url = null;
		BufferedReader in = null;
		StringBuffer sb = new StringBuffer();
		try {
			url = new URL(urlStr);
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
		String result = sb.toString();
		MasterServiceBeans bean = JSON.parseObject(result,
				MasterServiceBeans.class);

		MasterServiceBean[] beans = bean.getBeans();
		RegionServer[] rs = beans[0].getRegionServers();
		for (RegionServer r : rs) {
			RegionsLoad[] rl = r.getValue().getRegionsLoad();
			for (RegionsLoad l : rl) {
				System.out.println(l.getValue().getNameAsString());
				System.out.println(l.getValue().getReadRequestsCount());
				System.out.println(l.getValue().getWriteRequestsCount());
				System.out.println(l.getValue().getRequestsCount());
			}
			System.out.println("----------");
		}
		

	}
}
