package edu.wlu.graffiti.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Class holds the property link information for Inscription.java
 * class it receives its information from property_links table in the database.
 * 
 * @author Hammad Ahmad
 *
 */
public class PropertyLink implements Comparable<PropertyLink> {

	private int id;
	private String linkName;
	private String link;

	public PropertyLink() {
		super();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(linkName);
		builder.append(" ");
		builder.append(link);
		return builder.toString();
	}

	public void setId(int id) {
		this.id = id;
	}

	@JsonIgnore
	public String getLinkName() {
		return this.linkName;
	}

	public void setLinkName(final String linkName) {
		this.linkName = linkName;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.link == null) ? 0 : this.link.hashCode());
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
		final PropertyLink other = (PropertyLink) obj;
		if (this.link == null) {
			if (other.link != null)
				return false;
		} else if (!this.link.equals(other.link))
			return false;
		if (this.linkName != other.linkName)
			return false;
		return true;
	}

	/**
	 * compares by name, alphabetically
	 */
	public int compareTo(final PropertyLink other) {
		return this.getLink().compareTo(other.getLink());
	}

}
