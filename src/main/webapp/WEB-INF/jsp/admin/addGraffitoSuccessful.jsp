<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html lang="en">
<head>
<meta charset="UTF-8">
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
<body>
	<%@include file="/WEB-INF/jsp/header.jsp"%>
	<h3>The graffito has been successfully submitted to the database</h3> 
	<%@include file="/WEB-INF/jsp/footer.jsp"%>
</body>
</html>