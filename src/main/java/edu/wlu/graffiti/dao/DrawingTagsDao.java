package edu.wlu.graffiti.dao;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;

import edu.wlu.graffiti.bean.DrawingTag;
import edu.wlu.graffiti.data.rowmapper.DrawingTagRowMapper;

/**
 * Class to extract property types from the DB
 * 
 * @author Sara Sprenkle
 * 
 */
public class DrawingTagsDao extends JdbcTemplate {

	private static final String SELECT_STATEMENT = "SELECT * " + " FROM drawing_tags ORDER BY name";
	
	private static final String SELECT_BY_ID = "SELECT * " + " FROM drawing_tags WHERE id = ?";

	public static final String SELECT_BY_EDR_ID = "SELECT drawing_tags.id, name, description "
			+ "FROM graffitotodrawingtags, drawing_tags WHERE graffito_id = ? "
			+ "AND drawing_tags.id = graffitotodrawingtags.drawing_tag_id ORDER BY name;";

	private List<DrawingTag> drawingTags = null;

	@Cacheable("drawingTags")
	public List<DrawingTag> getDrawingTags() {
		drawingTags = query(SELECT_STATEMENT, new DrawingTagRowMapper());
		return drawingTags;
	}
	
	@Cacheable("drawingTags")
	public DrawingTag getDrawingTagById(int drawing_tag_id) {
		DrawingTag dt = queryForObject(SELECT_BY_ID, new DrawingTagRowMapper(), drawing_tag_id);
		return dt;
	}

}
