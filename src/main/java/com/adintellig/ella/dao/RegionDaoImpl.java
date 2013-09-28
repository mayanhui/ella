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

import com.adintellig.ella.model.Region;
import com.adintellig.ella.util.JdbcUtil;

public class RegionDaoImpl {
	private static Logger logger = LoggerFactory.getLogger(RegionDaoImpl.class);

	static final String insertSQL = "INSERT INTO hbase.regions(region_name, update_time) "
			+ "VALUES(?, ?)";

	public void batchUpdate(List<Region> beans) throws Exception {
		Connection con = JdbcUtil.getConnection();

		PreparedStatement stmt = con.prepareStatement(insertSQL);

		if (null != beans && beans.size() > 0) {
			for (Region r : beans) {
				stmt.setString(1, r.getRegionName());
				stmt.setTimestamp(2, r.getUpdateTime());
				stmt.addBatch();
			}
			logger.info("[INSERT] Load Regions info into 'regions'. Size="
					+ beans.size());
		}
		stmt.executeBatch();
		JdbcUtil.close(con, stmt, null);
	}

	public boolean needUpdate(List<Region> regions) {
		if (null != regions && regions.size() > 0) {
			if (regions.size() != getTotalNumber())
				return true;
		}
		return false;
	}

	public void truncate() throws SQLException {
		Connection conn = JdbcUtil.getConnection();
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("truncate table hbase.regions");
		JdbcUtil.close(conn);
	}

	public List<Region> list() throws Exception {
		Connection conn = JdbcUtil.getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("select * from hbase.regions");
		List<Region> tables = new ArrayList<Region>();
		while (rs.next()) {
			Region t = new Region();
			t.setRegionName(rs.getString("region_name"));
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
					.executeQuery("SELECT count(*) FROM hbase.regions");
			if (rs.next())
				num = rs.getInt(1);

			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return num;
	}

	public int getRegionNumberByTableName(String tableName) {
		int num = -1;
		try {
			Connection con = JdbcUtil.getConnection();
			Statement stmt = con.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT count(*) FROM hbase.regions where region_name like '"
							+ tableName + "%'");
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
		RegionDaoImpl dao = new RegionDaoImpl();
		dao.truncate();
	}

}
