<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Ancient Graffiti Project</title>
<%@include file="../../resources/common_head.txt"%>
<style>
.fluid-img {
	margin-right: auto;
	margin-left: auto;
	max-height: 325px;
	max-width: 100%;
	width: auto;
	border: 3px solid black;
}

.footer-img {
	display: inline-block;
	margin-left: 10px;
	margin-right: 10px;
	width: 110px;
}

.leftcol {
	float: left;
	width: 50%;
	margin-bottom: 25px;
}

.rightcol {
	float: right;
	width: 50%;
	margin-bottom: 25px;
}

h3 {
	text-align: center;
}

.alert-info {
	color: black;
	background-color: beige;
	border-color: lightgray;
	background-image: none;
}

@media only screen and (max-width: 1023px) {
	[class*="col"] {
		width: 100%;
	}
}

#titles {
	font-size: 20px;
	margin-bottom: 5px;
}

#content {
	font-size: 16px;
	break-inside: avoid;
}

#welcome, #guide, #use {
	break-after: column;
	width: 300px;
}

a {
	color: #113CFE;
}

#benefiel {
	color: black;
}

#benefiel:hover {
	color: inherit;
}

#searchButton {
	background: none;
	color: #113CFE;
	font-style: normal;
	border: none;
	padding: 0;
	font: inherit;
	cursor: pointer;
}
</style>

<script>
	function change_color() {
		var searchLink = document.getElementById("searchButton");
		searchLink.style.color = 'inherit';
		searchLink.style.textDecoration = 'underline';
		searchLink.style.fontStyle = 'normal';
	}

	function original_color() {
		var searchLink = document.getElementById("searchButton");
		searchLink.style.color = '#113CFE';
		searchLink.style.textDecoration = 'none';
		searchLink.style.fontStyle = 'normal';
	}

	function make_search_active() {
		var searchBar = document.getElementById("globalSearch");
		searchBar.focus();
		document.getElementById("top").scrollIntoView();
	}
</script>

</head>
<body>
	<%@include file="header.jsp"%>
	<div class="container" style="width: 100%;">
		<div
			style="max-width: 1100px; float: center; margin: auto; margin-top: 20px;">
			<div class="leftcol">
				<h3>Herculaneum</h3>
				<a href="searchHerculaneum"><img class="fluid-img"
					style="max-height: 280px" alt="Map of Herculaneum properties"
					src="<%=request.getContextPath()%>/resources/images/Herculaneum.jpg"
					onmouseover="this.src='<%=request.getContextPath()%>/resources/images/exploreHerculaneum.jpg'"
					onmouseout="this.src='<%=request.getContextPath()%>/resources/images/Herculaneum.jpg'" /></a>
			</div>

			<div class="rightcol">
				<h3>Pompeii</h3>
				<a href="searchPompeii"><img class="fluid-img"
					style="max-height: 280px" alt="Map of Pompeii"
					src="<%=request.getContextPath()%>/resources/images/Pompeii.jpg"
					onmouseover="this.src='<%=request.getContextPath()%>/resources/images/explorePompeii.jpg'"
					onmouseout="this.src='<%=request.getContextPath()%>/resources/images/Pompeii.jpg'" /></a>
			</div>
		</div>
	</div>

	<div class="container"
		style="max-width: 1100px; float: center; margin: auto">
		<%--Check for error message informing user of invalid city name or inscription id --%>
		<c:if test="${not empty requestScope.error}">
			<p style="color: red;">${requestScope.error}
			<p />
		</c:if>
		<div id="trifold" class="flex-container"
			style="display: flex; column-count: 3; text-align: justify; column-gap: 20px; margin-bottom: 5px; margin-top: 10px; align-items: top; justify-content: space-around; flex-wrap: wrap; max-width: 1100px;">
			<div id="welcome">
				<h3 id="titles">Ancient Graffiti</h3>
				<p id="content" style="padding: 5px 5px 5px;">
					Welcome to The Ancient Graffiti Project, a digital resource for
					locating and studying handwritten inscriptions of the early Roman
					empire. These ancient messages and sketches offer a window into the
					daily life and interests of the people who lived in the ancient
					world, especially in Herculaneum and Pompeii. They provide
					perspectives on Roman society, the ancient economy, religion,
					spoken language, literacy, and activities within the ancient city.
					<br>
					<br>(N.B. The word "graffiti" was originally a technical term
					for ancient handwritten wall-inscriptions that were scratched into
					wall plaster. The term later came to mean any writing on a wall.)
				</p>
			</div>

			<div id="use">
				<h3 id="titles">Welcome</h3>
				<p id="content" style="padding: 5px 5px 71px;">
					The aim of AGP is to allow scholars and the public to explore
					ancient handwritten wall-inscriptions and to understand them in
					context. We have designed AGP to be a <b>user-friendly resource</b>.
					We provide maps to help viewers understand where graffiti appeared
					in the ancient city and we offer our own translations and brief
					summaries of the graffiti. Try out the <a href="#top">maps</a>
					above, <a href="<%=request.getContextPath()%>/results">browse</a>
					around, or begin a
					<button id="searchButton" onmouseover="change_color()"
						onmouseout="original_color()" onclick="make_search_active()">search</button>
					. <br>
					<br>
					<br>We hope you enjoy exploring the Ancient Graffiti Project
					and learning more about the ancient world!
				</p>
			</div>

			<div id="guide">
				<h3 id="titles">Scholarly Editions</h3>
				<p id="content" style="padding: 5px 5px 5px;">
					The inscriptions presented here are our critical editions of the
					ancient texts, many of which offer updates to the <i>Corpus
						Inscriptionum Latinarum</i>. We provide information on how to cite our
					editions in each entry. We have compiled up-to-date bibliography, a
					critical apparatus, and links to further information, and we
					include photographs from our fieldwork as well as the enhanced
					photographs and line-drawings we have created in order to
					accurately represent the inscriptions and make them legible to
					modern viewers. <br>
					<br>We are pleased to contribute our editions to the <a
						href="http://www.edr-edr.it/edr_programmi/res_complex_comune.php?lang=en">
						Epigraphic Database Roma</a> and <a
						href="https://www.eagle-network.eu/">EAGLE</a>, the Europeana
					network of Ancient Greek and Latin Epigraphy. For linked open data
					and teaching materials, please see the Resources menu above.
				</p>
			</div>
		</div>
		<p
			style="text-align: center; font-size: 18px; font-weight: bold; margin-top: 30px">
			<a id="benefiel"
				href="https://www.wlu.edu/directory/profile?ID=x3919">Rebecca R.
				Benefiel</a>, <a href="http://ancientgraffiti.org/about/teams">Director</a>
		</p>
	</div>

	<footer style="margin-top: 50px">
		<p style="text-align: center;">
			<a href="http://www.neh.gov/"> <img class="footer-img"
				style="height: 36px; width: 150px"
				src="http://www.neh.gov/files/neh_at_logo.png" alt="NEH"></a> <a
				href="http://digitalhumanities.wlu.edu/"> <img
				class="footer-img" style="height: 50px; width: 150px"
				src="http://ancientgraffiti.wlu.edu/files/2016/07/dh_at_wandl.png"
				alt="W&L Digital Humanitites"></a> <a href="https://mellon.org/">
				<img class="footer-img" style="height: 50px; width: 150px"
				src="http://ancientgraffiti.wlu.edu/files/2015/06/mellon-e1467740285109.jpeg"
				alt="Mellon Foundation">
			</a> <a href="http://chs.harvard.edu/"> <img class="footer-img"
				style="height: 72px; width: 150px"
				src="http://ancientgraffiti.org/about/wp-content/uploads/2017/06/CHS.png"
				alt="CHS Harvard"></a>
		</p>

		<p id="socials"
			style="width: 100%; height: 32px; text-align: right; margin-top: 0px; margin-bottom: 30px; font-style: normal; font-size: 12px;">
			Visit us on: <a
				href="https://www.facebook.com/HerculaneumGraffitiProject/"><img
				class="footer-img"
				style="height: 32px; width: 32px; margin-right: 2px; margin-left: 2px"
				src="<%=request.getContextPath()%>/resources/images/f-ogo_RGB_HEX-58.png"
				alt="facebook"></a> <a href="https://twitter.com/HercGraffProj"><img
				class="footer-img"
				style="height: 32px; width: 32px; margin-left: 0px; margin-right: 0px;"
				src="<%=request.getContextPath()%>/resources/images/Twitter_Social_Icon_Square_Color.png"
				alt="twitter"></a> <a
				href="https://github.com/AncientGraffitiProject/"><img
				class="footer-img"
				style="height: 32px; width: 32px; margin-left: 2px"
				src="<%=request.getContextPath()%>/resources/images/GitHub-Mark-32px.png"
				alt="github"></a>
		</p>
		<%@include file="footer.jsp"%>
	</footer>
</body>
</html>
