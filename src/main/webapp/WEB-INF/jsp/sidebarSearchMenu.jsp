<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%@ page import="edu.wlu.graffiti.controller.GraffitiController"%>

<%@ page import="java.util.*" %>

<script>
	$(document).ready(function() {
		$('[data-toggle="tooltip"]').tooltip();
	});
</script>

<script type="text/javascript"
	src="<c:url value="/resources/js/bootstrap-modal-popover.js"/>"></script>

<style type="text/css">
.panel-default>.panel-heading {
	border-color: #bbb;
	background-image: -webkit-linear-gradient(top, #d0d0d0 0%, #dfdfdf 100%);
	background-image: linear-gradient(to bottom, #d0d0d0 0%, #dfdfdf 100%);
	margin-top: 5px;
	cursor: pointer;
}

.top-panel-heading {
	margin-top: 0px;
}

.panel-default {
	border-color: #fff;
}

table.center {
	margin-left: auto;
	margin-right: auto;
}

.btn-custom {
	width: 78px;
	font-size: 12px;
	background-color: #ddd;
}

.btn-keyboard {
	width: 160px;
	font-size: 12px;
	background-color: #ddd;
}

#greekKeys input[type="button"] {
	padding-top: 2px;
	padding-right: 6px;
	padding-bottom: 3px;
	padding-left: 6px;
	border-width: 2px;
}

.label-primary, .label {
	border: 2px solid #428bca;
	margin-right: 1px;
	font-size: 12px;
}

.large-font {
	font-size: 18px;
}

.checkbox-label {
	opacity: 1;
	border: none;
	width: 100%;
}

input {
	cursor: pointer;
}

.input-table {
	width: 100%;
}

.checkbox {
	margin-top: 0;
	margin-bottom: 0;
}

.panel-body {
	padding-top: 20px;
}

#refine, #report {
	width: 185px;
	height: 30px;
}

button:disabled {
	color: #aaa;
}

.pointer {
	cursor: pointer;
}
</style>

<%--script for checking hierarchical checkboxes for search--%>
<script type="text/javascript">

//not used anymore ==> used to be called when a child checkbox was checked 
//in property types
function check_big_category(childCheckbox, parentID){
	var parent = document.getElementById(parentID);
	var checkboxes = document.getElementsByName(childCheckbox.name);
	for (var i=0; i<checkboxes.length; i++){
		if (checkboxes[i].checked != true){
			parent.checked = false;
			return false;
		}
	}
	parent.checked = true;
}

function check_sub_category(parentCheckbox, childrenName){
	var checkboxes = document.getElementsByName(childrenName);
	for(var i=0; i<checkboxes.length; i++) {
		checkboxes[i].checked = parentCheckbox.checked;
	}	
}

function select_parent_and_children(parentCheckbox, childrenName, filterParentFunction, parentLabel){
	filterParentFunction;
	var checkboxes = document.getElementsByName(childrenName);
	for(var i=0; i<checkboxes.length; i++){
		var necessaryAspects = checkboxes[i].value.split(", ");
		if (termExists("Property Type: " + parentLabel) && !termExists("Property Type: " + necessaryAspects[0])){
			filterBy('Property Type', necessaryAspects[0], necessaryAspects[1], necessaryAspects[2]);
		}
		else if (!termExists("Property Type: " + parentLabel) && termExists("Property Type: " + necessaryAspects[0])){
			filterBy('Property Type', necessaryAspects[0], necessaryAspects[1], necessaryAspects[2]);
		}
		
	}
}

//not used anymore ==> used to be called when a child checkbox was checked 
//in property types
function filter_children_selection(childCheckbox, parentID, parentLabel, parentChoice){
	var parent = document.getElementById(parentID);
	var checkboxes = document.getElementsByName(childCheckbox.name);
	for (var i=0; i<checkboxes.length; i++){
		if (checkboxes[i].checked != true){
			if (termExists("Property Type: " + parentLabel)){
				removeSearchTermByName("Property Type: " + parentLabel);
			}
			return false;
		}
	}
	addSearchTerm('Property Type', parentLabel, parentChoice, parentID);
}

</script>

<%
	boolean s = false;
	if (session.getAttribute("authenticated") != null) {
		s = (Boolean) session.getAttribute("authenticated");
	}
%>

<div id="popupButton" class="popover">
	<div class="arrow"></div>
	<div class="popover-content">
		<table class="center">
			<tr>
				<td nowrap align="center"><div id="greekKeys"></div>
			</tr>
		</table>
	</div>
</div>

<div class="panel-group" style="width: 185px; float: left;">
	<div class="panel panel-default">

		<%-- City --%>
		<div class="panel-heading" data-toggle="collapse"
			data-target="#collapse2" onclick="switchArrow(2);">
			<h4 class="panel-title">
				City<span id="menu2" style="float: right;">&#9660;</span>
			</h4>
		</div>
		<div id="collapse2" class="panel-collapse collapse">
			<div class="panel-body">
				<div class="checkbox" id="City">
					<c:forEach var="k" begin="${1}"
						end="${fn:length(requestScope.cities)}">
						<c:set var="city" value="${requestScope.cities[k-1]}" />
						<label class="checkbox-label"><input id="c${k-1}"
							type="checkbox" value=""
							onclick="filterBy('City', '${city}', '${city}', 'c${k-1}');" />${city}</label>
					</c:forEach>
				</div>
			</div>
		</div>

		<%-- Insula --%>
		<div class="panel-heading" data-toggle="collapse"
			data-target="#collapse3" onclick="switchArrow(3);">
			<h4 class="panel-title">
				Insula<span id="menu3" style="float: right;">&#9660;</span>
			</h4>
		</div>
		<div id="collapse3" class="panel-collapse collapse">
			<div class="panel-body" id="Insula">
				<c:forEach var="k" begin="${1}"
					end="${fn:length(requestScope.cities)}">
					<c:set var="city" value="${requestScope.cities[k-1]}" />
					<span class="pointer" style="display: block;" 
						 data-toggle="collapse"
						 data-target="#i_${city}" 
						 onclick="switchSign('i_${city}');">
						${city}<span class="pointer" 
						 id="expandi_${city}"
						 style="float: right;">&#43;</span>
					</span>
					<div class="checkbox collapse" id="i_${city}">
						<c:forEach var="l" begin="${1}"
							end="${fn:length(requestScope.insulaList)}">
							<c:set var="insula" value="${requestScope.insulaList[l-1]}" />
							<c:if test="${insula.modernCity == city}">
								<label class="checkbox-label"><input id="i${insula.id}"
									type="checkbox" value=""
									onclick="filterBy('Insula', '${insula.fullName}', '${insula.id}', 'i${insula.id}');" />
									${insula.fullName}</label>
							</c:if>
						</c:forEach>
					</div>
				</c:forEach>
			</div>
		</div>

		<%-- Property --%>
		<div class="panel-heading" data-toggle="collapse"
			data-target="#collapse4" onclick="switchArrow(4);">
			<h4 class="panel-title">
				Property<span id="menu4" style="float: right;">&#9660;</span>
			</h4>
		</div>
		<div id="collapse4" class="panel-collapse collapse">
			<div class="panel-body" id="Property">
				<c:forEach var="k" begin="${1}"
					end="${fn:length(requestScope.cities)}">
					<c:set var="city" value="${requestScope.cities[k-1]}" />
					<div class="${city} collapse in">
						<span style="display: block;" class="pointer"
							data-toggle="collapse"
							data-target=".p_${city}"
							onclick="switchSign('p_${city}');">
							${city}<span class="pointer" 
							id="expandp_${city}" 
							style="float: right;" >&#43;</span>
						</span>
						<c:forEach var="l" begin="${1}"
							end="${fn:length(requestScope.insulaList)}">
							<c:set var="insula" value="${requestScope.insulaList[l-1]}" />
							<c:if test="${insula.modernCity == city}">
								<div class="p_${city} collapse" style="margin-left: 10px;">
									<span style="display: block;" class="pointer"
										data-toggle="collapse"
										data-target="#p_${insula.id}"
										onclick="switchSign('p_${insula.id}');">
										${insula.fullName}<span class = "pointer"
										id="expandp_${insula.id}"
										style="float: right;">&#43;</span>
									</span>
									<div class="checkbox collapse" id="p_${insula.id}">
										<c:forEach var="m" begin="${1}"
											end="${fn:length(requestScope.propertiesList)}">
											<c:set var="prop" value="${requestScope.propertiesList[m-1]}" />
											<c:if test="${prop.insula.shortName == insula.shortName}">
												<label class="checkbox-label"><input
													id="p${prop.id}" type="checkbox"
													onclick="filterBy('Property', '${insula.modernCity} ${insula.shortName}.${prop.propertyNumber} ${prop.propertyName}', '${prop.id}', 'p${prop.id}');" />${prop.propertyNumber}
													${prop.propertyName}</label>
											</c:if>
										</c:forEach>
									</div>
								</div>
							</c:if>
						</c:forEach>
					</div>
				</c:forEach>
			</div>
		</div>

		<%-- Property Type --%>
		<div class="panel-heading" data-toggle="collapse"
			data-target="#collapse5" onclick="switchArrow(5);">
			<h4 class="panel-title">
				Property Type<span id="menu5" style="float: right;">&#9660;</span>
			</h4>
		</div>
		<div id="collapse5" class="panel-collapse collapse">
			<div class="panel-body">
				<div class="checkbox" id="Property_Type">
				
					<c:forEach var="k" begin="${1}" end="${fn:length(requestScope.propertyTypes)}">
						<c:set var="pt" value="${requestScope.propertyTypes[k-1]}" />
						<c:set var="parentID" value="${pt.id}"/>
						<c:if test="${pt.isParent}">
						
						<ul style="list-style: none; padding-left: 0px;">
						<li>
						<c:choose>
						<c:when test="${fn:length(pt.children)!=0}">
							<span style="display: inline; max-height: 20px"><span style="padding-left: 0px">
							<label class="checkbox-label" style="display: inline;"><input id="pt${pt.id}" type="checkbox" value="" 
							onchange="check_sub_category(this, '${pt.name}s'); 
							select_parent_and_children(this, '${pt.name}s', filterBy('Property Type', '${pt.name}', '${pt.id}', 'pt${pt.id }'), '${pt.name}')"/>${pt.name}
							</label><span id="expandpt_${pt.id}" class="pointer" style="float: right;" data-toggle="collapse" data-target="#pt_${pt.id}" onclick="switchSign('pt_${pt.id}');">&#43;</span></span></span>
							
							<div class="collapse" id="pt_${pt.id}">
							<ul style="list-style: none; padding-left: 20px;">
								<c:forEach var="j" begin="${1}" end="${fn:length(pt.children)}">
									<c:set var="subpt" value="${pt.children[j-1]}"/>
									<li><label class="checkbox-label"><input type="checkbox"  
									id="pt${subpt.id}" value="${subpt.name}, ${subpt.id}, pt${subpt.id }"
									name="${pt.name}s" onchange="filterBy('Property Type', '${subpt.name}', '${subpt.id}', 'pt${subpt.id }');"/>
									${subpt.name}</label></li>
								</c:forEach>	
							</ul></div>
						</c:when>
						<c:otherwise>
							<label class="checkbox-label"><input id="pt${pt.id}" type="checkbox" value="" 
							onchange="check_sub_category(this, '${pt.name}s'); 
							select_parent_and_children(this, '${pt.name}s', filterBy('Property Type', '${pt.name}', '${pt.id}', 'pt${pt.id }'), '${pt.name}')"/>
							${pt.name}</label>
						</c:otherwise>
						</c:choose>
							
							
						</li></ul>
						</c:if>
					</c:forEach>			
				</div>
			</div>
		</div>

		<%-- Drawing Category --%>
		<div class="panel-heading" data-toggle="collapse"
			data-target="#collapse6" onclick="switchArrow(6);">
			<h4 class="panel-title">
				Drawing Category<span id="menu6" style="float: right;">&#9660;</span>
			</h4>
		</div>
		<div id="collapse6" class="panel-collapse collapse">
			<div class="panel-body">
				<div class="checkbox" id="Drawing_Category">
					<label class="checkbox-label"><input id="dc0"
						type="checkbox" value=""
						onclick="filterBy('Drawing Category', 'All', 'All', 'dc0');" />All</label>
					<c:forEach var="k" begin="${1}"
						end="${fn:length(requestScope.drawingCategories)}">
						<c:set var="dc" value="${requestScope.drawingCategories[k-1]}" />
						<label class="checkbox-label"><input id="dc${dc.id}"
							type="checkbox" value=""
							onclick="filterBy('Drawing Category', '${dc.name}', '${dc.id}', 'dc${dc.id}');" />${dc.name}</label>
					</c:forEach>
				</div>
			</div>
		</div>

		<%-- Writing Style --%>
		<div class="panel-heading" data-toggle="collapse"
			data-target="#collapse7" onclick="switchArrow(7);">
			<h4 class="panel-title">
				Writing Style<span id="menu7" style="float: right;">&#9660;</span>
			</h4>
		</div>
		<div id="collapse7" class="panel-collapse collapse">
			<div class="panel-body">
				<div class="checkbox"
					id="<%=GraffitiController.WRITING_STYLE_PARAM_NAME%>">
					<label class="checkbox-label"><input id="ws1"
						type="checkbox" value=""
						onclick="filterBy('<%=GraffitiController.WRITING_STYLE_SEARCH_DESC%>', 'Inscribed/Scratched', '<%=GraffitiController.WRITING_STYLE_GRAFFITI_INSCRIBED%>', 'ws1');" />Inscribed/Scratched</label>
					<label class="checkbox-label"><input id="ws2"
						type="checkbox" value=""
						onclick="filterBy('<%=GraffitiController.WRITING_STYLE_SEARCH_DESC%>', 'charcoal', 'charcoal', 'ws2');" />Charcoal</label>
					<label class="checkbox-label"><input id="ws3"
						type="checkbox" value=""
						onclick="filterBy('<%=GraffitiController.WRITING_STYLE_SEARCH_DESC%>', 'other', 'other', 'ws3');" />Other</label>
				</div>
			</div>
		</div>

		<%-- Language --%>
		<div class="panel-heading" data-toggle="collapse"
			data-target="#collapse8" onclick="switchArrow(8);">
			<h4 class="panel-title">
				Language<span id="menu8" style="float: right;">&#9660;</span>
			</h4>
		</div>
		<div id="collapse8" class="panel-collapse collapse">
			<div class="panel-body">
				<div class="checkbox" id="Language">
					<label class="checkbox-label"><input id="l1"
						type="checkbox" value=""
						onclick="filterBy('Language', 'Latin', 'Latin', 'l1');" />Latin</label> <label
						class="checkbox-label"><input id="l2" type="checkbox"
						value="" onclick="filterBy('Language', 'Greek', 'Greek', 'l2');" />Greek</label>
					<label class="checkbox-label"><input id="l3"
						type="checkbox" value=""
						onclick="filterBy('Language', 'Latin-Greek', 'Latin/Greek', 'l3');" />Latin-Greek</label>
					<label class="checkbox-label"><input id="l4"
						type="checkbox" value=""
						onclick="filterBy('Language', 'Other', 'other', 'l4');" />Other</label>
				</div>
			</div>
		</div>

		<%-- Keyword --%>
		<div class="panel-heading" data-toggle="collapse"
			data-target="#collapse1" onclick="switchArrow(1);">
			<h4 class="panel-title">
				Search<span id="menu1" style="float: right">&#9660;</span>
			</h4>
		</div>
		<div id="collapse1" class="panel-collapse collapse">
			<div class="panel-body">
				<div class="input-group">
					<input id="keyword" type="text" class="form-control"
						style="border-radius: 4px; margin-bottom: 3px;" />
					<button class="btn btn-default btn-custom"
						onclick="contentSearch();" style="float: left;"
						data-toggle="tooltip" data-placement="bottom"
						title="Perform a search based only on the text of the graffiti">Text</button>
					<button class="btn btn-default btn-custom"
						onclick="cilSearch();" style="float: left;"
						data-toggle="tooltip" data-placement="bottom"
						title="Perform a search based only on the CIL number">CIL</button>
					<button class="btn btn-default btn-keyboard"
						onclick="globalSearch();"
						style="float: center; margin-bottom: 3px; margin-top: 3px" data-toggle="tooltip"
						data-placement="bottom"
						title="Perform a search based on all data fields">Global</button>
					<a href="#popupButton" role="button"
						class="btn btn-default btn-keyboard" data-toggle="modal-popover"
						data-placement="bottom">Greek Alphabet</a>
				</div>
			</div>
		</div>
	</div>
	<%
		if (s == true) {
	%>
	<button class="btn btn-agp" onclick="generateReport();" id="report">Generate
		Report</button>
	<%
		}
	%>
</div>

<script type="text/javascript">
	function createButton(i) {
		var textBox = document.getElementById("keyword");
		var v = document.createElement("input");
		v.type = "button";
		v.value = String.fromCharCode(i);
		v.addEventListener("click", function(event) {
			textBox.value += this.value;
		});
		document.getElementById("greekKeys").appendChild(v);
	}

	// Create the keyboard buttons
	window.onload = function() {
		checkboxesAfterBack();

		<c:if test="${not empty sessionScope.returnFromEDR}">
		document.getElementById("${sessionScope.returnFromEDR}")
				.scrollIntoView();
		<c:set var="returnFromEDR" value="" scope="session" />
		</c:if>

		var brCount = 1;

		for (var i = 945; i < 962; i++) {
			createButton(i);
			if (brCount == 8) {
				var brTag = document.createElement("br");
				document.getElementById("greekKeys").appendChild(brTag);
				brCount = 0;
			}
			brCount++;
		}
		for (var i = 963; i < 970; i++) {
			createButton(i);
			if (brCount == 8) {
				var brTag = document.createElement("br");
				document.getElementById("greekKeys").appendChild(brTag);
				brCount = 0;
			}
			brCount++;
		}
	}
</script>

