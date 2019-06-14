package edu.wlu.graffiti.data.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import edu.wlu.graffiti.bean.PropertyLink;

/**
 * 
 * @author Hammad Ahmad
 *
 */
public final class PropertyLinkRowMapper implements RowMapper<PropertyLink> {
	public PropertyLink mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
		final PropertyLink propertyLink = new PropertyLink();
		propertyLink.setId(resultSet.getInt("property_id"));
		propertyLink.setLinkName(resultSet.getString("link_name"));
		propertyLink.setLink(resultSet.getString("link"));
		return propertyLink;
	}
}