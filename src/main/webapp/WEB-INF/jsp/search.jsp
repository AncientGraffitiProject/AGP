<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Ancient Graffiti Project :: Search ${requestScope.city}</title>
<%@include file="../../resources/common_head.txt"%>
<link rel="stylesheet" type="text/css"
	href="<c:url value="/resources/css/chosen.css" />" />
<script type="text/javascript"
	src="<c:url value="/resources/js/jquery.imagemapster-1.2.js" />"></script>
<script type="text/javascript"
	src="<c:url value="/resources/js/chosen.jquery.js" />"></script>
</head>

<body>

	<%@include file="header.jsp"%>

	<div class="container">
		<h2>Search by Map</h2>

		<div id="map">
			<p>${requestScope.message}</p>
			<img id="theMap" src="${requestScope.displayImage}" class="mapper" usemap="#regionmap">
			<map name="regionmap">
				<c:forEach var="k" begin="${1}" end="${fn:length(requestScope.coords)}">
					<area shape="poly" data-key="${requestScope.regionIds[k-1]}"
						coords="${requestScope.coords[k-1]}" href="#" title="${requestScope.regionNames[k-1]}"/>
				</c:forEach>
			</map>
		</div>
		<button id="mapsearch" class="btn btn-agp" onclick="DoSubmit();"
			style="visibility: hidden;">Search</button>
		<p style="color:red;visibility:hidden;" id="hidden_p">You must select at least one property.</p>
		<hr />
	</div>

	<script type="text/javascript">
		function DoSubmit() {
			var argString = "city=" + "${requestScope.city}" + "&";
			if (highlighted.length > 0){
				for (var i = 0; i < highlighted.length; i++) {
					argString = argString + "property=" + highlighted[i];
					argString = argString + "&";
				}
	
				window.location = "results?" + argString;
				return true;
			}
			else {
				document.getElementById("hidden_p").style.visibility = "visible";
			}
		};
	</script>

	<script type="text/javascript">
		var mapName = "${requestScope.city}";
		var highlighted = new Array();
		var locName;
		function start() {
			$('img')
					.mapster(
							{
								mapKey : 'data-key',
								clickNavigate : false,
								onClick : function(data) {
									if (mapName == "Title"
											|| mapName == "Pompeii"){
										xmlHttp = new XMLHttpRequest();
										xmlHttp.open("GET",
												"map?city=Pompeii&clickedRegion=" + data.key.substring(1)
														+ "&search=yes", false);
										xmlHttp.send(null);
										document.getElementById("map").innerHTML = xmlHttp.responseText;
										mapName = data.key.substring(1);
										start();
									} else {
										document.getElementById("mapsearch").style.visibility = "visible";
										if (!isNaN(data.key.substring(1))){ // number-->is a property_id-->property is in the database and should be added as location parameter
											var index = highlighted
													.indexOf(data.key.substring(1));
											if (index == -1){
												highlighted.push(data.key.substring(1));
												document.getElementById("hidden_p").style.visibility = "hidden";
											}
											else
												highlighted.splice(index, 1);
										}
									}
								}
							});
		}

		start();
	</script>
</body>
</html>