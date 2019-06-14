/*
 * Drawing tag class holds the photos information for Inscription.java class it receives its information
 * from photos table in the database. 
 */
package edu.wlu.graffiti.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * @author Hammad Ahmad
 *
 */
public class Photo implements Comparable<Photo> {
	
	private int id;
	private String edrId;
	private String photoId;
	

	public Photo() {
		super();
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(edrId);
		builder.append(" ");
		builder.append(photoId);
		return builder.toString();
	}

	public void setId(int id) {
		this.id = id;
	}
	
	@JsonIgnore
	public String getEdrId() {
		return this.edrId;
	}

	public void setEdrId(final String id) {
		this.edrId = id;
	}


	public String getPhotoId() {
		return photoId;
	}

	public void setPhotoId(String name) {
		if(name != null) {
			while (name.length() < 6) {
				name = "0" + name;
			}
		}
		this.photoId = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.photoId == null) ? 0 : this.photoId.hashCode());
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
		final Photo other = (Photo) obj;
		if (this.photoId == null) {
			if (other.photoId != null)
				return false;
		} else if (!this.photoId.equals(other.photoId))
			return false;
		if (this.edrId != other.edrId)
			return false;
		return true;
	}

	/**
	 * compares by name, alphabetically
	 */
	public int compareTo(final Photo photo) {
		return this.getPhotoId().compareTo(photo.getPhotoId());
	}

}
