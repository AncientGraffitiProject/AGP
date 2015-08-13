package edu.wlu.graffiti.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import edu.wlu.graffiti.bean.PropertyType;

/**
 * Class to extract property types from the DB
 * 
 * @author Sara Sprenkle
 * 
 */
public class PropertyTypesDao extends JdbcTemplate {

	private static final String SELECT_STATEMENT = "SELECT * "
			+ " FROM propertyTypes";

	private static final String SELECT_BY_ID_STMT = "SELECT *"
			+ " FROM propertyTypes WHERE id = ?";

	private static final class PropertyTypeRowMapper implements
			RowMapper<PropertyType> {
		public PropertyType mapRow(final ResultSet resultSet, final int rowNum)
				throws SQLException {
			final PropertyType propType = new PropertyType();
			propType.setId(resultSet.getInt("id"));
			propType.setName(resultSet.getString("name"));
			propType.setDescription(resultSet.getString("description"));
			return propType;
		}
	}

	// TODO: Set up to cache this
	public List<PropertyType> getPropertyTypes() {
		List<PropertyType> results = query(SELECT_STATEMENT,
				new PropertyTypeRowMapper());
		Collections.sort(results);
		return results;
	}

	public String getPropertyName(int propertyTypeID) {
		PropertyType results = queryForObject(SELECT_BY_ID_STMT,
				new PropertyTypeRowMapper(), propertyTypeID);
		return results.getName();
	}

}
