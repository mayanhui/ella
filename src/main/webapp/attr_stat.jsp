<%@ page language="java" contentType="text/html; charset=UTF-8"
        pageEncoding="UTF-8"%>
        
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page language="java" import="java.util.*"%>
<%@ page language="java" import="com.adintellig.ella.model.*"%>
<%@ page language="java" import="com.adintellig.ella.dao.*"%>
<%@ page language="java" import="com.adintellig.ella.util.*"%>
<%@ page language="java" import="com.adintellig.ella.model.zookeeper.*"%>
<%@ page language="java" import="com.adintellig.ella.hbase.*"%>
<%@ page language="java" import="com.adintellig.ella.hbase.beans.attr.*"%>
<%@ page language="java" import="com.adintellig.ella.hbase.beans.stat.*"%>

<%
		HBaseAttributeBean[] attrArr = JMXHBaseAttrService.getInstance().genBeans().getBeans();
		request.setAttribute("attr",attrArr[0]);
		
		MasterStat[] mstatsArr = JMXMasterStatService.getInstance().genBeans().getBeans();
		request.setAttribute("mstat",mstatsArr[0]);
		
		RPCStat[] rstatArr = JMXRPCStatService.getInstance().genBeans().getBeans();
		request.setAttribute("rstat",rstatArr[0]);
		
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
						
						<li class="nav-item">
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
										<td>Date</td>
										<td>${attr.date}</td>
									</tr>
									<tr>
										<td>Revision</td>
										<td>${attr.revision}</td>
									</tr>
									<tr>
										<td>URL</td>
										<td>${attr.url}</td>
									</tr>
									<tr>
										<td>User</td>
										<td>${attr.user}</td>
									</tr>
									<tr>
										<td>Version</td>
										<td>${attr.version}</td>
									</tr>
									<tr>
										<td>HdfsDate</td>
										<td>${attr.hdfsDate}</td>
									</tr>
									<tr>
										<td>hdfsRev</td>
										<td>${attr.hdfsRevision}</td>
									</tr>
									<tr>
										<td>hdfsUrl</td>
										<td>${attr.hdfsUrl}</td>
									</tr>
									<tr>
										<td>hdfsUser</td>
										<td>${attr.hdfsUser}</td>
									</tr>
									<tr>
										<td>hdfsVer</td>
										<td>${attr.hdfsVersion}</td>
									</tr>
							</tbody>
						</table>
					</div>

					<br/><br/>
					
					<!-- Master Stats -->
					<div class="mod-header radius">
						<h2>
							Master Stats
						</h2>
					</div>
					<div class="mod-body" id="data-load">
						<table class="data-load" width="100%" border="0" cellspacing="0">
							<thead>
								<tr>
									<th>Stats Field</th>
									<th>Value</th>
								</tr>
							</thead>
							<tbody id="data-list">
									<tr>
										<td>modelerType</td>
										<td>${mstat.modelerType}</td>
									</tr>
									<tr>
										<td>splitTimeNumOps</td>
										<td>${mstat.splitTimeNumOps}</td>
									</tr>
									<tr>
										<td>splitTimeAvgTime</td>
										<td>${mstat.splitTimeAvgTime}</td>
									</tr>
									<tr>
										<td>splitTimeMinTime</td>
										<td>${mstat.splitTimeMinTime}</td>
									</tr>
									<tr>
										<td>splitTimeMaxTime</td>
										<td>${mstat.splitTimeMaxTime}</td>
									</tr>
									<tr>
										<td>splitSizeNumOps</td>
										<td>${mstat.splitSizeNumOps}</td>
									</tr>
									<tr>
										<td>splitSizeAvgTime</td>
										<td>${mstat.splitSizeAvgTime}</td>
									</tr>
									<tr>
										<td>splitSizeMinTime</td>
										<td>${mstat.splitSizeMinTime}</td>
									</tr>
									<tr>
										<td>splitSizeMaxTime</td>
										<td>${mstat.splitSizeMaxTime}</td>
									</tr>
									<!--
									<tr>
										<td>cluster_requests</td>
										<td>${mstat.cluster_requests}</td>
									</tr>
									-->
							</tbody>
						</table>
					</div>
					
					
					
					<br/><br/>
					
					<!-- RPC Stats -->
					<div class="mod-header radius">
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