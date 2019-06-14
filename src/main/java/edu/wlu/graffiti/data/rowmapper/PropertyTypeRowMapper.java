package edu.wlu.graffiti.data.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import edu.wlu.graffiti.bean.PropertyType;

public final class PropertyTypeRowMapper implements RowMapper<PropertyType> {
	public PropertyType mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
		final PropertyType propType = new PropertyType();
		propType.setId(resultSet.getInt("id"));
		propType.setName(resultSet.getString("name"));
		propType.setDescription(resultSet.getString("commentary"));
		propType.setParentId(resultSet.getInt("parent_id"));
		propType.setIsParent(resultSet.getBoolean("is_parent"));
		return propType;
	}
}