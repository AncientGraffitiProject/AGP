
<header class="navbar navbar-static-top bs-docs-nav" id="top"
	role="banner">
	<div class="navbar navbar-inverse navbar-fixed-top">
		<div class="container">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle" data-toggle="collapse"
					data-target=".bs-navbar-collapse">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
			</div>
			<nav class="collapse navbar-collapse bs-navbar-collapse"
				role="navigation">
				<ul class="nav navbar-nav" id="nav">
					<li><a href="http://ancientgraffiti.wlu.edu/">About the
							Project</a></li>
					<li><a href="search">Search by Map</a></li>
					<li><a href="search#options">Additional Search Options</a></li>
					<!-- 
					<li><a href="AdminFunctions">Admin</a></li>
					<li><a href="login">Login</a> --><!-- <a
						href="<c:url value="/j_spring_security_logout" />">Logout</a> --></li>
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
				<p>Developing a search engine for studying the graffiti of
					Herculaneum and Pompeii</p>
					<% 
					// only show up on the "home" page
					if( request.getRequestURI().endsWith("WEB-INF/jsp/index.jsp")) { %>
				<p>
					<a href="search" class="btn btn-primary btn-lg" role="button">See
						it in Action &raquo; </a>
				</p>
				<% } %>
			</div>
		</div>
	</div>

</header>