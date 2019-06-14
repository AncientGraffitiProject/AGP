<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html lang="en">
<head>

<meta charset="UTF-8">
<title>Ancient Graffiti Project</title>
<!--    Fix responsive navbar-->
<%@include file="/resources/common_head.txt"%>
<link rel="stylesheet" type="text/css"
	href="<c:url value="/resources/css/featured_graffiti.css"/>">
<meta name="viewport" content="width=device-width, initial-scale=1">
<style>
@media only screen and (max-width: 1023px) {
	[class*="col"] {
		width: 100%;
	}
}
</style>

</head>
<body>
	
	<div id="blackOverlay" class="blackOverlay"></div>
	<%@include file="header.jsp"%>

	<div class="container">

		<h2>Figural Graffiti</h2>

		<c:set var="figuralHits" value="${figuralHits}" />
		
		<%@include file="featured_figural_hits.jsp"%>
	</div>

	<script type="text/javascript" src="<c:url value="/resources/js/greatestHits.js"/>"></script>
	<%@include file="footer.jsp" %>
</body>
</html>