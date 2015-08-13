/*
 * The inscription.java class holds all the information that is used to display in the results.
 */
package edu.wlu.graffiti.bean;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Inscription implements Comparable<Inscription> {

	private final static Map<String, String> cityToPleidesMapping = new HashMap<>();
	static {
		cityToPleidesMapping.put("Herculaneum", "432873");
		cityToPleidesMapping.put("Pompeii", "433032");
	}

	private int id;
	private String ancientCity;
	private String pleidesID;
	private String findSpot;
	private int findSpotPropertyID;
	private String measurements;
	private String language;
	private String content;
	private String bibliography;
	private String eagleId;
	private String writingStyle;
	private String url;
	private AgpInscription agp;
	private Set<DrawingTag> drawingTags;

	public Inscription() {
		drawingTags = new HashSet<DrawingTag>();
	}

	public int getId() {
		return this.id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public String getAncientCity() {
		return this.ancientCity;
	}

	public void setAncientCity(final String ancientCity) {
		this.ancientCity = ancientCity;

		this.pleidesID = cityToPleidesMapping.get(this.ancientCity);
	}

	public String getPleidesID() {
		return pleidesID;
	}

	// Uses the split up fields from agpInscription.java to create the findspot
	public String getFindSpot() {
		findSpot = "";
		findSpot += agp.getProperty_name() + " (" + agp.getInsula() + "."
				+ agp.getProperty_number() + ")";
		return findSpot;
	}

	/**
	 * @return the findSpotPropertyID
	 */
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
	 * @param findSpot
	 *            the findSpot to set
	 */
	public void setFindSpot(String findSpot) {
		this.findSpot = findSpot;
	}

	// Used in the GrafittiController.java in the method findLocationKeys to
	// send to map.jsp in order to highlight the map
	public String getSpotKey() {
		if (getFindSpot() != null) {
			String genspot = "";
			genspot += agp.getInsula() + "." + agp.getProperty_number();
			return genspot;
		}
		return null;
	}

	public String getGenSpotKey() {
		final String spotKey = getSpotKey();
		if (spotKey != null) {
			final int end = spotKey.lastIndexOf('.');
			return spotKey.substring(0, end);
		}
		return null;
	}

	public void setfindSpot(final String findSpot) {
		this.findSpot = findSpot;
	}

	public String getMeasurements() {
		return this.measurements;
	}

	public void setMeasurements(final String measurements) {
		this.measurements = measurements;
	}

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

	public String getBibliographyTrunc() {
		if (this.bibliography != null) {
			return this.bibliography.substring(0,
					this.bibliography.indexOf('('));
		}
		return null;
	}

	public void setBibliography(final String bibliography) {
		this.bibliography = bibliography;
	}

	public String getEagleId() {
		return this.eagleId;
	}

	public void setEagleId(final String eagleId) {
		this.eagleId = eagleId;
	}

	public String getWritingStyle() {
		return this.writingStyle;
	}

	public void setWritingStyle(final String writingStyle) {
		this.writingStyle = writingStyle;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	public AgpInscription getAgp() {
		return agp;
	}

	public Set<DrawingTag> getDrawingTags() {
		return drawingTags;
	}

	public void setAgp(AgpInscription agp) {
		this.agp = agp;
	}

	public void addDrawingTag(DrawingTag drawingtag) {
		this.drawingTags.add(drawingtag);
	}

	public void addDrawingTags(List<DrawingTag> drawingTags) {
		for (DrawingTag dt : drawingTags) {
			addDrawingTag(dt);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.eagleId == null) ? 0 : this.eagleId.hashCode());
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
		if (this.eagleId == null) {
			if (other.eagleId != null)
				return false;
		} else if (!this.eagleId.equals(other.eagleId))
			return false;
		if (this.id != other.id)
			return false;
		return true;
	}

	public int compareTo(final Inscription inscription) {
		return this.getEagleId().compareTo(inscription.getEagleId());
	}

}
