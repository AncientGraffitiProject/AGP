/*
 * The inscription.java class holds all the information that is used to display in the results.
 */
package edu.wlu.graffiti.bean;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.text.StringEscapeUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Inscription implements Comparable<Inscription> {

	private static final String BASE_EDR_THUMBNAIL_PHOTO_URL = "http://www.edr-edr.it/foto_epigrafi/thumbnails/";
	private static final String BASE_EDR_IMAGE_PAGE_URL = "http://www.edr-edr.it/edr_programmi/view_img.php?id_nr=";
	public static final String BASE_EDR_PHOTO_URL = "http://www.edr-edr.it/foto_epigrafi/immagini_uso/";
	
	private int id;
	private String ancientCity;
	private String findSpot;
	private int findSpotPropertyID;
	private String measurements;
	private String language;
	private String content;
	private String bibliography;
	private String edrId;
	private String writingStyle;
	private String apparatus;
	private String apparatusDisplay;
	private List<Photo> photos;
	private AGPInfo agp;
	private String edrFindSpot;
	private String dateBeginning;
	private String dateEnd;
	private String dateExplanation;

	public Inscription() {
		
	}

	@JsonIgnore
	public int getId() {
		return this.id;
	}

	public void setId(final int id) {
		this.id = id;
	}
	
	@JsonIgnore
	public String getDateBeginning() {
		return this.dateBeginning;
	}

	public void setDateBeginning(final String date) {
		this.dateBeginning = date;
	}
	
	@JsonIgnore
	public String getDateEnd() {
		return this.dateEnd;
	}

	public void setDateEnd(final String date) {
		this.dateEnd = date;
	}

	@JsonIgnore
	public String getAncientCity() {
		return this.ancientCity;
	}

	public void setAncientCity(final String ancientCity) {
		this.ancientCity = ancientCity;
	}

	// Uses the split up fields from agpInscription.java to create the findspot
	@JsonIgnore
	public String getFindSpot() {
		findSpot = "";
		findSpot += agp.getProperty().getPropertyName() + " (" + agp.getProperty().getInsula().getFullName() + "." + agp.getProperty().getPropertyNumber() + ")";
		return findSpot;
	}

	/**
	 * @return the findSpotPropertyID
	 */
	@JsonIgnore
	public int getFindSpotPropertyID() {
		return findSpotPropertyID;
	}

	/**
	 * @param findSpotPropertyID
	 *            the findSpotPropertyID to set
	 */
	public void setFindSpotPropertyID(int findSpotPropertyID) {
		this.findSpotPropertyID = findSpotPropertyID;
	}

	/**
	 * @param findSpot the findSpot from EDR
	 */
	public void setEDRFindSpot(String findSpot) {
		this.edrFindSpot = findSpot;
	}
	
	public String getEDRFindSpot() {
		return edrFindSpot;
	}

	// Used in the GrafittiController.java in the method findLocationKeys to
	// send to map.jsp in order to highlight the map
	// highlighting properties --> Just need to use the property id?
	@JsonIgnore
	public int getSpotKey() {
		if (getFindSpot() != null) { // TODO do we need to check this? when will
										// an inscription not have a findspot?
			return agp.getProperty().getId();
		}
		return -1;
	}

	// highlighting insula
	@JsonIgnore
	public int getGenSpotKey() {
		if (getSpotKey() != -1) { // TODO do we need to check this?
			return agp.getProperty().getInsula().getId();
		}
		return 0;
	}

	public void setFindSpot(final String findSpot) {
		this.findSpot = findSpot;
	}

	@JsonIgnore
	public String getMeasurements() {
		return this.measurements;
	}

	public void setMeasurements(final String measurements) {
		this.measurements = measurements;
	}

	@JsonIgnore
	public String getLanguage() {
		return this.language;
	}

	public void setLanguage(final String language) {
		this.language = language;
	}

	public String getContent() {
		return this.content;
	}

	// replaces the line breaks in the database with <br> tag to create line
	// breaks for the html page
	@JsonIgnore
	public String getContentWithLineBreaks() {
		if (this.content != null) {
			return this.content.replace("\n", "<br/>");
		} else {
			return "";
		}
	}

	public void setContent(final String content) {
		this.content = content;
	}

	public String getBibliography() {
		return this.bibliography;
	}

	@JsonIgnore
	public String getBibliographyTrunc() {
		if (this.bibliography != null && !this.bibliography.isEmpty()) {
			return this.bibliography.substring(0, this.bibliography.indexOf('('));
		}
		return null;
	}

	public void setBibliography(final String bibliography) {
		this.bibliography = bibliography;
	}

	public String getEdrId() {
		return this.edrId;
	}

	public void setEdrId(final String eagleId) {
		this.edrId = eagleId;
	}

	@JsonIgnore
	public String getWritingStyle() {
		return this.writingStyle;
	}

	public void setWritingStyle(final String writingStyle) {
		this.writingStyle = writingStyle;
	}

	public String getApparatus() {
		return this.apparatus;
	}

	public void setApparatus(final String apparatus) {
		this.apparatus = apparatus;
	}
	
	@JsonIgnore
	public String getApparatusDisplay() {
		return this.apparatusDisplay;
	}
	
	public void setApparatusDisplay(final String apparatusDisplay) {
		this.apparatusDisplay = apparatusDisplay;
	}

	public AGPInfo getAgp() {
		return agp;
	}

	public void setAgp(AGPInfo agp) {
		this.agp = agp;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.edrId == null) ? 0 : this.edrId.hashCode());
		result = prime * result + this.id;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Inscription other = (Inscription) obj;
		if (this.edrId == null) {
			if (other.edrId != null)
				return false;
		} else if (!this.edrId.equals(other.edrId))
			return false;
		if (this.id != other.id)
			return false;
		return true;
	}

	public int compareTo(final Inscription inscription) {
		return this.getEdrId().compareTo(inscription.getEdrId());
	}

	/**
	 * 
	 * @return the list of EDR image URLs
	 *
	@JsonIgnore
	public List<String> getImages() {
		int numImages = this.getNumberOfImages();
		List<String> imageList = new ArrayList<String>();
		String id = this.getEdrId();
		String baseUrl = BASE_EDR_PHOTO_URL + id.substring(3, 6) + "/" + id.substring(3);
		if (numImages > 0) {
			int ind = startImageId;
			if (startImageId == 0) {
				imageList.add(baseUrl + ".jpg");
				ind = ind + 1;
			}
			for (; ind <= stopImageId; ind++) {
				imageList.add(baseUrl + "-" + ind + ".jpg");
			}
		}
		return imageList;
	}
	*/
	
	/**
	 * 
	 * @return the list of EDR image URLs
	 */
	@JsonIgnore
	public List<String> getImages() {
		List<String> imageList = new ArrayList<String>();
		//String id = this.getEdrId();
		for(Photo p : photos) {
			//imageList.add(BASE_EDR_PHOTO_URL + id.substring(3, 6) + "/" + p.getPhotoId() + ".jpg");
			imageList.add(BASE_EDR_PHOTO_URL + getEdrDirectory() + "/" + p.getPhotoId() + ".jpg");
		}
		//System.out.println("Images: " + imageList);
		return imageList;
	}
	
	/**
	 * 
	 * @return the list of the image page links on the EDR site
	 *
	@JsonIgnore
	public List<String> getPages() {
		int numImages = this.getNumberOfImages();
		List<String> pageList = new ArrayList<String>();
		String baseUrl = BASE_EDR_IMAGE_PAGE_URL + getEdrId().substring(3);
		if (numImages > 0) {
			int ind = startImageId;
			if (startImageId == 0) {
				pageList.add(baseUrl);
				ind = ind + 1;
			}
			for (; ind <= stopImageId; ind++) {
				pageList.add(baseUrl + "-" + ind);
			}
		}
		return pageList;
	}
	*/
	
	/**
	 * 
	 * @return the list of the image page links on the EDR site
	 */
	@JsonIgnore
	public List<String> getPages() {
		List<String> pageList = new ArrayList<String>();
		for(Photo p : this.photos) {
			pageList.add(BASE_EDR_IMAGE_PAGE_URL + p.getPhotoId());
		}
		//System.out.println("Pages: " + pageList);
		return pageList;
	}

	/**
	 * 
	 * @return the list of image thumbnail urls
	 * 
	 *
	@JsonIgnore
	public List<String> getThumbnails() {
		int numImages = getNumberOfImages();
		List<String> imageList = new ArrayList<String>();
		String id = getEdrId();
		String baseUrl = BASE_EDR_THUMBNAIL_PHOTO_URL + id.substring(3, 6) + "/th_" + id.substring(3);
		if (numImages > 0) {
			int ind = startImageId;
			if (startImageId == 0) {
				imageList.add(baseUrl + ".jpg");
				ind = ind + 1;
			}
			for (; ind <= stopImageId; ind++) {
				imageList.add(baseUrl + "-" + ind + ".jpg");
			}
		}
		return imageList;
	}
	*/
	
	/**
	 * 
	 * @return the list of image thumbnail urls
	 * 
	 */
	@JsonIgnore
	public List<String> getThumbnails() {
		List<String> imageList = new ArrayList<String>();
		//String id = getEdrId();
		for(Photo p : photos) {
			imageList.add(BASE_EDR_THUMBNAIL_PHOTO_URL + getEdrDirectory() + "/th_" + p.getPhotoId() + ".jpg");
		}
		//System.out.println("Thumbnails: " + imageList);
		return imageList;
	}
	
	/**
	 * @return the citation for the graffito page in AGP
	 */
	public String getCitation() {
		String title = agp.getCaption();
		if(title == null)
			title = "Graffito";
		
		DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
		Date date = new Date();
		String dateString = dateFormat.format(date);
		
		return "AGP-"+edrId+", <i>The Ancient Graffiti Project</i>, &lt;http://ancientgraffiti.org/Graffiti/graffito/AGP-"+edrId+"&gt; [accessed: "+dateString+"]";
	}
	
	/**
	 * @return the photos
	 */
	public List<Photo> getPhotos() {
		return photos;
	}

	/**
	 * @param photos the photos to set
	 */
	public void setPhotos(List<Photo> photos) {
		this.photos = photos;
	}
	
	/**
	 * @param myContent the content to pre-process
	 * @return myContent html characters converted to unicode
	 */
	public String getPreprocessedContent(String myContent) {
		if(myContent != null)
			return StringEscapeUtils.unescapeHtml4(myContent);
		
		return null;
	}
	
	/**
	 * @return the dataExplanation
	 */
	@JsonIgnore
	public String getDateExplanation() {
		return dateExplanation;
	}

	/**
	 * @param dataExplanation the dataExplanation to set
	 */
	public void setDateExplanation(String dateExplanation) {
		this.dateExplanation = dateExplanation;
	}
	
	public String getEdrDirectory() {
		String dir = this.edrId.substring(3,6);
		int i = 0;
		while (dir.charAt(i) == '0') {
			i++;
		}
		return dir.substring(i);
	}
}
