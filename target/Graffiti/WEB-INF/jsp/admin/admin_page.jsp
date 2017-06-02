<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Ancient Graffiti Project</title>
<%@include file="/resources/common_head.txt"%>

<style type="text/css">
.fluid-img {
	margin-right: auto;
	margin-left: auto;
	max-height: 325px;
	max-width: 100%;
	width: auto;
	border: 3px solid black;
}

.leftcol {
	float: left;
	width: 50%;
	margin-bottom: 25px;
}

.rightcol {
	float: right;
	width: 50%;
	margin-bottom: 25px;
}

li {
	padding: 2px;
}

@media only screen and (max-width: 1023px) {
	[class*="col"] {
		width: 100%;
	}
}
</style>
</head>

<body>
	<%
		String role = (String) session.getAttribute("role");
	%>

	<%@include file="/WEB-INF/jsp/header.jsp"%>
	<div class="container">
		<h2>
			Welcome,
			<%=session.getAttribute("name")%>!
		</h2>
		<ul>
			<li><a href="<%=request.getContextPath()%>/admin/editGraffito">Edit
					a Graffito</a></li>
			<li><a
				href="<%=request.getContextPath()%>/admin/report?query_all=true?query_all=true">Generate
					Report for all Graffiti</a></li>

			<%
				if (role != null && role.equals("admin")) {
			%>
			<li><a href="<%=request.getContextPath()%>/admin/AddEditor">Add
					a new Editor</a></li>
			<li><a href="<%=request.getContextPath()%>/admin/RemoveEditors">Remove
					Editor</a></li>
			<%
				}
			%>

		</ul>
	</div>

</body>
</html>