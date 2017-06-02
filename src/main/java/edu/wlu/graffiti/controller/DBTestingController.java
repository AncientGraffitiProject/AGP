/**
 * 
 */
package edu.wlu.graffiti.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.wlu.graffiti.bean.Insula;
import edu.wlu.graffiti.bean.Property;
import edu.wlu.graffiti.bean.PropertyType;
import edu.wlu.graffiti.dao.InsulaDao;
import edu.wlu.graffiti.dao.FindspotDao;

/**
 * Special controller for testing the consistency of the information in the
 * database.
 * 
 * @author Sara Sprenkle
 *
 */
@Controller
public class DBTestingController {

	@Resource
	private FindspotDao propertyDao;

	@Resource
	private InsulaDao insulaDao;

	@RequestMapping(value = "/database/propertyInfo", method = RequestMethod.GET)
	public String searchForm(final HttpServletRequest request) {
		// get all the property types, all the properties and their mappings to
		// property types

		final List<PropertyType> propertyTypes = propertyDao.getPropertyTypes();

		final List<Property> properties = propertyDao.getProperties();

		final List<Insula> insula = insulaDao.getInsula();

		for (Property p : properties) {
			p.setPropertyTypes(propertyDao.getPropertyTypeForProperty(p.getId()));
		}

		request.setAttribute("insula", insula);
		request.setAttribute("properties", properties);
		request.setAttribute("propertyTypes", propertyTypes);

		return "database/showPropertyInfo";
	}

}
