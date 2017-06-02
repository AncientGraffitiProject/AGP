<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Pompeii Map</title>
<%@ include file="/resources/common_head.txt" %>
<link rel="stylesheet" href="https://npmcdn.com/leaflet@1.0.0-rc.2/dist/leaflet.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/resources/css/details.css"/>
<link rel="stylesheet" href="<%=request.getContextPath() %>/resources/css/main.css" />
<script type="text/javascript"
	src="<c:url value="/resources/js/updatedEschebach.js"/>"></script>
</head>

<body>
<script src="https://npmcdn.com/leaflet@1.0.0-rc.2/dist/leaflet.js"></script>

<div>
<div id="pompeiimap" class="mapdiv"></div>
<div id="newDiv"></div>
</div>

<div id="moreInfo"><button id="search" class="btn btn-agp">Click here to search</button></div>

<script type="text/javascript"
	src="<c:url value="/resources/js/pompeiiMap.js"/>"></script>
<script>
	window.initmap();
</script>
</body>
</html>