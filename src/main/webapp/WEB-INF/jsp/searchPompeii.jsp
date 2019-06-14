<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Ancient Graffiti Project :: Search by Pompeii Map</title>
<%@ include file="/resources/common_head.txt"%>
<%@ include file="/resources/leaflet_common.txt"%>
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resources/css/details.css" />
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/resources/css/main.css" />
<style>
.info {
    padding: 6px 8px;
    font: 14px/16px Arial, Helvetica, sans-serif;
    background: white;
    background: rgba(255,255,255,0.8);
    box-shadow: 0 0 15px rgba(0,0,0,0.2);
    border-radius: 5px;
}
.info h4 {
    margin: 0 0 5px;
    color: #777;
}
.legend {
    line-height: 18px;
    color: #555;
}
.legend i {
    width: 18px;
    height: 18px;
    float: left;
    margin-right: 8px;
    opacity: 0.7;
}

#propertyPopup{
	display: none;
	position: absolute;
	margin-left: 30%;
	margin-top: 30%;
	width: 275px;
	height: auto;
}

.modal-content{
	background-color: #FFF;
	text-align: center;
	padding: 5px;
	border: 1px solid #FFF;
}
</style>
<script type="text/javascript">
	//function showSelectInsula(){
		//document.getElementById("insulaSelect").style.display = 'inline';
		//document.getElementById("facadeSelect").style.display = 'none';
		//toggleInsulavsFacade();
	//}
	
	//function showSelectFacade(){
		//document.getElementById("facadeSelect").style.display = 'inline';
		//document.getElementById("insulaSelect").style.display = 'none';
		//toggleInsulavsFacade();
	//}
	
	function closePropertyPopup(){
		document.getElementById("propertyPopup").style.display = "none";
	}
	
	//document.getElementsByClassName("close")[0].onclick = function() {
		//document.getElementById("propertyPopup").style.display = "none";
	//}

</script>
</head>
<body>
	<%@include file="header.jsp"%>
	<div class="container">
		<h2>Search Pompeii by Map</h2>
		<p style="text-align: left; margin-bottom: 25px; font-style: italic">
		Special acknowledgements to <a
			href="http://digitalhumanities.umass.edu/pbmp/">Eric Poehler</a> for
		the geospatial data used to create the map of Pompeii.
		</p>Click on one or more areas within the map, then hit the
			"Search" button below.</p>
		<%--for facades 
		<div id="facadeinsula" style="margin-bottom: 10px;">
		<button id="layerSelect" class="btn btn-agp">Toggle</button>
		<button id="insulaSelect" class="btn btn-agp" onclick="showSelectFacade()">Select by Insula</button>
		<button id="facadeSelect" class="btn btn-agp" onclick="showSelectInsula()">Select by Facade</button>
		</div>--%>
		<div id="pompeiimap" class="mapdiv"></div>
		<div id="propertyPopup" class="modal"><div class="modal-content"><span class="close"></span>
		<p>Do you want to select specific <br> properties in this insula?
		</p><button id="yesButton" class="btn btn-agp" onclick="closePropertyPopup()">Select Properties</button>
		<button id="noButton" class="btn btn-agp" onclick="closePropertyPopup()">Select Insula</button></div></div>
		<div id="search_info" style="min-height: 650px; margin-bottom: 50px;">
			<div id="selectionDiv"></div>
			<button id="search" class="btn btn-agp">Search</button>
		</div>
	</div>
	<script>
		window.initPompeiiMap();
		//showSelectFacade();
	</script>
	<footer>
	<%--Uncomment out once the issue where the footer does not recognize the 
	presence of the map and lays behind it --%>
	<%@include file="footer.jsp" %>
	</footer>
</body>
</html>