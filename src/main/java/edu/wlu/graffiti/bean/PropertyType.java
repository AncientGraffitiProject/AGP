/**
 * 
 */
package edu.wlu.graffiti.bean;

import java.util.ArrayList;
import java.util.List;

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
	private String commentary;
	//private String[] synonyms;
	private int parent_id;
	private boolean is_parent;
	private List<PropertyType> children = new ArrayList<PropertyType>();

	/**
	 * 
	 */
	public PropertyType() {
		super();
	}

	/**
	 * @param id
	 * @param name
	 * @param commentary
	 * @param parent_id
	 * @param is_parent
	 * @param children
	 */
	public PropertyType(int id, String name, String commentary, int parent_id, boolean is_parent, List<PropertyType> children) {
		super();
		this.id = id;
		this.name = name;
		this.commentary = commentary;
		//this.synonyms = parseDescription(description);
		this.parent_id = parent_id;
		this.is_parent = is_parent;
		this.children = children;
	}

	//private static String[] parseDescription(String description) {
		//String[] synonyms = description.split(", ");
		//return synonyms;
	//}

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
		return commentary;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String commentary) {
		this.commentary = commentary;
		//this.synonyms = parseDescription(description);
	}
	
	/**
	 * @return the parent id
	 */
	public int getParentId() {
		return parent_id;
	}
	
	/**
	 * @param parent_id
	 *            the id of parent category to set
	 */
	public void setParentId(int parent_id) {
		this.parent_id = parent_id;
	}
	
	/**
	 * @return true if property type is parent, false otherwise
	 */
	public boolean getIsParent() {
		return is_parent;
	}
	
	/**
	 * @param is_parent
	 *            set true if parent categroy, false otherwise
	 */
	public void setIsParent(boolean is_parent) {
		this.is_parent = is_parent;
	}
	
	/**
	 * @return the children of the property type in hierarchy
	 */
	public List<PropertyType> getChildren(){
		return children;
	}
	
	/**
	 * @param children
	 *            the list of children ids for the property type
	 */
	public void setChildren(List<PropertyType> children) {
		this.children = children;
	}

	/**
	 * checks if the synonyms for this property type are included in the
	 * property's name, as a whole word. Doesn't handle when the synonym is
	 * multiple words.
	 * 
	 * @return true if one of the synonyms is contained as a whole word in the
	 *         property name
	 */
	/*public boolean includedIn(Property p) {
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
	}*/

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
		if (term.toLowerCase().equals(name.toLowerCase())) {
			return true;
		}
		return false;
		/*String[] nameSyn = name.split("(|)");
		for (String synonym : nameSyn) {
			System.out.print(synonym + "   ");
			if (term.toLowerCase().equals(synonym.toLowerCase())) {
				return true;
			}
		}
		return false;*/
	}

}
