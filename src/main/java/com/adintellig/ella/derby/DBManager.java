package com.adintellig.ella.derby;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.BatchUpdateException;
import java.sql.Statement;

import com.adintellig.ella.derby.model.RequestDAO;

public class DBManager {
	private static Connection con = null;
	private RequestDAO rdao = null;

	private static final String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	private static final String url = "jdbc:derby:";
	private static final String dbName = "DerbyHBase";

	private static final String createRegionRequestSQL = "CREATE TABLE IF NOT EXISTS HBASE.REGIONREQUEST ("
			+ "ID INT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT rid_pk PRIMARY KEY,"
			+ "REGIONNAME VARCHAR(200),"
			+ "WRITECOUNT BIGINT,"
			+ "READCOUNT BIGINT,"
			+ "TOTALCOUNT BIGINT,"
			+ "UPDATETIME TIMESTAMP," + "INSERTTIME TIMESTAMP" + ")";

	private static final String createTableRequestSQL = "CREATE TABLE IF NOT EXISTS HBASE.TABLEREQUEST ("
			+ "ID INT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT rid_pk PRIMARY KEY,"
			+ "TABLENAME VARCHAR(200),"
			+ "WRITECOUNT BIGINT,"
			+ "READCOUNT BIGINT,"
			+ "TOTALCOUNT BIGINT,"
			+ "UPDATETIME TIMESTAMP," + "INSERTTIME TIMESTAMP" + ")";

	private static final String createServerRequestSQL = "CREATE TABLE IF NOT EXISTS HBASE.SERVERREQUEST ("
			+ "ID INT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT rid_pk PRIMARY KEY,"
			+ "HOST VARCHAR(200),"
			+ "WRITECOUNT BIGINT,"
			+ "READCOUNT BIGINT,"
			+ "TOTALCOUNT BIGINT,"
			+ "UPDATETIME TIMESTAMP,"
			+ "INSERTTIME TIMESTAMP" + ")";

	public DBManager() {
		if (!dbExists()) {
			try {
				Class.forName(driver);
				con = DriverManager
						.getConnection(url + dbName + ";create=true");

				processStatement(createRegionRequestSQL);
				processStatement(createTableRequestSQL);
				processStatement(createServerRequestSQL);

				con.setAutoCommit(false);
				con.commit();

			} catch (BatchUpdateException bue) {
				try {
					con.rollback();
					System.err
							.println("Batch Update Exception: Transaction Rolled Back");
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
		stmt.executeUpdate(sql);
		stmt.close();
	}

	public static void main(String[] args) throws SQLException {
		// DBManager dbm = new DBManager();
	}

	private void printSQLException(SQLException se) {
		while (se != null) {

			System.out.print("SQLException: State:   " + se.getSQLState());
			System.out.println("Severity: " + se.getErrorCode());
			System.out.println(se.getMessage());

			se = se.getNextException();
		}
	}

	public RequestDAO getRequestDAO() {
		return rdao;
	}
}