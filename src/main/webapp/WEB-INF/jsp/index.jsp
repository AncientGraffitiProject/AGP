<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Ancient Graffiti Project</title>
<%@include file="../../resources/common_head.txt" %>
</head>
<body>
	<%@include file="header.jsp"%>
	<div class="container">
	<p>Welcome to The Ancient Graffiti Project, a website that provides
		a search engine for locating and studying graffiti of the early Roman
		empire from the cities of Pompeii and Herculaneum.</p>
	<p>Ancient graffiti, inscriptions that have been incised or
		scratched into wall-plaster, comprise a special branch of epigraphy.
		They differ from inscriptions on stone in several respects. An
		inscription on stone may be commemorative, dedicatory, sacred (to name
		just a few classes of inscription), but in almost all cases
		forethought has gone into the preparation of the text and the
		inscribed monument. Graffiti, by contrast, are more often the result
		of spontaneous composition and are the handwritten creation of the
		"man on the street." Since graffiti are scratched into friable
		wall-plaster, they are more easily perishable, but when they do
		survive they are almost always found in-situ, unlike many stone
		inscriptions that have survived to the present day through re-use.</p>
	<p>This website provides a search engine that allows three
		different types of searches.</p>
	<ul>
		<li>You can search for graffiti by location, selecting either the
			pull-down menu, or by clicking on the map, and</li>
		<li>You can search specifically for graffiti drawings by choosing
			the class of drawing that interests you, and</li>
		<li>You can search for a specific word or phrase and find where
			it occurs within the ancient city.</li>
	</ul>
	<p>At present, the search engine and database are under
		construction, so searches are limited to Regio I, insula 8 in the city
		of Pompeii. More will be available as the project progresses.</p>

	<p>
		Special acknowledgements to <a
			href="http://digitalhumanities.umass.edu/pbmp/">Eric Poehler</a> for
		the map of Pompeii and the creators of <a
			href="http://www.outsharked.com/imagemapster/">imagemapster</a> for
		the highlighting map feature.
	</p>
	</div>
</body>
</html>