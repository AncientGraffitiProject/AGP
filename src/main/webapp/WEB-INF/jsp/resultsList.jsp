<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%
	/* Used by the filter and stable URIs to display results */
%>
<c:forEach var="i" items="${resultsLyst}" varStatus="graffitoIndex">
	<h4 id="${i.edrId }">
		<c:choose>
			<c:when test="${not empty i.agp.summary }">
				<c:out value="${i.agp.summary }" />
			</c:when>
			<c:otherwise>
		Graffito
		</c:otherwise>
		</c:choose>
	</h4>

	<table class="main-table" style="margin-bottom: 30px;">
		<c:choose>
			<c:when test="${not empty i.contentWithLineBreaks}">
				<tr>
					<th class="propertyLabel">Graffito:</th>
					<td>${i.contentWithLineBreaks}
				</tr>
				<c:if test="${not empty i.agp.contentTranslation}">
					<tr>
						<th><span class="propertyLabel">Translation:</span></th>
						<td>${i.agp.contentTranslation}</td>
					</tr>
				</c:if>
			</c:when>
			<c:otherwise>
				<tr>
					<th class="propertyLabel">Drawing Description:</th>
					<td>${i.agp.figuralInfo.descriptionInEnglish}</td>
				</tr>
				<c:if test="${not empty i.agp.figuralInfo.descriptionInLatin}">
					<tr>
						<th class="propertyLabel">Description In Latin:</th>
						<td>${i.agp.figuralInfo.descriptionInLatin}</td>
					</tr>
				</c:if>
			</c:otherwise>
		</c:choose>
		<c:if test="${i.numberOfImages > 0}">
			<c:set var="len" value="${i.numberOfImages}" />
			<c:set var="images" value="${i.images }" />
			<c:set var="thumbnails" value="${i.thumbnails}" />
			<c:set var="pages" value="${i.pages}" />

			<c:choose>
				<c:when test="${len == 1}">
					<tr>
						<td colspan="2"><a target="_blank" href="${pages[0]}"><img
								style="display: block; float: center; margin-left: auto; margin-right: auto;"
								class="thumbnail" src="${thumbnails[0]}" /></a></td>
					</tr>
				</c:when>
				<c:otherwise>
					<c:set var="k" value="${i.edrId}" />
				
					<tr>
						<td colspan="2"><a target="_blank" href="${pages[0]}"
							id="imgLink${k}"><img class="thumbnail"
								src="${thumbnails[0]}" id="imgSrc${k}" /></a>
							<table class="buttons">
								<tr>
									<c:forEach var="j" begin="${0}" end="${len-1}">
										<td><input type="radio" name="image${k}"
											onclick="selectImg2(${j},'${k}','${pages[j] }','${thumbnails[j]}');"
											id="${k}${j}" /> <label for="${k}${j}"> <img
												src="${thumbnails[j] }" height="60px" />
										</label></td>
									</c:forEach>
								</tr>
							</table></td>
					</tr>
				</c:otherwise>
			</c:choose>
		</c:if>
		<tr>
			<th class="propertyLabel">City:</th>
			<td><a
				href="http://pleiades.stoa.org/places/${i.agp.property.insula.city.pleiadesId}">${i.ancientCity}</a></td>
		</tr>
		<tr>
			<th class="propertyLabel">Findspot:</th>
			<td><a
				href="<%=request.getContextPath() %>/results?property=${i.agp.property.id}">${i.agp.property.propertyName}
					(${i.agp.property.insula.shortName}.${i.agp.property.propertyNumber})</a>
			</td>
		</tr>
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
						<a href="<%=request.getContextPath() %>/results?drawing=${dt.id}">${dt.name}</a>
						<c:if test="${!loopStatus.last}">, </c:if>
					</c:forEach></td>
			</tr>
		</c:if>
		<tr style="line-height: 30px;">
			<td><a
				href="<%=request.getContextPath() %>/graffito/AGP-${i.edrId}"
				id="${i.edrId}"> See more information
					&#10140;</a></td>
		</tr>

		<%
			if (session.getAttribute("authenticated") != null) {
		%>
		<tr>
			<td colspan="2" align="center">
				<form action="<%=request.getContextPath()%>/admin/updateGraffito">
					<input class="btn btn-agp" type=submit value="Edit Graffito"><input
						type="hidden" name="edrID" value="${i.edrId}" />
				</form>
			</td>
		</tr>
		<%
			}
		%>
	</table>
	<hr class="main-table" />
	
</c:forEach>