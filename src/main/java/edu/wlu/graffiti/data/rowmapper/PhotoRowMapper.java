package edu.wlu.graffiti.data.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import edu.wlu.graffiti.bean.Photo;

/**
 * 
 * @author Hammad Ahmad
 *
 */
public final class PhotoRowMapper implements RowMapper<Photo> {
	public Photo mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
		final Photo photo = new Photo();
		photo.setId(rowNum);
		photo.setEdrId(resultSet.getString("edr_id"));
		photo.setPhotoId(resultSet.getString("photo_id"));
		return photo;
	}
}