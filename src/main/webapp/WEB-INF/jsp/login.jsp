<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<meta charset="UTF-8">

<link rel="stylesheet" type="text/css"
	href='<c:url value="/resources/css/proj.css" />' />
<title>Login Page</title>
<%@include file="/resources/common_head.txt"%>

<style type="text/css">
.form-login {
	background-color: #EEE;
	padding-top: 10px;
	padding-bottom: 20px;
	padding-left: 10px;
	padding-right: 10px;
	border-radius: 15px;
	border-color: #ddd;
	border-width: 5px;
	box-shadow: 0 1px 0 #ccc;
	padding-bottom: 20px;
	padding-left: 10px;
	padding-right: 10px;
}

h4 {
	border: 0 solid #fff;
	border-bottom-width: 1px;
	padding-bottom: 10px;
	text-align: center;
}

.form-control {
	border-radius: 10px;
}

.wrapper {
	text-align: center;
}

.login_container {
	padding-right: 2cm;
}
</style>
</head>


<body>
	<%@include file="/WEB-INF/jsp/header.jsp"%>
	<div class="container">
		<form name='login' method='POST' action="<%=request.getContextPath() %>/LoginValidator">
			<div id="error_msg">
				<%
					String error_msg = "";
					if (request.getAttribute("error_msg") != null) {
						error_msg = (String) request.getAttribute("error_msg");
				%>

				<div class="alert alert-danger" role="alert">
					<%=error_msg%>
				</div>

				<%
					}
				%>
			</div>

			<div class="login_container">
				<div class="row">
					<div class="col-md-offset-5 col-md-3">
						<div class="form-login">
							<h4>Login</h4>
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" class="form-control" />
							 <input type="text" id="userName"
								name="username" class="form-control input-sm chat-input"
								placeholder="username" /> <br /> <input type="password"
								id="userPassword" name="password"
								class="form-control input-sm chat-input" placeholder="password" />
							<br />
							<div class="wrapper">
								<span class="group-btn"> <input type="submit"
									class="btn btn-primary" value="Login">
								</span>
							</div>
						</div>
					</div>
				</div>
			</div>
		</form>
	</div>
</body>
</html>