<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<%@include file="../../resources/common_head.txt"%>
<title>Ancient Graffiti Project :: Search Results</title>
<script type="text/javascript"
	src="<c:url value="/resources/js/jquery.imagemapster-1.2.js" />"></script>
<script type="text/javascript"
	src="<c:url value="/resources/js/filterSearch.js"/>"></script>
<script type="text/javascript">

function start() {
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

function generatePompeii(name) {
	xmlHttp = new XMLHttpRequest();
	xmlHttp.open("GET",
			"map?clickedRegion="+name+"&city="+name, false); 
	xmlHttp.send(null);
	document.getElementById("pompeiiCityMap").innerHTML = xmlHttp.responseText;
	start();
}

function generateHerculaneum(name) {
	xmlHttp = new XMLHttpRequest();
	xmlHttp.open("GET",
			"map?clickedRegion="+name+"&second=yes"+"&city="+name, false); 
	xmlHttp.send(null);
	document.getElementById("herculaneumCityMap").innerHTML = xmlHttp.responseText;
	start();
}

function selectImg(ind, k, shortId, longId){
	if (ind == 0){
		document.getElementById("imgLink"+k).href = "http://www.edr-edr.it/edr_programmi/view_img.php?id_nr=" + longId;
		document.getElementById("imgSrc"+k).src = "http://www.edr-edr.it/foto_epigrafi/thumbnails/" + shortId + "/th_" + longId +".jpg";
	}
	else {
		document.getElementById("imgLink"+k).href = "http://www.edr-edr.it/edr_programmi/view_img.php?id_nr=" + longId + "-" + ind;
		document.getElementById("imgSrc"+k).src = "http://www.edr-edr.it/foto_epigrafi/thumbnails/" + shortId + "/th_" + longId + "-" + ind + ".jpg";	
	}
}

function selectImg2(ind, k, page, thumbnail){
	document.getElementById("imgLink"+k).href = page;
	document.getElementById("imgSrc"+k).src = thumbnail;
}

function checkAlreadyClicked(ids){
	idList = ids.split(";");
	for (var i = 0; i < idList.length-1; i++){
		$("#"+idList[i]).click();
	}
}

function updatePage(){
	if ("${sessionScope.requestURL}" != ""){
		requestUrl = "${sessionScope.requestURL}";
		var params = requestUrl.split("?")[1].split("&");
		for (var i in params){
			if (params[i] != "query_all=false"){
				parseParam(params[i]);
			}
		}
		refine=true;
	}
	else {
		// used for ways we can arrive at the results page that are not through filtering.
		// Arrive here from the search by map and browse drawings pages
		// --> could be from the links for a specific location or for a drawing category. 
		refine = false;
		checkAlreadyClicked('${sessionScope.checkboxIds}');
		refine = true;
	}
	<c:if test="${requestScope.returnFromEDR} not empty">
	document.getElementById("${requestScope.returnFromEDR}").scrollIntoView();
	</c:if>
}
</script>
<style>
th {
	vertical-align: top;
	width: 185px;
}

.main-table {
	max-width: 475px;
	min-width: 375px;
}

hr.main-table {
	margin-left: 0px;
}

.scroll_top {
	float: right;
	position: fixed;
	color: white;
	cursor: pointer;
	align: right;
	display: inline;
	margin: 225px 0px 0px 560px;
}

.map-override1 {
	position: static
}
</style>
</head>
<body onload="updatePage();">

	<%@include file="header.jsp"%>

	<div id="contain" class="container" style="margin-bottom: 50px;">

		<%@include file="sidebarSearchMenu.jsp"%>
		<!--  SideBar Map  -->
		<div class="map-override1">
			<div id="pompeiiCityMap"></div>
			<div id="herculaneumCityMap"></div>
		</div>

		<div style="margin-left: 200px;">
			<div style="width: 100%; float: left; padding-bottom: 10px;">
				<div id="searchTerms" style="width: 475px"></div>
			</div>
			<div id="search-results">
				<%@include file="filter.jsp"%>
			</div>
		</div>
	</div>

	<script type="text/javascript">
			generateHerculaneum("Herculaneum");
			generatePompeii("Pompeii");
	</script>
</body>
</html>
