package com.adintellig.ella.derby.model;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class RequestDAO {

	private String getRequestSQL = "SELECT * FROM hbase.requests WHERE ID = ?";
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
		try {
			this.pstmt = con.prepareStatement(getRequestSQL);

			Statement stmt = con.createStatement();

			ResultSet rs = stmt
					.executeQuery("SELECT MAX(ID) FROM hbase.requests");

			if (rs.next())
				maxItemNumber = rs.getInt(1);

			rs.close();
			stmt.close();

		} catch (SQLException se) {
			printSQLException(se);
		}
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

	private void printSQLException(SQLException se) {
		while (se != null) {

			System.out.print("SQLException: State:   " + se.getSQLState());
			System.out.println("Severity: " + se.getErrorCode());
			System.out.println(se.getMessage());

			se = se.getNextException();
		}
	}

	private void batchInsert(String sql, List<Object> beans)
			throws SQLException {

		// PreparedStatement stmt = con.prepareStatement(sql) ;
		//
		// for(int itemNumber: itemNumbers){
		// stmt.setInt(1, itemNumbers[itemNumber - 1]) ;
		// stmt.setBigDecimal(2, prices[itemNumber - 1]) ;
		// stmt.setDate(3, dates[itemNumber - 1]) ;
		// stmt.setString(4, descriptions[itemNumber - 1]) ;
		// stmt.addBatch() ;
		// }
		//
		// int[] counts = stmt.executeBatch() ;
		//
		// stmt.close() ;

		PreparedStatement stmt = con.prepareStatement(sql);

		stmt.setString(1, "hadoop-node-20");
		stmt.setString(
				2,
				"lvv_uid,{0405BD52-C505-C3D6-EC89-C735F3D6DEB3},1372149624541.5ed9f894d4e640fa2260fdcada5fc59a.");
		stmt.setString(3, "lvv_uid");
		stmt.setLong(4, 100L);
		stmt.setLong(5, 1000L);
		stmt.setLong(6, 1100L);
		stmt.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
		stmt.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
		stmt.addBatch();

		int[] counts = stmt.executeBatch();
		stmt.close();

	}
}