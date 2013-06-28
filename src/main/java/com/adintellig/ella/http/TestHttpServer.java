package com.adintellig.ella.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.http.HttpServer;

public class TestHttpServer {
	static final Log LOG = LogFactory.getLog(TestHttpServer.class);

	private HttpServer server;
	private URL baseUrl;

	@SuppressWarnings("serial")
	public static class EchoMapServlet extends HttpServlet {
		@SuppressWarnings("unchecked")
		@Override
		public void doGet(HttpServletRequest request,
				HttpServletResponse response) throws ServletException,
				IOException {
			PrintStream out = new PrintStream(response.getOutputStream());
			Map<String, String[]> params = request.getParameterMap();
			SortedSet<String> keys = new TreeSet<String>(params.keySet());
			for (String key : keys) {
				out.print(key);
				out.print(':');
				String[] values = params.get(key);
				if (values.length > 0) {
					out.print(values[0]);
					for (int i = 1; i < values.length; ++i) {
						out.print(',');
						out.print(values[i]);
					}
				}
				out.print('\n');
			}
			out.close();
		}
	}

	@SuppressWarnings("serial")
	public static class EchoServlet extends HttpServlet {
		@SuppressWarnings("unchecked")
		@Override
		public void doGet(HttpServletRequest request,
				HttpServletResponse response) throws ServletException,
				IOException {
			PrintStream out = new PrintStream(response.getOutputStream());
			SortedSet<String> sortedKeys = new TreeSet<String>();
			Enumeration<String> keys = request.getParameterNames();
			while (keys.hasMoreElements()) {
				sortedKeys.add(keys.nextElement());
			}
			for (String key : sortedKeys) {
				out.print(key);
				out.print(':');
				out.print(request.getParameter(key));
				out.print('\n');
			}
			out.close();
		}
	}

	public void setup() throws Exception {
		new File(System.getProperty("build.webapps", "build/webapps") + "/test")
				.mkdirs();
		server = new HttpServer("test", "0.0.0.0", 0, true);
		server.addServlet("echo", "/echo", EchoServlet.class);
		server.addServlet("echomap", "/echomap", EchoMapServlet.class);
		// server.addJerseyResourcePackage(JerseyResource.class.getPackage()
		// .getName(), "/jersey/*");
		server.start();
		int port = server.getPort();
		baseUrl = new URL("http://localhost:" + port + "/");
	}

	private static String readOutput(URL url) throws IOException {
		StringBuilder out = new StringBuilder();
		InputStream in = url.openConnection().getInputStream();
		byte[] buffer = new byte[64 * 1024];
		int len = in.read(buffer);
		while (len > 0) {
			out.append(new String(buffer, 0, len));
			len = in.read(buffer);
		}
		return out.toString();
	}

	public static void main(String[] args) throws Exception {
		TestHttpServer test = new TestHttpServer();
		test.setup();
		String one = readOutput(new URL(test.baseUrl, "/echo?a=b&c=d"));
		System.out.println(one);
	}

}
