<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page import="java.util.*"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link href="<c:url value="/resources/css/proj.css" />" type="text/css"
	rel="stylesheet">
<title>Ancient Graffiti Project :: Search Results</title>
<%@include file="../../resources/common_head.txt"%>
<script type="text/javascript"
	src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.js"></script>
<script type="text/javascript"
	src="<c:url value="/resources/js/jquery.imagemapster.js" />"></script>
<script type="text/javascript">
	function selectImg(ind, k, shortId, longId) {
		if (ind == 1) {
			document.getElementById("imgLink" + k).href = "http://www.edr-edr.it/edr_programmi/view_img.php?id_nr="
					+ longId;
			document.getElementById("imgSrc" + k).src = "http://www.edr-edr.it/foto_epigrafi/thumbnails/"
					+ shortId + "/th_" + longId + ".jpg";
		} else {
			ind--;
			document.getElementById("imgLink" + k).href = "http://www.edr-edr.it/edr_programmi/view_img.php?id_nr="
					+ longId + "-" + ind;
			document.getElementById("imgSrc" + k).src = "http://www.edr-edr.it/foto_epigrafi/thumbnails/"
					+ shortId + "/th_" + longId + "-" + ind + ".jpg";
		}
	}
</script>

</head>

<body>

	<%@include file="header.jsp"%>

	<div class="container">
		<p>
			<c:out value="${fn:length(requestScope.resultsLyst)} graffiti found" />
		</p>
		<%@ include file="resultsList.jsp"%>
	</div>
</body>

</html>
