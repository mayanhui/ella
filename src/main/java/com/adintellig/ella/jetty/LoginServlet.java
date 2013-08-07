package com.adintellig.ella.jetty;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.adintellig.ella.model.user.User;
import com.adintellig.ella.mysql.UserDaoImpl;

public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	UserDaoImpl dao = new UserDaoImpl();

	@Override
	public void init() throws ServletException {
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String username = request.getParameter("username").trim();
		String password = request.getParameter("password").trim();

		try {
			User u = dao.findByNameAndPassword(username, password);
			RequestDispatcher dispatcher = null;
			if (u != null) {
				dispatcher = request.getRequestDispatcher("/index.jsp");
			} else {
				dispatcher = request.getRequestDispatcher("/login.jsp");
			}

			dispatcher.forward(request, response);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
