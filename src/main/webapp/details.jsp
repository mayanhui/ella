<%@ page language="java" contentType="text/html; charset=UTF-8"
        pageEncoding="UTF-8"%>

<%@ page language="java" import="java.util.*"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page language="java" import="com.adintellig.ella.util.*"%>

<%
      String tableName = (String)request.getParameter("tn");
	  request.setAttribute("tableName",tableName);
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

<script src="js/d3/d3.v3.js"></script>
<script>
var margin = {top: 20, right: 90, bottom: 30, left: 90},
    width = 1060 - margin.left - margin.right,
    height = 500 - margin.top - margin.bottom;

var parseDate = d3.time.format("%Y-%m-%dT%H:%M:%SZ").parse;
//var parseDate = d3.time.format("%H:%M:%S").parse;

var x = d3.time.scale()
    .range([0, width]);

var y = d3.scale.linear()
    .range([height, 0]);

var color = d3.scale.category10();

var xAxis = d3.svg.axis()
    .scale(x)
    .orient("bottom");

var yAxis = d3.svg.axis()
    .scale(y)
    .orient("left");

var line = d3.svg.line()
    .interpolate("basis")
    .x(function(d) { return x(d.date); })
    .y(function(d) { return y(d.temperature); });

var svg = d3.select("body").append("div")
	.attr("id","graph")
	.attr("align","center")
    .append("svg")
    .attr("width", width + margin.left + margin.right)
    .attr("height", height + margin.top + margin.bottom)
    .append("g")
    .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

d3.tsv("tsv.do?tableName=${tableName}", function(error, data) {
  color.domain(d3.keys(data[0]).filter(function(key) { return key !== "date"; }));

  data.forEach(function(d) {
    d.date = parseDate(d.date);
  });

  var cities = color.domain().map(function(name) {
    return {
      name: name,
      values: data.map(function(d) {
        return {date: d.date, temperature: +d[name]};
      })
    };
  });

  x.domain(d3.extent(data, function(d) { return d.date; }));

  y.domain([
    d3.min(cities, function(c) { return d3.min(c.values, function(v) { return v.temperature; }); }),
    d3.max(cities, function(c) { return d3.max(c.values, function(v) { return v.temperature; }); })
  ]);

  svg.append("g")
      .attr("class", "x axis")
      .attr("transform", "translate(0," + height + ")")
      .call(xAxis);

  svg.append("g")
      .attr("class", "y axis")
      .call(yAxis)
    .append("text")
      .attr("transform", "rotate(-90)")
      .attr("y", 6)
      .attr("dy", ".71em")
      .style("text-anchor", "end")
      .text("Count Statistics");

  var city = svg.selectAll(".city")
      .data(cities)
    .enter().append("g")
      .attr("class", "city");

  city.append("path")
      .attr("class", "line")
      .attr("d", function(d) { return line(d.values); })
      .style("stroke", function(d) { return color(d.name); });

  city.append("text")
      .datum(function(d) { return {name: d.name, value: d.values[d.values.length - 1]}; })
      .attr("transform", function(d) { return "translate(" + x(d.value.date) + "," + y(d.value.temperature) + ")"; })
      .attr("x", 3)
      .attr("dy", ".35em")
      .text(function(d) { return d.name; });
});

</script>


<jsp:include page="footer.jsp"></jsp:include>
</body>
</html>