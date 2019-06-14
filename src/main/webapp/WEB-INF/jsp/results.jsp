<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Ancient Graffiti Project :: Search Results</title>
<script type="text/javascript"
	src="<c:url value="/resources/js/filterSearch.js"/>"></script>
<%@ include file="/resources/common_head.txt"%>
<%@ include file="/resources/leaflet_common.txt"%>
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/resources/css/main.css" />
<%@ page import="java.util.*"%>

<script type="text/javascript">
var locationKeys; 

function setLocationKeys(){
	<%List<Integer> locationKeys = (List<Integer>) request.getAttribute("findLocationKeys");
			if (locationKeys == null) {
				locationKeys = new ArrayList();
			}
		List<Integer> insulaLocations = (List<Integer>) request.getAttribute("insulaLocationKeys");
			if( insulaLocations == null ) {
				insulaLocations = new ArrayList();
			}
			locationKeys.addAll(insulaLocations);
			%>
	locationKeys = <%=locationKeys%>;
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

function printResults() {
	var labels = document.getElementsByClassName("search-term-label");
	newUrl = createURL("print");
	window.open(newUrl, '_blank');
}

function checkAlreadyClicked(ids){
	idList = ids.split(";");
	for (var i = 0; i < idList.length-1; i++){
		$("#"+idList[i]).click();
	}
}

function checkboxesAfterBack() {
	contentsUrl = window.location.href;
	if(contentsUrl.split("?")[1]) {
		var params = contentsUrl.split("?")[1].split("&");
		
		var dict = {
				"drawing_category" : "dc",
				"property" : "p",
				"property_type" : "pt",
				"insula" : "i",
				"city" : "c",
				"writing_style" : "ws",
				"language" : "l"
		};
		
		var cities = {
				"Herculaneum" : 0,
				"Pompeii" : 1
		};
		
		var writingStyle = {
			"Graffito/incised" : 1,
			"charcoal" : 2,
			"other" : 3
		};

		var languages = {
			"Latin" : 1,
			"Greek" : 2,
			"Latin/Greek" : 3,
			"other" : 4
		};
		
		for (var i in params){
			if (params[i] != "query_all=false"){
				var param = params[i];
				var term = param.split("=");
				var type = term[0];
				var value = term[1];
				if (type == "drawing_category" && value == "All") {
					value = 0;
				}
				if (type in dict) {
					var typeToken = dict[type];
					// convert the human-readable description into IDs for checkboxes
					if (typeToken == "ws") {
						value = writingStyle[value];
					} else if (typeToken == "c") {
						value = cities[value];
					} else if (typeToken == "l") {
						value = languages[value];
					} else if (typeToken == "dc" && value == 0) {
						// do nothing if All is selected
						var locationKeys = "<%=locationKeys%>";} else {
							value = value.replace("_", " ");
						}
						var id = typeToken + value;
						//type = type.replace("_", " ");
						$("#" + id).click();
					} else if (type == "content") {
						addSearchTerm("Content", value, value);
					} else if (type == "cil"){
						addSearchTerm("CIL", value, value);
					}
					else if (type == "global") {
						addSearchTerm("Global", value, value);
					}
				}
			}
		}
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

#scroll_top {
	float: right;
	position: fixed;
	color: white;
	cursor: pointer;
	/*align: right;*/
	display: inline;
	margin: 500px 0px 0px 715px;
	z-index: 1;
}

#herculaneummap, #pompeiimap{
	z-index: 2;
}

.btn-agp {
	margin-bottom: 10px;
}

ul#searchTerms li {
	display: inline-block;
	margin: 0 0 7px 0;
}
</style>
</head>
<body>
	<%@include file="header.jsp"%>
	<div id="contain" class="container" style="margin-bottom: 50px; min-height: 750px;">
		<%@include file="sidebarSearchMenu.jsp"%>
		<!--  Right Column -->
		<div>
			<div id="herculaneummap" class="searchResultsHerculaneum"></div>
			<div id="pompeiimap" class="searchResultsPompeii"></div>
			
			<a href="#top" id="scroll_top" class="btn btn-agp">Return To Top</a>
			
			<%--<a href="#top">
				<button id="scroll_top" class="btn btn-agp">Return To Top</button>
			</a> --%>
		</div>

		<div style="margin-left: 200px;">
			<div style="width: 480px;">
				<ul id="searchTerms" style="width: 525px; margin-left: -40px;"></ul>
			</div>
			<div id="search-results">
				<%@include file="filter.jsp"%>
			</div>
		</div>
	</div>

	<script type="text/javascript"
		src="<c:url value="/resources/js/pompeiiMap.js"/>"></script>
	<script type="text/javascript"
		src="<c:url value="/resources/js/herculaneumMap.js"/>"></script>

	<script>
		setLocationKeys();
		//Apparently, these need to be used in same order as they are in div. 
		window.initHerculaneumMap(true, false, false, false, 0, locationKeys);
		window.initPompeiiMap("regio", false, false, false, 0, locationKeys);
	</script>
	<%@include file="footer.jsp" %>
</body>
</html>