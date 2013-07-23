package com.adintellig.ella.hbase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adintellig.ella.hbase.beans.MasterServiceBeans;
import com.adintellig.ella.model.Region;
import com.adintellig.ella.model.RequestCount;
import com.adintellig.ella.model.Server;
import com.adintellig.ella.model.Table;
import com.adintellig.ella.model.zookeeper.Base;
import com.adintellig.ella.model.zookeeper.Client;
import com.adintellig.ella.model.zookeeper.Quorum;
import com.adintellig.ella.mysql.RegionDaoImpl;
import com.adintellig.ella.mysql.RequestCountDaoImpl;
import com.adintellig.ella.mysql.ServerDaoImpl;
import com.adintellig.ella.mysql.TableDaoImpl;
import com.adintellig.ella.util.ConfigFactory;
import com.adintellig.ella.util.ConfigProperties;

public class ZookeeperService extends Thread {
	private static Logger logger = LoggerFactory
			.getLogger(ZookeeperService.class);

	static ConfigProperties config = ConfigFactory.getInstance()
			.getConfigProperties(ConfigFactory.ELLA_CONFIG_PATH);

	public static String url;

	private RequestCountDaoImpl reqDao = null;
	private TableDaoImpl tblDao = null;
	private RegionDaoImpl regDao = null;
	private ServerDaoImpl serDao = null;

	private static ZookeeperService service;

	private ZookeeperService() {
		this.reqDao = new RequestCountDaoImpl();
		this.tblDao = new TableDaoImpl();
		this.regDao = new RegionDaoImpl();
		this.serDao = new ServerDaoImpl();
		url = config.getProperty("ella.hbase.master.baseurl")
				+ config.getProperty("ella.hbase.zookeeper.suburl");
	}

	public static synchronized ZookeeperService getInstance() {
		if (service == null)
			service = new ZookeeperService();
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

	public Base parseBean(String str) {
		Base base = null;
		if (null != str && str.trim().length() > 0) {
			String[] htmls = str.split("<pre>|</pre>", -1);
			String content = null;
			if(htmls.length == 3){
				content = htmls[1];
			}
			
			System.out.println(content);
			
			String[] arr = content.split("Quorum Server Statistics:", -1);
			if (arr.length == 2) {
				String baseStr = arr[0];
				String quorumClientsStr = arr[1];
				// base
				base = new Base();
				String[] baseStrArr = baseStr.split("Region servers:", -1);
				if (baseStrArr.length == 2) {
					String baseHead = baseStrArr[0].trim();
					String regionServers = baseStrArr[1].trim();
					String[] baseHeadArr = baseHead.split("\n", -1);
					for (String s : baseHeadArr) {
						s = s.trim();
						if (s.startsWith("HBase is rooted at")) {
							String hdfsRoot = s.replaceAll(
									"HBase is rooted at", "").trim();
							base.setHdfsRoot(hdfsRoot);
						} else if (s.startsWith("Active master address:")) {
							String masterAddress = s.replaceAll(
									"Active master address:", "").trim();
							base.setMasterAddress(masterAddress);
						} else if (s.startsWith("Backup master addresses:")) {
							String backupMasterAddress = s.replaceAll(
									"Backup master addresses:", "").trim();
							base.setBackupMasterAddress(backupMasterAddress);
						} else if (s.startsWith("Region server holding ROOT:")) {
							String regionServerHoldingRoot = s.replaceAll(
									"Region server holding ROOT:", "").trim();
							base.setRegionServerHoldingRoot(regionServerHoldingRoot);
						}
					}
					base.setRegionServers(regionServers);
					base.setUpdateTime(new Timestamp(System.currentTimeMillis()));
				}

				// quorum&client
				String[] quorumClientsStrArr = quorumClientsStr.split("\n", -1);
				List<String> quorumStrs = new ArrayList<String>();
				StringBuilder sb = new StringBuilder();
				for (String qcs : quorumClientsStrArr) {
					if (null != qcs && qcs.trim().length() > 0) {
						if (qcs.indexOf("Node count:") >= 0) {
							sb.append(qcs);
							quorumStrs.add(sb.toString());
							sb = new StringBuilder();
						} else {
							sb.append(qcs + "\n");
						}
					}
				}
				List<Quorum> quorums = new ArrayList<Quorum>();
				for (String quorumStr : quorumStrs) {
					Quorum quorum = new Quorum();
					List<Client> clients = new ArrayList<Client>();
					String[] quorumArr = quorumStr.split("\n", -1);
					for (String qa : quorumArr) {
						qa = qa.trim();
						if (qa.startsWith("Zookeeper version:")) {
							String version = qa.replaceAll(
									"Zookeeper version:", "").trim();
							quorum.setVersion(version);
						} else if (qa.startsWith("Latency min/avg/max:")) {
							String latency = qa.replaceAll(
									"Latency min/avg/max:", "").trim();
							String[] latencyArr = latency.split("/", -1);
							if (latencyArr.length == 3) {
								quorum.setLatencyMin(Integer
										.parseInt(latencyArr[0].trim()));
								quorum.setLatencyAvg(Integer
										.parseInt(latencyArr[1].trim()));
								quorum.setLatencyMax(Integer
										.parseInt(latencyArr[2].trim()));
							}
						} else if (qa.startsWith("Received:")) {
							String receivedStr = qa.replaceAll("Received:", "")
									.trim();
							long received = Long.parseLong(receivedStr);
							quorum.setReceived(received);
						} else if (qa.startsWith("Sent:")) {
							String sentStr = qa.replaceAll("Sent:", "").trim();
							long sent = Long.parseLong(sentStr);
							quorum.setSent(sent);
						} else if (qa.startsWith("Outstanding:")) {
							String outstandingStr = qa.replaceAll(
									"Outstanding:", "").trim();
							int outstanding = Integer.parseInt(outstandingStr);
							quorum.setOutstanding(outstanding);
						} else if (qa.startsWith("Zxid:")) {
							String zxid = qa.replaceAll("Zxid:", "").trim();
							quorum.setZxid(zxid);
						} else if (qa.startsWith("Mode:")) {
							String mode = qa.replaceAll("Mode:", "").trim();
							quorum.setMode(mode);
						} else if (qa.startsWith("Node count:")) {
							String nodeCountStr = qa.replaceAll("Node count:",
									"").trim();
							int nodeCount = Integer.parseInt(nodeCountStr);
							quorum.setNodeCount(nodeCount);
						} else if (qa.indexOf("](") >= 0) {
							// /192.168.2.62:35811[1](queued=0,recved=138146,sent=138146)
							Client client = new Client();
							String[] qaArr = qa.split("\\[|\\]|\\(|,|\\)", -1);
							for (String q : qaArr) {
								if (q.startsWith("/")) {
									String clientHost = q.replaceAll("/", "");
									client.setHost(clientHost);
								} else if (q.trim().length() == 1) {
									int tag = Integer.parseInt(q.trim());
									client.setTag(tag);
								} else if (q.startsWith("queued=")) {
									String queuedStr = q.replaceAll("queued=",
											"").trim();
									int queued = Integer.parseInt(queuedStr);
									client.setQueued(queued);
								} else if (q.startsWith("recved=")) {
									String recvedStr = q.replaceAll("recved=",
											"").trim();
									long recved = Long.parseLong(recvedStr);
									client.setRecved(recved);
								} else if (q.startsWith("sent=")) {
									String sentStr = q.replaceAll("sent=", "")
											.trim();
									long sent = Long.parseLong(sentStr);
									client.setSent(sent);
								}
								client.setUpdateTime(new Timestamp(System
										.currentTimeMillis()));
							}
							clients.add(client);
						} else {
							if (qa.length() > 0 && qa.indexOf("Clients:") < 0) {
								quorum.setHost(qa);
							}
						}
					}
					quorum.setUpdateTime(new Timestamp(System
							.currentTimeMillis()));
					quorum.setClients(clients);
					quorums.add(quorum);
				}
			}

		}
		return base;
	}

	@Override
	public void run() {
		String result = request(url);
		logger.info("Request URL: " + url);
		// MasterServiceBeans bean = parseBean(result);
		MasterServiceBeans bean = null;
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
		}

	}

	public static void main(String[] args) throws JsonParseException,
			JsonMappingException, IOException, SQLException {
		ZookeeperService service = ZookeeperService.getInstance();
		// Thread t = new Thread(service);
		// t.start();
		String result = service.request(url);
		System.out.println(result);
		Base base = service.parseBean(result);
		System.out.println(base.toString());
//		String str = "/192.168.2.62:35811[1](queued=0,recved=138146,sent=138146)";
//		String[] arr = str.split("\\[|\\]|\\(|,|\\)", -1);
//		for (String a : arr) {
//			System.out.println(a);
//		}
	}
}
