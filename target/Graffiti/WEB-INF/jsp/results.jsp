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

/* commented out the top map bc it is obsolete after the standardization of all 
 * headers to be 'header.jsp' */

function generateHeaderMap(name) {
	xmlHttp = new XMLHttpRequest();
	xmlHttp.open("GET",
			"map?clickedRegion="+name+"&city="+"${requestScope.city}", false); 
	xmlHttp.send(null);
	document.getElementById("headerMap").innerHTML = xmlHttp.responseText;
	start();
}

function generateSidebarMap(name2)
{
	xmlHttp = new XMLHttpRequest();
	xmlHttp.open("GET",
			"map?clickedRegion="+name2+"&second=yes"+"&city="+"${requestScope.city}", false);
	xmlHttp.send(null);
	document.getElementById("sidebarMap").innerHTML = xmlHttp.responseText;
	start2();
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
	}
	else {
		// used for ways we can arrive at the results page that are not through filtering.
		// Arrive here from the search by map and browse drawings pages
		// --> could be from the links for a specific location or for a drawing category. 
		refine = false;
		checkAlreadyClicked('${requestScope.checkboxIds}');
		refine = true;
	}
	<c:if test="${requestScope.returnFromEDR} not empty">
	document.getElementById("${requestScope.returnFromEDR}").scrollIntoView();
	</c:if>
	document.getElementById("${requestScope.returnFromEDR}").scrollIntoView();
}
// Functions for displaying and hiding the convention table 

function displayConventionsScroll() { 

	
	if (document.body.scrollTop >= 172  || document.documentElement.scrollTop >= 172) {
		document.getElementById("convention_table").style.display = 'inline';
		document.getElementById("hideConvBtn").style.display = 'inline';
		
		
		// Map Stuff
		document.getElementById("sidebarMap").style.position = 'fixed';
		document.getElementById("sidebarMap").style.margin = '-170px 0px 0px 675px';
		
	
	}else{
		document.getElementById("convention_table").style.display = 'none';
		document.getElementById("hideConvBtn").style.display = 'none';
		document.getElementById("sidebarMap").style.position = 'absolute';
		document.getElementById("sidebarMap").style.margin = '5px 0px 0px 678px';
		document.getElementById("showConvBtn").style.display = 'none';
		document.getElementById("hideConvBtn").style.display = 'none';
		document.getElementById("1").style.display = 'none';
		document.getElementById("2").style.display = 'none';
		document.getElementById("3").style.display = 'none';
		document.getElementById("4").style.display = 'none';
		document.getElementById("5").style.display = 'none';
		document.getElementById("7").style.display = 'none';
		document.getElementById("8").style.display = 'none';
		document.getElementById("9").style.display = 'none';
		document.getElementById("10").style.display ='none';
		
	
	}
}


function displayConventions() { 
	document.getElementById("convention_table").style.display = 'inline';
	document.getElementById("hideConvBtn").style.display = 'inline';
	document.getElementById("showConvBtn").style.display = 'none';
}

function hideConventions() {
	document.getElementById("convention_table").style.display = 'none';
	document.getElementById("hideConvBtn").style.display = 'none';
	document.getElementById("showConvBtn").style.display = 'inline';
	for( x = 1; x < 11; x++ ) {
		document.getElementById(x).style.display = 'none';
	}
}

function displayDefinition(currentDef) {
	for( x = 1; x < 11; x++ ) {
		document.getElementById(x).style.display = 'none';
	}
	document.getElementById(currentDef).style.display = 'inline';
}

/* window.onscroll = function() {displayConventionsScroll()}; */

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

.convention-header {
	float: right;
	margin-left: 540px;
	max-width: 450px;
	margin-top: -70px;
	position: fixed;
	font-size: 12t;
	display: none;
}

.convention_button {
	float: right;
	margin-top: -80px;
	position: fixed;
	color: blue;
	cursor: pointer;
	align: right;
	margin-left: 476px;
	display: none;
}

#convention_table {
	display: none;
	float: right;
	margin-left: 480px;
	max-width: 70px;
	margin-top: -40px;
	position: fixed;
	font-size: 10pt;
}

#convention_table td {
	padding-left: 13.5px;
	cursor: pointer;
	color: blue;
}

#convention_table th {
	text-align: center;
}

.map-override1 {
	position: static
}

.definition {
	float: right;
	margin-left: 670px;
	max-width: 450px;
	margin-top: -50px;
	position: fixed;
	font-size: 11t;
	display: none;
	width: 300px;
	color: maroon;
}

.map-override2 {
	position: fixed;
	margin: 0;
	top: 0;
}
</style>
</head>
<body onload="updatePage();">

	<%@include file="header.jsp"%>

	<div id="contain" class="container" style="margin-bottom: 50px;">

		<%@include file="sidebarSearchMenu.jsp"%>
		<!--  SideBar Map  -->
		<div class="map-override1">
			<div id="headerMap"></div>

			<div id="sidebarMap"></div>
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
	
	<object data=http://www.web-source.net width="300" height="200"> 
	<embed src=http://www.web-source.net width="300" height="200"> 
	</embed> Error: Embedded data could not be displayed. </object>

	<script type="text/javascript">
		if ("${requestScope.city}" != "Herculaneum" && "${requestScope.insula}" != ""){ 
			generateSidebarMap("${requestScope.insula}");
			generateHeaderMap("${requestScope.city}");
		}
		else
			generateSidebarMap("${requestScope.city}");
	</script>
</body>
</html>
