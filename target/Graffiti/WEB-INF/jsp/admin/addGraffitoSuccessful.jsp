<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" type="text/css"
	href="<c:url value="/resources/css/proj.css" />" />
<title>Successful addition of Graffito</title>
<%@include file="/resources/common_head.txt" %>

<style>
.errorblock {
	color: #ff0000;
	background-color: #ffEEEE;
	border: 3px solid #ff0000;
	padding: 8px;
	margin: 16px;
}
</style>
</head>
<!-- <body onload='document.f.j_username.focus();'> -->
<body>
	<%@include file="/WEB-INF/jsp/header.jsp"%>
	<h3>The graffito has been successfully submitted to the database</h3>
 
</body>
</html>