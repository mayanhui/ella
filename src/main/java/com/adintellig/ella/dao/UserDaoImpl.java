package com.adintellig.ella.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adintellig.ella.model.user.User;
import com.adintellig.ella.util.JdbcUtil;

public class UserDaoImpl {
	private static Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);

	public User findByNameAndPassword(String username, String password)
			throws SQLException {
		String sql = "select * from hbase.user where username=? and password=?";

		Connection conn = JdbcUtil.getConnection();
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, username);
		ps.setString(2, password);
		ResultSet rs = ps.executeQuery();
		logger.info("[QUERY]: " + sql);

		User u = null;
		if (rs.next()) {
			u = new User();
			u.setUsername(rs.getString("username"));
			u.setPassword(rs.getString("password"));
		}
		JdbcUtil.close(conn);
		return u;
	}
}
