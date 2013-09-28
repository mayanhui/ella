package com.adintellig.ella.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adintellig.ella.model.RegionRequestCount;
import com.adintellig.ella.model.ServerRequestCount;
import com.adintellig.ella.model.RequestCount;
import com.adintellig.ella.model.TableRequestCount;
import com.adintellig.ella.util.JdbcUtil;

public class RequestCountDaoImpl {
	private static Logger logger = LoggerFactory
			.getLogger(RequestCountDaoImpl.class);

	static final String insertRegionRequestsSQL = "INSERT INTO hbase.region_requests(region_name, write_count, read_count, total_count, update_time) "
			+ "VALUES(?, ?, ?, ?, ?)";
	static final String insertTableRequestsSQL = "INSERT INTO hbase.table_requests(table_name, write_count, read_count, total_count, update_time) "
			+ "VALUES(?, ?, ?, ?, ?)";
	static final String insertServerRequestsSQL = "INSERT INTO hbase.server_requests(host, write_count, read_count, total_count, update_time) "
			+ "VALUES(?, ?, ?, ?, ?)";

	public void add(RequestCount req) throws Exception {
		Connection con = JdbcUtil.getConnection();
		String sql = null;
		if (req instanceof RegionRequestCount) {
			sql = insertRegionRequestsSQL;
		} else if (req instanceof TableRequestCount) {
			sql = insertTableRequestsSQL;
		} else if (req instanceof ServerRequestCount) {
			sql = insertServerRequestsSQL;
		}

		PreparedStatement stmt = con.prepareStatement(sql);

		String name = null;
		if (req instanceof RegionRequestCount) {
			name = ((RegionRequestCount) req).getRegionName();
		} else if (req instanceof TableRequestCount) {
			name = ((TableRequestCount) req).getTableName();
		} else if (req instanceof ServerRequestCount) {
			name = ((ServerRequestCount) req).getServerHost();
		}
		stmt.setString(1, name);
		stmt.setLong(2, req.getWriteCount());
		stmt.setLong(3, req.getReadCount());
		stmt.setLong(4, req.getTotalCount());
		stmt.setTimestamp(5, req.getUpdateTime());

		stmt.executeUpdate();
		JdbcUtil.close(con);
	}

	public void batchAdd(List<RequestCount> beans) throws SQLException {
		Connection con = JdbcUtil.getConnection();
		String sql = null;
		if (null != beans && beans.size() > 0) {
			RequestCount req = beans.get(0);
			if (req instanceof RegionRequestCount) {
				sql = insertRegionRequestsSQL;
			} else if (req instanceof TableRequestCount) {
				sql = insertTableRequestsSQL;
			} else if (req instanceof ServerRequestCount) {
				sql = insertServerRequestsSQL;
			}
		}

		PreparedStatement stmt = con.prepareStatement(sql);

		for (RequestCount req : beans) {
			String name = null;
			if (req instanceof RegionRequestCount) {
				name = ((RegionRequestCount) req).getRegionName();
			} else if (req instanceof TableRequestCount) {
				name = ((TableRequestCount) req).getTableName();
			} else if (req instanceof ServerRequestCount) {
				name = ((ServerRequestCount) req).getServerHost();
			}
			stmt.setString(1, name);
			stmt.setLong(2, req.getWriteCount());
			stmt.setLong(3, req.getReadCount());
			stmt.setLong(4, req.getTotalCount());
			stmt.setTimestamp(5, req.getUpdateTime());

			stmt.addBatch();
		}

		stmt.executeBatch();
		JdbcUtil.close(con, stmt, null);
	}

	// public void delete(long id) throws Exception {
	// Connection conn = JdbcUtil.getConnection();
	// PreparedStatement ps = conn
	// .prepareStatement("delete from t_student where id = ?");
	// ps.setLong(1, id);
	// ps.executeQuery();
	// JdbcUtil.close(conn);
	//
	// }

	// public RequestCount find(long id) throws Exception {
	// Connection conn = JdbcUtil.getConnection();
	// PreparedStatement ps = conn
	// .prepareStatement("select * from table_requests where id=?");
	// ps.setLong(1, id);
	// ResultSet rs = ps.executeQuery();
	// TableRequestCount s = new RequestCount();
	// while (rs.next()) {
	// s.setId(id);
	// s.setStuNum(rs.getString("stuNum"));
	// s.setName(rs.getString("name"));
	// s.setSex(rs.getString("sex"));
	// s.setAddress(rs.getString("address"));
	// }
	// JdbcUtil.close(conn);
	// return s;
	// }

	public List<RequestCount> list() throws Exception {
		Connection conn = JdbcUtil.getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt
				.executeQuery("select * from (select table_name,write_count,read_count,total_count,update_time from hbase.table_requests order by id desc limit "
						+ TableDaoImpl.getTotalNumber()
						* 2
						+ ") a order by a.table_name");
		List<RequestCount> list = new ArrayList<RequestCount>();
		while (rs.next()) {
			TableRequestCount req = new TableRequestCount();
			req.setTableName(rs.getString(1));
			req.setWriteCount(rs.getLong(2));
			req.setReadCount(rs.getLong(3));
			req.setTotalCount(rs.getLong(4));
			req.setUpdateTime(rs.getTimestamp(5));
			list.add(req);
		}

		// compute Tps
		Map<String, RequestCount> map = new HashMap<String, RequestCount>();
		for (RequestCount rc : list) {
			if (rc instanceof TableRequestCount) {
				TableRequestCount trc = (TableRequestCount) rc;
				String tableName = trc.getTableName();
				if (null != map.get(tableName)) {
					TableRequestCount trcOld = (TableRequestCount) map
							.get(tableName);
					long writeDiff = trcOld.getWriteCount()
							- trc.getWriteCount();
					long readDiff = trcOld.getReadCount() - trc.getReadCount();
					long totalDiff = trcOld.getTotalCount()
							- trc.getTotalCount();

					long timeDiff = (trcOld.getUpdateTime().getTime() - trc
							.getUpdateTime().getTime()) / 1000;

					if (timeDiff == 0l) {
						timeDiff = 1l;
					}

					int writeTps = (int) (Math.abs(writeDiff / timeDiff));
					int readTps = (int) (Math.abs(readDiff / timeDiff));
					int totalTps = (int) (Math.abs(totalDiff / timeDiff));

					if (timeDiff >= 0) {
						trcOld.setReadTps(readTps);
						trcOld.setWriteTps(writeTps);
						trcOld.setTotalTps(totalTps);
						map.put(tableName, trcOld);
					} else {
						trc.setReadTps(readTps);
						trc.setWriteTps(writeTps);
						trc.setTotalTps(totalTps);
						map.put(tableName, trc);
					}

				} else {
					map.put(tableName, trc);
				}
			}
		}

		list = new ArrayList<RequestCount>();
		Set<String> keys = map.keySet();
		for (Iterator<String> it = keys.iterator(); it.hasNext();) {
			String key = it.next();
			list.add(map.get(key));
		}

		JdbcUtil.close(conn);
		return list;
	}
	
	public List<RequestCount> listServers() throws Exception {
		Connection conn = JdbcUtil.getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt
				.executeQuery("select * from (select host,write_count,read_count,total_count,update_time from hbase.server_requests order by id desc limit "
						+ TableDaoImpl.getTotalNumber()
						* 2
						+ ") a order by a.host");
		List<RequestCount> list = new ArrayList<RequestCount>();
		while (rs.next()) {
			ServerRequestCount req = new ServerRequestCount();
			req.setServerHost(rs.getString(1));
			req.setWriteCount(rs.getLong(2));
			req.setReadCount(rs.getLong(3));
			req.setTotalCount(rs.getLong(4));
			req.setUpdateTime(rs.getTimestamp(5));
			list.add(req);
		}

		// compute Tps
		Map<String, RequestCount> map = new HashMap<String, RequestCount>();
		for (RequestCount rc : list) {
			if (rc instanceof ServerRequestCount) {
				ServerRequestCount trc = (ServerRequestCount) rc;
				String host = trc.getServerHost();
				if (null != map.get(host)) {
					ServerRequestCount trcOld = (ServerRequestCount) map
							.get(host);
					long writeDiff = trcOld.getWriteCount()
							- trc.getWriteCount();
					long readDiff = trcOld.getReadCount() - trc.getReadCount();
					long totalDiff = trcOld.getTotalCount()
							- trc.getTotalCount();

					long timeDiff = (trcOld.getUpdateTime().getTime() - trc
							.getUpdateTime().getTime()) / 1000;

					if (timeDiff == 0l) {
						timeDiff = 1l;
					}

					int writeTps = (int) (Math.abs(writeDiff / timeDiff));
					int readTps = (int) (Math.abs(readDiff / timeDiff));
					int totalTps = (int) (Math.abs(totalDiff / timeDiff));

					if (timeDiff >= 0) {
						trcOld.setReadTps(readTps);
						trcOld.setWriteTps(writeTps);
						trcOld.setTotalTps(totalTps);
						map.put(host, trcOld);
					} else {
						trc.setReadTps(readTps);
						trc.setWriteTps(writeTps);
						trc.setTotalTps(totalTps);
						map.put(host, trc);
					}

				} else {
					map.put(host, trc);
				}
			}
		}

		list = new ArrayList<RequestCount>();
		Set<String> keys = map.keySet();
		for (Iterator<String> it = keys.iterator(); it.hasNext();) {
			String key = it.next();
			list.add(map.get(key));
		}

		JdbcUtil.close(conn);
		return list;
	}


	public List<RequestCount> listWriteHotRegions() throws Exception {
		Connection conn = JdbcUtil.getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt
				.executeQuery("select a.region_name,a.write_count from (select region_name,write_count from hbase.region_requests order by id desc limit "
						+ RegionDaoImpl.getTotalNumber()
						+ ") a order by a.write_count desc limit 10");
		List<RequestCount> list = new ArrayList<RequestCount>();
		while (rs.next()) {
			RegionRequestCount req = new RegionRequestCount();
			req.setRegionName(rs.getString(1));
			req.setWriteCount(rs.getLong(2));
			list.add(req);
		}

		JdbcUtil.close(conn);
		return list;
	}

	public List<RequestCount> listReadHotRegions() throws Exception {
		Connection conn = JdbcUtil.getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt
				.executeQuery("select a.region_name,a.read_count from (select region_name,read_count from hbase.region_requests order by id desc limit "
						+ RegionDaoImpl.getTotalNumber()
						+ ") a order by a.read_count desc limit 10");
		List<RequestCount> list = new ArrayList<RequestCount>();
		while (rs.next()) {
			RegionRequestCount req = new RegionRequestCount();
			req.setRegionName(rs.getString(1));
			req.setReadCount(rs.getLong(2));
			list.add(req);
		}

		JdbcUtil.close(conn);
		return list;
	}

	public List<RequestCount> listDetails(String tableName) throws Exception {
		String sql = "select a.table_name,a.write_count,a.read_count,a.total_count,a.update_time from (select id,table_name,write_count,read_count,total_count,update_time from hbase.table_requests where table_name='"
				+ tableName + "' order by id desc limit 100) a order by a.id";
		Connection conn = JdbcUtil.getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		List<RequestCount> list = new ArrayList<RequestCount>();
		while (rs.next()) {
			TableRequestCount req = new TableRequestCount();
			req.setTableName(rs.getString(1));
			req.setWriteCount(rs.getLong(2));
			req.setReadCount(rs.getLong(3));
			req.setTotalCount(rs.getLong(4));
			req.setUpdateTime(rs.getTimestamp(5));
			list.add(req);
		}
		JdbcUtil.close(conn);
		logger.info("[QUERY]" + sql + " -> Result:" + list.size());
		return list;
	}

	// public void update(RequestCount s) throws Exception {
	// Connection conn = JdbcUtil.getConnection();
	// PreparedStatement ps = conn
	// .prepareStatement("update t_student(stuNum,name,sex,address) values(?,?,?,?)");
	// ps.setString(1, s.getStuNum());
	// ps.setString(2, s.getName());
	// ps.setString(3, s.getSex());
	// ps.setString(4, s.getAddress());
	// ps.executeUpdate();
	// JdbcUtil.close(conn);
	// }

}
