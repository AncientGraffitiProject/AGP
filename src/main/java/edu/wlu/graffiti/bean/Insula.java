package edu.wlu.graffiti.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Represents an insula
 * 
 * @author whitej
 * 
 */
public class Insula {

	private int id;
	private String ancientCity;
	private String shortName;
	private String fullName;
	private String pleiadesId;
	private City city;

	/**
	 * 
	 */
	public Insula() {
		super();
	}

	@Override
	public String toString() {
		return "Insula [id=" + id + ", modern_city=" + ancientCity + ", name="
				+ shortName + ", description=" + fullName + "]";
	}

	/**
	 * @param id
	 * @param modern_city
	 * @param name
	 * @param description
	 */
	public Insula(int id, String modern_city, String name, String description) {
		super();
		this.id = id;
		this.ancientCity = modern_city;
		this.shortName = name;
		this.fullName = description;
	}

	/**
	 * @return the id
	 */
	@JsonIgnore
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the modern_city
	 */
	@JsonIgnore
	public String getModernCity() {
		return ancientCity;
	}

	/**
	 * @param modern_city
	 *            the modern_city to set
	 */
	public void setModernCity(String modern_city) {
		this.ancientCity = modern_city;
	}

	/**
	 * @return the insula
	 */
	public String getShortName() {
		return shortName;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setShortName(String name) {
		this.shortName = name;
	}

	/**
	 * @return the description
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * @param fullname
	 *            the full name of the insula
	 */
	public void setFullName(String fullname) {
		this.fullName = fullname;
	}

	/**
	 * @return the pleiadesId
	 */
	public String getPleiadesId() {
		return pleiadesId;
	}

	/**
	 * @param pleiadesId the pleiadesId to set
	 */
	public void setPleiadesId(String pleiadesId) {
		this.pleiadesId = pleiadesId;
	}

	/**
	 * @return the city
	 */
	public City getCity() {
		return city;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(City city) {
		this.city = city;
	}

}
