
<header class="navbar navbar-static-top bs-docs-nav" id="top"
	role="banner">

	<div class="navbar navbar-inverse navbar-fixed-top">
		<div class="container-fluid" style="padding: 0 25px;">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle" data-toggle="collapse"
					data-target=".bs-navbar-collapse" style="width: 45px;">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
			</div>
			<%
				boolean sess = false;
				if (session.getAttribute("authenticated") != null) {
					sess = (Boolean) session.getAttribute("authenticated");
				}
			%>
			<nav class="collapse navbar-collapse bs-navbar-collapse"
				role="navigation">
				<ul class="nav navbar-nav" id="nav">
					<li><a href="<%=request.getContextPath()%>/">Home</a></li>
					<li><a
						href="<%=request.getContextPath()%>/results?browse=true">Browse All
							Inscriptions</a></li>
					<li><a
						href="<%=request.getContextPath()%>/results?drawing=all">Browse
							Figural Graffiti (Drawings)</a></li>	
					<li><a
						href="<%=request.getContextPath()%>/search?city=Pompeii">Search
							Pompeii</a></li>
						
					<li><a
						href="<%=request.getContextPath()%>/search?city=Herculaneum">Search
							Herculaneum</a></li>
					<li><a href="<%=request.getContextPath()%>/featured-graffiti">Featured
							Graffiti</a></li>
					<li><a href="/about">About the Project</a></li>
					<!-- If user is authenticated, Login disappears and Logout appears. Vice versa if admin is not authenticated -->
					<%
						if (sess == true) {
					%>
					<li><a href="<%=request.getContextPath()%>/admin">Admin</a></li>
					<li id="logout" style="visibility: visible"><a href="logout">Logout</a></li>
					<%
						}
					%>
				</ul>

			</nav>
			<!--/.navbar-collapse -->
		</div>
	</div>
	<!-- Main jumbotron  -->
	<div class="block" id="Jumbo">
		<div class="jumbotron">
			<div class="container">
				<h1>The Ancient Graffiti Project Search Engine</h1>
				<p>A digital resource for studying the graffiti of Herculaneum
					and Pompeii</p>
			</div>
		</div>
	</div>

</header>
