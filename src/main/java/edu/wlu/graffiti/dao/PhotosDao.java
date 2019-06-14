package edu.wlu.graffiti.dao;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import edu.wlu.graffiti.bean.Photo;
import edu.wlu.graffiti.data.rowmapper.PhotoRowMapper;

/**
 * Class to extract photos from the DB
 * 
 * @author Hammad Ahmad
 * 
 */
public class PhotosDao extends JdbcTemplate {

	private static final String SELECT_STATEMENT = "SELECT * " + "FROM photos ORDER BY edr_id";
	
	private static final String SELECT_BY_EDR_ID = "SELECT * " + "FROM photos WHERE edr_id = ?";

	private List<Photo> photos = null;

	public List<Photo> getPhotos() {
		photos = query(SELECT_STATEMENT, new PhotoRowMapper());
		return photos;
	}
	
	public List<Photo> getPhotosByEDR(String edr) {
		photos = query(SELECT_BY_EDR_ID, new PhotoRowMapper(), edr);
		return photos;
	}

}
