<%@page import="edu.wlu.graffiti.dao.DrawingTagsDao"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.*"%>
<%@ page import="edu.wlu.graffiti.bean.DrawingTag"%>
<%@ page import="edu.wlu.graffiti.bean.PropertyType"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Ancient Graffiti Project :: Search</title>
<%@include file="../../resources/common_head.txt"%>
<link rel="stylesheet" type="text/css"
	href="<c:url value="/resources/css/chosen.css" />" />
<script type="text/javascript"
	src="<c:url value="/resources/js/jquery.imagemapster-1.2.js" />"></script>
<script type="text/javascript"
	src="<c:url value="/resources/js/chosen.jquery.js" />"></script>
<script>
	function generateSearchMap(name) {
		xmlHttp = new XMLHttpRequest();
		xmlHttp.open("GET", "map?clickedRegion=" + name + "&search=yes", false);
		xmlHttp.send(null);
		document.getElementById("map").innerHTML = xmlHttp.responseText;
		start();
	}
</script>
</head>

<%
	List<DrawingTag> drawingTags = (List<DrawingTag>)request.getAttribute("drawingTags");
	List<PropertyType> propertyTypes = (List<PropertyType>)request.getAttribute("propertyTypes");

	// this should be set at some point by the application--maybe when user picks a city
// to search?
//String whichMap = "Herculaneum";
	String whichMap = "Pompeii";
%>
<body onload="generateSearchMap('<%=whichMap%>')">

	<style>
.OR {
	color: blue;
	padding: 10px 5px;
	font-style: italic;
}
</style>
	<script type="text/javascript">
		
	<%ArrayList<String> regions=(ArrayList)request.getAttribute("regions");%>
		var varieties = [
	<%for(int x=0;x<regions.size();x++){%>
		[
	<%=regions.get(x)%>
		],
	<%}%>
		];

		function addSelect() {
			/*
			var box = document.getElementsByName("location");
			if (box[0] != null) {
				box[0].parentNode.removeChild(box[0]);
			}*/
			
			var sel = document.getElementById("property_names");
			sel.options.length = 0;
			
			var location = document.search.loc;
			var ind = location.selectedIndex;

			if (varieties[ind].length > 0) {
				// create 2nd select
				/*
				try { // IE

					var sel = document
							.createElement("<select class=\"chosen-select\" multiple name=\"location\"><\/select>");
				} catch (e) {
					var sel = document.createElement("select");
					sel.name = "location";
					sel.setAttribute("class", "chosen-select");
					sel.multiple = true;
				}
				
				var nextSibling = location.nextSibling;
				alert(nextSibling);

				document.search.getElementsByTagName('li')[0].replaceChild(sel,
						location.lastChild);
				*/
				
				
				// sel.width=500;
				
				for (var i = 0; i < varieties[ind].length; i = i + 2) {
					sel.options[sel.options.length] = new Option(
							varieties[ind][i + 1], varieties[ind][i]);
				}
			}
			$('#property_names').trigger("chosen:updated");
		}

		$(function() {
			$(".chosen-select").chosen({
				allow_single_deselect : true
			});
			addSelect();

			$('.chosen-drop').css({
				minWidth : '200px',
				width : 'auto'
			});
			$('.chosen-single').css({
				minWidth : '200px',
				width : 'auto'
			});

			$("input[type='reset'], button[type='reset']").click(function() {
				$(".chosen-select").val('').trigger("chosen:updated");
				addSelect();
			});
		});
	</script>

	<%@include file="header.jsp"%>

	<div class="container">
		<h2>Search by Map</h2>

		<div id="map">
			<!--  gets filled in by JavaScript function call -->
		</div>
		<button id="mapsearch" onclick="DoSubmit();"
			style="visibility: hidden;">Search</button>
		<hr />
		<h2 id="options">Additional Search Options</h2>
		<form action="<c:url value="/results" />" name="search" method="get"
			id="search">
			<ul>
				<li>Location: &nbsp;&nbsp;&nbsp; <select id="loc" name="loc"
					onchange="addSelect()" class="chosen-select" style="width: 500px;"
					data-placeholder="Choose an Insula" tabindex="2">
						<option value=""></option>
						<option value="I.7">I.7</option>
						<option value="I.8">I.8</option>
				</select>
				<select multiple id="property_names" class="chosen-select" name="location" style="width: 500px;" data-placeholder="Select Properties">
				</select>
				
				<br /> <em class="OR">OR</em>
				</li>

				<li>Property Type: &nbsp;&nbsp;&nbsp; <select name="property"
					class="chosen-select" data-placeholder="Choose a Property Type"
					style="width: 500px;" tabindex="2">
						<option value=""></option>

						<%
							for( PropertyType pt : propertyTypes ) {
						%>
						<option value="<%=pt.getId()%>"><%=pt.getName()%></option>
						<%
							}
						%>
				</select> <br /> <em class="OR">OR</em>
				</li>

				<li>Drawing Type: &nbsp;&nbsp;&nbsp; <select
					class="chosen-select" name="drawing" style="width: 500px;"
					tabindex="2">

						<option value=""></option>
						<option value="0">All</option>

						<%
							for( DrawingTag dt : drawingTags ) {
						%>

						<option value="<%=dt.getId()%>"><%=dt.getName()%></option>

						<%
							}
						%>

				</select> <br /> <em class="OR">OR</em>
				</li>

				<li>Graffiti Content Search: &nbsp; <label> <input
						type="text" name="query" size="40" value="" />
				</label>
				</li>

				<em class="OR">OR</em>

				<li>Global Search: &nbsp; <label> <input type="text"
						name="keyword" size="40" value="" />
				</label>
				</li>
			</ul>
			<p>
				<input type="submit" name="search" value="Search" /> <input
					type="reset" name="reset" value="Clear" />
			</p>
		</form>
	</div>

	<script type="text/javascript">
		function DoSubmit() {
			var argString = "loc=" + mapName + "&";
			for (var i = 0; i < highlighted.length; i++) {
				argString = argString + "location=" + highlighted[i];
				argString = argString + "&";
			}

			window.location = "results?" + argString;
			return true;
		};
	</script>

	<script type="text/javascript">
		var mapName = "Pompeii";
		var highlighted = new Array();

		function start() {
			$('img')
					.mapster(
							{
								mapKey : 'data-key',
								clickNavigate : false,
								onClick : function(data) {
									if (mapName == "Title"
											|| mapName == "Pompeii"
											|| mapName == "Herculaneum") {
										xmlHttp = new XMLHttpRequest();
										xmlHttp.open("GET",
												"map?clickedRegion=" + data.key
														+ "&search=yes", false);
										xmlHttp.send(null);
										document.getElementById("map").innerHTML = xmlHttp.responseText;
										mapName = data.key;
										start();
									} else {
										document.getElementById("mapsearch").style.visibility = "visible";

										var index = highlighted
												.indexOf(data.key);
										if (index == -1)
											highlighted.push(data.key);
										else
											highlighted.splice(index, 1);
									}

								}
							});
		}

		start();
	</script>
</body>
</html>