package edu.wlu.graffiti.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import edu.wlu.graffiti.bean.Inscription;
import edu.wlu.graffiti.bean.Property;
import edu.wlu.graffiti.dao.FindspotDao;
import edu.wlu.graffiti.dao.GraffitiDao;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value="JSON", description="Operations pertaining to JSON data exports.")
public class JsonController {
	@Resource
	private GraffitiDao graffitiDao;

	@Resource
	private FindspotDao findspotDao;
	
	@ApiOperation(value="Download the individual graffito in JSON format.")
	@RequestMapping(value = "/graffito/{agpId}/json", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public Inscription getInscription(@PathVariable String agpId, HttpServletResponse response) {
		String edrId = agpId.replaceFirst("AGP-", "");
		response.addHeader("Content-Disposition", "attachment; filename="+ agpId +".json");
		return graffitiDao.getInscriptionByEDR(edrId);
	}
	
	@ApiOperation(value="Download all graffiti in JSON format.")
	@RequestMapping(value = "/all/json", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public List<Inscription> getInscriptions(HttpServletResponse response) {
		response.addHeader("Content-Disposition", "attachment; filename=all.json");
		return graffitiDao.getAllInscriptions();
	}
	
	@SuppressWarnings("unchecked")
	@ApiOperation(value="Download the filtered graffiti in JSON format.")
	@RequestMapping(value = "/filtered-results/json", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public List<Inscription> getFilteredInscriptions(final HttpServletRequest request, HttpServletResponse response) {
		HttpSession s = request.getSession();
		response.addHeader("Content-Disposition", "attachment; filename=filtered-results.json");
		List<Inscription> results = (List<Inscription>) s.getAttribute("filteredList");
		//System.out.println(results.size());
		return results;
	}
	
	@ApiOperation(value="Download the property in JSON format.")
	@RequestMapping(value = "/property/{city}/{insula}/{property}/json", method = RequestMethod.GET, produces = "application/json")
	public Property getProperty(@PathVariable String city, @PathVariable String insula, @PathVariable String property, HttpServletResponse response) {
		return findspotDao.getPropertyByCityAndInsulaAndProperty(city, insula, property);
	}
	
	@ApiOperation(value="Download all properties in JSON format.")
	@RequestMapping(value = "/properties/json", method = RequestMethod.GET, produces = "application/json")
	public List<Property> getPropertyList(HttpServletResponse response) {
		final List<Property> properties = findspotDao.getProperties();

		for (Property p : properties) {
			p.setPropertyTypes(findspotDao.getPropertyTypeForProperty(p.getId()));
		}
		
		response.addHeader("Content-Disposition", "attachment; filename=all-properties.json");
		
		return properties;
	}
	
	@ApiOperation(value="Download all properties in the city in JSON format.")
	@RequestMapping(value = "/properties/{city}/json", method = RequestMethod.GET, produces = "application/json")
	public List<Property> getPropertyListByCity(@PathVariable String city, HttpServletResponse response) {
		final List<Property> properties = findspotDao.getPropertiesByCity(city);

		for (Property p : properties) {
			p.setPropertyTypes(findspotDao.getPropertyTypeForProperty(p.getId()));
		}
		
		response.addHeader("Content-Disposition", "attachment; filename=" + city + "-properties.json");
		
		return properties;
	}
	
	@ApiOperation(value="Download all properties in the city and insula in JSON format.")
	@RequestMapping(value = "/properties/{city}/{insula}/json", method = RequestMethod.GET, produces = "application/json")
	public List<Property> getPropertyListByCityInsula(@PathVariable String city, @PathVariable String insula, HttpServletResponse response) {
		final List<Property> properties = findspotDao.getPropertiesByCityAndInsula(city, insula);

		for (Property p : properties) {
			p.setPropertyTypes(findspotDao.getPropertyTypeForProperty(p.getId()));
		}
		
		response.addHeader("Content-Disposition", "attachment; filename=" + city + "-" + insula + "-properties.json");
		
		return properties;
	}
	
}
