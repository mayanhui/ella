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
						<li class="nav-item">
						<span>
						<a href="#mainContainer2">Hot Region监控</a>
						</span></li>

						<li class="nav-item">
						<span>
						<a href="#mainContainer3">Server监控</a>
						</span></li>

						<li class="nav-item">
						<span>
						<a href="#mainContainer4">Zookeeper监控</a>
						</span>
						</li>
						
						<li class="nav-item">
						<span>
						<a href="#">集群属性和统计</a>
						</span>
						</li>
						
						<li class="nav-item">
						<span>
						<a href="#">Task监控</a>
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
									<th>Date</th>
									<th>Revision</th>
									<th>URL</th>
									<th>User</th>
									<th>Version</th>
									
									<th>HdfsDate</th>
									<th>hdfsRev</th>
									<th>hdfsUrl</th>
									<th>hdfsUser</th>
									<th>hdfsVer</th>
								</tr>
							</thead>
							<tbody id="data-list">
									<tr>
										<td>${attr.date}</td>
										<td>${attr.revision}</td>
										<td>${attr.url}</td>
										<td>${attr.user}</td>
										<td>${attr.version}</td>
										
										<td>${attr.hdfsDate}</td>
										<td>${attr.hdfsRevision}</td>
										<td>${attr.hdfsUrl}</td>
										<td>${attr.hdfsUser}</td>
										<td>${attr.hdfsVersion}</td>
									</tr>
							</tbody>
						</table>
					</div>
				</div>
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
									<th>Date</th>
									<th>Revision</th>
									<th>URL</th>
									<th>User</th>
									<th>Version</th>
									
									<th>HdfsDate</th>
									<th>hdfsRev</th>
									<th>hdfsUrl</th>
									<th>hdfsUser</th>
									<th>hdfsVer</th>
								</tr>
							</thead>
							<tbody id="data-list">
									<tr>
										<td>${attr.date}</td>
										<td>${attr.revision}</td>
										<td>${attr.url}</td>
										<td>${attr.user}</td>
										<td>${attr.version}</td>
										
										<td>${attr.hdfsDate}</td>
										<td>${attr.hdfsRevision}</td>
										<td>${attr.hdfsUrl}</td>
										<td>${attr.hdfsUser}</td>
										<td>${attr.hdfsVersion}</td>
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