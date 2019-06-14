/*
 * PropertyController -- handles serving property information
 */
package edu.wlu.graffiti.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.wlu.graffiti.bean.Property;
import edu.wlu.graffiti.dao.InsulaDao;
import edu.wlu.graffiti.dao.FindspotDao;
import edu.wlu.graffiti.dao.PropertyTypesDao;

@Controller
public class PropertyController {

	@Resource
	private PropertyTypesDao propertyTypesDao;

	@Resource
	private FindspotDao propertyDao;

	@Resource
	private InsulaDao insulaDao;

	@RequestMapping(value = "/properties/{city}/{insula}/{property}", method = RequestMethod.GET)
	public String propertyPage(@PathVariable String city, @PathVariable String property, @PathVariable String insula,
			HttpServletRequest request) {
		try {
			final Property prop = this.propertyDao.getPropertyByCityAndInsulaAndProperty(city, insula, property);
			request.setAttribute("prop", prop);
			List<String> locationKeys = new ArrayList<>();
			locationKeys.add(prop.getLocationKey());
			request.setAttribute("findLocationKeys", locationKeys);
			return "property/propertyInfo";
		} catch (Exception e) {
			request.setAttribute("message", "No property with address " + city + " " + insula + " " + property);
			return "property/error";
		}
	}
	
	@RequestMapping(value = "/properties", method = RequestMethod.GET)
	public String searchProperties(final HttpServletRequest request) {
		// get all the property types, all the properties and their mappings to
		// property types

		final List<Property> pompeiiProperties = propertyDao.getPropertiesByCity("Pompeii");

		for (Property p : pompeiiProperties) {
			p.setPropertyTypes(propertyDao.getPropertyTypeForProperty(p.getId()));
		}
		
		request.setAttribute("pompeiiProperties", pompeiiProperties);
		
		final List<Property> herculaneumProperties = propertyDao.getPropertiesByCity("Herculaneum");

		for (Property p : herculaneumProperties) {
			p.setPropertyTypes(propertyDao.getPropertyTypeForProperty(p.getId()));
		}
		
		request.setAttribute("herculaneumProperties", herculaneumProperties);

		return "property/propertyList";
	}
	
	@RequestMapping(value = "/properties/{city}", method = RequestMethod.GET)
	public String searchByCityProperties(@PathVariable String city, final HttpServletRequest request) {
		// get all the property types, all the properties and their mappings to
		// property types
		
		if(!propertyDao.getCityNamesUpperCase().contains(city.toUpperCase())) {
			request.setAttribute("message", city + " is not a valid city.");
			return "property/error";
		}
		
		final List<Property> properties = propertyDao.getPropertiesByCity(city);
		request.setAttribute(city.toLowerCase() + "Properties", properties);

		return "property/propertyList";
	}
	
	@RequestMapping(value = "/properties/{city}/{insula:.+}", method = RequestMethod.GET)
	public String searchPropertiesByCityInsula(@PathVariable String city, @PathVariable String insula, final HttpServletRequest request) {
		// get all the property types, all the properties and their mappings to
		// property types
		
		if(!propertyDao.getCityNamesUpperCase().contains(city.toUpperCase())) {
			request.setAttribute("message", city + " is not a valid city.");
			return "property/error";
		}
		
		// TODO: add checks for invalid insula ID.
		
		final List<Property> properties = propertyDao.getPropertiesByCityAndInsula(city, insula);
		request.setAttribute(city.toLowerCase() + "Properties", properties);
		request.setAttribute("filterByInsula", insula);

		return "property/propertyList";
	}

}
