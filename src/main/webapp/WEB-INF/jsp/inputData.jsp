<%@ page language="java" contentType="text/html; charset=EUC-KR"
	pageEncoding="EUC-KR"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" type="text/css"
	href="<c:url value="/resources/css/proj.css" />" />
<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
<title>inputData</title>
</head>
<body>
	<%@include file="header.jsp"%>
	<h3>File Upload:</h3>
	Select a file to upload:
	<br />
	<form action="inputDataComplete" method="post" enctype="multipart/form-data">
		<input type="file" name="file"/> <br /> 
		<input type="submit" value="Upload File" />
	</form>
</body>
</html>