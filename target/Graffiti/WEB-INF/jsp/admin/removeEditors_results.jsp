<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" type="text/css"
	href='<c:url value="/resources/css/proj.css" />' />
<title>Remove Editor</title>
<%@include file="/resources/common_head.txt"%>
<%@page import="java.util.ArrayList"%>

<style type="text/css">
.errorblock {
	color: #ff0000;
	background-color: #ffEEEE;
	border: 3px solid #ff0000;
	padding: 8px;
	margin: 16px;
}

table, td, th {
	border-collapse: collapse;
	border-bottom: 1px solid #ddd;
	width: 50%;
}

th, td {
	border-bottom: 1px solid #ddd;
	text-align: left;
	padding: 15px;
}

tr:nth-child(even) {
	background-color: #f2f2f2
}
</style>
</head>
<body>

	<%@include file="/WEB-INF/jsp/header.jsp"%>
	<div class="container">
		<h2>Remove Editor</h2>

		<!-- The POST method removes the editors that the user has selected -->
		<form action="RemoveEditors" method="POST" id="remove_editor_form">
			<table id="editors">

				<tr>
					<th>Names</th>
					<th>Usernames</th>
					<th>Remove</th>
				</tr>
				<%
					ArrayList<String> usernames = (ArrayList<String>) request.getAttribute("usernames");
					ArrayList<String> names = (ArrayList<String>) request.getAttribute("names");

					if (usernames != null && names != null) {
						for (int i = 0; i < usernames.size(); i++) {
				%>
				<tr>
					<td><%=usernames.get(i)%></td>
					<td><%=names.get(i)%></td>
					<!-- The user name is passed onto the servlet -->
					<td><input type="checkbox" id="removeEditorsChckBox"
						name="removeEditors" value="<%=usernames.get(i)%>"
						onClick="verifyCheckbox();" /></td>

				</tr>
				<%
					}
				%>
				<%
					}
				%>
			</table>
			<input type="submit" value="Remove Editors" id="removeEditorsButton"
				disabled style="margin-top: 0.5cm;">

		</form>

		<script type="text/javascript">
			//The button is enabled only if a checkbox is chosen.
			function verifyCheckbox() {
				var checkbox = document.getElementsByName("removeEditors");
				var button = document.getElementById("removeEditorsButton");
				button.disabled = true;//To disable the button if I uncheck.
				for ( var elem in checkbox) {
					if (checkbox[elem].checked == true) {
						button.disabled = false;
						return;
					}
				}
			}
		</script>
	</div>
</body>
</html>