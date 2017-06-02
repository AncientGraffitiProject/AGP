<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page import="java.util.List"%>
<%@ page import="edu.wlu.graffiti.bean.Inscription"%>
<div id="gallery">
	<!-- Captions for the image modals -->
	<!--  Hidden Photos -->
	<div>
		<!--  Hidden Photos -->
		<%
			int idCounter = 1;
		%>
		<ul>
			<c:forEach var="i" items="${figuralHits}">
				<span class="poppedPhoto none centered" id="<%=idCounter%>">

					<li><a class="poppedPhoto2"
						href="http://www.edr-edr.it/edr_programmi/view_img.php?id_nr=${i.edrId.substring(3)}"
						target="_blank"> <img
							src="http://www.edr-edr.it/foto_epigrafi/immagini_uso/${i.edrId.substring(3,6)}/${i.agp.greatestHitsInfo.preferredImage}.jpg" />
					</a> <!--  Next and Previous Button --> <%
 	List<Inscription> hits = (List<Inscription>) request.getAttribute("figuralHits");

 		if (idCounter != hits.size()) {
 %>
						<p>
							<a class="poppedPhoto rightArrow" onclick="next(<%=idCounter%>)">
								<img src="/Graffiti/resources/images/rightArrow.jpg">
							</a>
						</p> <%
 	}
 %> <%
 	if (idCounter != 1) {
 %>
						<p>
							<a class="poppedPhoto leftArrow"
								onclick="previous(<%=idCounter%>)"> <img
								src="/Graffiti/resources/images/leftArrow.jpg">
							</a>
						</p> <%
 	}
 %> <!-- Caption -->
						<div class="poppedCaption white">
							<p>
								<a class="closeX" onclick="closePopup(<%=idCounter%>)"> <img
									src="/Graffiti/resources/images/CloseX2.png">
								</a> <a
									href="<%=request.getContextPath() %>/graffito/${i.agp.agpId}"
									target="_blank">${i.agp.summary }</a>
							</p>
							<p>${i.agp.greatestHitsInfo.commentary }</p>
						</div></li>

				</span>
				<%
					idCounter = idCounter + 1;
				%>
			</c:forEach>

		</ul>



	</div>



	<!--  UnHidden Photos-->
	<p class="centered">

		<c:forEach var="k" begin="1" end="${fn:length(figuralHits) }">

			<c:set var="inscription" value="${figuralHits[k-1]}" />
			<img class="imgPop"
				src="http://www.edr-edr.it/foto_epigrafi/thumbnails/${inscription.edrId.substring(3,6)}/th_${inscription.agp.greatestHitsInfo.preferredImage}.jpg"
				onclick="showPopup(${k})" />
		</c:forEach>
	</p>
</div>