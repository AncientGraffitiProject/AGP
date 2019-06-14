package edu.wlu.graffiti.data.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import edu.wlu.graffiti.bean.Theme;

/**
 * 
 * @author Hammad Ahmad
 *
 */
public final class ThemeRowMapper implements RowMapper<Theme> {
	public Theme mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
		final Theme theme = new Theme();
		theme.setId(resultSet.getInt("theme_id"));
		theme.setName(resultSet.getString("name"));
		theme.setDescription(resultSet.getString("description"));
		return theme;
	}
}