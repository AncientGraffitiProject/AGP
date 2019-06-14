<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="edu.wlu.graffiti.bean.Theme"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="en">
<head>
<link rel="stylesheet" type="text/css"
	href="../resources/css/themedGraffitiResults.css" />
<meta charset="UTF-8">
<title>Ancient Graffiti Project</title>
<%@include file="/resources/common_head.txt"%>
<meta name="viewport" content="width=device-width, initial-scale=1">
</head>
<body>

	<%@include file="header.jsp"%>

	<%
		Theme theme = (Theme) request.getAttribute("theme");
	%>

	<div class="container">

		<h2><%=theme.getName()%>
			Graffiti
		</h2>

		<p><%=theme.getDescription()%></p>

		<c:set var="inscriptions" value="${inscriptions}" />

		<div>
			<table class="table table-striped table-bordered" id="themedGraffiti">
				<thead>
					<tr>
						<th id="idCol" style="width: 175px">ID</th>
						<th id="textCol" style="width: 325px">Text (Latin or Greek) /
							English Translation</th>
						<th id="transCol">Commentary</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="i" items="${inscriptions}">
						<tr>
							<td class="center"><c:if
									test="${not empty i.agp.greatestHitsInfo.preferredImage}">
									<img
										src="http://www.edr-edr.it/foto_epigrafi/immagini_uso/${i.edrDirectory}/${i.agp.greatestHitsInfo.preferredImage}.jpg"
										alt="No Image Available" class="graffitoImg" />
								</c:if> <a
								href="<%=request.getContextPath() %>/graffito/AGP-${i.edrId}">AGP-${i.agp.edrId}</a><br>
								${i.agp.cil}</td>
							<td>
								<p>Text:</p>
								<p class="tabify">${i.contentWithLineBreaks}</p>
								<p>Translation:</p>
								<p class="tabify">${i.agp.contentTranslation}</p>
							</td>
							<td style="vertical-align: bottom">
								<table class="table" id="nestedTable">
									<tbody>
										<tr id="nestedRow">
											<td id="nestedCommentary">
												<p>${i.agp.greatestHitsInfo.commentary}</p>
											</td>
										</tr>
										<tr id="nestedRow">
											<td id="nestedLink"><a id="learnMore"
												href="<%=request.getContextPath() %>/graffito/AGP-${i.edrId}"
												id="${i.edrId}"> Learn More &#10140;</a></td>
										</tr>
									</tbody>
								</table>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
	<%@include file="footer.jsp"%>
</body>
</html>