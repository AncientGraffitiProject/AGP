<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page import="java.util.*"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>UpdateTest</title>
<%@include file="/resources/common_head.txt"%>
</head>
<body>
	<%@include file="/WEB-INF/jsp/header.jsp"%>
	<div class="container">

		<h1>Click checkbox to approve users</h1>

		<form action="<c:url value="/approveComplete" />" name="approve"
			method="get" id="approve">
			<c:forEach var="k" begin="${1}"
				end="${fn:length(requestScope.users)}">
				<c:set var="i" value="${requestScope.users[k-1]}" />
				<ul>
					<li><span class="prop">UserName:</span>${i.userName} <label>
							<input type="checkbox" name="userName" value="${i.userName}" />
					</label></li>
				</ul>
			</c:forEach>
			<p>
				<input type="submit" name="submit" value="submit" /> <input
					type="reset" name="reset" value="Clear" />
			</p>
		</form>
	</div>
</body>
</html>