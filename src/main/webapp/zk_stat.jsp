<%@ page language="java" contentType="text/html; charset=UTF-8"
        pageEncoding="UTF-8"%>
        
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page language="java" import="java.util.*"%>
<%@ page language="java" import="com.adintellig.ella.model.*"%>
<%@ page language="java" import="com.adintellig.ella.dao.*"%>
<%@ page language="java" import="com.adintellig.ella.model.zookeeper.*"%>
<%@ page language="java" import="com.adintellig.ella.util.*"%>

<%
	   Base base = HBaseUtil.dumpZookeeperInfo();
	   request.setAttribute("base",base);
	   request.setAttribute("regionserver",StringUtil.replaceSlashNToBr(base.getRegionServers()));
		
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
						<a href="index.jsp">表访问量监控</a>
						</span></li>
						<li class="nav-item">
						<span>
						<a href="rs_stat.jsp">Hot Region监控</a>
						</span></li>

						<li class="nav-item">
						<span>
						<a href="server_stat.jsp">Server监控</a>
						</span></li>

						<li class="nav-item">
						<span>
						<a href="zk_stat.jsp">Zookeeper监控</a>
						</span>
						</li>
						
						<li class="nav-item">
						<span>
						<a href="attr_stat.jsp">集群属性和统计</a>
						</span>
						</li>
						
					</ul>
				</div>
			</div>
		</div>

	<!-- zk -->
		<div id="mainContainer4">
			<div class="contentCol">
				<div class="mod mod1" id="today_table">
					<div class="mod-header radius">
						<h2>
							今日zookeeper数据<a class="icon help poptips" action-frame="tip_todayData"
								title=""></a>
						</h2>
					</div>
					<div class="mod-body" id="data-load">
						<table class="data-load" width="100%" border="0" cellspacing="0">
							<thead>
								<tr>
									<th>HDFS</th>
									<th>Master Address</th>
									<th>Backup Master Addresses</th>
									<th>Region server holding ROOT</th>
									<th>Region Servers</th>
								</tr>
							</thead>
							<tbody id="data-list">
									<tr>
										<td>${base.hdfsRoot}</td>
										<td>${base.masterAddress}</td>
										<td>${base.backupMasterAddress}</td>
										<td>${base.regionServerHoldingRoot}</td>
										<td>${regionserver})</td>
									</tr>
							</tbody>
						</table>
						
						<table class="data-load" width="100%" border="0" cellspacing="0">
							<thead>
								<tr>
									<th>Quorum Host</th>
									<th>Verion</th>
									<!--<th>Latency min</th>-->
									<!--<th>Latency avg</th>-->
									<th>Latency max</th>
									<th>Received</th>
									<th>Sent</th>
									<!--<th>Outstanding</th>-->
									<th>Zxid</th>
									<th>Mode</th>
									<th>Node count</th>
								</tr>
							</thead>
							<tbody id="data-list">
							<c:forEach var="quorum" items="${base.quorums}">
								<tr>
									<td>${quorum.host}</td>
									<td>${quorum.version}</td>
									<!--<td>${quorum.latencyMin}</td>-->
									<!--<td>${quorum.latencyAvg}</td>-->
									<td>${quorum.latencyMax}</td>
									<td>${quorum.received}</td>
									<td>${quorum.sent}</td>
									<!--<td>${quorum.outstanding}</td>-->
									<td>${quorum.zxid}</td>
									<td style="font-weight:bold;">${quorum.mode}</td>
									<td>${quorum.nodeCount}</td>
								</tr>
							</c:forEach>
							</tbody>
						</table>
						
						<table class="data-load" width="100%" border="0" cellspacing="0">
							<thead>
								<tr>
									<th>Quorum Host</th>
									<th>Host(Only External IPs)</th>
									<th>Tag</th>
									<th>Queued</th>
									<th>Recved</th>
									<th>Sent</th>
								</tr>
							</thead>
							<tbody id="data-list">
							<c:forEach var="quorum" items="${base.quorums}">
								<c:forEach var="client" items="${quorum.clients}">
								<tr>
									<td>${quorum.host}</td>
									<td style="font-weight:bold;">${client.host}</td>
									<td>${client.tag}</td>
									<td>${client.queued}</td>
									<td>${client.recved}</td>
									<td>${client.sent}</td>
								</tr>
								</c:forEach>
								<tr>
									<td colspan="6"><HR style="FILTER: alpha(opacity=100,finishopacity=0,style=3)" width="100%" color="#d0d0d0" SIZE="3"></td>
								</tr>
							</c:forEach>
							</tbody>
						</table>
						
					</div>
				</div>
			</div>
		</div>
	
	
<!--	
<div style="left:178px;display:block;" class="back-to" id="toolBackTop">
	<a title="返回顶部" onclick="window.scrollTo(0,0);return false;" href="#top" class="back-top">返回顶部</a>
</div>
<style>
.back-to {bottom: 35px;overflow:hidden;position:fixed;right:10px;width:50px;z-index:999;}
.back-to .back-top {background: url("static/back-top.png") no-repeat scroll 0 0 transparent;display: block;float: right;height:50px;margin-left: 10px;outline: 0 none;text-indent: -9999em;width: 50px;}
.back-to .back-top:hover {background-position: -50px 0;}
</style>
<script src="js/jquery.min.js"></script> 
<script type="text/javascript">
$(document).ready(function () {
        var bt = $('#toolBackTop');
        var sw = $(document.body)[0].clientWidth;

        var limitsw = (sw - 1060) / 2 - 40;
        if (limitsw > 0){
                limitsw = parseInt(limitsw);
                bt.css("right",limitsw);
        }

        $(window).scroll(function() {
                var st = $(window).scrollTop();
                if(st > 30){
                        bt.show();
                }else{
                        bt.hide();
                }
        });
})
</script>
-->

	<!-- FOOTER -->
<jsp:include page="footer.jsp"></jsp:include>
</body>
</html>