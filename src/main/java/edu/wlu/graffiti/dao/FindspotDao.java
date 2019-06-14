/**
 * 
 */
package edu.wlu.graffiti.dao;

import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import javax.annotation.Resource;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;

import edu.wlu.graffiti.bean.Property;
import edu.wlu.graffiti.bean.PropertyLink;
import edu.wlu.graffiti.bean.PropertyType;
import edu.wlu.graffiti.data.rowmapper.PropertyLinkRowMapper;
import edu.wlu.graffiti.data.rowmapper.PropertyRowMapper;
import edu.wlu.graffiti.data.rowmapper.PropertyTypeRowMapper;

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
	 * Order by the properties.id --> if order by properties.property_number,
	 * ordered as strings because property_number is a var char.
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
			+ "LEFT JOIN insula ON properties.insula_id=insula.id "
			+ "LEFT JOIN cities ON insula.modern_city=cities.name " + "WHERE UPPER(modern_city) = UPPER(?) "
			+ ORDER_BY_CLAUSE;

	public static final String SELECT_BY_CITY_AND_INSULA_STATEMENT = "SELECT *, " + "cities.name as city_name, "
			+ "cities.pleiades_id as city_pleiades_id, " + "insula.pleiades_id as insula_pleiades_id, "
			+ "properties.pleiades_id as property_pleiades_id " + " FROM properties "
			+ "LEFT JOIN insula ON properties.insula_id=insula.id "
			+ "LEFT JOIN cities ON insula.modern_city=cities.name "
			+ "WHERE UPPER(modern_city) = UPPER(?) and insula.short_name = ? " + ORDER_BY_CLAUSE;

	public static final String SELECT_BY_CITY_AND_INSULA_AND_PROPERTY_STATEMENT = "SELECT *, "
			+ "cities.name as city_name, " + "cities.pleiades_id as city_pleiades_id, "
			+ "insula.pleiades_id as insula_pleiades_id, " + "properties.pleiades_id as property_pleiades_id "
			+ "FROM properties " + "LEFT JOIN insula ON properties.insula_id=insula.id "
			+ "LEFT JOIN cities ON insula.modern_city=cities.name "
			+ "WHERE UPPER(modern_city) = UPPER(?) and insula.short_name = ? and property_number = ?";

	public static final String SELECT_BY_CITY_AND_PROPERTY_NAME_STATEMENT = "SELECT *, "
			+ "cities.name as city_name, cities.pleiades_id as city_pleiades_id, "
			+ "insula.pleiades_id as insula_pleiades_id, " + "properties.pleiades_id as property_pleiades_id "
			+ "FROM properties " + "LEFT JOIN insula ON properties.insula_id=insula.id "
			+ "LEFT JOIN cities ON insula.modern_city=cities.name "
			+ "WHERE UPPER(modern_city) = UPPER(?) and property_name = ?";

	public static final String SELECT_BY_PROPERTY_ID_STATEMENT = "SELECT *, " + "cities.name as city_name, "
			+ "cities.pleiades_id as city_pleiades_id, " + "insula.pleiades_id as insula_pleiades_id, "
			+ "properties.pleiades_id as property_pleiades_id " + " FROM properties "
			+ "LEFT JOIN insula ON properties.insula_id=insula.id "
			+ "LEFT JOIN cities ON insula.modern_city=cities.name WHERE properties.id = ?";
	
	private static final String SELECT_PROPERTY_LINKS = "SELECT * FROM property_links";
	
	private static final String SELECT_PROPERTY_LINKS_BY_ID = "SELECT * from property_links WHERE property_id = ?";

	public static final String SELECT_PROPERTY_TYPES = "SELECT * " + " FROM propertytypes";
	
	public static final String SELECT_PROPERTY_TYPES_BY_PARENT_ID = "SELECT * FROM propertytypes WHERE parent_id=?";

	private static final String SELECT_PROP_TYPES_BY_ID_STMT = "SELECT *" + " FROM propertytypes WHERE id = ?";

	public static final String SELECT_PROP_TYPES_BY_PROP_ID = "SELECT propertytypes.id, propertytypes.name, propertytypes.commentary," 
			+" propertytypes.parent_id, propertytypes.is_parent from propertytypes, propertytopropertytype "
			+ "WHERE propertytopropertytype.property_id = ? AND propertytopropertytype.property_type = propertytypes.id";

	public static final String SELECT_BY_OSM_WAY_ID_STATEMENT = "SELECT * " + " FROM properties WHERE osm_way_id = ?";
	public static final String SELECT_BY_OSM_ID_STATEMENT = "SELECT * " + " FROM properties WHERE osm_id = ?";

	public static final String SELECT_CITY_NAMES = "SELECT name from cities ORDER BY name";

	@Resource
	private GraffitiDao graffitiDao;

//	private static final class PropertyTypeRowMapper implements RowMapper<PropertyType> {
//		public PropertyType mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
//			final PropertyType propType = new PropertyType();
//			propType.setId(resultSet.getInt("id"));
//			propType.setName(resultSet.getString("name"));
//			propType.setDescription(resultSet.getString("commentary"));
//			propType.setParentId(resultSet.getInt("parent_id"));
//			propType.setIsParent(resultSet.getBoolean("is_parent"));
//			return propType;
//		}
//	}

	@Cacheable("cities")
	public List<String> getCityNames() {
		List<String> results = queryForList(SELECT_CITY_NAMES, String.class);
		return results;
	}

	// TODO: set up caching for this
	// @Cacheable("citiesUpperCase")
	public List<String> getCityNamesUpperCase() {
		List<String> results = queryForList(SELECT_CITY_NAMES, String.class);
		// change names to upper case to allow for either lower or upper case in
		// URIs
		ListIterator<String> iterator = results.listIterator();
		while (iterator.hasNext()) {
			iterator.set(iterator.next().toUpperCase());
		}
		return results;
	}

	@Cacheable("propertyTypes")
	public List<PropertyType> getPropertyTypes() {
		List<PropertyType> results = query(SELECT_PROPERTY_TYPES, new PropertyTypeRowMapper());
		for (PropertyType pt : results) {
			pt.setChildren(getChildrenFromPropertyType(pt.getId()));
		}
		Collections.sort(results);
		return results;
	}
	
	public List<PropertyType> getChildrenFromPropertyType(int propertyTypeId){
		List<PropertyType> results = query(SELECT_PROPERTY_TYPES_BY_PARENT_ID, new PropertyTypeRowMapper(), propertyTypeId);
		return results;
	}

	// TODO: set up caching for this
	public List<PropertyLink> getPropertyLinks() {
		List<PropertyLink> results = query(SELECT_PROPERTY_LINKS, new PropertyLinkRowMapper());
		return results;
	}
	
	@Cacheable("propertyNames")
	public String getPropertyName(int propertyTypeID) {
		PropertyType results = queryForObject(SELECT_PROP_TYPES_BY_ID_STMT, new PropertyTypeRowMapper(),
				propertyTypeID);
		return results.getName();
	}

	// TODO: set up caching for this
	public List<PropertyLink> getPropertyLinksForProperty(int propertyID) {
		List<PropertyLink> propertyLinks = query(SELECT_PROPERTY_LINKS_BY_ID, new PropertyLinkRowMapper(), propertyID);
		return propertyLinks;
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
			prop.setPropertyLinks(getPropertyLinksForProperty(prop.getId()));
		}
		return results;
	}

	// TODO: Set up to cache this
	public List<Property> getPropertiesByCityAndInsula(String city, String insulaName) {
		List<Property> results = query(SELECT_BY_CITY_AND_INSULA_STATEMENT, new PropertyRowMapper(), city, insulaName);
		for (Property prop : results) {
			prop.setPropertyTypes(getPropertyTypeForProperty(prop.getId()));
			prop.setPropertyLinks(getPropertyLinksForProperty(prop.getId()));
		}
		return results;
	}

	// TODO: Set up to cache this
	@JsonInclude(Include.NON_NULL)
	public Property getPropertyByCityAndInsulaAndProperty(String city, String insulaName, String property_number) {
		Property result = queryForObject(SELECT_BY_CITY_AND_INSULA_AND_PROPERTY_STATEMENT, new PropertyRowMapper(),
				city, insulaName, property_number);
		result.setPropertyTypes(getPropertyTypeForProperty(result.getId()));
		result.setPropertyLinks(getPropertyLinksForProperty(result.getId()));
		result.setNumberOfGraffiti(graffitiDao.getInscriptionCountByFindSpot(result.getId()));
		return result;
	}

	public Property getPropertyByCityAndProperty(String city, String propertyName) {
		Property result = queryForObject(SELECT_BY_CITY_AND_PROPERTY_NAME_STATEMENT, new PropertyRowMapper(), city,
				propertyName);
		if (result == null) {
			return result;
		}
		result.setPropertyTypes(getPropertyTypeForProperty(result.getId()));
		result.setPropertyLinks(getPropertyLinksForProperty(result.getId()));
		result.setNumberOfGraffiti(graffitiDao.getInscriptionCountByFindSpot(result.getId()));
		return result;
	}

	@Cacheable("propertyById")
	public Property getPropertyById(int property_id) {
		Property result = queryForObject(SELECT_BY_PROPERTY_ID_STATEMENT, new PropertyRowMapper(), property_id);
		result.setPropertyTypes(getPropertyTypeForProperty(property_id));
		result.setNumberOfGraffiti(graffitiDao.getInscriptionCountByFindSpot(result.getId()));
		result.setPropertyLinks(getPropertyLinksForProperty(result.getId()));
		return result;
	}
}
