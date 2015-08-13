package edu.wlu.graffiti.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import edu.wlu.graffiti.bean.DrawingTag;
import edu.wlu.graffiti.bean.PropertyType;

/**
 * Class to extract property types from the DB
 * 
 * @author Sara Sprenkle
 * 
 */
public class DrawingTagsDao extends JdbcTemplate {

	private static final String SELECT_STATEMENT = "SELECT * "
			+ " FROM drawing_tags";

	private static final class DrawingTagMapper implements
			RowMapper<DrawingTag> {
		public DrawingTag mapRow(final ResultSet resultSet, final int rowNum)
				throws SQLException {
			final DrawingTag drawingTag = new DrawingTag();
			drawingTag.setId(resultSet.getInt("id"));
			drawingTag.setName(resultSet.getString("name"));
			drawingTag.setDescription(resultSet.getString("description"));
			return drawingTag;
		}
	}

	// TODO: Set up to cache this
	public List<DrawingTag> getDrawingTags() {
		List<DrawingTag> results = query(SELECT_STATEMENT,
				new DrawingTagMapper());
		Collections.sort(results);
		return results;
	}

}
