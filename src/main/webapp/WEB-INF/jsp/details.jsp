<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Ancient Graffiti Project :: Graffito Information</title>
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resources/css/details.css" />
<%@ include file="/resources/common_head.txt"%>
<%@ include file="/resources/leaflet_common.txt"%>
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/resources/css/main.css" />
<script type="text/javascript"
	src="<c:url value="/resources/js/pompeiiPropertyData.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="/resources/js/herculaneumPropertyData.js"/>"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/geojson.js"></script>
<script type="text/javascript"
	src="<c:url value="/resources/js/pompeiiMap.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="/resources/js/herculaneumMap.js"/>"></script>

<style type="text/css">
#pompCityMap {
	margin-top: 0px;
}

.convention-header {
	float: right;
	max-width: 450px;
	font-size: 12pt;
	display: none;
}

#ep_con_link:link, #ep_con_link:visited, #ep_con_link:active{
	color: maroon;
	font-size: 18px;
	text-decoration: none;
	
}

#ep_con_link:hover{
	color: #D34444;
	text-decoration: underline;
}

#second_ep_con_link:link, #second_ep_con_link:visited, #second_ep_con_link:active{
	color: grey;
	font-size: 12px;
	text-decoration: none;
	
}

#second_ep_con_link:hover{
	color: maroon;
	text-decoration: underline;
}

#convention_table {
	display: inline;
	margin-top: 20px;
	max-width: 70px;
	font-size: 10pt;
	border: 1px;
}


#convention_table th {
	text-align: center;
}

#sym{
	text-align:center;
	color: black;
	width: 30%;
	padding: 5px;
}

#def{
	text-align: center;
	color: maroon;
	width: 70%;
	padding: 2px;
}

.definition {
	float: right;
	max-width: 450px;
	font-size: 10pt;
	display: none;
	width: 300px;
	color: maroon;
}

h4 {
	float: right;
	position: relative;
	margin-bottom: 10px;
	margin-top: 10px;
	margin-right: 78%;
}

.btn-agp {
	margin-bottom: 10px;
}
</style>
<script type="text/javascript">

function displayConventions() { 
	document.getElementById("convention_table").style.display = 'inline';
	document.getElementById("ep_con_link").style.display = 'inline';
	document.getElementById("second_ep_con_link").style.display = 'inline';
	document.getElementById("pad_ep_con").style.display = 'inline';
	document.getElementById("hideConvBtn").style.display = 'inline';
	document.getElementById("showConvBtn").style.display = 'none';
}

function hideConventions() {
	document.getElementById("convention_table").style.display = 'none';
	document.getElementById("ep_con_link").style.display = 'none';
	document.getElementById("second_ep_con_link").style.display = 'none';
	document.getElementById("pad_ep_con").style.display = 'none';
	document.getElementById("hideConvBtn").style.display = 'none';
	document.getElementById("showConvBtn").style.display = 'inline';
	//for( x = 1; x < 10; x++ ) {
		//document.getElementById("def" + x).style.display = 'none';
	//}
}

//function displayDefinition(currentDef) {
	//for( x = 1; x < 10; x++ ) {
		//document.getElementById("def"+x).style.display = 'none';
	//}
	//document.getElementById(currentDef).style.display = 'inline';
//}

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
	if(url.includes("filter")) {
		url = url.replace("filter", "results"); // generate the results page--makes sure the page is formatted
	} else if(url.includes("print")) {
		url = url.replace("print", "results");
	}
	window.location.href = url;
}
</script>
</head>
<body>
	<%@include file="header.jsp"%>

	<c:set var="i" value="${requestScope.inscription}" />
	<c:set var="notations" value="${requestScope.notations}" />

	<script>
	//Function to hide measurements on load:
	//Note: putting these in the head can cause problems, including endless loading
	$(document).ready(function() {
		$("#measurements").hide();
		});
	
	//Do NOT delete the commented out code in this function. It is for the Show/Hide
	//measurements button and will be re-implemented when we have the data that we need.  
	
	//Toggles Measurements to hide and show the text
	$(document).ready(function() {
		$("#showMeasure").click(function(){
			var button = $(this);
			if (button.val() == "Show Measurements"){
				button.val("Hide Measurements");
				$("#measurements").show();
			}else{
				button.val("Show Measurements");
				$("#measurements").hide();
			}
			});
		});
	</script>

	<div class="button_bar">
		<button class="btn btn-agp" onclick="backToResults();">Back
			to Results</button>
		<a href="<%=request.getContextPath() %>/graffito/${i.agp.agpId}/csv"
			id="csv">
			<button class="btn btn-agp right-align">Export as CSV</button>
		</a><a href="<%=request.getContextPath() %>/graffito/${i.agp.agpId}/json"
			id="json">
			<button class="btn btn-agp right-align">Export as JSON</button>
		</a> <a href="<%=request.getContextPath() %>/graffito/${i.agp.agpId}/xml"
			id="xml">
			<button class="btn btn-agp right-align">Export as EpiDoc</button>
		</a> 
	</div>
	<div class="container">
		<div class="top-div">
			<!-- sets the title of graffito -->
			<c:choose>
				<c:when test="${not empty i.agp.caption}">
					<c:set var="summary" value="${i.agp.caption}" />
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
					<c:set var="findspot" value="Unnamed" />
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

					<%-- <p id="def1" class="definition" style="margin-top: 20px">Letters
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
						(used by EDR and AGP)</p>--%>
					<%--<td onclick="displayDefinition('def2')">--%>
					<br />
					
					<div  style="text-align:center;">
					<a href="http://ancientgraffiti.org/about/main/epigraphic-conventions/" 
					id="ep_con_link">Epigraphic Convention Key</a>
					<table class="table-bordered" id="convention_table">
						<thead>
							<tr>
								<th>Symbol</th>
								<th>Meaning</th>
							</tr>
						</thead>
						<tbody>
						<c:if test="${fn:contains(notations, 'oncePres')}">
							<tr>
								<td id="sym">[abc]</td>
								<td id="def">Letters once present, now missing due to damage 
								to the surface or support</td>
							</tr>
						</c:if>
						<c:if test="${fn:contains(notations, 'lostContent')}">
							<tr>
								<td id="sym">[- - -]</td>
								<td id="def">Damage to the surface or support; letters cannot be 
								restored with certainty</td>
							</tr>
						</c:if>
						<c:if test="${fn:contains(notations, 'abbr')}">
							<tr>
								<td id="sym">a(bc)</td>
								<td id="def">Abbreviation; text was never written out; expanded by 
								editor</td>
							</tr>
						</c:if>
						<c:if test="${fn:contains(notations, 'intErased')}">
							<tr>
								<td id="sym">[[abc]]</td>
								<td id="def">Letters intentionally erased in antiquity</td>
							</tr>
						</c:if>
						<c:if test="${fn:contains(notations, 'upper')}">
							<tr>
								<td id="sym">ABC</td>
								<td id="def">Letters whose reading is clear but meaning is 
								incomprehensible</td>
							</tr>
						</c:if>
						<c:if test="${fn:contains(notations, 'dots')}">
							<tr>
								<td id="sym">a&#803;b&#803;</td>
								<td id="def">Characters damaged or unclear that would be unintelligible 
								without context</td>
							</tr>
						</c:if>
						<c:if test="${fn:contains(notations, 'underline')}">
							<tr>
								<td id="sym"><span style="text-decoration: underline">abc</span></td>
								<td id="def">Characters formerly visible, now missing</td>
							</tr>
						</c:if>
						<c:if test="${fn:contains(notations, 'fig')}">
							<tr>
								<td id="sym">((:abc))</td>
								<td id="def">Description of a figural graffito (used by EDR and AGP)</td>
							</tr>
						</c:if>
						<c:if test="${fn:contains(notations, 'nonStandSpell')}">
							<tr>
								<td id="sym">(:abc)</td>
								<td id="def">Gives standard spelling to explain non-standard text in an 
								inscription (used by EDR and AGP)</td>
							</tr>
						</c:if>
						<c:if test="${fn:contains(notations, 'lig')}">
							<tr>
								<td id="sym">^</td>
								<td id="def">Letters joined in ligature, each letter that is joined to the 
								next letter is indicated by a caret</td>
							</tr>
						</c:if>
						<c:if test="${fn:contains(notations, 'illegChar')}">
							<tr>
								<td id="sym">+</td>
								<td id="def">Illegible/unclear character</td>
							</tr>
						</c:if>
						<c:if test="${fn:contains(notations, 'lostLines')}">
							<tr>
								<td id="sym">– – – – – –</td>
								<td id="def">	Lost lines, quantity unknown</td>
							</tr>
						</c:if>
						<c:if test="${fn:contains(notations, 'uncert')}">
							<tr>
								<td id="sym">?</td>
								<td id="def">Represents Uncertainty</td>
							</tr>
						</c:if>
						</tbody>
					</table>
					<a href="http://ancientgraffiti.org/about/main/epigraphic-conventions/" 
					id="second_ep_con_link">Full List of Conventions &#10140;</a>
					</div>
				</div>
				<br id="pad_ep_con" />
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
					<tr>
						<th class="propertyLabel">Findspot:</th>
						<td><a
							href="http://pleiades.stoa.org/places/${i.agp.property.insula.city.pleiadesId}">${city}</a>
							<c:if test="${i.ancientCity != 'Smyrna'}">, ${findspot}<a
								href="<%=request.getContextPath() %>/results?property=${i.agp.property.id}">
									(${i.agp.property.insula.shortName}.${i.agp.property.propertyNumber})</a>
							</c:if>
							<br /> 
							<%--If not known where property is, property number will still be at init value of 0--%>
 							<c:if test="${not i.agp.property.propertyNumber.isEmpty() and ! i.agp.property.propertyNumber.equals('0')}"> 
 							<a 
 							href="<%=request.getContextPath() %>/properties/${i.agp.property.insula.city.name}/${i.agp.property.insula.shortName}/${i.agp.property.propertyNumber}">property 
 								metadata &#10140;</a></c:if></td> 
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
					<c:if test="${not empty i.agp.graffitoHeight || not empty i.agp.graffitoLength || 
					not empty i.agp.heightFromGround || not empty i.agp.minLetterHeight || not empty i.agp.maxLetterHeight 
					|| not empty i.agp.minLetterWithFlourishesHeight || not empty i.agp.maxLetterWithFlourishesHeight}">
						<tr>
							<th class="propertyLabel"><input type="button"
								id="showMeasure" class="btn btn-agp" value="Show Measurements"></th>
							<td>
								<div id="measurements">
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
								</div>
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
					<c:if test="${i.ancientCity != 'Smyrna'}">
						<tr>
							<th class="propertyLabel">Link to EDR:</th>
							<td><a
								href="http://www.edr-edr.it/edr_programmi/res_complex_comune.php?id_nr=${i.edrId}&lang=en">#${i.edrId}</a></td>
						</tr>
					</c:if>
					<tr>
						<th class="propertyLabel">Suggested Citation:</th>
						<td>${i.citation}</td>
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
			<c:set var="len" value="${fn:length(i.photos)}" />
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
			<c:if test="${i.ancientCity != 'Smyrna'}">
				<div id="maps" style="min-height: 400px;">
					<h4>Findspot:</h4>
					<div id="pompeiimap" class="findspotMap"></div>
					<div id="herculaneummap" class="findspotMap"></div>
				</div>
			</c:if>

			<script type="text/javascript">
				hideConventions();
				
				if ("${i.ancientCity}" == "Pompeii"){
					window.initPompeiiMap("property",false,false,false,<c:out value = "${i.agp.property.id}"/>,[],true);
				}
				else{
					window.initHerculaneumMap(true,false,false,false,<c:out value = "${i.agp.property.id}"/>,[],true);
				}
				
			</script>
		</div>
	</div>
	<%@include file="footer.jsp" %>
</body>
</html>