package com.adintellig.ella.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.CoordinatedStateManager;
import org.apache.hadoop.hbase.CoordinatedStateManagerFactory;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.master.HMaster;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.zookeeper.ZKUtil;
import org.apache.hadoop.hbase.zookeeper.ZooKeeperWatcher;
import org.apache.zookeeper.KeeperException;

import com.adintellig.ella.model.zookeeper.Base;
import com.adintellig.ella.model.zookeeper.Client;
import com.adintellig.ella.model.zookeeper.Quorum;

/**
 * 操作HBase ZK的util，访问ZK的时候不太稳定
 * TODO: 筛选问题所在，初步定为很多ZK的类都是HBase内部使用，runtime用的类。
 * @author didi
 *
 */
public class HBaseUtil extends ZKUtil {

	static Configuration conf;
	static HMaster master;
	static ZooKeeperWatcher watcher;
	static int zkDumpConnectionTimeOut = 15000;
	static ConfigProperties config = ConfigFactory.getInstance()
			.getConfigProperties(ConfigFactory.ELLA_CONFIG_PATH);

	static {
		conf = HBaseConfiguration.create();
		conf.set(ConfigProperties.CONFIG_NAME_HBASE_MASTER,
				config.getProperty(ConfigProperties.CONFIG_NAME_HBASE_MASTER));
		conf.set(
				ConfigProperties.CONFIG_NAME_HBASE_ZOOKEEPER_PROPRERTY_CLIENTPORT,
				config.getProperty(ConfigProperties.CONFIG_NAME_HBASE_ZOOKEEPER_PROPRERTY_CLIENTPORT));
		conf.set(
				ConfigProperties.CONFIG_NAME_HBASE_ZOOKEEPER_QUORUM,
				config.getProperty(ConfigProperties.CONFIG_NAME_HBASE_ZOOKEEPER_QUORUM));
		try {
			CoordinatedStateManager csm = CoordinatedStateManagerFactory.getCoordinatedStateManager(conf);
			master = new HMaster(conf,csm);
			//HMaster master = (HMaster)getServletContext().getAttribute(HMaster.MASTER);
			  
			
			//watcher = master.getZooKeeperWatcher();
			watcher = master.getZooKeeper();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Base dumpZookeeperInfo() {
		Base base = null;
		try {
			if (null != watcher) {
				base = new Base();
				base.setHdfsRoot(watcher.baseZNode);
//				System.out.println(watcher.baseZNode);
//				System.out.println(watcher.toString());
//				try {
//					System.out.println(Bytes.toStringBinary(getData(watcher, watcher.getMasterAddressZNode())));
//				} catch (InterruptedException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
				
//				try {
//					base.setMasterAddress(ServerName.parseVersionedServerName(
//							getData(watcher, watcher.getMasterAddressZNode()))
//							.toString());
					base.setMasterAddress(watcher.getMasterAddressZNode());
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}

				StringBuilder sb = new StringBuilder();
				for (String child : listChildrenNoWatch(watcher,
						watcher.backupMasterAddressesZNode)) {
					sb.append(child + "\n");
				}
				if (sb.length() > 0)
					sb.setLength(sb.length() - 1);
				base.setBackupMasterAddress(sb.toString());

//				base.setRegionServerHoldingRoot(Bytes.toStringBinary(getData(
//						watcher, watcher.rootServerZNode)));
				
				try {
					base.setRegionServerHoldingRoot(Bytes.toStringBinary(getData(
							watcher, watcher.getBaseZNode())));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				sb = new StringBuilder();
				for (String child : listChildrenNoWatch(watcher,
						watcher.rsZNode)) {
					sb.append(child + "\n");
				}
				if (sb.length() > 0)
					sb.setLength(sb.length() - 1);
				base.setRegionServers(sb.toString());
				base.setUpdateTime(new Timestamp(System.currentTimeMillis()));
			}

			List<Quorum> quorums = new ArrayList<Quorum>();
			String[] servers = watcher.getQuorum().split(",");
			System.out.println(Arrays.toString(servers));
			// generate inner ips
			Set<String> zkServers = new HashSet<String>();
			for (String server : servers) {
				zkServers.add(server.trim().replaceAll(":2181", ""));
			}

//			String masterAddress = null;
//			try {
//				masterAddress = ServerName.parseVersionedServerName(
//						getData(watcher, watcher.getMasterAddressZNode())).toString();
//			} catch (InterruptedException e1) {
//				e1.printStackTrace();
//			}
//			masterAddress = masterAddress.substring(0,
//					masterAddress.indexOf(","));
//			List<String> mips = IPHostUtil.getAllInternalHostIPs(masterAddress);
//			if (null != mips && mips.size() > 0) {
//				for (String mip : mips)
//					zkServers.add(mip);
//			}

			for (String child : listChildrenNoWatch(watcher, watcher.rsZNode)) {
				String rshost = child.substring(0, child.indexOf(","));
				List<String> rsips = IPHostUtil.getAllInternalHostIPs(rshost);
				if (null != rsips && rsips.size() > 0) {
					for (String rsip : rsips)
						zkServers.add(rsip);
				}
			}

			for (String server : servers) {
				Quorum quorum = new Quorum();
				List<Client> clients = new ArrayList<Client>();
				quorum.setHost(server);
				try {
					System.out.println(dump(watcher));
					String[] stat = getServerStats(server,
								zkDumpConnectionTimeOut);
					for (String s : stat) {
						s = s.trim();
						if (s.startsWith("Zookeeper version:")) {
							String version = s.replaceAll("Zookeeper version:",
									"").trim();
							quorum.setVersion(version);
						} else if (s.startsWith("Latency min/avg/max:")) {
							String latency = s.replaceAll(
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
						} else if (s.startsWith("Received:")) {
							String receivedStr = s.replaceAll("Received:", "")
									.trim();
							long received = Long.parseLong(receivedStr);
							quorum.setReceived(received);
						} else if (s.startsWith("Sent:")) {
							String sentStr = s.replaceAll("Sent:", "").trim();
							long sent = Long.parseLong(sentStr);
							quorum.setSent(sent);
						} else if (s.startsWith("Outstanding:")) {
							String outstandingStr = s.replaceAll(
									"Outstanding:", "").trim();
							int outstanding = Integer.parseInt(outstandingStr);
							quorum.setOutstanding(outstanding);
						} else if (s.startsWith("Zxid:")) {
							String zxid = s.replaceAll("Zxid:", "").trim();
							quorum.setZxid(zxid);
						} else if (s.startsWith("Mode:")) {
							String mode = s.replaceAll("Mode:", "").trim();
							quorum.setMode(mode);
						} else if (s.startsWith("Node count:")) {
							String nodeCountStr = s.replaceAll("Node count:",
									"").trim();
							int nodeCount = Integer.parseInt(nodeCountStr);
							quorum.setNodeCount(nodeCount);
						} else if (s.indexOf("](") >= 0) {
							// /192.168.2.62:35811[1](queued=0,recved=138146,sent=138146)
							Client client = new Client();
							String[] qaArr = s.split("\\[|\\]|\\(|,|\\)", -1);
							boolean validClient = false;
							for (String q : qaArr) {
								if (q.startsWith("/")) {
									String clientHost = q.replaceAll("/", "");
									if (checkExternalHost(clientHost, zkServers)) {
										client.setHost(clientHost);
										validClient = true;
									} else
										break;
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
							if (validClient)
								clients.add(client);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				quorum.setUpdateTime(new Timestamp(System.currentTimeMillis()));
				quorum.setClients(clients);
				quorums.add(quorum);
			}
			base.setQuorums(quorums);
		} catch (KeeperException ke) {
			ke.printStackTrace();
		}
		return base;
	}

	private static boolean checkExternalHost(String host,
			Set<String> internalHost) {
		if (null != internalHost && internalHost.size() > 0 && null != host
				&& host.trim().length() > 0) {
			for (String ihost : internalHost) {
				if (host.startsWith(ihost))
					return false;
			}
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		System.out.println(dumpZookeeperInfo());

	}
}
