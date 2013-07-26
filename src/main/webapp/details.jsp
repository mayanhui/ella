<%@ page language="java" contentType="text/html; charset=UTF-8"
        pageEncoding="UTF-8"%>

<%@ page language="java" import="java.util.*"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page language="java" import="com.adintellig.ella.util.*"%>

<%
      String tableName = (String)request.getParameter("tn");
	  request.setAttribute("tableName",tableName);
	   
	  PersistDisk.persistMysqlToFile(tableName,"/tmp/jetty-0.0.0.0-8080-ella.war-_ella-any-/webapp/data/");
	
%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head><meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
<title>HBase Monitor</title>
<link rel="stylesheet" type="text/css" href="static/hbase.css" />

<meta charset="utf-8">
<style>

body {
  font: 10px sans-serif;
}

.axis path,
.axis line {
  fill: none;
  stroke: #000;
  shape-rendering: crispEdges;
}

.x.axis path {
  display: none;
}

.line {
  fill: none;
  stroke: steelblue;
  stroke-width: 3px;
}
</style>

<link href="static/cached_report.css" media="screen" rel="stylesheet"
	type="text/css">
</head>
<body>
	<div class="hd">
	<div class="userHeader clearfix">
	<span>Ella: A Watchdog on HBase</span>
		<div class="logo">
			<a id="logo" href="http://wiki.apache.org/lucene-hadoop/Hbase"><img
			src="static/hbase_logo.png" alt="HBase Logo" title="HBase Logo" />
			</a>
		</div>
	</div>
	</div>

	<br/><br/>


<center><h2>"${tableName}" Requests Graph  @<%=DateFormatUtil.formatToString(new java.util.Date())%></h2></center>


<table class="data-load" width="50%"  height="90%" border="1" cellspacing="0" cellpadding="1" align="center">
<tr>
<td align="center">
<iframe src="write.jsp" width="600" height="350" align="middle" scrolling="auto" frameborder="0"></iframe>
</td>
</tr>

<tr>
<td align="center">
<iframe src="read.jsp" width="600" height="350" align="middle" scrolling="auto" frameborder="0"></iframe>
</td>
</tr>

<tr>
<td align="center">
<iframe src="total.jsp" width="600" height="350" align="middle" scrolling="auto" frameborder="0"></iframe>
</td>
</tr>
</table> 
<jsp:include page="footer.jsp"></jsp:include>
</body>
</html>