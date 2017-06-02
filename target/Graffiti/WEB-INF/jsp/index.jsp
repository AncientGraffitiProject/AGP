<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Ancient Graffiti Project</title>
<%@include file="../../resources/common_head.txt"%>

<style>
.fluid-img {
	margin-right: auto;
	margin-left: auto;
	max-height: 325px;
	max-width: 100%;
	width: auto;
	border: 3px solid black;
}

.leftcol {
	float: left;
	width: 50%;
	margin-bottom: 25px;
}

.rightcol {
	float: right;
	width: 50%;
	margin-bottom: 25px;
}

h3 {
	text-align: center;
}

@media only screen and (max-width: 1023px) {
	[class*="col"] {
		width: 100%;
	}
}
</style>

</head>
<body>
	<%@include file="header.jsp"%>
	<div class="container">
		<%--Check for error message informing user of invalid city name or inscription id --%>
		<c:if test="${not empty requestScope.error}">
			<p style="color: red;">${requestScope.error}
			<p />
		</c:if>
		<p style="font-size: 16px; text-align: center;">Welcome to The
			Ancient Graffiti Project, a digital resource for locating and
			studying graffiti of the early Roman empire from the cities of
			Pompeii and Herculaneum. More than 500 ancient graffiti are now
			available here, ca. 300 from Herculaneum and another 200 from Pompeii
			(from the Lupanar, Insula I.8, and other locations). Entries for the
			Herculaneum graffiti now include photographs from our fieldwork in
			2016.</p>
	</div>
	<!-- <h2 style="text-align:center;">Click on a map to search</h2> -->
	<div style="max-width: 1100px; float: center; margin: auto;">
		<div class="leftcol">
			<h3>Herculaneum</h3>
			<a href="search?city=Herculaneum"><img class="fluid-img"
				src="<%=request.getContextPath()%>/resources/images/Herculaneum.jpg"
				onmouseover="this.src='<%=request.getContextPath()%>/resources/images/exploreHerculaneum.jpg'"
				onmouseout="this.src='<%=request.getContextPath()%>/resources/images/Herculaneum.jpg'" /></a>
		</div>

		<div class="rightcol">
			<h3>Pompeii</h3>
			<a href="search?city=Pompeii"><img class="fluid-img"
				src="<%=request.getContextPath()%>/resources/images/Pompeii.jpg"
				onmouseover="this.src='<%=request.getContextPath()%>/resources/images/explorePompeii.jpg'"
				onmouseout="this.src='<%=request.getContextPath()%>/resources/images/Pompeii.jpg'" /></a>
		</div>
	</div>
	<p style="text-align: center;">
		Special acknowledgements to <a
			href="http://digitalhumanities.umass.edu/pbmp/">Eric Poehler</a> for
		the map of Pompeii and the creators of <a
			href="http://www.outsharked.com/imagemapster/">ImageMapster</a> for
		the highlighting map feature.
	</p>
</body>
</html>
