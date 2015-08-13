/**
 * 
 */
package edu.wlu.graffiti.bean;

/**
 * Represents the types of properties
 * 
 * @author Sara Sprenkle
 * 
 */
public class PropertyType implements Comparable<PropertyType> {
	private int id;
	private String name;
	private String description;
	private String[] synonyms;

	/**
	 * 
	 */
	public PropertyType() {
		super();
	}

	/**
	 * @param id
	 * @param name
	 * @param description
	 */
	public PropertyType(int id, String name, String description) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.synonyms = parseDescription(description);
	}

	private static String[] parseDescription(String description) {
		String[] synonyms = description.split(",");
		return synonyms;
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
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
		this.synonyms = parseDescription(description);
	}

	public boolean includes(Property p) {
		for (String synonym : synonyms) {
			if (p.getPropertyName().toLowerCase()
					.contains(synonym.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int compareTo(PropertyType propertyType) {
		return this.getName().compareTo(propertyType.getName());
	}

}
