/**
 * 
 */
package edu.wlu.graffiti.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import edu.wlu.graffiti.bean.Property;

/**
 * Class to extract property information
 * 
 * @author Sara Sprenkle
 * 
 */
public class PropertyDao extends JdbcTemplate {
	private static final String SELECT_STATEMENT = "SELECT * "
			+ " FROM properties";

	private static final String SELECT_BY_CITY_STATEMENT = "SELECT * "
			+ " FROM properties WHERE modern_city = ?";
	
	private static final String SELECT_BY_CITY_AND_INSULA_STATEMENT = "SELECT * "
			+ " FROM properties WHERE modern_city = ? and insula = ?";

	private static final class PropertyRowMapper implements RowMapper<Property> {
		public Property mapRow(final ResultSet resultSet, final int rowNum)
				throws SQLException {
			final Property property = new Property();
			property.setId(resultSet.getInt("id"));
			property.setInsula(resultSet.getString("insula"));
			property.setModern_city(resultSet.getString("modern_city"));
			property.setPropertyName(resultSet.getString("property_name"));
			property.setPropertyNumber(resultSet.getInt("property_number"));
			return property;
		}
	}

	// TODO: Set up to cache this
	public List<Property> getProperties() {
		List<Property> results = query(SELECT_STATEMENT,
				new PropertyRowMapper());
		return results;
	}

	// TODO: Set up to cache this
	public List<Property> getPropertiesByCity(String city) {
		List<Property> results = query(SELECT_BY_CITY_STATEMENT,
				new PropertyRowMapper(), city);
		return results;
	}

	// TODO: Set up to cache this
	public List<Property> getPropertiesByCityAndInsula(String city, String insula) {
		List<Property> results = query(SELECT_BY_CITY_AND_INSULA_STATEMENT,
				new PropertyRowMapper(), city, insula);
		return results;
	}
}
