package com.adintellig.ella.derby.model;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class RequestDAO {

	private String getRequestSQL = "SELECT * FROM hbase.requests WHERE ID = ?";

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
			System.out.println(maxItemNumber);
			
			rs.close();
			stmt.close();

		} catch (SQLException se) {
			printSQLException(se);
		}
	}

	public int getMaxItemNumber() {
		return (this.maxItemNumber);
	}

	public Request getRequest(int targetItemNumber) {

		Request product = null;

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

				product = new Request(host, regionName, tableName, writeCount,
						readCount, totalCount, updateTime, insertTime);
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
}