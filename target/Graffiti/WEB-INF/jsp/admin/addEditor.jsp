<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<%@include file="/resources/common_head.txt"%>
<title>Ancient Graffiti Project</title>
<script type="text/javascript"
	src='<c:url value="/resources/js/addEditorHelpers.js" />'></script>

<style type="text/css">
td {
	padding: 5px;
}

@media only screen and (max-width: 1023px) {
	[class*="col"] {
		width: 100%;
	}
}
</style>

</head>
<body>
	<%@include file="/WEB-INF/jsp/header.jsp"%>
	<div class="container">
		<%
			String msg = "";
			if (request.getAttribute("msg") != null) {
				msg = (String) request.getAttribute("msg");
		%>
		<div class="alert alert-info" role="alert">
			<%=msg%>
		</div>
		<%
			}
		%>

		<form name="addEditor" action="AddEditor" method="POST">
			<h2 class="admin">Add a New Editor</h2>
			<table>
				<tr>
					<td>Name:</td>
					<td><input type="text" name="name" size='30' required /></td>
					<td></td>
				</tr>

				<tr>
					<td>Username:</td>
					<td><input type="text" name="username" size='30' required /></td>
				</tr>
				<tr>
					<td>Password:</td>
					<td><input type="password" id="password1" name="password1"
						size='30' required /></td>
				</tr>
				<tr>
				</tr>
				<tr>
					<td>Confirm Password:</td>
					<td><input type="password" style="backgroundColor:"
						name="password2" id="password2"
						onkeyup="checkPassword();return false;" size='30' required /></td>

				</tr>

				<tr>
					<td></td>
					<td><button class="btn btn-agp" type="submit" id="add_button">Add</button>
						<button class="btn btn-agp" id="goBack" name="back" onclick="location.href='<%=request.getContextPath() + "/admin" %>'">Cancel</button></td>
				</tr>
			</table>
		</form>
		<script
			src="https://cdn.rawgit.com/twbs/bootstrap/v4-dev/dist/js/bootstrap.js"
			integrity="sha384-XXXXXXXX" crossorigin="anonymous"></script>
	</div>
</body>
</html>