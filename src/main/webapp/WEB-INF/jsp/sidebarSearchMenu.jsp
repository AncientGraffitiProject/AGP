<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%@ page import="edu.wlu.graffiti.controller.FilterController"%>


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

.btn-custom {
	width: 70px;
	font-size: 12px;
	background-color: #ddd;
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
</style>
<%
	boolean s = false;
	if (session.getAttribute("authenticated") != null) {
		s = (Boolean) session.getAttribute("authenticated");
	}
%>
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
					<span style="display: block;" data-toggle="collapse"
						data-target="#i_${city}" onclick="switchSign('i_${city}');">
						${city}<span id="expandi_${city}" style="float: right;">&#43;</span>
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
						<span style="display: block;" data-toggle="collapse"
							data-target=".p_${city}" onclick="switchSign('p_${city}');">
							${city}<span id="expandp_${city}" style="float: right;">&#43;</span>
						</span>
						<c:forEach var="l" begin="${1}"
							end="${fn:length(requestScope.insulaList)}">
							<c:set var="insula" value="${requestScope.insulaList[l-1]}" />
							<c:if test="${insula.modernCity == city}">
								<div class="p_${city} collapse" style="margin-left: 10px;">
									<span style="display: block;" data-toggle="collapse"
										data-target="#p_${insula.id}"
										onclick="switchSign('p_${insula.id}');">
										${insula.fullName}<span id="expandp_${insula.id}"
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
					<c:forEach var="k" begin="${1}"
						end="${fn:length(requestScope.propertyTypes)}">
						<c:set var="pt" value="${requestScope.propertyTypes[k-1]}" />
						<label class="checkbox-label"><input id="pt${pt.id}"
							type="checkbox" value=""
							onclick="filterBy('Property Type', '${pt.name}', '${pt.id}', 'pt${pt.id }');" />${pt.name}</label>
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
					id="<%=FilterController.WRITING_STYLE_PARAM_NAME%>">
					<label class="checkbox-label"><input id="ws1"
						type="checkbox" value=""
						onclick="filterBy('<%=FilterController.WRITING_STYLE_SEARCH_DESC%>', 'Inscribed/Scratched', '<%=FilterController.WRITING_STYLE_GRAFFITI_INSCRIBED%>', 'ws1');" />Inscribed/Scratched</label>
					<label class="checkbox-label"><input id="ws2"
						type="checkbox" value=""
						onclick="filterBy('<%=FilterController.WRITING_STYLE_SEARCH_DESC%>', 'charcoal', 'charcoal', 'ws2');" />Charcoal</label>
					<label class="checkbox-label"><input id="ws3"
						type="checkbox" value=""
						onclick="filterBy('<%=FilterController.WRITING_STYLE_SEARCH_DESC%>', 'other', 'other', 'ws3');" />Other</label>
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
						onclick="contentSearch();" style="float: left;">Content</button>
					<button class="btn btn-default btn-custom"
						onclick="globalSearch();" style="float: right;">Global</button>
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

