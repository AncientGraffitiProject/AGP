<%@ page language="java" contentType="text/html; charset=US-ASCII"
	pageEncoding="US-ASCII"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
propertyInfo.jsp
<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>Ancient Graffiti Project :: Property Info</title>
<%@include file="/resources/common_head.txt"%>
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resources/css/details.css" />
</head>
<body>
	<%@include file="/WEB-INF/jsp/header.jsp"%>
	<div class="container">

		<div class="alert alert-warning">
			<p>${message}</p>
		</div>

	</div>

</body>
</html>