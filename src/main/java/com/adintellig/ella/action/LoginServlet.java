package com.adintellig.ella.action;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adintellig.ella.dao.UserDaoImpl;
import com.adintellig.ella.model.user.User;

public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerFactory.getLogger(LoginServlet.class);

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
		logger.info("{POST}");
		
		RequestDispatcher dispatcher = null;
		String username = request.getParameter("username").trim();
		String password = request.getParameter("password").trim();

		HttpSession session = request.getSession(true);

		String sessionUsername = (String) session.getAttribute("username");

		if (null != sessionUsername && sessionUsername.length() > 0)
			sessionUsername = sessionUsername.trim();

		if (username.equals(sessionUsername)) {
			dispatcher = request.getRequestDispatcher("/index.jsp");
			dispatcher.forward(request, response);
		} else {
			try {
				User u = dao.findByNameAndPassword(username, password);
				if (u != null) {
					dispatcher = request.getRequestDispatcher("/init.do");
					// put username into session
					session.setAttribute("username", username);
				} else {
					dispatcher = request.getRequestDispatcher("/login.jsp");
				}
				dispatcher.forward(request, response);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
