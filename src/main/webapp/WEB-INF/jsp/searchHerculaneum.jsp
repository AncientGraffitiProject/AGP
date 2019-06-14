<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Ancient Graffiti Project :: Herculaneum Map</title>
<%@ include file="/resources/common_head.txt"%>
<%@ include file="/resources/leaflet_common.txt"%>
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resources/css/details.css" />
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/resources/css/main.css" />
<style>
.info {
    padding: 6px 8px;
    font: 14px/16px Arial, Helvetica, sans-serif;
    background: white;
    background: rgba(255,255,255,0.8);
    box-shadow: 0 0 15px rgba(0,0,0,0.2);
    border-radius: 5px;
}
.info h4 {
    margin: 0 0 5px;
    color: #777;
}
.legend {
    line-height: 18px;
    color: #555;
}
.legend i {
    width: 18px;
    height: 18px;
    float: left;
    margin-right: 8px;
    opacity: 0.7;
}
</style>
</head>
<body>
	<%@include file="header.jsp"%>
	<div class="container">
		<h2>Search Herculaneum by Map</h2>
		<p>Click on one or more properties within the map, then hit the
			"Search" button.</p>
		<div id="herculaneummap" class="mapdiv"></div>
		<div id="search_info" style="min-height: 650px; margin-bottom: 50px;">
			<div id="toSearch"></div>
			<button id="search" class="btn btn-agp">Search</button>
		</div>
	</div>
	<script>
		window.initHerculaneumMap();
	</script>
	
	<%--Uncomment out once the issue where the footer does not recognize the 
	presence of the map and lays behind it --%>
	<%@include file="footer.jsp" %>
</body>
</html>