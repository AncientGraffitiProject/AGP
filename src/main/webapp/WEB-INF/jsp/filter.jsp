<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set var="num" value="${fn:length(requestScope.resultsLyst)}" />
<p class="alert alert-info" style="width: 475px">
	<c:out value="${num} results found ${searchQueryDesc}" />
</p>
<c:if test="${num == 0}">
	<br />
	<c:out value="Try broadening your search" />
</c:if>

<a href="#top">
<button class="btn btn-agp scroll_top">Return To Top</button>
</a>

<%@ include file="resultsList.jsp"%>