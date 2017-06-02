<%@ page language="java" contentType="text/html; charset=US-ASCII"
	pageEncoding="US-ASCII"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>Ancient Graffiti Project :: Property Info</title>

<!-- this is the stuff for leaflet map -->
<link rel="stylesheet" href="https://npmcdn.com/leaflet@1.0.0-rc.2/dist/leaflet.css" />
<link rel="stylesheet" href="<%=request.getContextPath() %>/resources/css/main.css" />
<script type="text/javascript"
	src="<c:url value="/resources/js/updatedEschebach.js"/>"></script>

<%@include file="/resources/common_head.txt"%>
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resources/css/details.css" />
</head>
<body>
<!-- this script is also for leaflet -->
<script src="https://npmcdn.com/leaflet@1.0.0-rc.2/dist/leaflet.js"></script>

	<%@include file="/WEB-INF/jsp/header.jsp"%>
	<div class="container">


		<h1>Property</h1>

		<div class="button_bar">
			<a
				href="<%=request.getContextPath() %>/property/${prop.insula.city.name}/${prop.insula.shortName}/${prop.propertyNumber }/json"
				id="json"><button class="btn btn-agp right-align">Download
					JSON Data</button></a>
		</div>

		<table class="property-table table-striped">
			<tr>
				<th class="propertyLabel">City:</th>
				<td><a
					href="http://pleiades.stoa.org/places/${prop.insula.city.pleiadesId}">${prop.insula.modernCity}</a></td>
			</tr>
			<tr>
				<th class="propertyLabel">Insula:</th>
				<td>${prop.insula.fullName}</td>
			</tr>
			<tr>
				<th class="propertyLabel">Property Number:</th>
				<td>${prop.propertyNumber}</td>
			</tr>
			<tr>
				<th class="propertyLabel">Property Name<br />(in English):
				</th>
				<td>${prop.propertyName}</td>
			</tr>
			<c:if test="${not empty prop.italianPropertyName}">
				<tr>
					<th class="propertyLabel">Property Name<br />(in Italian):
					</th>
					<td>${prop.italianPropertyName}</td>
				</tr>
			</c:if>

			<c:if test="${not empty prop.pleiadesId}">
				<tr>
					<th class="propertyLabel">Pleiades ID:</th>
					<td>${prop.pleiadesId }</td>
				</tr>
			</c:if>
			<tr>
				<th class="propertyLabel">Property Type:</th>
				<td><c:forEach var="k" begin="${1}"
						end="${fn:length(prop.propertyTypes)}">
						<c:set var="pt" value="${prop.propertyTypes[k-1]}" />
						${pt.name} <!-- link to the vocab info about this property type -->
				</c:forEach></td>
			</tr>
			<c:if test="{not empty prop.commentary}">
				<tr>
					<th class="propertyLabel">Commentary:</th>
					<td>${prop.commentary }</td>
				</tr>
			</c:if>
			<tr>
				<th class="propertyLabel">Related Info:</th>
				<td>
				<!-- 
					<ul>
						<li> -->
						<a
							href="<%=request.getContextPath()%>/results?property=${ prop.id}">Graffiti
								in this property</a></li>
								<!-- 
						<li>PBMP Property Info</li>
						<li>Pleiades Link for this property</li>
						 -->
				<!--  	</ul> -->
				</td>
			</tr>
		</table>
	<!-- 	<div id="pompeiimap" class="propertymapdiv"></div>  -->
	</div>
	
	<!-- 
	<script type="text/javascript"
		src="<c:url value="/resources/js/pompeiiMap.js"/>">
	</script>
	<script>
		window.initmap();
	</script>
	 -->

</body>
</html>