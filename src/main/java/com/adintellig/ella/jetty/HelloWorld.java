package com.adintellig.ella.jetty;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
 
import java.io.IOException;
 
import org.mortbay.jetty.Server;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.handler.AbstractHandler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
 
public class HelloWorld extends AbstractHandler
{
    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response) 
        throws IOException, ServletException
    {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        response.getWriter().println("<h1>Hello World</h1>");
    }
 
    public static void main(String[] args) throws Exception
    {
//        System.out.println("start");
//   		System.getProperties()
//   				.put("org.quartz.properties", "quartz.properties");
//   		try {
//   			StdSchedulerFactory.getDefaultScheduler().start();
//   		} catch (SchedulerException e) {
//   			e.printStackTrace();
//   		}
//   		System.out.println("end");
    	
        Server server = new Server(8089);
        server.setHandler(new HelloWorld());
 
        server.start();
        server.join();
        
    }

	@Override
	public void handle(String arg0, HttpServletRequest arg1,
			HttpServletResponse arg2, int arg3) throws IOException,
			ServletException {
		// TODO Auto-generated method stub
		
	}
}