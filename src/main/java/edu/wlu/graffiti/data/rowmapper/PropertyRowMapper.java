package edu.wlu.graffiti.data.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import edu.wlu.graffiti.bean.City;
import edu.wlu.graffiti.bean.Insula;
import edu.wlu.graffiti.bean.Property;

public final class PropertyRowMapper implements RowMapper<Property> {
	public Property mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
		final Property property = new Property();
		final Insula insula = new Insula();
		final City city = new City();

		property.setId(resultSet.getInt("id"));
		property.setPropertyName(resultSet.getString("property_name"));
		property.setPropertyNumber(resultSet.getString("property_number"));
		property.setItalianPropertyName(resultSet.getString("italian_property_name"));
		property.setPleiadesId(resultSet.getString("property_pleiades_id"));
		property.setCommentary(resultSet.getString("commentary"));
		insula.setId(resultSet.getInt("insula_id"));
		insula.setModernCity(resultSet.getString("modern_city"));
		insula.setShortName(resultSet.getString("short_name"));
		insula.setFullName(resultSet.getString("full_name"));
		insula.setPleiadesId(resultSet.getString("insula_pleiades_id"));
		city.setName(resultSet.getString("city_name"));
		city.setPleiadesId(resultSet.getString("city_pleiades_id"));
		insula.setCity(city);
		property.setInsula(insula);
		return property;
	}
}