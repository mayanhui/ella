<%@ page language="java" contentType="text/html; charset=UTF-8"
        pageEncoding="UTF-8"%>
        
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page language="java" import="java.util.*"%>
<%@ page language="java" import="com.adintellig.ella.model.*"%>
<%@ page language="java" import="com.adintellig.ella.dao.*"%>
<%@ page language="java" import="com.adintellig.ella.util.*"%>
<%@ page language="java" import="com.adintellig.ella.model.zookeeper.*"%>
<%@ page language="java" import="com.adintellig.ella.model.jmxbeans.*"%>
<%@ page language="java" import="com.adintellig.ella.hbase.handler.*"%>

<%
		LiveRegionServerBean[] attrArr = ((LiveRegionServerBeans)JMXHMasterHandler.getInstance().handle()).getBeans();
		request.setAttribute("attr",attrArr[0]);
		
		//RPCStat[] rstatArr = JMXRPCStatService.getInstance().genBeans().getBeans();
		//request.setAttribute("rstat",rstatArr[0]);
		
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
						<li class="nav-item">
						<span>
						<a href="index.jsp">表访问量监控 </a>
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
						
						<li class="nav-item item-top current-item on">
						<span>
						<a href="attr_stat.jsp">集群属性和统计</a>
						</span>
						</li>
						
					</ul>
				</div>
			</div>
		</div>

	<!-- Attibute -->
		<div id="mainContainer">
			<div class="contentCol">
				<div class="mod mod1" id="today_table">
					<div class="mod-header radius">
						<h2>
							HBase Cluster Attibutes @<%=DateFormatUtil.formatToString(new java.util.Date())%>
						</h2>
					</div>
					<div class="mod-body" id="data-load">
						<table class="data-load" width="100%" border="0" cellspacing="0">
							<thead>
								<tr>
									<th>Attribute Field</th>
									<th>Value</th>
								</tr>
							</thead>
							<tbody id="data-list">
									<tr>
										<td>Start Time</td>
										<td>${attr.masterStartTime}</td>
									</tr>
									<tr>
										<td>Server Name</td>
										<td>${attr.serverName}</td>
									</tr>
									<tr>
										<td>liveRegionServers</td>
										<td>${attr.liveRegionServers}</td>
									</tr>
									<tr>
										<td>zookeeperQuorum</td>
										<td>${attr.zookeeperQuorum}</td>
									</tr>
									<tr>
										<td>clusterId</td>
										<td>${attr.clusterId}</td>
									</tr>
									<tr>
										<td>averageLoad</td>
										<td>${attr.averageLoad}</td>
									</tr>
									<tr>
										<td>numRegionServers</td>
										<td>${attr.numRegionServers}</td>
									</tr>
									<tr>
										<td>clusterRequests</td>
										<td>${attr.clusterRequests}</td>
									</tr>
									<tr>
										<td>isActiveMaster</td>
										<td>${attr.isActiveMaster}</td>
									</tr>
									<tr>
										<td>deadRegionServers</td>
										<td>${attr.deadRegionServers}</td>
									</tr>
							</tbody>
						</table>
					</div>

					<br/><br/>
					
					<!-- RPC Stats -->
					<!-- <div class="mod-header radius">
						<h2>
							RPC Stats
						</h2>
					</div>
					<div class="mod-body" id="data-load">
						<table class="data-load" width="100%" border="0" cellspacing="0">
							<thead>
								<tr>
									<th>Stat Field</th>
									<th>Value</th>
								</tr>
							</thead>
							<tbody id="data-list">
									<tr>
										<td>modelerType</td>
										<td>${rstat.modelerType}</td>
									</tr>
									<tr>
										<td>deleteTableMinTime</td>
										<td>${rstat.deleteTableMinTime}</td>
									</tr>
									<tr>
										<td>deleteTableMaxTime</td>
										<td>${rstat.deleteTableMaxTime}</td>
									</tr>
									<tr>
										<td>getHTableDescriptorsMinTime</td>
										<td>${rstat.getHTableDescriptorsMinTime}</td>
									</tr>
									<tr>
										<td>getHTableDescriptorsMaxTime</td>
										<td>${rstat.getHTableDescriptorsMaxTime}</td>
									</tr>
									<tr>
										<td>disableTableMinTime</td>
										<td>${rstat.disableTableMinTime}</td>
									</tr>
									<tr>
										<td>disableTableMaxTime</td>
										<td>${rstat.disableTableMaxTime}</td>
									</tr>
									<tr>
										<td>createTableMinTime</td>
										<td>${rstat.createTableMinTime}</td>
									</tr>
									<tr>
										<td>createTableMaxTime</td>
										<td>${rstat.createTableMaxTime}</td>
									</tr>
									<tr>
										<td>rpcProcessingTimeMinTime</td>
										<td>${rstat.rpcProcessingTimeMinTime}</td>
									</tr>
									<tr>
										<td>rpcProcessingTimeMaxTime</td>
										<td>${rstat.rpcProcessingTimeMaxTime}</td>
									</tr>
									<tr>
										<td>regionServerStartupMinTime</td>
										<td>${rstat.regionServerStartupMinTime}</td>
									</tr>
									<tr>
										<td>regionServerStartupMaxTime</td>
										<td>${rstat.regionServerStartupMaxTime}</td>
									</tr>
									<tr>
										<td>rpcQueueTimeMinTime</td>
										<td>${rstat.rpcQueueTimeMinTime}</td>
									</tr>
									<tr>
										<td>rpcQueueTimeMaxTime</td>
										<td>${rstat.rpcQueueTimeMaxTime}</td>
									</tr>
									<tr>
										<td>regionServerReportMinTime</td>
										<td>${rstat.regionServerReportMinTime}</td>
									</tr>
									<tr>
										<td>regionServerReportMaxTime</td>
										<td>${rstat.regionServerReportMaxTime}</td>
									</tr>
							</tbody>
						</table>
					</div>
					 -->
					
					
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