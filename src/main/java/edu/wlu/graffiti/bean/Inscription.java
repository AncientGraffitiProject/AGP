/*
 * The inscription.java class holds all the information that is used to display in the results.
 */
package edu.wlu.graffiti.bean;

import java.util.ArrayList;
import java.util.List;

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
	private int numberOfImages;
	private int startImageId;
	private int stopImageId;
	private AGPInfo agp;
	private String edrFindSpot;

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
	public String getSpotKey() {
		if (getFindSpot() != null) { // TODO do we need to check this? when will
										// an inscription not have a findspot?
			return "p" + String.valueOf(agp.getProperty().getId());
		}
		return "";
	}

	// highlighting insula
	@JsonIgnore
	public String getGenSpotKey() {
		if (getSpotKey() != "") { // TODO do we need to check this?
			return "i" + String.valueOf(agp.getProperty().getInsula().getId());
		}
		return "";
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

	@JsonIgnore
	public int getNumberOfImages() {
		return this.numberOfImages;
	}

	public void setNumberOfImages(final int num) {
		this.numberOfImages = num;
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
	 * @return the startImageId
	 */
	@JsonIgnore
	public int getStartImageId() {
		return startImageId;
	}

	/**
	 * @param startImageId
	 *            the startImageId to set
	 */
	public void setStartImageId(int startImageId) {
		this.startImageId = startImageId;
	}

	/**
	 * @return the stopImageId
	 */
	@JsonIgnore
	public int getStopImageId() {
		return stopImageId;
	}

	/**
	 * @param stopImageId
	 *            the stopImageId to set
	 */
	public void setStopImageId(int stopImageId) {
		this.stopImageId = stopImageId;
	}

	/**
	 * 
	 * @return the list of EDR image URLs
	 */
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

	/**
	 * 
	 * @return the list of the image page links on the EDR site
	 */
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

	/**
	 * 
	 * @return the list of image thumbnail urls
	 * 
	 */
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

}
