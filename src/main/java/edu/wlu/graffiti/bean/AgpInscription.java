/*
 * AGP inscription class holds information for inscription.java that is not found in the EAGLE database and is used to either simplify
 * the information from EAGLE such as the separation of findspot into multiple fields or use the extra information
 * for additional functionalities such as drawing tags and heights.
 * Gets the information from agp_inscription_annotations in the database.
 */
package edu.wlu.graffiti.bean;

public class AgpInscription implements Comparable<AgpInscription> {
	private int id;
	private String eagleId;
	private String floor_to_graffito_height ;
	private String description;
	private String comment;
	private String translation;
	private String modern_city;
	private String property_name;
	private String insula;
	private String property_number;
	

	public AgpInscription() {

	}

	public int getId() {
		return this.id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	

	public String getEagleId() {
		return eagleId;
	}

	public String getFloor_to_graffito_height() {
		return floor_to_graffito_height;
	}

	public String getDescription() {
		return description;
	}

	public String getComment() {
		return comment;
	}

	public String getTranslation() {
		return translation;
	}

	public void setEagleId(String eagleId) {
		this.eagleId = eagleId;
	}

	public void setFloor_to_graffito_height(String floor_to_graffito_height) {
		this.floor_to_graffito_height = floor_to_graffito_height;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setTranslation(String translation) {
		this.translation = translation;
	}
	

	public String getModern_city() {
		return modern_city;
	}

	public String getProperty_name() {
		return property_name;
	}

	public String getInsula() {
		return insula;
	}

	public String getProperty_number() {
		return property_number;
	}

	public void setModern_city(String modern_city) {
		this.modern_city = modern_city;
	}

	public void setProperty_name(String property_name) {
		this.property_name = property_name;
	}

	public void setInsula(String insula) {
		this.insula = insula;
	}

	public void setPropertyNumber(String property_number) {
		this.property_number = property_number;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.eagleId == null) ? 0 : this.eagleId.hashCode());
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
		final AgpInscription other = (AgpInscription) obj;
		if (this.eagleId == null) {
			if (other.eagleId != null)
				return false;
		} else if (!this.eagleId.equals(other.eagleId))
			return false;
		if (this.id != other.id)
			return false;
		return true;
	}

	public int compareTo(final AgpInscription inscription) {
		return this.getEagleId().compareTo(inscription.getEagleId());
	}

}
