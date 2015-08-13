<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page import="java.util.*"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link href="<c:url value="/resources/css/proj.css" />" type="text/css"
	rel="stylesheet">
<title>Ancient Graffiti Project :: Search Results</title>
<%@include file="../../resources/common_head.txt"%>
<script type="text/javascript"
	src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.js"></script>
<script type="text/javascript"
	src="<c:url value="/resources/js/jquery.imagemapster.js" />"></script>

</head>

<body>

	<div class="container">
		<p>
			<c:out value="${fn:length(requestScope.resultsLyst)} results found" />
		</p>
		<c:forEach var="k" begin="${1}"
			end="${fn:length(requestScope.resultsLyst)}">
			<c:set var="i" value="${requestScope.resultsLyst[k-1]}" />
			<h4>
				<c:out value="Result ${k}" />
			</h4>
			<ul>
				<li><span class="prop">City:</span> <a
					href="http://pleiades.stoa.org/places/433032">${i.ancientCity}</a></li>
				<li><span class="prop">Findspot:</span>
					${i.findSpot}</li>
				<li><span class="prop">CIL Number:</span> ${i.bibliography}</li>
				<c:choose>
					<c:when test="${not empty i.contentWithLineBreaks}">
						<li><span class="prop">Content:</span><br />
							${i.contentWithLineBreaks}</li>
					</c:when>
				</c:choose>
				<li><span class="prop">For more information, see Eagle
						ID: </span> <a
					href="http://www.edr-edr.it/edr_programmi/visualizza.php?id_nr=${i.eagleId}">#${i.eagleId}</a></li>
				<c:if test="${i.getDrawingTags().size() > 0}">
					<c:choose>
						<c:when test="${not empty i.url}">
							<li><a href="${i.url}"><img class="thumbnail"
									src="http://www.edr-edr.it/foto_epigrafi/thumbnails/${fn:substring(i.eagleId, 3, 6)}/th_${i.eagleId.substring(3)}.jpg" /></a></li>

						</c:when>
						<c:otherwise>
							<li><img class="thumbnail"
								src="<c:url value="/resources/images/NoImg.gif" />" width=100
								height=auto></li>
						</c:otherwise>
					</c:choose>
					<li><span class="prop">Drawing Tag Name(s):</span> <c:forEach
							var="dt" items="${i.getDrawingTags()}" varStatus="loopStatus">
							<c:out value="${dt.name}"></c:out>
							<c:if test="${!loopStatus.last}">, </c:if>
						</c:forEach></li>
					<li><span class="prop">Drawing Description:</span>
						${i.getAgp().getDescription()}</li>
				</c:if>
			</ul>
		</c:forEach>
	</div>
</body>

</html>
