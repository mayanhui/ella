<%@ page language="java" contentType="text/html; charset=UTF-8"
        pageEncoding="UTF-8"%>
        
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page language="java" import="java.util.*"%>
<%@ page language="java" import="com.adintellig.ella.model.*"%>
<%@ page language="java" import="com.adintellig.ella.mysql.*"%>
<%@ page language="java" import="com.adintellig.ella.util.*"%>
<%@ page language="java" import="com.adintellig.ella.model.zookeeper.*"%>
<%@ page language="java" import="com.adintellig.ella.hbase.*"%>
<%@ page language="java" import="com.adintellig.ella.hbase.beans.attr.*"%>


<%
       RequestCountDaoImpl impl = new RequestCountDaoImpl();
	   
	   List<RequestCount> tables = impl.list();
	   request.setAttribute("tables",tables);
	   
	   request.setAttribute("wtps",RequestCountUtil.sumTps(tables,"WRITE"));
	   request.setAttribute("rtps",RequestCountUtil.sumTps(tables,"READ"));
	   request.setAttribute("ttps",RequestCountUtil.sumTps(tables,"TOTAL"));
	   
	   List<RequestCount> wregions = impl.listWriteHotRegions();
	   request.setAttribute("wregions",wregions);
	   
	   List<RequestCount> rregions = impl.listReadHotRegions();
	   request.setAttribute("rregions",rregions);
	   
	   List<RequestCount> servers = impl.listServers();
	   request.setAttribute("servers",servers);
	   
	   Base base = HBaseUtil.dumpZookeeperInfo();
	   request.setAttribute("base",base);
	   request.setAttribute("regionserver",StringUtil.replaceSlashNToBr(base.getRegionServers()));
	   
		JMXHBaseAttrService attr = JMXHBaseAttrService.getInstance();
		HBaseAttributeBeans beans = attr.getBeans();
		HBaseAttributeBean[] beanArr = beans.getBeans();
		request.setAttribute("attr",beanArr[0]);
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
						</span>
						</li>
						
						<li class="nav-item ">
						<span>
						<a href="#">集群属性</a>
						</span>
						</li>
						
						<li class="nav-item ">
						<span>
						<a href="#">Task监控</a>
						</span>
						</li>
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
							HBase Cluster Attibutes @<%=DateFormatUtil.formatToString(new java.util.Date())%>
						</h2>
					</div>
					<div class="mod-body" id="data-load">
						
							<table class="data-load" width="100%" border="0" cellspacing="0">
							<thead>
								<tr>
									<th>Show All Monitored Tasks</th>
									<th>Show non-RPC Tasks</th>
									<th>Show All RPC Handler Tasks</th>
									<th> Show Active RPC Calls</th>
									<th> Show Client Operations</th>
								</tr>
							</thead>
							<tbody id="data-list">
								<tr>
									<td colspan="6"><HR style="FILTER: alpha(opacity=100,finishopacity=0,style=3)" width="100%" color="#d0d0d0" SIZE="3"></td>
								</tr>
							</tbody>
						</table>
						
						<table class="data-load" width="100%" border="0" cellspacing="0">
							<thead>
								<tr>
									<th>Start Time</th>
									<th>Description</th>
									<th>State</th>
									<th>Status</th>
								</tr>
							</thead>
							<tbody id="data-list">
									<tr>
										<td>${attr.hbaseVersion}</td>
										<td>${attr.hbaseRootDir}</td>
									</tr>
								<c:forEach var="t" items="${tasks}">
									<tr>
										<td>${t.startTime}</td>
										<td>${t.description}</td>
										<td>${t.state}</td>
										<td>${t.status}</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
						<font color="Red" style="font-weight:bold;"><span>The red tables are being accessed!</span></font>
						<div class="wait-load" style="display: none;">
							<img src="/images/pic/ajax-loader.gif">
						</div>
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