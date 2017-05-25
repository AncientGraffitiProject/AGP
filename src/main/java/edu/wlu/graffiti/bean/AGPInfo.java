/*
 * AGP inscription class holds information for inscription.java that is not found in the EAGLE database and is used to either simplify
 * the information from EAGLE such as the separation of findspot into multiple fields or use the extra information
 * for additional functionalities such as drawing tags and heights.
 * Gets the information from agp_inscription_annotations in the database.
 */
package edu.wlu.graffiti.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class AGPInfo implements Comparable<AGPInfo> {

	private int id;
	private String edrId;
	private String commentary;
	private String contentTranslation;
	private String summary;
	private String writingStyleInEnglish;
	private String languageInEnglish;
	private Property property;
	private String cil;
	private String langner;
	private String graffito_height;
	private String graffito_length;
	private String letter_height_min;
	private String letter_height_max;
	private String letter_with_flourishes_height_min;
	private String letter_with_flourishes_height_max;
	private String height_from_ground;
	private String individualLetterHeights;
	private String epidoc;
	private FiguralInfo figuralInfo;
	private boolean hasFiguralComponent = false;
	private boolean isGreatestHitTranslation = false;
	private boolean isGreatestHitFigural = false;
	private GreatestHitsInfo ghInfo;

	public AGPInfo() {

	}

	@JsonIgnore
	public int getId() {
		return this.id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	@JsonIgnore
	public String getEdrId() {
		return edrId;
	}

	public String getAgpId() {
		return "AGP-" + edrId;
	}

	public String getSummary() {
		return summary;
	}

	public String getCommentary() {
		return commentary;
	}

	public String getContentTranslation() {
		return contentTranslation;
	}

	public String getWritingStyleInEnglish() {
		return writingStyleInEnglish;
	}

	public void setWritingStyleInEnglish(String writingStyleInEnglish) {
		this.writingStyleInEnglish = writingStyleInEnglish;
	}

	public String getLanguageInEnglish() {
		return languageInEnglish;
	}

	public void setLanguageInEnglish(String languageInEnglish) {
		this.languageInEnglish = languageInEnglish;
	}

	// measurement related getter methods
	public String getGraffitoHeight() {
		return graffito_height;
	}

	public String getGraffitoLength() {
		return graffito_length;
	}

	public String getMinLetterHeight() {
		return letter_height_min;
	}

	public String getMaxLetterHeight() {
		return letter_height_max;
	}

	/**
	 * @return the letter_with_flourishes_height_min
	 */
	public String getMinLetterWithFlourishesHeight() {
		return letter_with_flourishes_height_min;
	}

	/**
	 * @param letter_with_flourishes_height_min the letter_with_flourishes_height_min to set
	 */
	public void setMinLetterWithFlourishesHeight(String letter_with_flourishes_height_min) {
		this.letter_with_flourishes_height_min = letter_with_flourishes_height_min;
	}

	/**
	 * @return the letter_with_flourishes_height_max
	 */
	public String getMaxLetterWithFlourishesHeight() {
		return letter_with_flourishes_height_max;
	}

	/**
	 * @param letter_with_flourishes_height_max the letter_with_flourishes_height_max to set
	 */
	public void setMaxLetterWithFlourishesHeight(String letter_with_flourishes_height_max) {
		this.letter_with_flourishes_height_max = letter_with_flourishes_height_max;
	}

	// measurement related setter methods
	public void setGraffitoHeight(String graffito_height) {
		this.graffito_height = graffito_height;
	}

	public void setGraffitoLength(String graffito_length) {
		this.graffito_length = graffito_length;
	}

	public void setMinLetterHeight(String letter_height_min) {
		this.letter_height_min = letter_height_min;
	}

	public void setMaxLetterHeight(String letter_height_max) {
		this.letter_height_max = letter_height_max;
	}
	//

	public void setEdrId(String edrId) {
		this.edrId = edrId;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public void setCommentary(String commentary) {
		this.commentary = commentary;
	}

	public void setContentTranslation(String translation) {
		this.contentTranslation = translation;
	}

	public String getCil() {
		return cil;
	}

	public void setCil(String cil) {
		this.cil = cil;
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
		final AGPInfo other = (AGPInfo) obj;
		if (this.edrId == null) {
			if (other.edrId != null)
				return false;
		} else if (!this.edrId.equals(other.edrId))
			return false;
		if (this.id != other.id)
			return false;
		return true;
	}

	public int compareTo(final AGPInfo inscription) {
		return this.getEdrId().compareTo(inscription.getEdrId());
	}

	/**
	 * @return the langner
	 */
	public String getLangner() {
		return langner;
	}

	/**
	 * @param langner
	 *            the langner to set
	 */
	public void setLangner(String langner) {
		this.langner = langner;
	}

	/**
	 * @return the individualLetterHeights
	 */
	public String getIndividualLetterHeights() {
		return individualLetterHeights;
	}

	/**
	 * @param individualLetterHeights
	 *            the individualLetterHeights to set
	 */
	public void setIndividualLetterHeights(String individualLetterHeights) {
		this.individualLetterHeights = individualLetterHeights;
	}

	/**
	 * @return the isGreatestHitTranslation
	 */
	@JsonIgnore
	public boolean isGreatestHitTranslation() {
		return isGreatestHitTranslation;
	}

	/**
	 * @param isGreatestHitTranslation
	 *            the isGreatestHitTranslation to set
	 */
	public void setGreatestHitTranslation(boolean isGreatestHitTranslation) {
		this.isGreatestHitTranslation = isGreatestHitTranslation;
	}

	/**
	 * @return the isGreatestHitFigural
	 */
	@JsonIgnore
	public boolean isGreatestHitFigural() {
		return isGreatestHitFigural;
	}

	/**
	 * @param isGreatestHitFigural
	 *            the isGreatestHitFigural to set
	 */
	public void setGreatestHitFigural(boolean isGreatestHitFigural) {
		this.isGreatestHitFigural = isGreatestHitFigural;
	}

	/**
	 * @return the figuralInfo
	 */
	@JsonInclude(Include.NON_NULL)
	public FiguralInfo getFiguralInfo() {
		return figuralInfo;
	}

	/**
	 * @param figuralInfo
	 *            the figuralInfo to set
	 */
	public void setFiguralInfo(FiguralInfo figuralInfo) {
		this.figuralInfo = figuralInfo;
	}

	/**
	 * @return the hasFiguralComponent
	 */
	@JsonIgnore
	public boolean hasFiguralComponent() {
		return hasFiguralComponent;
	}

	/**
	 * @param hasFiguralComponent
	 *            the hasFiguralComponent to set
	 */
	public void setHasFiguralComponent(boolean hasFiguralComponent) {
		this.hasFiguralComponent = hasFiguralComponent;
	}

	@JsonIgnore
	public boolean getHasFiguralComponent() {
		return hasFiguralComponent;
	}

	/**
	 * @return the epidoc
	 */
	@JsonIgnore
	public String getEpidoc() {
		return epidoc;
	}

	/**
	 * @param epidoc
	 *            the epidoc to set
	 */
	public void setEpidoc(String epidoc) {
		this.epidoc = epidoc;
	}

	/**
	 * @return the height_from_ground
	 */
	public String getHeightFromGround() {
		return height_from_ground;
	}

	/**
	 * @param height_from_ground
	 *            the height_from_ground to set
	 */
	public void setHeightFromGround(String height_from_ground) {
		this.height_from_ground = height_from_ground;
	}

	/**
	 * @return the greatest hits info
	 */
	@JsonIgnore
	public GreatestHitsInfo getGreatestHitsInfo() {
		return ghInfo;
	}

	/**
	 * @param ghInfo
	 *            the greatest hits info
	 */
	public void setGreatestHitsInfo(GreatestHitsInfo ghInfo) {
		this.ghInfo = ghInfo;
	}

	/**
	 * @return the property
	 */
	public Property getProperty() {
		return property;
	}

	/**
	 * @param property
	 *            the property to set
	 */
	public void setProperty(Property property) {
		this.property = property;
	}

}
