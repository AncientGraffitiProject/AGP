/*
 * Drawing tag class holds the drawing tag information for Inscription.java class it receives its information
 * from drawing_tags table in the database. 
 */
package edu.wlu.graffiti.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class DrawingTag implements Comparable<DrawingTag> {
	private int id;
	private String name;
	private String description;
	

	public DrawingTag() {
		super();
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(id);
		builder.append(" ");
		builder.append(name);
		builder.append(" ");
		builder.append(description);
		return builder.toString();
	}

	@JsonIgnore
	public int getId() {
		return this.id;
	}

	public void setId(final int id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}
	

	public void setDescription(String description) {
		this.description = description;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
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
		final DrawingTag other = (DrawingTag) obj;
		if (this.name == null) {
			if (other.name != null)
				return false;
		} else if (!this.name.equals(other.name))
			return false;
		if (this.id != other.id)
			return false;
		return true;
	}

	/**
	 * compares by name, alphabetically
	 */
	public int compareTo(final DrawingTag inscription) {
		return this.getName().compareTo(inscription.getName());
	}

}
