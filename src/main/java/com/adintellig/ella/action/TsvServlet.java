package com.adintellig.ella.action;

import java.io.IOException;
import java.io.PrintWriter;
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
import com.adintellig.ella.util.DateFormatUtil;

public class TsvServlet extends HttpServlet {
	private static final long serialVersionUID = 4394185906131598647L;

	private static Logger logger = LoggerFactory.getLogger(TsvServlet.class);

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
		logger.info("Tsv Request: " + tableName);

		StringBuilder sb = new StringBuilder();

		List<RequestCount> reqs = null;
		try {
			reqs = impl.listDetails(tableName);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		sb.append("date\tWrite\tRead\tTotal\n");
		for (RequestCount req : reqs) {
			if (req instanceof TableRequestCount) {
				TableRequestCount treq = (TableRequestCount) req;

				treq.getTableName();
				long wc = treq.getWriteCount();
				long rc = treq.getReadCount();
				long tc = treq.getTotalCount();
				String utc = DateFormatUtil.formatToUTC(treq.getUpdateTime());
				sb.append(utc + "\t" + wc + "\t" + rc + "\t" + tc + "\n");
			}
		}

		if (sb.length() > 0)
			sb.setLength(sb.length() - 1);

		PrintWriter out = response.getWriter();
		try {
			response.setContentType("text/tab-separated-values;charset=UTF-8");
			response.setCharacterEncoding("utf-8");
			out = response.getWriter();
			out.print(sb.toString());
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
