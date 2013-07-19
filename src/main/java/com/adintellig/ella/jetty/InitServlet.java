package com.adintellig.ella.jetty;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

public class InitServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public InitServlet() {

	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		System.out.println("============ Start Quartz Job =============");
		System.getProperties()
				.put("org.quartz.properties", "quartz.properties");
		try {
			StdSchedulerFactory.getDefaultScheduler().start();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		System.out.println("============ Quartz Job Started! ===========");

		RequestDispatcher dispatcher = request
				.getRequestDispatcher("/index.jsp");
		dispatcher.forward(request, response);
	}
}
