package com.adintellig.ella.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.util.Bytes;

import com.adintellig.ella.dao.RequestCountDaoImpl;
import com.adintellig.ella.model.RequestCount;
import com.adintellig.ella.model.TableRequestCount;
import com.adintellig.ella.util.ConfigFactory;
import com.adintellig.ella.util.ConfigProperties;

public class TableService {

	RequestCountDaoImpl rcDao = new RequestCountDaoImpl();
	ConfigProperties config = ConfigFactory.getInstance().getConfigProperties(
			ConfigFactory.ELLA_CONFIG_PATH);
	HBaseAdmin admin;

	public TableService() {
//		Configuration conf = HBaseConfiguration.create();
//		// hbase master
//		conf.set(ConfigProperties.CONFIG_NAME_HBASE_MASTER,
//				config.getProperty(ConfigProperties.CONFIG_NAME_HBASE_MASTER));
//		// zookeeper quorum
//		conf.set(
//				ConfigProperties.CONFIG_NAME_HBASE_ZOOKEEPER_QUORUM,
//				config.getProperty(ConfigProperties.CONFIG_NAME_HBASE_ZOOKEEPER_QUORUM));
//		try {
//			admin = new HBaseAdmin(conf);
//		} catch (MasterNotRunningException e) {
//			e.printStackTrace();
//		} catch (ZooKeeperConnectionException e) {
//			e.printStackTrace();
//		}
	}

	public List<RequestCount> list() throws Exception {
		List<RequestCount> tables = rcDao.list();
		List<RequestCount> tempList = new ArrayList<RequestCount>();
		for (RequestCount rc : tables) {
			if (rc instanceof TableRequestCount) {
				TableRequestCount trc = (TableRequestCount) rc;
				String tableName = trc.getTableName();
//				List<HRegionInfo> regions = admin.getTableRegions(Bytes
//						.toBytes(tableName));
//				if (null != regions)
//					trc.setRegionCount(regions.size());

				tempList.add(trc);
			}
		}
		return tempList;
	}
}
