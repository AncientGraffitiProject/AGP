<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Ancient Graffiti Project :: Edit Graffito</title>
<%@include file="/resources/common_head.txt"%>
</head>
<body>
	<%@include file="/WEB-INF/jsp/header.jsp"%>
	<div class="container">
		<h2>Edit Graffito</h2>

		<c:if test="${requestScope.msg != null }">
			<p class="alert alert-danger" role="alert">${requestScope.msg}</p>
		</c:if>

		<form class="form-inline" action="updateGraffito" method="GET">
			<input type="text" name="edrID" placeholder="EDR Number" size="20" />
			<input type="submit" class="btn btn-agp" value="Edit" />

		</form>

	</div>
</body>
</html>