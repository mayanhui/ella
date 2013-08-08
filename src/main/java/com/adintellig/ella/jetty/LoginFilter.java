package com.adintellig.ella.jetty;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.RequestDispatcher;

public class LoginFilter implements Filter {
	private static final String LOGON_URI = "LOGON_URI";
	private static final String ROOT_URI = "ROOT_URI";
	private String logon_page;
	private String root_page;

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		// HttpServletResponse resp = (HttpServletResponse) response;
		// resp.setContentType("text/html;");
		// resp.setCharacterEncoding("utf-8");
		HttpSession session = req.getSession();
		// PrintWriter out = resp.getWriter();
		// 得到用户请求的URI
		String request_uri = req.getRequestURI();
		// 得到web应用程序的上下文路径
		String ctxPath = req.getContextPath();
		// 去除上下文路径，得到剩余部分的路径
		String uri = request_uri.substring(ctxPath.length());
		// 判断用户访问的是否是登录页面
		System.out.println(uri + "==========");
		if (uri.equals(logon_page) || uri.equals(root_page)) {
			chain.doFilter(request, response);
			// return;
		} else {
			// 如果访问的不是登录页面，则判断用户是否已经登录
			if (null != session.getAttribute("username")
					&& ((String) session.getAttribute("username")).length() > 0) {
				chain.doFilter(request, response);
				// return;
			} else {
				RequestDispatcher dispatcher = request
						.getRequestDispatcher("/login.jsp");
				dispatcher.forward(request, response);
			}
		}
	}

	public void init(FilterConfig config) throws ServletException {
		// 从部署描述符中获取登录页面和首页的URI
		logon_page = config.getInitParameter(LOGON_URI);
		root_page = config.getInitParameter(ROOT_URI);
		if (null == logon_page || null == root_page) {
			throw new ServletException("没有找到登录页面或主页");
		}
	}
}