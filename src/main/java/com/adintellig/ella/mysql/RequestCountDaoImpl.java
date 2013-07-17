package com.adintellig.ella.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.adintellig.ella.model.RegionRequestCount;
import com.adintellig.ella.model.RegionServerRequestCount;
import com.adintellig.ella.model.RequestCount;
import com.adintellig.ella.model.TableRequestCount;
import com.adintellig.ella.util.JdbcUtil;

public class RequestCountDaoImpl {

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
		} else if (req instanceof RegionServerRequestCount) {
			sql = insertServerRequestsSQL;
		}

		PreparedStatement stmt = con.prepareStatement(sql);

		String name = null;
		if (req instanceof RegionRequestCount) {
			name = ((RegionRequestCount) req).getRegionName();
		} else if (req instanceof TableRequestCount) {
			name = ((TableRequestCount) req).getTableName();
		} else if (req instanceof RegionServerRequestCount) {
			name = ((RegionServerRequestCount) req).getServerHost();
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
			} else if (req instanceof RegionServerRequestCount) {
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
			} else if (req instanceof RegionServerRequestCount) {
				name = ((RegionServerRequestCount) req).getServerHost();
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

//	public RequestCount find(long id) throws Exception {
//		Connection conn = JdbcUtil.getConnection();
//		PreparedStatement ps = conn
//				.prepareStatement("select * from table_requests where id=?");
//		ps.setLong(1, id);
//		ResultSet rs = ps.executeQuery();
//		TableRequestCount s = new RequestCount();
//		while (rs.next()) {
//			s.setId(id);
//			s.setStuNum(rs.getString("stuNum"));
//			s.setName(rs.getString("name"));
//			s.setSex(rs.getString("sex"));
//			s.setAddress(rs.getString("address"));
//		}
//		JdbcUtil.close(conn);
//		return s;
//	}

//	public List<RequestCount> list() throws Exception {
//		Connection conn = JdbcUtil.getConnection();
//		Statement stmt = conn.createStatement();
//		ResultSet rs = stmt.executeQuery("select * from t_student");
//		List<RequestCount> students = new ArrayList<RequestCount>();
//		while (rs.next()) {
//			RequestCount s = new RequestCount();
//			s.setId(rs.getLong("id"));
//			s.setStuNum(rs.getString("stuNum"));
//			s.setName(rs.getString("name"));
//			s.setSex(rs.getString("sex"));
//			s.setAddress(rs.getString("address"));
//			students.add(s);
//		}
//		JdbcUtil.close(conn);
//		return students;
//	}
//
//	public void update(RequestCount s) throws Exception {
//		Connection conn = JdbcUtil.getConnection();
//		PreparedStatement ps = conn
//				.prepareStatement("update t_student(stuNum,name,sex,address) values(?,?,?,?)");
//		ps.setString(1, s.getStuNum());
//		ps.setString(2, s.getName());
//		ps.setString(3, s.getSex());
//		ps.setString(4, s.getAddress());
//		ps.executeUpdate();
//		JdbcUtil.close(conn);
//	}

}
