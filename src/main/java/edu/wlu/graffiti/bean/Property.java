/**
 * 
 */
package edu.wlu.graffiti.bean;

/**
 * Represents a property
 * 
 * @author Sara Sprenkle
 * 
 */
public class Property {

	private int id;
	private String modern_city;
	private String insula;
	private int property_number;
	private String property_name;

	/**
	 * 
	 */
	public Property() {
		super();
	}

	/**
	 * @param id
	 * @param modern_city
	 * @param insula
	 * @param property_number
	 * @param property_name
	 */
	public Property(int id, String modern_city, String insula,
			int property_number, String property_name) {
		super();
		this.id = id;
		this.modern_city = modern_city;
		this.insula = insula;
		this.property_number = property_number;
		this.property_name = property_name;
	}

	/**
	 * @return the id
	 */
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
	public String getModern_city() {
		return modern_city;
	}

	/**
	 * @param modern_city
	 *            the modern_city to set
	 */
	public void setModern_city(String modern_city) {
		this.modern_city = modern_city;
	}

	/**
	 * @return the insula
	 */
	public String getInsula() {
		return insula;
	}

	/**
	 * @param insula
	 *            the insula to set
	 */
	public void setInsula(String insula) {
		this.insula = insula;
	}

	/**
	 * @return the property_number
	 */
	public int getPropertyNumber() {
		return property_number;
	}

	/**
	 * @param property_number
	 *            the property_number to set
	 */
	public void setPropertyNumber(int property_number) {
		this.property_number = property_number;
	}

	/**
	 * @return the property_name
	 */
	public String getPropertyName() {
		return property_name;
	}

	/**
	 * @param property_name
	 *            the property_name to set
	 */
	public void setPropertyName(String property_name) {
		this.property_name = property_name;
	}

}
