<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.*"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<%@include file="/resources/common_head.txt"%>
<meta name="viewport" content="width=device-width, initial-scale=1">

<title>Add Graffito</title>

<link rel="stylesheet"
	href="<c:url value="/resources/css/addGraffito-form-basic.css"/>" />
</head>

<body>
	<%@include file="/WEB-INF/jsp/header.jsp"%>

	<div class="container">

		<form class="form-basic" method="post" action="AdminAddGraffito">

			<div class="form-title-row">
				<h1>Add a New Graffito</h1>
			</div>

			<div class="form-row">
				<label> <span>EDR ID</span> <input type="text" name="edrID"
					required>
				</label>
			</div>

			<div class="form-row">
				<label> <span>CIL Number</span> <input type="text"
					name="cil">
				</label>
			</div>

			<div class="form-row">
				<label> <span>Graffito</span> <textarea name="content"></textarea>
				</label>
			</div>

			<div class="form-row">
				<label> <span>Graffito Translation</span> <textarea
						name="content_translation"></textarea>
				</label>
			</div>

			<div class="form-row">
				<label> <span>Height from ground</span> <input type="text"
					name="floor_to_graffito_height">
				</label>
			</div>

			<div class="form-row">
				<label> <span>Graffito Height</span> <input type="text"
					name="graffito_height">
				</label>
			</div>

			<div class="form-row">
				<label> <span>Graffito Length</span> <input type="text"
					name="graffito_length">
				</label>
			</div>

			<div class="form-row">
				<label> <span>Minimum Letter Height</span> <input
					type="text" name="letter_height_min">
				</label>
			</div>

			<div class="form-row">
				<label> <span>Maximum Letter Height</span> <input
					type="text" name="letter_height_max">
				</label>
			</div>

			<div class="form-row">
				<label><span>City</span></label>
				<div class="form-radio-buttons">

					<label> <input type="radio" name="city" value="Pompeii"><span>Pompeii</span></label>
					<label> <input type="radio" name="city" value="Herculaneum"><span>Herculaneum</span></label>
				</div>

			</div>

			<div class="form-row">
				<label><span>Find Spot/ Insula</span></label>
				<div class="form-radio-buttons">

					<div class="highlight">

						<select name="insula" required>
							<optgroup label="Pompeii">
								<option value="insula_I_8">Insula I.8</option>
								<option value="insula_VII_12">Insula VII.12</option>
							</optgroup>
							<optgroup label="Herculaneum">
								<option value="insula_II">Insula II</option>
								<option value="insula_III">Insula III</option>
								<option value="insula_IV">Insula IV</option>
								<option value="insula_V">Insula V</option>
								<option value="insula_VI">Insula VI</option>
								<option value="insula_VII">Insula VII</option>
								<option value="insula_orientalist_I">Insula Orientalist
									I</option>
								<option value="insula_orientalist_II">Insula
									Orientalist II</option>
								<option value="insula_outsideWalls">Insula Outside
									Walls</option>
								<option value="insula_withinWalls">Insula Within Walls</option>
							</optgroup>
						</select>
					</div>
				</div>
			</div>


			<div class="form-row">
				<label><span>Property</span></label>
				<div class="form-radio-buttons">

					<div class="highlight">

						<select name="property" required>
							<optgroup label="Insula II">
								<option value="II.1 House of Aristides">II.1 House of
									Aristides</option>
								<option value="II.2 House of Argus">II.2 House of Argus</option>
								<option value="II.3 House of the Genius">II.3 House of
									the Genius</option>
								<option value="II.4">II.4</option>
								<option value="II.5">II.5</option>
								<option value="II.7 Thermopolium">II.7 Thermopolium</option>
								<option value="II.8">II.8</option>
							</optgroup>
						</select>
					</div>
				</div>
			</div>

			<div class="form-row">
				<label><span>Drawing Category<br> <br> <small>Use
							CTRL to select multiple categories.</small></span></label> <select
					name="drawingCategory" multiple="multiple" required>
					<option value=3>Animals</option>
					<option value=1>Boats</option>
					<option value=4>Erotic Images</option>
					<option value=2>Geometric Designs</option>
					<option value=7>Gladiators</option>
					<option value=6>Human Figures</option>
					<option value=8>Plants</option>
					<option value=5>Other</option>

				</select>
			</div>

			<div class="form-row">
				<label><span>Writing Style</span></label>
				<div class="form-radio-buttons">

					<label> <input type="radio" name="writingStyle"
						value="inscribed" required> <span>Inscribed/Scratched</span></label>
					<label> <input type="radio" name="writingStyle"
						value="charcoal"><span>Charcoal</span></label> <label><input
						type="radio" name="writingStyle" value="other"><span>Other</span>
					</label>
				</div>
			</div>

			<div class="form-row">
				<label><span>Language</span></label>
				<div class="form-radio-buttons">

					<div>
						<label> <input type="radio" name="lang" value="Latina"
							required> <span>Latina</span></label> <label> <input
							type="radio" name="lang" value="Greek"><span>Greek</span></label>
						<label><input type="radio" name="lang" value="Latin-Greek"><span>Latin-Greek</span>
						</label>
					</div>
				</div>
			</div>

			<div class="form-row">
				<label> <span>Height from Ground</span> <input type="text"
					name="height_frm_grnd">
				</label>
			</div>

			<div class="form-row">
				<label> <span>Letter Height</span> <input type="text"
					name="letter_height">
				</label>
			</div>

			<div class="form-row">
				<label> <span>Reference(s)</span> <textarea
						name="references"></textarea>
				</label>
			</div>

			<div class="form-row">
				<label> <span>Apparatus Criticus</span> <textarea
						name="app_crit"></textarea>
				</label>
			</div>

			<div class="form-row">
				<button type="submit">Add Graffito</button>
				<button type="button" name="back" onclick="history.back()">Go
					Back</button>
			</div>

		</form>

	</div>
</body>
</html>