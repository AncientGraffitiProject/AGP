<!-- Author: Trevor Stalnaker -->
<!-- The page that displays the figural graffiti gallery -->
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page import="java.util.List"%>
<%@ page import="edu.wlu.graffiti.bean.Inscription"%>
<div id="gallery">
	<p>Selected Figural Graffiti</p>
			
	<!--Modal "Hidden" Photos-->
	<%int idCounter = 1;%>
		
	<!-- Iterate through all figural hits -->
	<c:forEach var="i" items="${figuralHits}">
	
		<!-- Create a modal element -->
		<div class="modal" id="<%=idCounter%>">
		
			<!-- Create the X in the upper right hand corner -->
			<span class="close" onclick="closeModal(<%=idCounter%>)">&times;</span>
			
			<!-- Add the EDR ID and the CIL above the image -->
			<p class="modal-content tagPara">AGP-${i.agp.edrId}</p>
			<p class="modal-content tagPara">${i.agp.cil}</p>
			
			<!-- Add an image link to the modal -->
			<a href="http://www.edr-edr.it/edr_programmi/view_img.php?id_nr=${i.agp.greatestHitsInfo.preferredImage}" 
			   target="_blank">
				<img class="modal-content" id="img01" 
				     src="http://www.edr-edr.it/foto_epigrafi/immagini_uso/${i.edrDirectory}/${i.agp.greatestHitsInfo.preferredImage}.jpg"/>
			</a>
				<!-- Code for testing on the local branch -->
				<!--<a href="../resources/images/${i.agp.greatestHitsInfo.preferredImage}" target="_blank">
					<img class="modal-content" id="img01" 
				     	src="../resources/images/${i.agp.greatestHitsInfo.preferredImage}"/>
				</a>-->
			
			<!-- Create the left and right arrow buttons -->
			<div class="modal-content" id="arrowButtonDiv">
				<%if (idCounter != 1){%>
					<button id="leftArrow" class="arrow" onclick="goLeft(<%=idCounter%>)">&lt&lt&lt</button>
				<%}%>
				<%List<Inscription> hits = (List<Inscription>) request.getAttribute("figuralHits");
				if (idCounter != hits.size()) {%>
					<button id="rightArrow" class="arrow" onclick="goRight(<%=idCounter%>)">&gt&gt&gt</button>
				<%}%>
			</div>
			
			<!-- Add caption below image -->
			<p class="modal-content caption">${i.agp.greatestHitsInfo.commentary }</p>
			
			<!-- Add the learn more link to the bottom of the modal -->
			<p class="modal-content linkPara">
			<a class="tagPara" href="<%=request.getContextPath() %>/graffito/AGP-${i.edrId}"
							  id="${i.edrId}"> Learn More </a></p>
							  
		</div>
		<%idCounter = idCounter + 1;%>
	</c:forEach>
	

	<!--Gallery "UnHidden" Photos-->
	<p class="centered" id="galleryPara">
		<br style="line-height:8px;">
		<c:forEach var="k" begin="1" end="${fn:length(figuralHits) }">
			<c:set var="inscription" value="${figuralHits[k-1]}" />
			<!-- Currently using thumb nails, to make images clearer use the same img src from above -->
			<img class="imgPop"			
				 src="http://www.edr-edr.it/foto_epigrafi/immagini_uso/${inscription.edrDirectory}/${inscription.agp.greatestHitsInfo.preferredImage}.jpg"
				 alt="No Image Available"
				 onclick="imgClicked(${k})"/>
			<!-- Code for testing on the Local Branch -->
			<!--<img class="imgPop"			
				 src="../resources/images/${inscription.agp.greatestHitsInfo.preferredImage}"
				 alt="No Image Available"
				 onclick="imgClicked(${k})"/>   -->
		</c:forEach>
	</p>
</div>
