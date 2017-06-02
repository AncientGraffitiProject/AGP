/**
 * 
 */
package edu.wlu.graffiti.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import edu.wlu.graffiti.bean.Property;
import edu.wlu.graffiti.bean.PropertyType;
import edu.wlu.graffiti.data.rowmapper.PropertyRowMapper;
import edu.wlu.graffiti.bean.City;
import edu.wlu.graffiti.bean.Insula;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


/**
 * Class to extract property information
 * 
 * @author Sara Sprenkle
 * 
 */
public class FindspotDao extends JdbcTemplate {
	/**
	 * Order by the properties.id --> if order by properties.property_number, ordered as strings because
	 * property_number is a var char.
	 */
	private static final String ORDER_BY_CLAUSE = "ORDER BY properties.id ASC";

	private static final String SELECT_STATEMENT = "SELECT *, " + "cities.name as city_name, "
			+ "cities.pleiades_id as city_pleiades_id, " + "insula.pleiades_id as insula_pleiades_id, "
			+ "properties.pleiades_id as property_pleiades_id " + " FROM properties "
			+ "LEFT JOIN insula ON properties.insula_id=insula.id "
			+ "LEFT JOIN cities ON insula.modern_city=cities.name " + ORDER_BY_CLAUSE;

	private static final String SELECT_BY_CITY_STATEMENT = "SELECT *, " + "cities.name as city_name, "
			+ "cities.pleiades_id as city_pleiades_id, " + "insula.pleiades_id as insula_pleiades_id, "
			+ "properties.pleiades_id as property_pleiades_id " + " FROM properties "
			+ "LEFT JOIN insula ON properties.insula_id=insula.id WHERE UPPER(modern_city) = UPPER(?) "
			+ "LEFT JOIN cities ON insula.modern_city=cities.name " + ORDER_BY_CLAUSE;

	private static final String SELECT_BY_CITY_AND_INSULA_STATEMENT = "SELECT *, " + "cities.name as city_name, "
			+ "cities.pleiades_id as city_pleiades_id, " + "insula.pleiades_id as insula_pleiades_id, "
			+ "properties.pleiades_id as property_pleiades_id " + " FROM properties "
			+ "LEFT JOIN insula ON properties.insula_id=insula.id WHERE UPPER(modern_city) = UPPER(?) and name = ? "
			+ "LEFT JOIN cities ON insula.modern_city=cities.name " + ORDER_BY_CLAUSE;

	public static final String SELECT_BY_CITY_AND_INSULA_AND_PROPERTY_STATEMENT = "SELECT *, "
			+ "cities.name as city_name, " + "cities.pleiades_id as city_pleiades_id, "
			+ "insula.pleiades_id as insula_pleiades_id, " + "properties.pleiades_id as property_pleiades_id "
			+ "FROM properties " + "LEFT JOIN insula ON properties.insula_id=insula.id "
			+ "LEFT JOIN cities ON insula.modern_city=cities.name "
			+ "WHERE UPPER(modern_city) = UPPER(?) and insula.short_name = ? and property_number = ?";
	
	public static final String SELECT_BY_CITY_AND_PROPERTY_NAME_STATEMENT = "SELECT *, "
			+ "cities.name as city_name, cities.pleiades_id as city_pleiades_id, "
			+ "insula.pleiades_id as insula_pleiades_id, " 
			+ "properties.pleiades_id as property_pleiades_id "
			+ "FROM properties " 
			+ "LEFT JOIN insula ON properties.insula_id=insula.id "
			+ "LEFT JOIN cities ON insula.modern_city=cities.name "
			+ "WHERE UPPER(modern_city) = UPPER(?) and property_name = ?";

	public static final String SELECT_BY_PROPERTY_ID_STATEMENT = "SELECT *, " + "cities.name as city_name, "
			+ "cities.pleiades_id as city_pleiades_id, " + "insula.pleiades_id as insula_pleiades_id, "
			+ "properties.pleiades_id as property_pleiades_id " + " FROM properties "
			+ "LEFT JOIN insula ON properties.insula_id=insula.id "
			+ "LEFT JOIN cities ON insula.modern_city=cities.name WHERE properties.id = ?";

	private static final String SELECT_PROPERTY_TYPES = "SELECT * " + " FROM propertyTypes";

	private static final String SELECT_PROP_TYPES_BY_ID_STMT = "SELECT *" + " FROM propertyTypes WHERE id = ?";

	public static final String SELECT_PROP_TYPES_BY_PROP_ID = "SELECT propertyTypes.id, propertyTypes.name, propertyTypes.description from propertyTypes, propertytopropertytype "
			+ "WHERE propertytopropertytype.property_id = ? AND propertytopropertytype.property_type = propertytypes.id";

	public static final String SELECT_CITY_NAMES = "SELECT name from cities ORDER BY name";
	
	private static final class PropertyTypeRowMapper implements RowMapper<PropertyType> {
		public PropertyType mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
			final PropertyType propType = new PropertyType();
			propType.setId(resultSet.getInt("id"));
			propType.setName(resultSet.getString("name"));
			propType.setDescription(resultSet.getString("description"));
			return propType;
		}
	}
	
	@Cacheable("cities")
	public List<String> getCityNames() {
		List<String> results = queryForList(SELECT_CITY_NAMES, String.class);
		return results;
	}

	@Cacheable("propertyTypes")
	public List<PropertyType> getPropertyTypes() {
		List<PropertyType> results = query(SELECT_PROPERTY_TYPES, new PropertyTypeRowMapper());
		Collections.sort(results);
		return results;
	}

	@Cacheable("propertyNames")
	public String getPropertyName(int propertyTypeID) {
		PropertyType results = queryForObject(SELECT_PROP_TYPES_BY_ID_STMT, new PropertyTypeRowMapper(),
				propertyTypeID);
		return results.getName();
	}

	@Cacheable("propertyTypesByPropertyId")
	public List<PropertyType> getPropertyTypeForProperty(int propertyID) {
		List<PropertyType> propertyTypes = query(SELECT_PROP_TYPES_BY_PROP_ID, new PropertyTypeRowMapper(), propertyID);
		return propertyTypes;
	}

	// TODO: Set up to cache this
	public List<Property> getProperties() {
		List<Property> results = query(SELECT_STATEMENT, new PropertyRowMapper());
		return results;
	}

	// TODO: Set up to cache this
	public List<Property> getPropertiesByCity(String city) {
		List<Property> results = query(SELECT_BY_CITY_STATEMENT, new PropertyRowMapper(), city);
		for (Property prop : results) {
			prop.setPropertyTypes(getPropertyTypeForProperty(prop.getId()));
		}
		return results;
	}

	// TODO: Set up to cache this
	public List<Property> getPropertiesByCityAndInsula(String city, String insulaName) {
		List<Property> results = query(SELECT_BY_CITY_AND_INSULA_STATEMENT, new PropertyRowMapper(), city, insulaName);
		for (Property prop : results) {
			prop.setPropertyTypes(getPropertyTypeForProperty(prop.getId()));
		}
		return results;
	}

	// TODO: Set up to cache this
	@JsonInclude(Include.NON_NULL)
	public Property getPropertyByCityAndInsulaAndProperty(String city, String insulaName, String property_number) {
		Property result = queryForObject(SELECT_BY_CITY_AND_INSULA_AND_PROPERTY_STATEMENT, new PropertyRowMapper(),
				city, insulaName, property_number);
		result.setPropertyTypes(getPropertyTypeForProperty(result.getId()));
		return result;
	}
	
	public Property getPropertyByCityAndProperty(String city, String propertyName) {
		Property result = queryForObject(SELECT_BY_CITY_AND_PROPERTY_NAME_STATEMENT, new PropertyRowMapper(), city, propertyName);
		if( result == null ) {
			return result;
		}
		result.setPropertyTypes(getPropertyTypeForProperty(result.getId()));
		return result;
	}

	@Cacheable("propertyById")
	public Property getPropertyById(int property_id) {
		Property result = queryForObject(SELECT_BY_PROPERTY_ID_STATEMENT, new PropertyRowMapper(), property_id);
		result.setPropertyTypes(getPropertyTypeForProperty(property_id));
		return result;
	}
}
