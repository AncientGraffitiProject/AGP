package edu.wlu.graffiti.data.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import edu.wlu.graffiti.bean.DrawingTag;

public final class DrawingTagRowMapper implements RowMapper<DrawingTag> {
	public DrawingTag mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
		final DrawingTag drawingtag = new DrawingTag();
		drawingtag.setId(resultSet.getInt("ID"));
		drawingtag.setName(resultSet.getString("name"));
		drawingtag.setDescription(resultSet.getString("description"));
		return drawingtag;
	}
}