<%-- <%@page import="edu.wlu.graffiti.dao.DrawingTagsDao"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.*"%>
<%@ page import="edu.wlu.graffiti.bean.DrawingTag"%>
<%@ page import="edu.wlu.graffiti.bean.PropertyType"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Ancient Graffiti Project :: Select City</title>
<%@include file="../../resources/common_head.txt"%>
<link rel="stylesheet" type="text/css"
	href="<c:url value="/resources/css/chosen.css" />" />
<script type="text/javascript"
	src="<c:url value="/resources/js/jquery.imagemapster-1.2.js" />"></script>
<script type="text/javascript"
	src="<c:url value="/resources/js/chosen.jquery.js" />"></script>

<style>
.fluid-img {
	margin-right: auto;
	margin-left: auto;
	max-height: 400px;
	max-width: 100%;
	width: auto;
	border: 3px solid black;
}
.leftcol {
	margin-top: 15px;
	float: left;
	width: 50%;
	margin-bottom: 15px;
}
.rightcol {
	margin-top: 15px;
	float: right;
	width: 50%;
	margin-bottom: 15px;
}
h3 {
	text-align: center;
}
@media only screen and (max-width: 850px){
	[class*="col"]{
		width: 100%;
	}
}
</style>
</head>


<body>

	<%@include file="header.jsp"%>
	
	<h2 style="text-align:center;">Click on a map to search</h2>
	
	<div class="leftcol">
		<a href="search?city=Pompeii"><img class="fluid-img" src="<%=request.getContextPath() %>/resources/images/Pompeii.jpg"/></a>
		<h3>Pompeii</h3>
	</div>
	
	<div class="rightcol">
		<a href="search?city=Herculaneum"><img class="fluid-img" src="<%=request.getContextPath() %>/resources/images/Herculaneum.jpg"/></a>
		<h3>Herculaneum</h3>
	</div>

</body>
</html> --%>