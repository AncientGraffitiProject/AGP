<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<%@include file="/resources/common_head.txt"%>
<title>Ancient Graffiti Project :: Report</title>

<script type="text/javascript"
	src='<c:url value="/resources/js/excellentExport.js"/>'></script>

<style type="text/css">
table, th, td {
	border: 1px solid black;
	border-collapse: collapse;
	padding: 10px;
}

th {
	text-align: center;
}
</style>

</head>
<body>

	<%@include file="/WEB-INF/jsp/header.jsp"%>

	<div id="contain" class="container">

		<c:set var="num" value="${fn:length(requestScope.resultsLyst)}" />
		<span style="width: 475px;"> <c:out
				value="${num} results ${searchQueryDesc}" />
		</span>
		<c:if test="${num == 0}">
			<br />
			<c:out value="Try broadening your search" />
		</c:if>

		<div>
			<h1>Report on Graffiti</h1>
			<a style="color: rgb(51, 51, 51);" download="AGP-report.xls" href="#"
				onclick="return ExcellentExport.excel(this, 'reportTable', 'Worksheet');"><button class="btn btn-agp">Export
					to Excel</button></a>
		</div>

		<br>

		<div id="dvData">
			<table id="reportTable">
				<tr>
					<th class="prop">AGP ID</th>
					<th class="prop">CIL #</th>
					<th class="prop">Langner</th>
					<th class="prop">City</th>
					<th class="prop">Location</th>
					<th class="prop">Description</th>
					<th class="prop">Category</th>
					<th class="prop">Language</th>
					<th class="prop">Writing Style</th>
					<th class="prop">Graffito Text</th>
					<th class="prop">Bibliography</th>
					<th></th>
				</tr>
				<c:forEach var="k" begin="${1}"
					end="${fn:length(requestScope.resultsLyst)}">
					<c:set var="i" value="${requestScope.resultsLyst[k-1]}" />
					<tr>
						<!-- AGP ID -->
						<td><a
							href="<%=request.getContextPath() %>/graffito/AGP-${i.edrId}">AGP-${i.edrId}</a></td>
						<!-- CIL # -->
						<td>${i.agp.cil}</td>
						<!-- Langner # -->
						<td>${i.agp.langner}</td>
						<!-- City -->
						<td><a
							href="http://pleiades.stoa.org/places/${i.agp.property.insula.city.pleiadesId}">${i.ancientCity}</a></td>
						<!-- Location -->
						<td>${i.agp.property.propertyName}&nbsp;(<a
							href="<%=request.getContextPath() %>/results?city=${i.agp.property.insula.city.name}&property=${i.agp.property.id}">${i.agp.property.insula.shortName}.${i.agp.property.propertyNumber}</a>)
						</td>
						<!-- Summary -->
						<td>${i.agp.summary }</td>
						<!-- Category -->
						<c:choose>
							<c:when test="${i.agp.figuralInfo.getDrawingTags().size() > 0}">
								<td><c:forEach var="dt"
										items="${i.agp.figuralInfo.getDrawingTags()}"
										varStatus="loopStatus">
										<a
											href="<%=request.getContextPath() %>/results?drawing=${dt.id}">${dt.name}</a>
										<c:if test="${!loopStatus.last}">, </c:if>
									</c:forEach></td>
							</c:when>
							<c:otherwise>
								<td></td>
							</c:otherwise>
						</c:choose>
						<!-- Language -->
						<td>${i.language}</td>
						<!-- Writing Style -->
						<td>${i.writingStyle}</td>
						<!-- Graffito Text -->
						<td style="text-align: center;">${i.contentWithLineBreaks}</td>

						<!-- Bibliography -->
						<td>${i.bibliography}</td>

						<td><form
								action="<%=request.getContextPath()%>/admin/updateGraffito">
								<input class="btn btn-agp" type=submit value="Edit Graffito" />
								<input type="hidden" name="edrID" value="${i.edrId}" />
							</form></td>
					</tr>
				</c:forEach>
			</table>
		</div>
	</div>
</body>
</html>