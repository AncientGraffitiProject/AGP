/**
 * 
 */
package edu.wlu.graffiti.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Stores information about the graffiti that are greatest hits
 * @author sprenkle
 *
 */
public class GreatestHitsInfo {

	private String commentary="";
	private String preferredImage="";
	/**
	 * @return the commentary
	 */
	@JsonIgnore
	public String getCommentary() {
		return commentary;
	}
	/**
	 * @param commentary the commentary to set
	 */
	public void setCommentary(String commentary) {
		this.commentary = commentary;
	}
	/**
	 * @return the preferredImage
	 */
	@JsonIgnore
	public String getPreferredImage() {
		return preferredImage;
	}
	/**
	 * @param preferredImage the preferredImage to set
	 */
	public void setPreferredImage(String preferredImage) {
		this.preferredImage = preferredImage;
	}
	
}
