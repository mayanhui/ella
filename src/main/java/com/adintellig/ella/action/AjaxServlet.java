package com.adintellig.ella.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adintellig.ella.dao.RequestCountDaoImpl;
import com.adintellig.ella.model.RequestCount;
import com.adintellig.ella.model.TableRequestCount;

public class AjaxServlet extends HttpServlet {
	private static final long serialVersionUID = 4394185906131598647L;

	private static Logger logger = LoggerFactory.getLogger(AjaxServlet.class);

	RequestCountDaoImpl impl = new RequestCountDaoImpl();

	@Override
	public void init() throws ServletException {

	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String tableName = request.getParameter("tableName").trim();
		logger.info("Ajax Request: " + tableName);

		List<RequestCount> reqs = null;
		try {
			reqs = impl.listDetails(tableName);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		logger.info("Ajax Request: " + reqs.size());
		
		List<List<Long>> wList = new ArrayList<List<Long>>();
		List<List<Long>> rList = new ArrayList<List<Long>>();
		List<List<Long>> tList = new ArrayList<List<Long>>();

		if (null != reqs && reqs.size() > 0) {
			for (RequestCount req : reqs) {
				if (req instanceof TableRequestCount) {
					TableRequestCount treq = (TableRequestCount) req;

					long wc = treq.getWriteCount();
					long rc = treq.getReadCount();
					long tc = treq.getTotalCount();

					long uts = treq.getUpdateTime().getTime();

					List<Long> w = new ArrayList<Long>();
					w.add(uts);
					w.add(wc);
					wList.add(w);

					List<Long> r = new ArrayList<Long>();
					r.add(uts);
					r.add(rc);
					rList.add(r);

					List<Long> t = new ArrayList<Long>();
					t.add(uts);
					t.add(tc);
					tList.add(t);
				}
			}
		}

		// mean of 3 kind of count
		long wListMean = 45000;
		long rListMean = 23423;
		long tListMean = 70000;

		StringBuilder sb = new StringBuilder();
		sb.append("[");

		sb.append("{key: \"Write-Count\",values:" + wList);
		sb.append(",mean:" + wListMean + "},");

		sb.append("{key: \"Read-Count\",values:" + rList);
		sb.append(",mean:" + rListMean + "},");

		sb.append("{key: \"Total-Count\",values:" + tList);
		sb.append(",mean:" + tListMean + "}");

		sb.append("]");

		PrintWriter out = response.getWriter();
		try {
			response.setContentType("text/html;charset=UTF-8");
			response.setCharacterEncoding("utf-8");
			out = response.getWriter();
			out.print(sb.toString());
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
