<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div id="original">
	<!--  <h3>Graffiti from Herculaneum</h3>  -->
	<table class="table table-striped table-bordered"
		id="greatest_hits_table">
		<thead>
			<tr>
				<th id="idCol">ID</th>
				<th id="textCol">Text (Latin or Greek)</th>
				<th id="transCol">Translation</th>
				<!-- 
				<th id="imgCol">Commentary</th>
				 -->
			</tr>
		</thead>
		<!--  LOOOP  -->
		<tbody>
			<c:forEach var="i" items="${translationHits}">
				<tr>
					<td><a
						href="<%=request.getContextPath() %>/graffito/${i.agp.agpId}">${i.agp.agpId}</a>
						<c:if test="${not empty i.agp.cil }">
							<br />${i.agp.cil}
						</c:if> <c:if test="${not empty i.agp.langner }">
							<br />${i.agp.langner}
						</c:if></td>
					<td><em>${i.contentWithLineBreaks }</em></td>
					<td><c:if test="${not empty i.agp.contentTranslation }">
							<p class="trans" style="display: none;">
								<em>${i.agp.contentTranslation}</em>
							</p>
							<input type="button" class="btn btn-agp showTrans"
								value="Show Translation">
						</c:if></td>
						<!--  
					<td style="white-space: pre-wrap">${i.agp.greatestHitsInfo.commentary }</td>
						 -->
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>