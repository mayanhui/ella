package com.adintellig.ella.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adintellig.ella.model.Table;
import com.adintellig.ella.util.JdbcUtil;

public class TableDaoImpl {
	private static Logger logger = LoggerFactory.getLogger(TableDaoImpl.class);

	static final String insertTablesSQL = "INSERT INTO hbase.tables(table_name, update_time) "
			+ "VALUES(?, ?)";

	public void batchUpdate(List<Table> beans) throws Exception {
		Connection con = JdbcUtil.getConnection();

		PreparedStatement stmt = con.prepareStatement(insertTablesSQL);

		if (null != beans && beans.size() > 0) {
			for (Table t : beans) {
				stmt.setString(1, t.getTableName());
				stmt.setTimestamp(2, t.getUpdateTime());
				stmt.addBatch();
			}
			logger.info("[INSERT] Load Tables info into 'tables'. Size=" + beans.size());
		}
		stmt.executeBatch();
		JdbcUtil.close(con, stmt, null);
	}
	
	public boolean needUpdate(List<Table> tables) {
		if (null != tables && tables.size() > 0) {
			if (tables.size() != getTotalNumber())
				return true;
		}
		return false;
	}
	
	public void truncate() throws SQLException {
		Connection conn = JdbcUtil.getConnection();
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("truncate table hbase.tables");
		JdbcUtil.close(conn);
	}

	public static void main(String[] args) {
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

	public List<Table> list() throws Exception {
		Connection conn = JdbcUtil.getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("select * from hbase.tables");
		List<Table> tables = new ArrayList<Table>();
		while (rs.next()) {
			Table t = new Table();
			t.setTableName(rs.getString("table_name"));
			t.setUpdateTime(rs.getTimestamp("update_time"));
			tables.add(t);
		}
		JdbcUtil.close(conn);
		return tables;
	}

	public static int getTotalNumber() {
		int num = -1;
		try {
			Connection con = JdbcUtil.getConnection();
			Statement stmt = con.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT count(*) FROM hbase.tables");
			if (rs.next())
				num = rs.getInt(1);

			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return num;
	}

	//
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
