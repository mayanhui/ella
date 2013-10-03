package com.adintellig.ella.action;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.RequestDispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginFilter implements Filter {
	private static Logger logger = LoggerFactory.getLogger(LoginFilter.class);
	private static final String LOGIN_URI = "LOGIN_URI";
	private static final String ROOT_URI = "ROOT_URI";
	private static final String LOGIN_SERVLET = "LOGIN_SERVLET";
	private String login_page;
	private String root_page;
	private String login_servlet;

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		logger.info("{POST}");

		HttpServletRequest req = (HttpServletRequest) request;
		HttpSession session = req.getSession();
		String requestUri = req.getRequestURI();
		String ctxPath = req.getContextPath();
		String uri = requestUri.substring(ctxPath.length());

		if (uri.equals(login_page) || uri.equals(root_page)
				|| uri.equals(login_servlet)) {
			if (null != session.getAttribute("username")
					&& ((String) session.getAttribute("username")).length() > 0) {
				RequestDispatcher dispatcher = request
						.getRequestDispatcher("/init.do");
				dispatcher.forward(request, response);
			} else {
				chain.doFilter(request, response);
			}

		} else {
			if (null != session.getAttribute("username")
					&& ((String) session.getAttribute("username")).length() > 0) {
				System.out.println("{session}" + uri);
				chain.doFilter(request, response);
			} else {
				System.out.println("{no session}" + uri);
				RequestDispatcher dispatcher = request
						.getRequestDispatcher("/login.jsp");
				dispatcher.forward(request, response);
			}
		}
	}

	public void init(FilterConfig config) throws ServletException {
		login_page = config.getInitParameter(LOGIN_URI);
		root_page = config.getInitParameter(ROOT_URI);
		login_servlet = config.getInitParameter(LOGIN_SERVLET);

		if (null == login_page || null == root_page || null == login_servlet) {
			throw new ServletException("Can not find login page!");
		}
	}
}