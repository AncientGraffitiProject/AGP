<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<%@include file="/resources/common_head.txt"%>
<title>Ancient Graffiti Project :: Details</title>
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resources/css/details.css" />
<script type="text/javascript"
	src="<c:url value="/resources/js/jquery.imagemapster-1.2.js" />"></script>
<c:set var="i" value="${requestScope.inscription}" />
<style type="text/css">
#pompCityMap {
	margin-top: 0px;
}

.convention-header {
	float: right;
	max-width: 450px;
	font-size: 12t;
	display: none;
}

#convention_table {
	display: inline;
	margin-top: 10px;
	max-width: 70px;
	font-size: 10pt;
	border: 1px;
	max-width: 70px;
}

#convention_table td {
	padding: 3px;
	text-align: center;
	cursor: pointer;
	color: blue;
}

#convention_table th {
	text-align: center;
	width: 100%;
}

.definition {
	float: right;
	max-width: 450px;
	font-size: 10pt;
	display: none;
	width: 300px;
	color: maroon;
}

.btn-agp {
	margin-bottom: 10px;
}
</style>
<script type="text/javascript">
//Functions for displaying and hiding the convention table 
function displayConventions() { 
	document.getElementById("convention_table").style.display = 'inline';
	document.getElementById("hideConvBtn").style.display = 'inline';
	document.getElementById("showConvBtn").style.display = 'none';
}

function hideConventions() {
	document.getElementById("convention_table").style.display = 'none';
	document.getElementById("hideConvBtn").style.display = 'none';
	document.getElementById("showConvBtn").style.display = 'inline';
	for( x = 1; x < 10; x++ ) {
		document.getElementById("def" + x).style.display = 'none';
	}
}

function displayDefinition(currentDef) {
	for( x = 1; x < 10; x++ ) {
		document.getElementById("def"+x).style.display = 'none';
	}
	document.getElementById(currentDef).style.display = 'inline';
}

function start() {
$('img').mapster({
	areas: [
		<c:forEach var="locKey" items="${requestScope.findLocationKeys}">
		{
			key: '${locKey}',
			fillColor: '0000FF',
			staticState: true
		},
		</c:forEach>
	], 
	isSelectable: false,
	mapKey: 'data-key',
	clickNavigate: false,
}); 
}

function generatePompeii(name) {
	xmlHttp = new XMLHttpRequest();
	path = "<%=request.getContextPath()%>/";
	xmlHttp.open("GET", path +
			"map?clickedRegion="+name+"&city="+"${i.ancientCity}", false); 
	xmlHttp.send(null);
	document.getElementById("pompCityMap").innerHTML = xmlHttp.responseText;
	start();
}

function generateHerculaneum(name) {
    xmlHttp = new XMLHttpRequest();
    path = "<%=request.getContextPath()%>/";
    xmlHttp.open("GET", path +
            "map?clickedRegion="+name+"&second=yes"+"&city="+"${i.ancientCity}", false); 
    xmlHttp.send(null);
    document.getElementById("hercCityMap").innerHTML = xmlHttp.responseText;
    start();
}

function selectImg(ind) {
	var hrefs = "${requestScope.imagePages}".slice(1,-1).split(','); //turns string of urls into array
	var srcs = "${requestScope.images}".slice(1,-1).split(',');
	document.getElementById("imgLink").href = hrefs[ind-1];
	document.getElementById("imgSrc").src = srcs[ind-1];
}

function backToResults(){
	xmlHttp = new XMLHttpRequest();
	xmlHttp.open("GET", "<%=request.getContextPath()%>/backToResults?edr=" + "${inscription.edrId}", false);
	xmlHttp.send(null);
	var url = "${sessionScope.returnURL}";
	window.location.href = url;
}
</script>
</head>
<body>
	<%@include file="/WEB-INF/jsp/header.jsp"%>

	<c:set var="i" value="${requestScope.inscription}" />

	<div class="button_bar">
		<!--  
		<button class="btn btn-agp" onclick="backToResults();">Back
			to Results</button>-->
		<a href="<%=request.getContextPath() %>/graffito/${i.agp.agpId}/json"
			id="json">
			<button class="btn btn-agp right-align">Download JSON Data</button>
		</a>
	</div>

	<div class="container">
		<div class="top-div">
			<!-- sets the title of graffito -->
			<c:choose>
				<c:when test="${not empty i.agp.summary}">
					<c:set var="summary" value="${i.agp.summary}" />
				</c:when>
				<c:otherwise>
					<c:set var="summary" value="Graffito" />
				</c:otherwise>
			</c:choose>
			<h3>${summary}</h3>
		</div>

		<div class="leftcol">
			<!-- sets the name of the city  -->
			<c:choose>
				<c:when test="${not empty i.ancientCity}">
					<c:set var="city" value="${i.ancientCity}" />
				</c:when>
				<c:otherwise>
					<c:set var="city" value="City Unavailable" />
				</c:otherwise>
			</c:choose>

			<!-- sets the findspot -->
			<c:choose>
				<c:when test="${not empty i.agp.property.propertyName}">
					<c:set var="findspot" value="${i.agp.property.propertyName}" />
				</c:when>
				<c:otherwise>
					<c:set var="findspot" value="findspot unavailable" />
				</c:otherwise>
			</c:choose>

			<div class="detail-body">
				<div class="graffiti_content">
					<c:choose>
						<c:when test="${not empty i.contentWithLineBreaks}">
							<p class="lead">${i.contentWithLineBreaks}</p>
						</c:when>
						<c:otherwise>
							<h3>Graffito Unavailable</h3>
						</c:otherwise>
					</c:choose>

					<button class="btn btn-agp" id="hideConvBtn"
						onclick="hideConventions()">Hide Epigraphic Convention
						Key</button>

					<button class="btn btn-agp" id="showConvBtn"
						onclick="displayConventions()">Show Epigraphic Convention
						Key</button>

					<!-- Make this into a loop? -->

					<p id="def1" class="definition" style="margin-top: 20px">Letters
						once present, now missing due to damage to the surface or support
					</p>
					<p id="def2" class="definition" style="margin-top: 50px">Damage
						to the surface or support; letters cannot be restored with
						certainty</p>
					<p id="def3" class="definition" style="margin-top: 80px">Abbreviation;
						text was never written out; expanded by editor</p>
					<p id="def4" class="definition" style="margin-top: 110px">Letters
						intentionally erased in antiquity</p>
					<p id="def5" class="definition" style="margin-top: 130px">Letters
						whose reading is clear but meaning is incomprehensible</p>
					<p id="def6" class="definition" style="margin-top: 160px">Characters
						damaged or unclear that would be unintelligible without context</p>
					<p id="def7" class="definition" style="margin-top: 190px">Characters
						formerly visible, now missing</p>
					<p id="def8" class="definition" style="margin-top: 215px">Description
						of a figural graffito (used by EDR and AGP)</p>
					<p id="def9" class="definition" style="margin-top: 240px">Gives
						standard spelling to explain non-standard text in an inscription
						(used by EDR and AGP)</p>
					<br />
					<table class="table-bordered" id="convention_table">
						<thead>
							<tr>
								<th>Symbol</th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td onclick="displayDefinition('def1')">[abc]</td>
							</tr>
							<tr>

								<td onclick="displayDefinition('def2')">[- - -]</td>
							</tr>

							<tr>
								<td onclick="displayDefinition('def3')">a(bc)</td>
							</tr>
							<tr>
								<td onclick="displayDefinition('def4')">[[abc]]</td>
							</tr>
							<tr>
								<td onclick="displayDefinition('def5')">ABC</td>
							</tr>
							<tr>
								<td onclick="displayDefinition('def6')">a&#803;b&#803;</td>
							</tr>
							<tr>
								<td onclick="displayDefinition('def7')"><span
									style="text-decoration: underline">abc</span></td>
							</tr>
							<tr>
								<td onclick="displayDefinition('def8')">((:abc))</td>
							</tr>
							<tr>
								<td onclick="displayDefinition('def9')">(:abc)</td>
							</tr>
						</tbody>
					</table>
				</div>
				<table class="table table-striped table-bordered">
					<c:if test="${not empty i.agp.contentTranslation}">
						<tr>
							<th class="propertyLabel">Translation:</th>
							<td>${i.agp.contentTranslation}</td>
						</tr>
					</c:if>
					<c:if test="${not empty i.agp.figuralInfo.descriptionInEnglish}">
						<tr>
							<th class="propertyLabel">Description of Drawing (English):</th>
							<td>${i.agp.figuralInfo.descriptionInEnglish}</td>
						</tr>
					</c:if>
					<!-- 
					<c:if test="${not empty i.agp.commentary}">
						<tr>
							<th class="propertyLabel">Commentary:</th>
							<td>${i.agp.commentary}</td>
						</tr>
					</c:if>
					 -->
					<tr>
						<th class="propertyLabel">Findspot:</th>
						<td><a
							href="http://pleiades.stoa.org/places/${i.agp.property.insula.city.pleiadesId}">${city}</a>,
							${findspot}<a
							href="<%=request.getContextPath() %>/results?property=${i.agp.property.id}">
								(${i.agp.property.insula.shortName}.${i.agp.property.propertyNumber})</a>
							<br /> <a
							href="<%=request.getContextPath() %>/property/${i.agp.property.insula.city.name}/${i.agp.property.insula.shortName}/${i.agp.property.propertyNumber}">property
								metadata &#10140;</a></td>
					</tr>
					<!-- defining key words -->
					<c:if test="${i.agp.figuralInfo.getDrawingTags().size() > 0}">
						<tr>
							<c:choose>
								<c:when test="${i.agp.figuralInfo.getDrawingTags().size() == 1}">
									<th class="propertyLabel">Drawing Category:</th>
								</c:when>
								<c:otherwise>
									<th class="propertyLabel">Drawing Categories:</th>
								</c:otherwise>
							</c:choose>
							<td><c:forEach var="dt"
									items="${i.agp.figuralInfo.getDrawingTags()}"
									varStatus="loopStatus">
									<a
										href="<%=request.getContextPath()%>/results?drawing=${dt.id}">${dt.name}</a>
									<c:if test="${!loopStatus.last}">, </c:if>
								</c:forEach></td>
						</tr>
					</c:if>

					<!--  add in here keywords, when controlled vocabulary is working -->

					<c:if test="${not empty i.agp.languageInEnglish}">
						<tr>
							<th class="propertyLabel">Language:</th>
							<td>${i.agp.languageInEnglish}</td>
						</tr>
					</c:if>
					<c:if test="${not empty i.agp.writingStyleInEnglish}">
						<tr>
							<th class="propertyLabel">Writing Style:</th>
							<td>${i.agp.writingStyleInEnglish}</td>
						</tr>
					</c:if>
					<c:if test="${not empty i.measurements}">
						<tr>
							<th class="propertyLabel">Measurements (cm):</th>
							<td>
								<ul>
									<c:if test="${not empty i.agp.graffitoHeight }">
										<li>Graffito Height: ${ i.agp.graffitoHeight }</li>
									</c:if>
									<c:if test="${not empty i.agp.graffitoLength }">
										<li>Graffito Length: ${i.agp.graffitoLength }</li>
									</c:if>
									<c:if test="${not empty i.agp.heightFromGround }">
										<li>Height from Ground: ${i.agp.heightFromGround }</li>
									</c:if>
									<c:if test="${not empty i.agp.minLetterHeight }">
										<li>Min Letter Height: ${i.agp.minLetterHeight}</li>
									</c:if>
									<c:if test="${not empty i.agp.maxLetterHeight }">
										<li>Max Letter Height: ${i.agp.maxLetterHeight }</li>
									</c:if>
									<c:if test="${not empty i.agp.minLetterWithFlourishesHeight }">
										<li>Min Letter Height with Flourishes:
											${i.agp.minLetterWithFlourishesHeight }</li>
									</c:if>
									<c:if test="${not empty i.agp.maxLetterWithFlourishesHeight }">
										<li>Max Letter Height with Flourishes:
											${i.agp.maxLetterWithFlourishesHeight }</li>
									</c:if>
								</ul>
							</td>
						</tr>
					</c:if>
					<c:if test="${not empty i.apparatusDisplay}">
						<tr>
							<th class="propertyLabel">Apparatus Criticus:</th>
							<td id="apparatus">${i.apparatusDisplay}</td>
						</tr>
					</c:if>
					<c:if test="${not empty i.bibliography}">
						<tr>
							<th class="propertyLabel">Bibliography:</th>
							<td>${i.bibliography}</td>
						</tr>
					</c:if>
					<tr>
						<th class="propertyLabel">Link to EDR:</th>
						<td><a
							href="http://www.edr-edr.it/edr_programmi/res_complex_comune.php?id_nr=${i.edrId}&lang=en">#${i.edrId}</a></td>
					</tr>
				</table>

			</div>
			<%
				if (session.getAttribute("authenticated") != null) {
			%>
			<form action="<%=request.getContextPath()%>/admin/updateGraffito">
				<input class="btn btn-agp" type=submit value="Edit Graffito"><input
					type="hidden" name="edrID" value="${i.edrId}" />
			</form>
			<%
				}
			%>
		</div>
		<div class="rightcol">

			<c:set var="len" value="${i.numberOfImages}" />
			<c:choose>
				<c:when test="${len == 1}">
					<a target="_blank" href="${requestScope.imagePages[0]}"><img
						src="${requestScope.images[0]}" class="fluid-img" /></a>
				</c:when>
				<c:when test="${len gt 1}">
					<a target="_blank" href="${requestScope.imagePages[0]}"
						id="imgLink"><img src="${requestScope.images[0]}" id="imgSrc" /></a>
					<table class="buttons">
						<tr>
							<!-- First Image -->
							<td><input type="radio" name="image" onclick="selectImg(1);"
								id="1" checked="checked" /> <label for="1"> <img
									src="${requestScope.thumbnails[0]}" class="thumb" />
							</label></td>
							<%
								int counter = 1;
							%>
							<!--  Every Other Image -->
							<c:forEach var="k" begin="${2}" end="${len}">
								<%
									if (counter % 3 == 0) {
								%>
							
						</tr>
						<tr>
							<%
								}
							%>

							<td><input type="radio" name="image"
								onclick="selectImg(${k});" id="${k}" /> <label for="${k}">
									<img src="${requestScope.thumbnails[k-1]}" class="thumb" />
							</label></td>
							<%
								counter = counter + 1;
							%>
							</c:forEach>
						</tr>
					</table>
				</c:when>
			</c:choose>

			<div id="maps">
				<h4>Findspot on map:</h4>
				<div id="hercCityMap"></div>
				<div id="pompCityMap"></div>
			</div>

			<script type="text/javascript">
				hideConventions();
				if ("${i.ancientCity}" == "Herculaneum") {
					generateHerculaneum("${i.ancientCity}");
				} else if ("${i.ancientCity}" == "Pompeii") {
					generatePompeii("${i.ancientCity}");
				} 
			</script>
		</div>
	</div>
</body>
</html>