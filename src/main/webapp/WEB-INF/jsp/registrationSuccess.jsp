<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html>
<head>
<meta charset="UTF-8">
<title>RegistrationSuccess</title>
<link rel="stylesheet" type="text/css"
	href="<c:url value="/resources/css/proj.css" />" />
</head>
<body>
	<%@include file="header.jsp"%>
	<h2>Register for AGP</h2>

	<table>
		<tr>
			<td>UserName :</td>
			<td>${user.userName}</td>
		</tr>

		<tr>
			<td>Password :</td>
			<td>${user.password}</td>
		</tr>
		<tr>
			<td>Confirm Password :</td>
			<td>${user.confirmPassword}</td>
		</tr>

	</table>

</body>
</html>