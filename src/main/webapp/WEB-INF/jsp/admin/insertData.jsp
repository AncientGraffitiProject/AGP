<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
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
	<h1>Add an Inscription</h1>

	<form action="<c:url value="/insertComplete" />" name="search"
		method="get" id="search">
		<ul>

			<li><span class="prop">EagleID:</span> <label> <input
					type="text" name=EDR size="9" value="" /></label></li>

			<li><span class="prop">City:</span> <label> <input
					type="text" name=ancientCity size="40" value="" />
			</label></li>

			<li><span class="prop">Findspot:</span> <input type="text"
				name=findspot size="40" value="" /></li>

			<li><span class="prop">measurements:</span> <input type="text"
				name=measurements size="40" value="" /></li>

			<li><span class="prop">language:</span> <input type="text"
				name=language size="40" value="" /></li>


			<li><span class="prop">Content:</span><br /> <input type="text"
				name=content size="40" value="" /></li>


			<li><span class="prop">bibliography:</span> <input type="text"
				name=bibliography size="40" value="" /></li>

			<li><span class="prop">writingStyle:</span> <input type="text"
				name=writingStyle size="40" value="" /></li>
			
			<li><span class="prop">Drawing Type:</span> <select
				name="drawing" class="chzn-select" style="width: 300px;"
				tabindex="2" multiple>
					<option value=""></option>
					<option value="1">Human motifs</option>
					<option value="2">Animal motifs</option>
					<option value="3">boats</option>
					<option value="4">geometric designs</option>
					<option value="5">other</option>
			</select></li>
			
		</ul>
		<p>
			<input type="submit" name="submit" value="submit" /> <input
				type="reset" name="reset" value="Clear" />
		</p>
	</form>


</body>
</html>