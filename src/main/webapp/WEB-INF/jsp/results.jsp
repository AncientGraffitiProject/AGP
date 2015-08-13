<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page import="java.util.*"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<%@include file="../../resources/common_head.txt"%>
<title>Ancient Graffiti Project :: Search Results</title>
<script type="text/javascript"
	src="<c:url value="/resources/js/jquery.imagemapster-1.2.js" />"></script>
<script type="text/javascript">
var mapName = "Pompeii"; 
function start()
{
$('img').mapster({
	areas: [
	        
		<c:forEach var="locKey" items="${requestScope.findLocationKeys}">
		{
			key: '${locKey}',
			fillColor: '0000FF',
			staticState: true
		},
		</c:forEach>
	], 
	isSelectable: false,
	mapKey: 'data-key',
	clickNavigate: false,
}); 
}

var mapName2 = "I.8"; 
function start2()
{
$('img').mapster({
	areas: [
	        
		<c:forEach var="locKey" items="${requestScope.findLocationKeys}">
		{
			key: '${locKey}',
			fillColor: '0000FF',
			staticState: true
		},
		</c:forEach>
	], 
	isSelectable: false,
	mapKey: 'data-key',
	clickNavigate: false,		
	}); 
}

function generateHeaderMap(name) {
	xmlHttp = new XMLHttpRequest();
	xmlHttp.open("GET",
			"map?clickedRegion="+name, false);
	xmlHttp.send(null);
	document.getElementById("headerMap").innerHTML = xmlHttp.responseText;
	mapName = name;
	start();
}

function generateSidebarMap(name2)
{
	xmlHttp = new XMLHttpRequest();
	xmlHttp.open("GET",
			"map?clickedRegion="+name2+"&second=yes", false);
	xmlHttp.send(null);
	document.getElementById("sidebarMap").innerHTML = xmlHttp.responseText;
	start2();
}
</script>
</head>
<body>

	<%@include file="results_header.jsp"%>

	<div class="container">
		<div id="sidebarMap"></div>
		<p>
			<c:out value="${fn:length(requestScope.resultsLyst)} results found" />
			<c:out value="${searchQueryDesc}" />
		</p>

		<c:forEach var="k" begin="${1}"
			end="${fn:length(requestScope.resultsLyst)}">
			<c:set var="i" value="${requestScope.resultsLyst[k-1]}" />
			<h4>
				<c:out value="Result ${k}" />
			</h4>
			<ul>
				<li><span class="prop">City:</span> <a
					href="http://pleiades.stoa.org/places/${i.pleidesID}">${i.ancientCity}</a></li>
				<li><span class="prop">Findspot:</span> ${i.findSpot}</li>
				<li><span class="prop">CIL:</span> ${i.bibliography}</li>
				<c:choose>
					<c:when test="${not empty i.contentWithLineBreaks}">
						<li><span class="prop">Content:</span><br />
							${i.contentWithLineBreaks}</li>
					</c:when>
				</c:choose>
				<li><span class="prop">For more information, see EAGLE
						ID: </span> <a
					href="http://www.edr-edr.it/edr_programmi/visualizza.php?id_nr=${i.eagleId}">#${i.eagleId}</a></li>
				<c:if test="${i.getDrawingTags().size() > 0}">
					<c:choose>
						<c:when test="${not empty i.url}">
							<li><a target="_blank" href="http://www.edr-edr.it/edr_programmi/view_img.php?id_nr=${i.eagleId.substring(3)}"><img class="thumbnail"
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
							<a href="results?drawing=${dt.id}"><c:out value="${dt.name}"/></a>
							<c:if test="${!loopStatus.last}">, </c:if>
						</c:forEach></li>
					<!-- 
					<li><span class="prop">Drawing Tag Description(s):</span> <c:forEach
							var="dt" items="${i.getDrawingTags()}">
							<c:out value="${dt.description}"></c:out>
							<c:if test="${!loopStatus.last}">, </c:if>
						</c:forEach></li> -->
					<li><span class="prop">Drawing Description:</span>
						${i.getAgp().getDescription()}</li>
				</c:if>
				<!--  <li><span class="prop">Click for Editing: </span> <a
					href="updateTest?EDR=${i.eagleId}">EDIT</a></li>-->
			</ul>
		</c:forEach>
	</div>

	<script type="text/javascript">
		generateHeaderMap("Pompeii");
		generateSidebarMap("I.8");
	</script>


</body>
</html>
