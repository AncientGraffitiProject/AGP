<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:if test="${requestScope.search != null}"><%--On search page; include message--%>
	<p>Select one or more properties below and hit "Search" to browse graffiti within the area(s).
	<!-- Click on one or more properties within the map, then hit the "Search" button below. --></p>
</c:if>

<c:choose>
	<c:when test="${requestScope.second != null}"><%--Second map; use regionmap2--%>
		<img id="resultsMap" src="${requestScope.displayImage}" class="mapper"
			usemap="#regionmap2">
		<map name="regionmap2">
			<c:forEach var="k" begin="${1}" end="${fn:length(requestScope.coords)}">
				<area shape="poly" data-key="${requestScope.regionIds[k-1]}"
					coords="${requestScope.coords[k-1]}" href="#" title="${requestScope.regionNames[k-1]}"/>
			</c:forEach>
		</map>	
	</c:when>
	<c:otherwise>
		<img id="theMap" src="${requestScope.displayImage}" class="mapper" usemap="#regionmap">
		<map name="regionmap">
			<c:forEach var="k" begin="${1}" end="${fn:length(requestScope.coords)}">
				<area shape="poly" data-key="${requestScope.regionIds[k-1]}"
					coords="${requestScope.coords[k-1]}" href="#" title="${requestScope.regionNames[k-1]}"/>
			</c:forEach>
		</map>	
	</c:otherwise>
</c:choose>
