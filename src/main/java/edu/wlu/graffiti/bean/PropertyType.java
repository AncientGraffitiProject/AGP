/**
 * 
 */
package edu.wlu.graffiti.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
		String[] synonyms = description.split(", ");
		return synonyms;
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

	/**
	 * checks if the synonyms for this property type are included in the
	 * property's name, as a whole word. Doesn't handle when the synonym is
	 * multiple words.
	 * 
	 * @return true if one of the synonyms is contained as a whole word in the
	 *         property name
	 */
	public boolean includedIn(Property p) {
		// TODO: Convert to using a regular expression instead of splitting on
		// spaces.
		String[] wordsInProperty = p.getPropertyName().toLowerCase().split(" ");
		for (String synonym : synonyms) {
			for (String word : wordsInProperty) {
				if (word.equals(synonym.toLowerCase())) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public int compareTo(PropertyType propertyType) {
		return this.getName().compareTo(propertyType.getName());
	}

	/**
	 * 
	 * @param term
	 * @return true if one of the synonyms equals the passed-in term.
	 */
	public boolean includes(String term) {
		for (String synonym : synonyms) {
			if (term.toLowerCase().equals(synonym.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

}
