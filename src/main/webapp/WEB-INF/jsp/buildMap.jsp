<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ page import="java.util.*"%>
<%
	String mapName = (String) request.getAttribute("mapName");
	//System.out.println("MapName: " + mapName);

	String displayImage = request.getContextPath()
			+ "/resources/images/" + mapName + ".jpg";

	List<String> coords = (ArrayList<String>) request
			.getAttribute("coords");
	/*
	if (coords == null) {
		coords = new ArrayList<String>();
		coords.add("2937,1737,3093,1996,3195,1954,3039,1677"); 
	}*/

	List<String> regionNames = (ArrayList<String>) request
			.getAttribute("regionNames");
	/*
	if (regionNames == null) {
		regionNames = new ArrayList<String>();
		regionNames.add("I.8");
	}*/

	List<String> highlighted = (ArrayList<String>) request
			.getAttribute("highlighted");
	/*if (highlighted == null) {
		highlighted = new ArrayList<String>();
	}*/

	// TODO: Do we need this section of code?
	String highlightedRegions = "";
	if (highlighted != null) {
		for (int j = 0; j < highlighted.size(); j++) {
			highlightedRegions += "&highlighted=" + highlighted.get(j);
		}
	}

	if (mapName.equals("Title")) {
		if (request.getAttribute("search") != null) {
%>
<p>Click on the map of Herculaneum or Pompeii to specify the search
	for a region.</p>
<%
	}
%>

<img id="theMap" src="<%=displayImage%>" class="mapper"
	usemap="#regionmap" style="height: 200px">
<%
	} else if (mapName.equals("Pompeii")
			|| mapName.equals("Herculanaem")) {
		if (request.getAttribute("search") != null) {
%>
<p>
	Click on the map to search for graffiti in a particular city-block or
	choose from the search options <a href="search#options">below</a>
</p>
<%
	}
%>

<img id="theMap" src="<%=displayImage%>" class="mapper"
	usemap="#regionmap">

<%
	} else {
		if (request.getAttribute("search") != null) {
%>
<p>Click on one or more properties within the map, then hit the
	“Search” button below.</p>
<%
	}
%>

<img id="theMap" src="<%=displayImage%>" class="mapper"
	usemap="#regionmap">
<%
	}
%>

<map name="regionmap">
	<%
		for (int i = 0; i < coords.size(); i++) {
	%>
	<area shape="poly" data-key="<%=regionNames.get(i)%>"
		coords="<%=coords.get(i)%>" href="#" title="<%=regionNames.get(i)%>" />
	<%
		}
	%>
</map>