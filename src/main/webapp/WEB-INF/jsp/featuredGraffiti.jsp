<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html lang="en">
<head>

<meta charset="UTF-8">
<title>Ancient Graffiti Project</title>
<!--    Fix responsive navbar-->
<%@include file="/resources/common_head.txt"%>

<link rel="stylesheet" type="text/css" href="resources/css/featured_graffiti.css">
<link rel="stylesheet" type="text/css" href="resources/css/new-featured-graffiti.css" />

<meta name="viewport" content="width=device-width, initial-scale=1">
</head>
<body>

	<div id="blackOverlay" class="blackOverlay"></div>
	<%@include file="header.jsp"%>

	<div class="container">
		<div id="selectors">
			<a href="<%=request.getContextPath()%>/TranslationPractice">
				<button class="btn btn-agp right-align">Translation Practice</button>
			</a> <a href="/about/teaching-resources/">
				<button class="btn btn-agp right-align">Teaching Resources</button>
			</a>
		</div>

		<h2>Featured Graffiti</h2>

		<p>You are viewing a prototype of our featured graffiti
			page that we will extend during Summer 2018.</p>

		<div id="portfolio">
			<ul class="portfolio-grid">
				<c:forEach var="theme" items="${themes}">
					<li><a
						href="<%=request.getContextPath()%>/themes/${theme.name}"
						class="darken"> <img
							src="/Graffiti/resources/images/featured_graffiti/${theme.name}.png"
							alt="Herculaneum" /> <span class="message">${theme.description}</span>
					</a></li>
				</c:forEach>
			</ul>
		</div>
	</div>

	<!-- JS for modal view, toggling, show translations-->
	<script type="text/javascript" src="resources/js/greatestHits.js"></script>

	<script type="text/javascript">
		$('.darken').hover(function() {
			$(this).find('.message').fadeIn(100);
		}, function() {
			$(this).find('.message').fadeOut(100);
		});
	</script>
	<%@include file="footer.jsp" %>
</body>
</html>