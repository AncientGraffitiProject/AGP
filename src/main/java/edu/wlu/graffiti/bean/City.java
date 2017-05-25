package edu.wlu.graffiti.bean;

public class City {
	private String name;
	private String description;
	private String pleiadesID;
	
	/**
	 * @return the city's name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name name of the city
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the city's description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description description of the city
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the pleiadesID
	 */
	public String getPleiadesId() {
		return pleiadesID;
	}
	/**
	 * @param pleiadesID the city's pleiadesID
	 */
	public void setPleiadesId(String pleiadesID) {
		this.pleiadesID = pleiadesID;
	}
}
