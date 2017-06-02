<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.*"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>UpdateTest</title>
<%@include file="/resources/common_head.txt"%>
	
</head>
<body>
	<%@include file="/WEB-INF/jsp/header.jsp"%>
	<%
		session.setAttribute("inscription",
				request.getAttribute("inscriptions"));
	%>
	<h1>
		For:
		<c:out value="${requestScope.inscriptions[0].eagleId}" />
	</h1>

	<form action="<c:url value="/editComplete" />" name="edit" method="get"
		id="search">
		<c:set var="i" value="${requestScope.inscriptions[0]}" />
		<input type="hidden" name=eagleId value="${i.eagleId}" />
		<ul>

			<li><span class="prop">City:</span> <label> <input
					type="text" name=ancientCity size="40" value="${i.ancientCity}" />
			</label></li>

			<li><span class="prop">Findspot:</span> <input type="text"
				name=findspot size="40" value="${i.findSpot}" /></li>

			<li><span class="prop">Measurements:</span> <input type="text"
				name=measurements size="40" value="${i.measurements}" /></li>

			<li><span class="prop">Language:</span> <input type="text"
				name=language size="40" value="${i.language}" /></li>

			<c:choose>
				<c:when test="${not empty i.contentWithLineBreaks}">
					<li><span class="prop">Content:</span></li>
					<textarea rows="4" cols="50" name="content" form="edit">"${i.content}" </textarea>
				</c:when>
			</c:choose>

			<li><span class="prop">Bibliography:</span> <input type="text"
				name=bibliography size="40" value="${i.bibliography}" /></li>

			<li><span class="prop">WritingStyle:</span> <input type="text"
				name=writingStyle size="40" value="${i.writingStyle}" /></li>

			<li><span class="prop">Drawing url:</span> <input type="text"
				name=url size="40" value="${i.url}" /></li>

			<c:if test="${i.getDrawingTags().size() > 0}">
				<li><span class="prop">Drawing Tag Name(s):</span> <c:forEach
						var="dt" items="${i.getDrawingTags()}" varStatus="loopStatus">
						<c:out value="${dt.name}"></c:out>
						<c:if test="${!loopStatus.last}">, </c:if>
					</c:forEach></li>
				<li><span class="prop">Drawing Description:</span>
					${i.getAgp().getDescription()}</li>
			</c:if>

			<li><span class="prop">Drawing Type:</span> <select
				name="drawing" class="chzn-select" style="width: 300px;"
				tabindex="2" multiple>
					<option value=""></option>
					<option value="1">Human motifs</option>
					<option value="2">Animal motifs</option>
					<option value="3">boats</option>
					<option value="4">geometric designs</option>
					<option value="5">other</option>
			</select></li>
		</ul>
		<p>
			<input type="submit" name="submit" value="submit" /> <input
				type="reset" name="reset" value="Clear" />
		</p>
	</form>


	<script type="text/javascript">
		$(function() {
			$(".chzn-select").chosen();
		});
	</script>

</body>
</html>