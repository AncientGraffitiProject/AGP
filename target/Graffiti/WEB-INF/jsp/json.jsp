<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.IOException"%>
<%@ page import="edu.wlu.graffiti.bean.*"%>
<%@ page import="com.fasterxml.jackson.core.JsonGenerationException"%>
<%@ page import="com.fasterxml.jackson.databind.JsonMappingException"%>
<%@ page import="com.fasterxml.jackson.databind.ObjectMapper"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link href="<c:url value="/resources/css/proj.css" />" type="text/css"
	rel="stylesheet">
<title>Ancient Graffiti Project :: JSON</title>
<%@include file="../../resources/common_head.txt"%>
</head>
<body>

	<%
		List<Inscription> inscriptionList = (List<Inscription>) request.getAttribute("graffitiList");
		ObjectMapper mapper = new ObjectMapper();
		String json = "";
	%>

	{ "graffiti":
	<%
		for (Inscription ins : inscriptionList) {
			json = "";
			try {
				json = mapper.writeValueAsString(ins);
	%>
	<%=json%>,
	<%
		} catch (JsonGenerationException | JsonMappingException e) {
				e.printStackTrace();
			}
		}
	%>
</body>
</html>
