/**
 * 
 */
package edu.wlu.graffiti.bean;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Information about the figural component
 * 
 * @author sprenkle
 *
 */
public class FiguralInfo {
	
	private String description_in_latin = "";
	private String description_in_english = "";
	private Set<DrawingTag> drawingTags;
	
	public FiguralInfo() {
		drawingTags = new HashSet<DrawingTag>();
	}

	/**
	 * @return the figural component's description in Latin
	 */
	public String getDescriptionInLatin() {
		return description_in_latin;
	}


	/**
	 * @param description_in_latin the figural component's description in Latin
	 */
	public void setDescriptionInLatin(String description_in_latin) {
		this.description_in_latin = description_in_latin;
	}


	/**
	 * @return the figural component's description in English
	 */
	public String getDescriptionInEnglish() {
		return description_in_english;
	}


	/**
	 * @param description_in_english the figural component's description in English
	 */
	public void setDescriptionInEnglish(String description_in_english) {
		this.description_in_english = description_in_english;
	}
	
	public Set<DrawingTag> getDrawingTags() {
		return drawingTags;
	}

	public void addDrawingTag(DrawingTag drawingtag) {
		this.drawingTags.add(drawingtag);
	}

	public void addDrawingTags(List<DrawingTag> drawingTags) {
		for (DrawingTag dt : drawingTags) {
			addDrawingTag(dt);
		}
	}

}
