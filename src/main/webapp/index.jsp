<%@ page language="java" contentType="text/html; charset=UTF-8"
        pageEncoding="UTF-8"%>
        
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page language="java" import="java.util.*"%>
<%@ page language="java" import="com.adintellig.ella.mysql.*"%>
<%@ page language="java" import="com.adintellig.ella.model.*"%>

<%
       RequestCountDaoImpl impl = new RequestCountDaoImpl();
	   
	   List<RequestCount> tables = impl.list();
	   request.setAttribute("tables",tables);
	   
	   List<RequestCount> wregions = impl.listWriteHotRegions();
	   request.setAttribute("wregions",wregions);
	   
	   List<RequestCount> rregions = impl.listReadHotRegions();
	   request.setAttribute("rregions",rregions);
	   
	   List<RequestCount> servers = impl.listServers();
	   request.setAttribute("servers",servers);
%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
<title>Ella: HBase Monitor</title>

<link href="static/cached_report.css" media="screen" rel="stylesheet"
	type="text/css">

</head>
<body>
	<!-- HEADER -->
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
	<!-- BODY LEFT -->
	<div class="bd clearfix">
		<div id="leftColContainer">
			<div class="leftCol">
				<div id="siderNav">
					<ul class="nav-items">
						<li class="nav-item item-top current-item on">
						<span>
						<a href="#">表访问量监控 </a>
						</span></li>
						<li class="nav-item ">
						<span>
						<a href="#mainContainer2">Hot Region监控</a>
						</span></li>

						<li class="nav-item ">
						<span>
						<a href="#mainContainer3">Server监控</a>
						</span></li>

						<li class="nav-item ">
						<span>
						<a href="#mainContainer4">Zookeeper监控</a>
						</span></li>
					</ul>
				</div>
			</div>
		</div>

	<!-- BODY RIGHT -->
		<div id="mainContainer">
			<div class="contentCol">

				<div class="mod mod1" id="today_table">
					<div class="mod-header radius">
						<h2>
							今日表数据<a class="icon help poptips" action-frame="tip_todayData"
								title=""></a>
						</h2>
					</div>
					<div class="mod-body" id="data-load">
						<table class="data-load" width="100%" border="0" cellspacing="0">
							<thead>
								<tr>
									<th>Table Name</th>
									<th>Write Count</th>
									<th>Read Count</th>
									<th>Total Count</th>
									<th>Write TPS</th>
									<th>Read TPS</th>
									<th>Total TPS</th>
								</tr>
							</thead>
							<tbody id="data-list">
								<c:forEach var="t" items="${tables}">
									<tr>
										<c:choose>
											<c:when
												test="${t.writeTps > 0 || t.readTps > 0 || t.totalTps > 0}">
												<td><a href="details.jsp?tn=${t.tableName}"><font
														color="Red">${t.tableName}</font>
												</a>
												</td>
											</c:when>
											<c:otherwise>
												<td><a href="details.jsp?tn=${t.tableName}">${t.tableName}</a>
												</td>
											</c:otherwise>
										</c:choose>
										<td>${t.writeCount}</td>
										<td>${t.readCount}</td>
										<td>${t.totalCount}</td>
										<c:choose>
											<c:when test="${t.writeTps > 0 }">
												<td><font color="Red">${t.writeTps}</font>
												</td>
											</c:when>
											<c:otherwise>
												<td>${t.writeTps}</td>
											</c:otherwise>
										</c:choose>
										<c:choose>
											<c:when test="${t.readTps > 0 }">
												<td><font color="Red">${t.readTps}</font>
												</td>
											</c:when>
											<c:otherwise>
												<td>${t.readTps}</td>
											</c:otherwise>
										</c:choose>
										<c:choose>
											<c:when test="${t.totalTps > 0 }">
												<td><font color="Red">${t.totalTps}</font>
												</td>
											</c:when>
											<c:otherwise>
												<td>${t.totalTps}</td>
											</c:otherwise>
										</c:choose>
									</tr>
								</c:forEach>
							</tbody>
						</table>
						<font color="Red"><span>The red tables are being accessed!</span></font>
						<div class="wait-load" style="display: none;">
							<img src="/images/pic/ajax-loader.gif">
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<!-- region -->
	<div class="bd clearfix">
		<div id="leftColContainer">
			<div class="leftCol">
				<div id="siderNav">
			</div>
		</div>
	</div>
	
	<div id="mainContainer2">
			<div class="contentCol">

				<div class="mod mod1" id="today_table">
					<div class="mod-header radius">
						<h2>
							今日Hot Region数据<a class="icon help poptips" action-frame="tip_todayData"
								title=""></a>
						</h2>
					</div>
					<div class="mod-body" id="data-load">
						<table class="data-load" width="100%" border="0" cellspacing="0">
							<thead>
								<tr>
									<th>Region Name(写)</th>
									<th>Write Count</th>
								</tr>
							</thead>
							<tbody id="data-list">
								<c:forEach var="w" items="${wregions}">
									<tr>
										<td><a href="#">${w.regionName}</a></td>
										<td>${w.writeCount}</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
						
						<table class="data-load" width="100%" border="0" cellspacing="0">
							<thead>
								<tr>
									<th>Region Name(读)</th>
									<th>Read Count</th>
								</tr>
							</thead>
							<tbody id="data-list">
								<c:forEach var="r" items="${rregions}">
									<tr>
										<td><a href="#">${r.regionName}</a></td>
										<td>${r.readCount}</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
						<div class="wait-load" style="display: none;">
							<img src="/images/pic/ajax-loader.gif">
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<!-- server -->
	<div class="bd clearfix">
		<div id="leftColContainer">
			<div class="leftCol">
				<div id="siderNav">
			</div>
		</div>
	</div>
	
	<div id="mainContainer3">
			<div class="contentCol">

				<div class="mod mod1" id="today_table">
					<div class="mod-header radius">
						<h2>
							今日Server数据<a class="icon help poptips" action-frame="tip_todayData"
								title=""></a>
						</h2>
					</div>
					<div class="mod-body" id="data-load">
						<table class="data-load" width="100%" border="0" cellspacing="0">
							<thead>
								<tr>
									<th>Server Name</th>
									<th>Write Count</th>
									<th>Read Count</th>
									<th>Total Count</th>
									<th>Write TPS</th>
									<th>Read TPS</th>
									<th>Total TPS</th>
								</tr>
							</thead>
							<tbody id="data-list">
								<c:forEach var="s" items="${servers}">
									<tr>
										<c:choose>
											<c:when
												test="${s.writeTps > 0 || s.readTps > 0 || s.totalTps > 0}">
												<td><a href="#"><font
														color="Red">${s.serverHost}</font>
												</a>
												</td>
											</c:when>
											<c:otherwise>
												<td><a href="#">${t.serverHost}</a>
												</td>
											</c:otherwise>
										</c:choose>
										<td>${s.writeCount}</td>
										<td>${s.readCount}</td>
										<td>${s.totalCount}</td>
										<c:choose>
											<c:when test="${s.writeTps > 0 }">
												<td><font color="Red">${s.writeTps}</font>
												</td>
											</c:when>
											<c:otherwise>
												<td>${s.writeTps}</td>
											</c:otherwise>
										</c:choose>
										<c:choose>
											<c:when test="${s.readTps > 0}">
												<td><font color="Red">${s.readTps}</font>
												</td>
											</c:when>
											<c:otherwise>
												<td>${s.readTps}</td>
											</c:otherwise>
										</c:choose>
										<c:choose>
											<c:when test="${s.totalTps > 0}">
												<td><font color="Red">${s.totalTps}</font>
												</td>
											</c:when>
											<c:otherwise>
												<td>${s.totalTps}</td>
											</c:otherwise>
										</c:choose>
									</tr>
								</c:forEach>
							</tbody>
						</table>
						<div class="wait-load" style="display: none;">
							<img src="/images/pic/ajax-loader.gif">
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<!-- FOOTER -->
<jsp:include page="footer.jsp"></jsp:include>
</body>
</html>