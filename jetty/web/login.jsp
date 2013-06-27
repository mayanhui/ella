<%@ page language="java" contentType="text/html; charset=GBK"
	pageEncoding="GBK"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>  
    <head></head>  
    <body>  
          
        <%
        	String username = (String)request.getParameter("username");
        	if(username != null && username.equals("aaa")){
        		out.println("welcome    "+ username);
        	}else{
        		out.println("sorry    " + username);
        	}
        	
        %>
      
      	<a href="index.jsp">ÍË»Ø</a>
    </body>     
</html>  