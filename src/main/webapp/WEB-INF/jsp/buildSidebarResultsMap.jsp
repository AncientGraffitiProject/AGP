<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%
	String mapName = (String) request.getAttribute("mapName");

	String displayImage2 = request.getContextPath()
			+ "/resources/images/" + mapName + ".jpg";

	List<String> coords2 = (ArrayList<String>) request
			.getAttribute("coords");
	/*
	if (coords2 == null) {
		coords2 = new ArrayList<String>();
		coords2.add("126,204,94,224,120,273,95,286,42,197,105,159");
	}
	*/

	List<String> regionNames2 = (ArrayList<String>) request
			.getAttribute("regionNames");
	if (regionNames2 == null) {
		regionNames2 = new ArrayList<String>();
		regionNames2.add("I.8.1");
	}

	List<String> highlighted2 = (ArrayList<String>) request
			.getAttribute("highlighted");
	if (highlighted2 == null) {
		highlighted2 = new ArrayList<String>();
	}

	String highlightedRegions2 = "";
	for (int j = 0; j < highlighted2.size(); j++) {
		highlightedRegions2 += "&highlighted=" + highlighted2.get(j);
	}
%>

<img id="resultsMap" src="<%=displayImage2%>" class="mapper"
	usemap="#regionmap2" width="300px" height="auto">
<map name="regionmap2">
	<%
		for (int i = 0; i < coords2.size(); i++) {
	%>
	<area shape="poly" data-key="<%=regionNames2.get(i)%>"
		coords="<%=coords2.get(i)%>" href="#" title="<%=regionNames2.get(i)%>" />
	<%
		}
	%>
</map>