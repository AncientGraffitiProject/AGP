<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html>
<head>

<meta charset="UTF-8">
<title>Ancient Graffiti Project</title>
<!--    Fix responsive navbar-->
<%@include file="/resources/common_head.txt"%>
<link rel="stylesheet" type="text/css" href="resources/css/greatestHits.css">
<meta name="viewport" content="width=device-width, initial-scale=1">
<!-- Certain JS and styles taken from NinjaSlider, available from http://www.menucool.com/responsive-slider -->


<style>
@media only screen and (max-width: 1023px) {
	[class*="col"] {
		width: 100%;
	}

</style>
<!--  Function to hide the gallery on load -->
<script> 
$(document).ready(function(){
	$("#gallery").hide();
});
</script>

</head>
<body onload="showOriginal">

<div id="blackOverlay" class="blackOverlay" ></div>
	<%@include file="header.jsp"%>

	<div class="container">
		<h2>Featured Graffiti</h2>

		<div id="selectors">
			<input type="button" id="showOriginal"
				value="Inscription Translations" class="btn btn-agp"> 
			<input
				type="button" id="showGallery" value="Figural Graffiti Gallery"
				class="btn btn-agp">
		</div>

		<c:set var="figuralHits" value="${figuralHits}" />
		<c:set var="translationHits" value="${translationHits}" />

		<%@include file="translation_greatest_hits.jsp" %>
		<%@include file="featured_figural_hits.jsp" %>

	</div>

	<!-- JS for modal view, toggling, show translations-->
	<script type="text/javascript" src="resources/js/greatestHits.js"></script>
</body>
</html>