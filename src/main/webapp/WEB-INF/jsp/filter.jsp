<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set var="num" value="${fn:length(requestScope.resultsLyst)}" />
<%@ page import="java.util.*"%>

<%
	List<Integer> updatedLocationKeys = (List<Integer>) request.getAttribute("findLocationKeys");
	List<Integer> updatedInsulaKeys = (List<Integer>) request.getAttribute("insulaLocationKeys");
	updatedLocationKeys.addAll(updatedInsulaKeys);
%>

<div id="mapkeys" style="display:none;"><%=updatedLocationKeys%></div>
<div style="width: 480px">
	<p class="alert alert-info">
		<c:out value="${num} results found" />
	</p>
	<a href="<%=request.getContextPath()%>/filtered-results/xml"
		id="bulkEpidocs" class="btn btn-agp btn-sm">Export as EpiDoc
	</a> <a href="<%=request.getContextPath()%>/filtered-results/json"
		id="bulkJson" class="btn btn-agp btn-sm">Export as JSON
	</a> <a href="<%=request.getContextPath()%>/filtered-results/csv"
		id="bulkEpidocs" class="btn btn-agp btn-sm">Export as CSV
	</a>
	<button id="print" class="btn btn-agp btn-sm" onclick="printResults();">Print</button>
	<div style="float: right">
		Sort by <select id="sortParam" onchange="refineResults('sort');">
			<%
				String param = request.getParameter("sort_by");
				boolean isNull = false;
				if (param == null)
					isNull = true;
			%>
			<option value="relevance"
				<%=!isNull && param.equals("relevance") ? "selected" : ""%>>
				Relevance</option>
			<option value="caption"
				<%=!isNull && param.equals("caption") ? "selected" : ""%>>
				Caption</option>
			<option value="cil" <%=!isNull && param.equals("cil") ? "selected" : ""%>>
				CIL #</option>
			<option value="property.property_id"
				<%=!isNull && param.equals("property.property_id") ? "selected" : ""%>>
				Findspot</option>
		</select>
	</div>
</div>
<c:if test="${num == 0}">
	<br />
	<c:out value="Try broadening your search" />
</c:if>

<%@ include file="resultsList.jsp"%>
