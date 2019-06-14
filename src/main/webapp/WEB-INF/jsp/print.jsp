<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set var="num" value="${fn:length(requestScope.resultsLyst)}" />

<%@ page import="java.util.*"%>
<p>
	<c:choose>
		<c:when test="${num == 1}">
			<c:out value="${num} result found ${searchQueryDesc}" />
		</c:when>
		<c:otherwise>
			<c:out value="${num} results found ${searchQueryDesc}" />
		</c:otherwise>
	</c:choose>
</p>

<c:if test="${num == 0}">
	<p>
		<c:out value="Try broadening your search" />
	</p>
</c:if>

<h1>Search Results</h1>

<%@ include file="printableResultsList.jsp"%>
