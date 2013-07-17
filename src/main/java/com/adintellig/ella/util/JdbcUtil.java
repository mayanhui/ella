package com.adintellig.ella.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JdbcUtil {

	private static String db_driver;
	private static String db_url;
	private static String db_userName;
	private static String db_passWord;

	static {
		ConfigProperties config = ConfigFactory.getInstance()
				.getConfigProperties(ConfigFactory.ELLA_CONFIG_PATH);
		db_driver = config.getProperty("mysql.db.driver");
		db_url = config.getProperty("mysql.db.url");
		db_userName = config.getProperty("mysql.db.user");
		db_passWord = config.getProperty("mysql.db.pwd");
	}

	// 获得连接
	public static Connection getConnection() {
		Connection con = null;
		// 1.加载oracle驱动
		try {
			Class.forName(db_driver);
		} catch (ClassNotFoundException e) {
			System.out.println("3-ClassNotFoundException");
			e.printStackTrace();
			return null;
		}
		// 2.获得数据库连接
		try {
			con = DriverManager.getConnection(db_url, db_userName, db_passWord);
		} catch (SQLException e) {
			System.out.println("4-SQLException");
			e.printStackTrace();
			return null;
		}
		return con;
	}

	// 关闭数据库资源
	public static void close(Connection con, Statement stmt, ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				System.out.println("5--SQLException");
				e.printStackTrace();
			}
		}
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				System.out.println("6-SQLException");
				e.printStackTrace();
			}
		}
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				System.out.println("7-closeSQLException");
				e.printStackTrace();
			}
		}
	}

	// 关闭数据库资源
	public static void close(Connection con, PreparedStatement pstmt,
			ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				System.out.println("8-SQLException");
				e.printStackTrace();
			}
		}
		if (pstmt != null) {
			try {
				pstmt.close();
			} catch (SQLException e) {
				System.out.println("9-SQLException");
				e.printStackTrace();
			}
		}
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				System.out.println("10-closeSQLException");
				e.printStackTrace();
			}
		}
	}

	// 关闭数据库资源
	public static void close(Connection con) {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				System.out.println("11-closeSQLException");
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws SQLException {
		Connection con = getConnection();
		con.prepareStatement("select * from hbase.table_requests");
	}

}