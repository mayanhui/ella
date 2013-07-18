<%@ page language="java" contentType="text/html; charset=UTF-8"
        pageEncoding="UTF-8"%>
        
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page language="java" import="java.util.*"%>
<%@ page language="java" import="com.adintellig.ella.mysql.*"%>
<%@ page language="java" import="com.adintellig.ella.model.*"%>

<%
       RequestCountDaoImpl impl = new RequestCountDaoImpl();
	   List<RequestCount> reqs = impl.listDetails((String)request.getParameter("tn"));
	   request.setAttribute("reqs",reqs);
%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head><meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
<title>HBase Master: hbase-master,60000,1363167283031</title>
<link rel="stylesheet" type="text/css" href="static/hbase.css" />
</head>
<body>
<a id="logo" href="http://wiki.apache.org/lucene-hadoop/Hbase"><img src="static/hbase_logo.png" alt="HBase Logo" title="HBase Logo" /></a>

<h1 id="page_title">HBase Monitor</h1>

<hr id="head_rule" />
<h2>Requests</h2>
<table id="requests_table">
<tr><th>Table Name</th><th>Write Count</th><th>Read Count</th><th>Total Count</th></tr>

<c:forEach var="r" items="${reqs}"> 
        <tr> 
          <td>${r.tableName}</td>
          <td>${r.writeCount}</td>
          <td>${r.readCount}</td>
          <td>${r.totalCount}</td>
          <td>${r.updateTime}</td>  
        </tr> 
</c:forEach> 

</table>

</body>
</html>


