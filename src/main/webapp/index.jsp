<%@ page language="java" contentType="text/html; charset=UTF-8"
        pageEncoding="UTF-8"%>
        
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page language="java" import="java.util.*"%>
<%@ page language="java" import="com.adintellig.ella.model.*"%>
<%@ page language="java" import="com.adintellig.ella.dao.*"%>
<%@ page language="java" import="com.adintellig.ella.util.*"%>
<%@ page language="java" import="com.adintellig.ella.service.*"%>

<%
	TableService service = new TableService();

	List<RequestCount> tables = service.list();
	request.setAttribute("tables",tables);
	   
	request.setAttribute("wtps",RequestCountUtil.sumTps(tables,"WRITE"));
	request.setAttribute("rtps",RequestCountUtil.sumTps(tables,"READ"));
	request.setAttribute("ttps",RequestCountUtil.sumTps(tables,"TOTAL"));
	   
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
						<li class="nav-item ">
						<span>
						<a href="rs_stat.jsp">Hot Region监控</a>
						</span></li>

						<li class="nav-item ">
						<span>
						<a href="server_stat.jsp">Server监控</a>
						</span></li>

						<li class="nav-item ">
						<span>
						<a href="zk_stat.jsp">Zookeeper监控</a>
						</span>
						</li>
						
						<li class="nav-item ">
						<span>
						<a href="attr_stat.jsp">集群属性和统计</a>
						</span>
						</li>
						
						<!--<li class="nav-item ">
						<span>
						<a href="task.jsp">Task监控</a>
						</span>
						</li>
						-->
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
							今日表数据 @<%=DateFormatUtil.formatToString(new java.util.Date())%>
							<!--<a class="icon help poptips" action-frame="tip_todayData"
								title=""></a>-->
						</h2>
					</div>
					<div class="mod-body" id="data-load">
						<table class="data-load" width="100%" border="0" cellspacing="0">
							<thead>
								<tr>
									<th>Table Name</th>
									<th>Number Of Regions</th>
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
												<td style="font-weight:bold;"><a href="details.jsp?tn=${t.tableName}"><font
														color="Red">${t.tableName}</font>
												</a>
												</td>
											</c:when>
											<c:otherwise>
												<td><a href="details.jsp?tn=${t.tableName}">${t.tableName}</a>
												</td>
											</c:otherwise>
										</c:choose>
										<td>${t.regionCount}</td>
										<td>${t.writeCount}</td>
										<td>${t.readCount}</td>
										<td>${t.totalCount}</td>
										<c:choose>
											<c:when test="${t.writeTps > 0 }">
												<td style="font-weight:bold;"><font color="Red">${t.writeTps}</font>
												</td>
											</c:when>
											<c:otherwise>
												<td>${t.writeTps}</td>
											</c:otherwise>
										</c:choose>
										<c:choose>
											<c:when test="${t.readTps > 0 }">
												<td style="font-weight:bold;"><font color="Red">${t.readTps}</font>
												</td>
											</c:when>
											<c:otherwise>
												<td>${t.readTps}</td>
											</c:otherwise>
										</c:choose>
										<c:choose>
											<c:when test="${t.totalTps > 0 }">
												<td style="font-weight:bold;"><font color="Red">${t.totalTps}</font>
												</td>
											</c:when>
											<c:otherwise>
												<td>${t.totalTps}</td>
											</c:otherwise>
										</c:choose>
									</tr>
								</c:forEach>
								<tr>
										<td style="font-weight:bold;">[TOTAL]</td>
										<td>--</td>
										<td>--</td>
										<td>--</td>
										<td>--</td>
										<td style="font-weight:bold;"><font color="Red">${wtps}</font></td>
										<td style="font-weight:bold;"><font color="Red">${rtps}</font></td>
										<td style="font-weight:bold;"><font color="Red">${ttps}</font></td>
									</tr>
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