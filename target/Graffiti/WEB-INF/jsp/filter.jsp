<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set var="num" value="${fn:length(requestScope.resultsLyst)}" />
<p class="alert alert-info" style="width: 475px">
	<c:out value="${num} results found ${searchQueryDesc}" />
</p>
<c:if test="${num == 0}">
	<br />
	<c:out value="Try broadening your search" />
</c:if>
<!--  

<button class="btn btn-agp convention-button" id="showConvBtn"
	onclick="displayConventions()">Show Epigraphic Convention Key</button>
<button class="btn convention-button btn-agp" id="hideConvBtn"
	onclick="hideConventions()" style="display: none;">Hide
	Epigraphic Convention Key</button>
<p id="1" class="definition" style="margin-top: -15px">Letters once
	present, now missing due to damage to the surface or support</p>
<p id="2" class="definition" style="margin-top: 9px">Damage to the
	surface or support; letters cannot be restored with certainty
<p id="3" class="definition" style="margin-top: 32px">Abbreviation;
	text was never written out; expanded by editor</p>
<p id="4" class="definition" style="margin-top: 56px">Letters
	intentionally erased in antiquity</p>
<p id="5" class="definition" style="margin-top: 80px">Letters whose
	reading is clear but meaning is incomprehensible</p>
<p id="7" class="definition" style="margin-top: 128px">Characters
	formerly visible, now missing</p>
<p id="8" class="definition" style="margin-top: 104px">Characters
	damaged or unclear that would be unintelligible without context</p>
<p id="9" class="definition" style="margin-top: 152px">Description
	of a figural graffito (used by EDR and AGP)</p>
<p id="10" class="definition" style="margin-top: 176px">Gives
	standard spelling to explain non-standard text in an inscription (used
	by EDR and AGP)</p>

<table class="table-bordered" id="convention_table">
	<tr>
		<th>Symbol</th>
	</tr>
	<tr>
		<td onclick="displayDefinition(1)">[abc]</td>
	</tr>
	<tr>
		<td onclick="displayDefinition('2')">[- - -]</td>
	</tr>
	<tr>
		<td onclick="displayDefinition('3')">a(bc)</td>
	</tr>
	<tr>
		<td onclick="displayDefinition('4')">[[abc]]</td>
	</tr>
	<tr>
		<td onclick="displayDefinition('5')">ABC</td>
	</tr>
	<tr>
		<td onclick="displayDefinition('8')">a&#803;b&#803;</td>
	</tr>
	<tr>
		<td onclick="displayDefinition('7')"><u> abc</u></td>
	</tr>
	<tr>
		<td onclick="displayDefinition('9')">((:abc))</td>
	</tr>
	<tr>
		<td onclick="displayDefinition('10')">(:abc)</td>
	</tr>
</table>
-->

<%@ include file="resultsList.jsp"%>