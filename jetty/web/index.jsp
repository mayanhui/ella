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
%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head><meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
<title>HBase Master: hbase-master,60000,1363167283031</title>
<link rel="stylesheet" type="text/css" href="static/hbase.css" />
</head>
<body>
<a id="logo" href="http://wiki.apache.org/lucene-hadoop/Hbase"><img src="static/hbase_logo.png" alt="HBase Logo" title="HBase Logo" /></a>

<h1 id="page_title">HBase Monitor</h1>

<p id="links_menu">
  <a href="/logs/">Local logs</a>,
  <a href="/stacks">Thread Dump</a>,
  <a href="/logLevel">Log Level</a>,
  <a href="/dump">Debug dump</a>,

</p>

<!-- Various warnings that cluster admins should be aware of -->

<hr id="head_rule" />
<h2>Requests</h2>
<table id="requests_table">
<tr><th>Table Name</th><th>Write Count</th><th>Read Count</th><th>Total Count</th></tr>

<c:forEach var="t" items="${tables}"> 
        <tr> 
          <td> <a href="details.jsp?tn=${t.tableName}">${t.tableName}</a></td>
          <td>${t.writeCount}</td>
          <td>${t.readCount}</td>
          <td>${t.totalCount}</td>  
        </tr> 
</c:forEach> 

</table>


<hr id="head_rule" />
<h2>Attributes</h2>
<table id="attributes_table">
<tr><th>Attribute Name</th><th>Value</th><th>Description</th></tr>
<tr><td>HBase Version</td><td>0.94.0, r1332822</td><td>HBase version and revision</td></tr>
<tr><td>HBase Compiled</td><td>Tue May  1 21:43:54 UTC 2012, jenkins</td><td>When HBase version was compiled and by whom</td></tr>
<tr><td>Hadoop Version</td><td>1.0.2, r1304954</td><td>Hadoop version and revision</td></tr>
<tr><td>Hadoop Compiled</td><td>Tue Mar 27 16:24:17 UTC 2012, hortonfo</td><td>When Hadoop version was compiled and by whom</td></tr>
<tr><td>HBase Root Directory</td><td>hdfs://hbase-master:9000/hbase</td><td>Location of HBase home directory</td></tr>
<tr><td>HBase Cluster ID</td><td>4eb70fe9-98c2-4c79-a7b4-38bfebd7c926<td>Unique identifier generated for each HBase cluster</td></tr>
<tr><td>Load average</td><td>43</td><td>Average number of regions per regionserver. Naive computation.</td></tr>

<tr><td>Zookeeper Quorum</td><td>hbase-regionserver-63:2181,hbase-regionserver-64:2181,hbase-regionserver-62:2181</td><td>Addresses of all registered ZK servers. For more, see <a href="/zk.jsp">zk dump</a>.</td></tr>
<tr>
  <td>
   Coprocessors</td><td>[]
  </td>
  <td>Coprocessors currently loaded loaded by the master</td>
</tr>
<tr><td>HMaster Start Time</td><td>Wed Mar 13 17:34:43 CST 2013</td><td>Date stamp of when this HMaster was started</td></tr>
<tr><td>HMaster Active Time</td><td>Wed Mar 13 17:34:43 CST 2013</td><td>Date stamp of when this HMaster became active</td></tr>
</table>


<h2>Tasks</h2>
  <div id="tasks_menu">
    <a href="?filter=all">Show All Monitored Tasks</a>
    <a href="?filter=general">Show non-RPC Tasks</a>
    <a href="?filter=handler">Show All RPC Handler Tasks</a>
    <a href="?filter=rpc">Show Active RPC Calls</a>
    <a href="?filter=operation">Show Client Operations</a>
    <a href="?format=json&filter=general">View as JSON</a>
  </div>
  
    No tasks currently running on this node.


<h2>Tables</h2>
<table>
<tr>
    <th>Catalog Table</th>
    
    <th>Description</th>
</tr>
<tr>
    <td><a href="table.jsp?name=-ROOT-">-ROOT-</a></td>
  
    <td>The -ROOT- table holds references to all .META. regions.</td>
</tr>
  
<tr>
    <td><a href="table.jsp?name=.META.">.META.</a></td>
    
    <td>The .META. table holds references to all User Table regions</td>
</tr>
  
  
</table>




<table>
<tr>
    <th>User Table</th>

    <th>Description</th>
</tr>

<tr>
    <td><a href=table.jsp?name=demo_index>demo_index</a> </td>
    
    <td>{NAME =&gt; 'demo_index', FAMILIES =&gt; [{NAME =&gt; 'cf1', BLOOMFILTER =&gt; 'ROW', VERSIONS =&gt; '2147483647', COMPRESSION =&gt; 'LZO'}, {NAME =&gt; 'cf2', BLOOMFILTER =&gt; 'ROW', VERSIONS =&gt; '2147483647', COMPRESSION =&gt; 'LZO'}]}</td>
</tr>

<tr>
    <td><a href=table.jsp?name=demo_test>demo_test</a> </td>
    
    <td>{NAME =&gt; 'demo_test', FAMILIES =&gt; [{NAME =&gt; 'attr', BLOOMFILTER =&gt; 'ROW', COMPRESSION =&gt; 'LZO', VERSIONS =&gt; '2147483647'}, {NAME =&gt; 'bhvr', BLOOMFILTER =&gt; 'ROW', COMPRESSION =&gt; 'LZO', VERSIONS =&gt; '2147483647'}, {NAME =&gt; 'ecf1', BLOOMFILTER =&gt; 'ROW', COMPRESSION =&gt; 'LZO', VERSIONS =&gt; '2147483647'}, {NAME =&gt; 'ecf2', BLOOMFILTER =&gt; 'ROW', COMPRESSION =&gt; 'LZO', VERSIONS =&gt; '2147483647'}]}</td>
</tr>

<tr>
    <td><a href=table.jsp?name=movie_fav_user_list>movie_fav_user_list</a> </td>
    
    <td>{NAME =&gt; 'movie_fav_user_list', FAMILIES =&gt; [{NAME =&gt; 'cf1', BLOOMFILTER =&gt; 'ROW', VERSIONS =&gt; '2147483647', COMPRESSION =&gt; 'LZO'}, {NAME =&gt; 'detail', BLOOMFILTER =&gt; 'ROW', VERSIONS =&gt; '2147483647', COMPRESSION =&gt; 'LZO'}]}</td>
</tr>

<tr>
    <td><a href=table.jsp?name=movie_fav_visitor_list>movie_fav_visitor_list</a> </td>
    
    <td>{NAME =&gt; 'movie_fav_visitor_list', FAMILIES =&gt; [{NAME =&gt; 'cf1', BLOOMFILTER =&gt; 'ROW', VERSIONS =&gt; '2147483647', COMPRESSION =&gt; 'LZO'}, {NAME =&gt; 'detail', BLOOMFILTER =&gt; 'ROW', VERSIONS =&gt; '2147483647', COMPRESSION =&gt; 'LZO'}]}</td>
</tr>

<tr>
    <td><a href=table.jsp?name=tsdb>tsdb</a> </td>
    
    <td>{NAME =&gt; 'tsdb', FAMILIES =&gt; [{NAME =&gt; 't', BLOOMFILTER =&gt; 'ROW', VERSIONS =&gt; '1', COMPRESSION =&gt; 'LZO'}]}</td>
</tr>

<tr>
    <td><a href=table.jsp?name=tsdb-uid>tsdb-uid</a> </td>
    
    <td>{NAME =&gt; 'tsdb-uid', FAMILIES =&gt; [{NAME =&gt; 'id', COMPRESSION =&gt; 'LZO'}, {NAME =&gt; 'name', COMPRESSION =&gt; 'LZO'}]}</td>
</tr>

<tr>
    <td><a href=table.jsp?name=user_behavior_attribute>user_behavior_attribute</a> </td>
    
    <td>{NAME =&gt; 'user_behavior_attribute', FAMILIES =&gt; [{NAME =&gt; 'attr', BLOOMFILTER =&gt; 'ROW', VERSIONS =&gt; '2147483647', COMPRESSION =&gt; 'LZO'}, {NAME =&gt; 'bhvr', BLOOMFILTER =&gt; 'ROW', VERSIONS =&gt; '2147483647', COMPRESSION =&gt; 'LZO'}, {NAME =&gt; 'ecf1', BLOOMFILTER =&gt; 'ROW', VERSIONS =&gt; '2147483647', COMPRESSION =&gt; 'LZO'}, {NAME =&gt; 'ecf2', BLOOMFILTER =&gt; 'ROW', VERSIONS =&gt; '2147483647', COMPRESSION =&gt; 'LZO'}]}</td>
</tr>

<tr>
    <td><a href=table.jsp?name=user_behavior_attribute_noregistered>user_behavior_attribute_noregistered</a> </td>
    
    <td>{NAME =&gt; 'user_behavior_attribute_noregistered', FAMILIES =&gt; [{NAME =&gt; 'attr', BLOOMFILTER =&gt; 'ROW', VERSIONS =&gt; '2147483647', COMPRESSION =&gt; 'LZO'}, {NAME =&gt; 'bhvr', BLOOMFILTER =&gt; 'ROW', VERSIONS =&gt; '2147483647', COMPRESSION =&gt; 'LZO'}, {NAME =&gt; 'ecf1', BLOOMFILTER =&gt; 'ROW', VERSIONS =&gt; '2147483647', COMPRESSION =&gt; 'LZO'}, {NAME =&gt; 'ecf2', BLOOMFILTER =&gt; 'ROW', VERSIONS =&gt; '2147483647', COMPRESSION =&gt; 'LZO'}]}</td>
</tr>

<tr>
    <td><a href=table.jsp?name=user_behavior_attribute_noregistered_index>user_behavior_attribute_noregistered_index</a> </td>
    
    <td>{NAME =&gt; 'user_behavior_attribute_noregistered_index', FAMILIES =&gt; [{NAME =&gt; 'cf1', BLOOMFILTER =&gt; 'ROW', VERSIONS =&gt; '2147483647'}, {NAME =&gt; 'cf2', BLOOMFILTER =&gt; 'ROW', VERSIONS =&gt; '2147483647'}]}</td>
</tr>

<tr>
    <td><a href=table.jsp?name=user_behavior_attribute_noregistered_mid_uid_index>user_behavior_attribute_noregistered_mid_uid_index</a> </td>
    
    <td>{NAME =&gt; 'user_behavior_attribute_noregistered_mid_uid_index', FAMILIES =&gt; [{NAME =&gt; 'cf1', BLOOMFILTER =&gt; 'ROW', VERSIONS =&gt; '2147483647', COMPRESSION =&gt; 'LZO'}]}</td>
</tr>

<tr>
    <td><a href=table.jsp?name=user_message>user_message</a> </td>
    
    <td>{NAME =&gt; 'user_message', FAMILIES =&gt; [{NAME =&gt; 'm', TTL =&gt; '-1', BLOCKCACHE =&gt; 'false'}]}</td>
</tr>

<tr>
    <td><a href=table.jsp?name=user_message_noregistered>user_message_noregistered</a> </td>
    
    <td>{NAME =&gt; 'user_message_noregistered', FAMILIES =&gt; [{NAME =&gt; 'm', TTL =&gt; '-1', BLOCKCACHE =&gt; 'false'}]}</td>
</tr>


<p> 12 table(s) in set. [<a href=tablesDetailed.jsp>Details</a>]</p>
</table>




<h2>Region Servers</h2>

<table>
<tr><th rowspan="5"></th><th>ServerName</th><th>Start time</th><th>Load</th></tr>
<tr><td><a href="http://hbase-regionserver-62:60030/">hbase-regionserver-62,60020,1366192369522</a></td><td>Wed Apr 17 17:52:49 CST 2013</td><td>requestsPerSecond=114, numberOfOnlineRegions=45, usedHeapMB=8591, maxHeapMB=16367</td></tr>
<tr><td><a href="http://hbase-regionserver-63:60030/">hbase-regionserver-63,60020,1366192188804</a></td><td>Wed Apr 17 17:49:48 CST 2013</td><td>requestsPerSecond=176, numberOfOnlineRegions=48, usedHeapMB=10952, maxHeapMB=16367</td></tr>
<tr><td><a href="http://hbase-regionserver-64:60030/">hbase-regionserver-64,60020,1366272553270</a></td><td>Thu Apr 18 16:09:13 CST 2013</td><td>requestsPerSecond=169, numberOfOnlineRegions=39, usedHeapMB=6543, maxHeapMB=16357</td></tr>
<tr><td><a href="http://hbase-regionserver-65:60030/">hbase-regionserver-65,60020,1366272640967</a></td><td>Thu Apr 18 16:10:40 CST 2013</td><td>requestsPerSecond=151, numberOfOnlineRegions=40, usedHeapMB=6283, maxHeapMB=16367</td></tr>
<tr><th>Total: </th><td>servers: 4</td><td></td><td>requestsPerSecond=610, numberOfOnlineRegions=172</td></tr>
</table>

<p>Load is requests per second and count of regions loaded</p>




<h3>Dead Region Servers</h3>




<h2>Regions in Transition</h2>

No regions in transition.


</body>
</html>


