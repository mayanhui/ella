package com.adintellig.ella.derby;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.BatchUpdateException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

import com.adintellig.ella.derby.model.RegionRequestCount;
import com.adintellig.ella.derby.model.RequestDAO;

//import java.sql.ResultSet;

public class DBManager {
	private static Connection con = null;

	private static final String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	private static final String url = "jdbc:derby:";
	private static final String dbName = "DerbyHBase";

	private static final String createRequestSQL = "CREATE TABLE IF NOT EXISTS hbase.requests ("
			+ "ID INT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT rid_pk PRIMARY KEY,"
			+ "HOST VARCHAR(200),"
			+ "REGIONNAME VARCHAR(200),"
			+ "TABLENAME VARCHAR(200),"
			+ "WRITECOUNT BIGINT,"
			+ "READCOUNT BIGINT,"
			+ "TOTALCOUNT BIGINT,"
			+ "UPDATETIME TIMESTAMP," + "INSERTTIME TIMESTAMP" + ")";

	private static final String insertRequestsSQL = "INSERT INTO hbase.requests(HOST, REGIONNAME, TABLENAME, WRITECOUNT, READCOUNT, TOTALCOUNT, UPDATETIME, INSERTTIME) "
			+ "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

	private RequestDAO rdao = null;

	public DBManager() {
		if (!dbExists()) {
			try {
				Class.forName(driver);
				con = DriverManager
						.getConnection(url + dbName + ";create=true");

				processStatement(createRequestSQL);

				con.setAutoCommit(false);
//				batchInsertData(insertRequestsSQL);
				con.commit();

			} catch (BatchUpdateException bue) {
				try {
					con.rollback();
					System.err.println("Batch Update Exception: Transaction Rolled Back");
					printSQLException((SQLException) bue);
				} catch (SQLException se) {
					printSQLException(se);
				}
			} catch (SQLException se) {
				printSQLException(se);
			} catch (ClassNotFoundException e) {
				System.err.println("JDBC Driver " + driver
						+ " not found in CLASSPATH");
			}
		}
		rdao = new RequestDAO(con);
	}

	public void close() {
		try {
			con = DriverManager.getConnection(url + ";shutdown=true");
		} catch (SQLException se) {
			; // Do Nothing. System has shut down.
		}
		con = null;
	}

	public RequestDAO getRequestDAO() {
		return rdao;
	}

	private Boolean dbExists() {
		Boolean exists = false;
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url + dbName);
			exists = true;
		} catch (Exception e) {
			; // Do nothing, as DB does not (yet) exist
		}
		return (exists);
	}

	// We ignore wranings and return counts for simplicity in this demo

	private void processStatement(String sql) throws SQLException {

		Statement stmt = con.createStatement();
		int count = stmt.executeUpdate(sql);

		stmt.close();
	}

	private void batchInsertData(String sql) throws SQLException {

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

	public static void main(String[] args) throws SQLException {
		DBManager dbm = new DBManager();
		RequestDAO rdao = dbm.getRequestDAO();
		RegionRequestCount r = rdao.getRequest(1);
		System.out.println(r.toString());
//		dbm.batchInsertData(insertRequestsSQL);
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