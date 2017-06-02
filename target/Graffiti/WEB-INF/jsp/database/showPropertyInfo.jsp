<%@ page language="java" contentType="text/html; charset=US-ASCII"
	pageEncoding="US-ASCII"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<link href="<c:url value="/resources/css/proj.css" />" type="text/css"
	rel="stylesheet">
<title>Property Info -- for Testing</title>
<%@include file="../../../resources/common_head.txt"%>
</head>
<body>
	<div class="container">

		<h1>Property Info</h1>
		
		<div style="float: right;">
			<h2>Property Types</h2>
			<table class="main-table" style="margin-bottom: 30px;" border="1">
				<tr>
					<th>id</th>
					<th>name</th>
				</tr>
				<c:forEach var="k" begin="${1}" end="${fn:length(requestScope.propertyTypes)}">
					<c:set var="pt" value="${requestScope.propertyTypes[k-1]}"/>
					<tr>
						<td>${pt.id}</td>
						<td>${pt.name}</td>
					</tr>
				</c:forEach>
			</table>
			
			<h2>Insula</h2>
			<table class="main-table" style="margin-bottom: 30px;" border="1">
				<tr>
					<th>id</th>
					<th>name</th>
					<th>description/long name</th>
					<th>modern city</th>
				</tr>
				<c:forEach var="k" begin="${1}" end="${fn:length(requestScope.insula)}">
					<c:set var="i" value="${requestScope.insula[k-1]}"/>
					<tr>
						<td>${i.id}</td>
						<td>${i.shortName}</td>
						<td>${i.fullName}</td>
						<td>${i.modernCity}</td>
					</tr>
				</c:forEach>
			</table>
		</div>
		
		<h2>Properties</h2>
		<table class="main-table" style="margin-bottom: 30px;" cellpadding="5"
			cellspacing="5" border="1">
			<tr>
				<th>id</th>
				<th>city</th>
				<th>insula</th>
				<th>number</th>
				<th>name</th>
				<th>Type</th>
			</tr>
			<c:forEach var="k" begin="${1}" end="${fn:length(requestScope.properties)}">
				<c:set var="prop" value="${requestScope.properties[k-1]}"/>
				<tr>
					<td>${prop.id}</td>
					<td>${prop.insula.modernCity }</td>
					<td>${prop.insula.shortName}</td>
					<td>${prop.propertyNumber}</td>
					<td>${prop.propertyName}</td>
					<td>
						<c:forEach var="l" begin="${1}" end="${fn:length(prop.propertyTypes)}">
							<c:set var="pt" value="${prop.propertyTypes[l-1]}"/>
							${pt.name}
						</c:forEach>
					</td>
				</tr>
			</c:forEach>
		</table>
	</div>

</body>
</html>