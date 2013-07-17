package com.adintellig.ella.mysql;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.adintellig.ella.model.RegionRequestCount;
import com.adintellig.ella.model.RegionServerRequestCount;
import com.adintellig.ella.model.RequestCount;
import com.adintellig.ella.model.TableRequestCount;

public class RequestDAO {

	private String getRequestSQL = "SELECT * FROM HBASE.REGIONREQUEST WHERE ID = ?";
	private static final String insertRegionRequestsSQL = "INSERT INTO HBASE.REGIONREQUEST(REGIONNAME, WRITECOUNT, READCOUNT, TOTALCOUNT, UPDATETIME, INSERTTIME) "
			+ "VALUES(?, ?, ?, ?, ?, ?)";
	private static final String insertTableRequestsSQL = "INSERT INTO HBASE.TABLEREQUEST(TABLENAME, WRITECOUNT, READCOUNT, TOTALCOUNT, UPDATETIME, INSERTTIME) "
			+ "VALUES(?, ?, ?, ?, ?, ?)";
	private static final String insertServerRequestsSQL = "INSERT INTO HBASE.SERVERREQUEST(HOST, WRITECOUNT, READCOUNT, TOTALCOUNT, UPDATETIME, INSERTTIME) "
			+ "VALUES(?, ?, ?, ?, ?, ?)";

	private Connection con = null;
	private PreparedStatement pstmt = null;
	private int maxItemNumber = 0;

	public RequestDAO(Connection theCon) {
		this.con = theCon;
		// try {
		// this.pstmt = con.prepareStatement(getRequestSQL);
		//
		// Statement stmt = con.createStatement();
		//
		// ResultSet rs = stmt
		// .executeQuery("SELECT MAX(ID) FROM HBASE.REGIONREQUEST");
		//
		// if (rs.next())
		// maxItemNumber = rs.getInt(1);
		//
		// rs.close();
		// stmt.close();
		//
		// } catch (SQLException se) {
		// printSQLException(se);
		// }
	}

	public int getMaxItemNumber() {
		return (this.maxItemNumber);
	}

	public RegionRequestCount getRequest(int targetItemNumber) {

		RegionRequestCount product = null;

		try {
			pstmt.clearParameters();
			pstmt.setInt(1, targetItemNumber);

			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				String host = rs.getString("HOST");
				String regionName = rs.getString("REGIONNAME");
				String tableName = rs.getString("TABLENAME");
				long writeCount = rs.getLong("WRITECOUNT");
				long readCount = rs.getLong("READCOUNT");
				long totalCount = rs.getLong("TOTALCOUNT");
				Timestamp updateTime = rs.getTimestamp("UPDATETIME");
				Timestamp insertTime = rs.getTimestamp("INSERTTIME");

				// product = new RegionRequestCount(host, regionName, tableName,
				// writeCount,
				// readCount, totalCount, updateTime, insertTime);
			}

			rs.close();

		} catch (SQLException se) {
			printSQLException(se);
		}

		return product;
	}

	public List<TableRequestCount> getRequests() {
		List<TableRequestCount> list = new ArrayList<TableRequestCount>();
		String sql = "SELECT TABLENAME,WRITECOUNT,READCOUNT,TOTALCOUNT FROM HBASE.TABLEREQUEST WHERE UPDATETIME >'2013-07-02 00:00:00'";
		try {
			Statement stmt = con.createStatement();
			stmt.setMaxRows(30);
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				TableRequestCount req = new TableRequestCount();
				req.setTableName(rs.getString(1));
				req.setWriteCount(rs.getLong(2));
				req.setReadCount(rs.getLong(3));
				req.setTotalCount(rs.getLong(4));
				list.add(req);
			}
			System.out.println(list.size());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	private void printSQLException(SQLException se) {
		while (se != null) {

			System.out.print("SQLException: State:   " + se.getSQLState());
			System.out.println("Severity: " + se.getErrorCode());
			System.out.println(se.getMessage());

			se = se.getNextException();
		}
	}

	public void batchInsert(List<RequestCount> beans) throws SQLException {
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
			stmt.setTimestamp(6, req.getInsertTime());

			stmt.addBatch();
		}

		stmt.executeBatch();
		stmt.close();

	}
}