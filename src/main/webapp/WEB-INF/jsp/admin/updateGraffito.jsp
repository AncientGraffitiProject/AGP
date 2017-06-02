<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.*"%>
<%@ page import="edu.wlu.graffiti.bean.Inscription"%>
<%@ page import="edu.wlu.graffiti.bean.AGPInfo"%>
<%@ page import="edu.wlu.graffiti.bean.DrawingTag"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Update a Graffito</title>
<%@include file="/resources/common_head.txt"%>
<style type="text/css">
.attribute {
	text-align: right;
}

#agp_info {
	width: 700px;
}

#edr_info {
	-moz-border-radius: 15px;
	border-radius: 15px;
	border: 1px solid gray;
	padding: 10px;
	width: 310px;
	float: right;
}

input[name*="image"] {
	display: inline;
}


/* Had to override a bunch of random classes from boostrap to get the page to prorperly align and float, We have no idea why */

.btn-group-vertical > .btn-group::after, .btn-toolbar::after, .clearfix::after, .container-fluid::after, .container::after, .dl-horizontal dd::after, .form-horizontal .form-group::after, .modal-footer::after, .modal-header::after, .nav::after, .navbar-collapse::after, .navbar-header::after, .navbar::after, .pager::after, .panel-body::after, .row::after
{   clear:left; }


</style>
<script type="text/javascript">
	$(document).ready(function() {
		$("#figural").click(function() {
			$("#drawing_info").toggle();
		});
	});
</script>
</head>
<body>

	<!-- Format for checking if this works:
	
	http://localhost:8080/Graffiti/updateGraffito?edrID=EDR153355
	
	-->

	<%@include file="/WEB-INF/jsp/header.jsp"%>

	<%
		Inscription inscription = (Inscription) request.getAttribute("graffito");

		Set<DrawingTag> drawingTags = inscription.getAgp().getFiguralInfo().getDrawingTags();
		List<Integer> drawingTagIds = new ArrayList<Integer>();

		for (DrawingTag i : drawingTags) {
			int id = i.getId();
			drawingTagIds.add(id);
		}
	%>

	<div class="container">

		<c:if test="${requestScope.msp != null}">
			<p class="alert alert-success" role="alert">${requestScope.msg}</p>
		</c:if>

		<c:set var="i" value="${requestScope.graffito}" />

		<p style="text-align: right;">
			<a class="btn btn-agp"
				href="<c:url value="/graffito/AGP-${i.edrId}"/>">AGP-<%=inscription.getEdrId()%>
				Details Page
			</a> <a class="btn btn-agp"
				href="http://www.edr-edr.it/edr_programmi/res_complex_comune.php?visua=si&id_nr=<%=inscription.getEdrId()%>">EDR
				link</a>
		</p>

		<h2>
			Update AGP-<%=inscription.getEdrId()%></h2>

		<div id="edr_info">
			<h3>Data from EDR</h3>
			<table class="table table-striped">
				<tbody>
					<tr>
						<td class="prop">City</td>
						<td><%=inscription.getAncientCity()%></td>
					</tr>
					<tr>
						<td class="prop">Find Spot</td>
						<td><%=inscription.getEDRFindSpot()!=null?inscription.getEDRFindSpot():""%></td>
					</tr>
					<tr>
						<td class="prop">Writing Style</td>
						<td><%=inscription.getWritingStyle()%>
					</tr>
					<tr>
						<td class="prop">Language</td>
						<td><%=inscription.getLanguage()%>
					</tr>
					<tr>
						<td class="prop">EDR Measurements</td>
						<td><%=inscription.getMeasurements()%></td>
					</tr>
					<tr>
						<td class="prop">Bibliography</td>
						<td>${i.bibliography}</td>
					</tr>
					<tr>
						<td class="prop">Apparatus Criticus</td>
						<td>${i.apparatus}</td>
					</tr>
				</tbody>
			</table>
		</div>


		<div id="agp_info">
			<form class="form-horizontal" method="post" action="updateGraffito">
				<input type="hidden" name="${_csrf.parameterName}"
					value="${_csrf.token}" class="form-control" />

				<div class="form-group" style="clear:left;">
					<strong class="col-sm-3 attribute">Graffito:</strong>
					<div class="col-sm-6">${i.contentWithLineBreaks}</div>
				</div>

				<div class="form-group">
					<label for="cil" class="col-sm-3 attribute">CIL Number:</label>
					<div class="col-sm-6">
						<input type="text" name="cil" id="cil" class="form-control"
							value="${i.agp.cil}" />
					</div>
				</div>
				<div class="form-group">
					<label for="langner" class="col-sm-3 attribute">Langner Number:</label>
					<div class="col-sm-6">
					<input type="text" name="langner" id="langner" class="form-control" value="${i.agp.langner }"/>
					</div>
				</div>

				<div class="form-group">
					<label for="summary" class="col-sm-3 control-label">Summary</label>
					<div class="col-sm-6">
						<textarea id="summary" name="summary" class="form-control">${i.agp.summary}</textarea>
					</div>
				</div>

				<div class="form-group">
					<label for="content_translation" class="col-sm-3 control-label">Graffito
						Translation</label>
					<div class="col-sm-6">
						<textarea name="content_translation" id="content_translation"
							class="form-control">${ i.agp.contentTranslation}</textarea>
					</div>
				</div>

				<div class="form-group">
					<label for="commentary" class="col-sm-3 control-label">Commentary</label>
					<div class="col-sm-6">
						<textarea rows=7 id="commentary" name="commentary" class="form-control">${ i.agp.commentary }</textarea>
					</div>
				</div>

				<hr />

				<!-- look at graffito2drawingtags -->

				<div class="form-group">
					<label for="figural" class="col-sm-3 control-label">Has
						Figural Component?</label>
					<div class="col-sm-1">
						<input type="checkbox" name="figural" id="figural"
							class="form-control"
							<%=inscription.getAgp().hasFiguralComponent() ? "checked" : ""%> />
					</div>
				</div>

				<div id="drawing_info"
					style="display:<%=inscription.getAgp().hasFiguralComponent() ? "inline" : "none"%>">
					<h3>Figural Metadata</h3>

					<div class="form-group">

						<!--  MAKE THIS INTO A FOR LOOP, drawn from DB -->

						<label for="drawingCategory" class="col-sm-3 control-label">Drawing
							Category</label>

						<div class="col-sm-6">
							<label><input type="checkbox" name="drawingCategory"
								value=3 <%=drawingTagIds.contains(3) ? "checked" : ""%>>Animals</label>
							<br /> <label><input type="checkbox"
								name="drawingCategory" value=1
								<%=drawingTagIds.contains(1) ? "checked" : ""%>>Boats</label> <br />
							<label><input type="checkbox" name="drawingCategory"
								value=4 <%=drawingTagIds.contains(4) ? "checked" : ""%>>Erotic
								Images</label> <br /> <label><input type="checkbox"
								name="drawingCategory" value=2
								<%=drawingTagIds.contains(2) ? "checked" : ""%>>Geometric
								Designs</label> <br /> <label><input type="checkbox"
								name="drawingCategory" value=7
								<%=drawingTagIds.contains(7) ? "checked" : ""%>>Gladiators</label>
							<br /> <label><input type="checkbox"
								name="drawingCategory" value=6
								<%=drawingTagIds.contains(6) ? "checked" : ""%>>Human
								Figures</label> <br /> <label><input type="checkbox"
								name="drawingCategory" value=8
								<%=drawingTagIds.contains(8) ? "checked" : ""%>>Plants</label> <br />
							<label><input type="checkbox" name="drawingCategory"
								value=5 <%=drawingTagIds.contains(5) ? "checked" : ""%>>Other</label>
						</div>
					</div>
					<div class="form-group">
						<label for="drawing_description_latin"
							class="col-sm-3 control-label">Drawing Description in
							Latin</label>
						<div class="col-sm-6">
							<textarea name="drawing_description_latin"
								id="drawing_description_latin" class="form-control">${i.agp.figuralInfo.descriptionInLatin}</textarea>
						</div>
					</div>
					<div class="form-group">
						<label for="drawing_description_english"
							class="col-sm-3 control-label">Drawing Description
							Translation</label>
						<div class="col-sm-6">
							<textarea name="drawing_description_english"
								id="drawing_description_english" class="form-control">${i.agp.figuralInfo.descriptionInEnglish}</textarea>
						</div>
					</div>
				</div>

				<h3>Measurements</h3>

				<div class="form-group">
					<label for="floor_to_graffito_height"
						class="col-sm-3 control-label"> Height from ground </label>
					<div class="col-sm-6">
						<input type="text" name="floor_to_graffito_height"
							id="floor_to_graffito_height" class="form-control"
							value="${i.agp.heightFromGround}">
					</div>
				</div>

				<div class="form-group">
					<label class="col-sm-3 control-label" for="graffito_height">
						Graffito Height</label>
					<div class="col-sm-6">
						<input type="text" name="graffito_height" id="graffito_height"
							class="form-control" value="${i.agp.graffitoHeight}">
					</div>
				</div>

				<div class="form-group">
					<label class="col-sm-3 control-label" for="graffito_length">
						Graffito Length</label>
					<div class="col-sm-6">
						<input type="text" name="graffito_length" id="graffito_length"
							class="form-control" value="${i.agp.graffitoLength}">
					</div>
				</div>

				<div class="form-group">
					<label class="col-sm-3 control-label" for="letter_height_min">
						Minimum Letter Height</label>
					<div class="col-sm-6">
						<input type="text" name="letter_height_min" id="letter_height_min"
							class="form-control" value="${i.agp.minLetterHeight}">
					</div>
				</div>

				<div class="form-group">
					<label class="col-sm-3 control-label" for="letter_height_max">
						Maximum Letter Height</label>
					<div class="col-sm-6">
						<input type="text" name="letter_height_max" id="letter_height_max"
							class="form-control" value="${i.agp.maxLetterHeight}">
					</div>
				</div>

				<div class="form-group">
					<label class="col-sm-3 control-label" for="character_heights">
						Character Heights</label>
					<div class="col-sm-6">
						<textarea name="character_heights" id="letter_height_max"
							class="form-control">${i.agp.individualLetterHeights}</textarea>
					</div>
				</div>

				<h3>Featured Graffiti</h3>

				<div class="form-group">
					<label for="gh_fig" class="col-sm-3 control-label">Figural Graffiti?</label>
					<div class="col-sm-1">
						<input type="checkbox" name="gh_fig" id="gh_fig"
							class="form-control"
							<%=inscription.getAgp().isGreatestHitFigural() ? "checked" : ""%> />
					</div>
				</div>

				<div class="form-group">
					<label for="gh_trans" class="col-sm-3 control-label">Translation
						 Graffiti?</label>
					<div class="col-sm-1">
						<input type="checkbox" name="gh_trans" id="gh_trans"
							class="form-control"
							<%=inscription.getAgp().isGreatestHitTranslation() ? "checked" : ""%> />
					</div>
				</div>
						<!-- Sprenkle's Comment to be deleted -->
				<p class="alert alert-info">Only show the following if it is a
					featured hit...</p>
				
				
				
				<div class="form-group">
					<label for="gh_commentary" class="col-sm-3 control-label">Featured Graffiti Commentary</label>
					<div class="col-sm-7">
						<textarea rows="15" name="gh_commentary" id="gh_commentary"
							class="form-control"><%=inscription.getAgp().getGreatestHitsInfo().getCommentary()%></textarea>
					</div>
				</div>

				<p class="alert alert-danger">Preferred Image is required if a
					figural graffito.</p>

				<div class="form-group">
					<label for="gh_preferred_image" class="col-sm-3 control-label">
						Preferred Image</label>, e.g., 123456-1
					<div class="col-sm-6">
						<input type="text" name="gh_preferred_image"
							id="gh_preferred_image" class="form-control"
							value="<%=inscription.getAgp().getGreatestHitsInfo().getPreferredImage()%>" />
					</div>
				</div>


				<div class="form-group">
					<button type="submit" class="btn btn-agp">Update Graffito</button>
					<button type="button" name="back" onclick="history.back()"
						class="btn btn-agp">Go Back</button>
				</div>
			</form>
		</div>
	</div>
</body>
</html>