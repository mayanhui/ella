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

import com.adintellig.ella.model.Server;
import com.adintellig.ella.util.JdbcUtil;

public class ServerDaoImpl {
	private static Logger logger = LoggerFactory.getLogger(ServerDaoImpl.class);

	static final String insertSQL = "INSERT INTO hbase.servers(host, update_time) "
			+ "VALUES(?, ?)";

	public void batchUpdate(List<Server> beans) throws Exception {
		Connection con = JdbcUtil.getConnection();

		PreparedStatement stmt = con.prepareStatement(insertSQL);

		if (null != beans && beans.size() > 0) {
			for (Server r : beans) {
				stmt.setString(1, r.getHost());
				stmt.setTimestamp(2, r.getUpdateTime());
				stmt.addBatch();
			}
			logger.info("[INSERT] Load Servers info into 'servers'. Size="
					+ beans.size());
		}
		stmt.executeBatch();
		JdbcUtil.close(con, stmt, null);
	}

	public boolean needUpdate(List<Server> beans) {
		if (null != beans && beans.size() > 0) {
			if (beans.size() != getTotalNumber())
				return true;
		}
		return false;
	}

	public void truncate() throws SQLException {
		Connection conn = JdbcUtil.getConnection();
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("truncate table hbase.servers");
		JdbcUtil.close(conn);
	}

	public List<Server> list() throws Exception {
		Connection conn = JdbcUtil.getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("select * from hbase.servers");
		List<Server> servers = new ArrayList<Server>();
		while (rs.next()) {
			Server s = new Server();
			s.setHost(rs.getString("host"));
			s.setUpdateTime(rs.getTimestamp("update_time"));
			servers.add(s);
		}
		JdbcUtil.close(conn);
		return servers;
	}

	public static int getTotalNumber() {
		int num = -1;
		try {
			Connection con = JdbcUtil.getConnection();
			Statement stmt = con.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT count(*) FROM hbase.servers");
			if (rs.next())
				num = rs.getInt(1);

			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return num;
	}

	public static void main(String[] args) throws SQLException {
		ServerDaoImpl dao = new ServerDaoImpl();
		dao.truncate();
	}

}
