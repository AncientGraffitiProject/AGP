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
import edu.wlu.graffiti.data.export.GenerateCSV;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * @author Hammad Ahmad
 *
 */
@RestController
@Api(value="CSV", description="Operations pertaining to CSV data exports.")
public class CSVController {
	
	private GenerateCSV generator = new GenerateCSV();
	
	@Resource
	private GraffitiDao graffitiDao;

	@Resource
	private FindspotDao findspotDao;
	
	@ApiOperation(value="Download the individual graffito as a CSV file.")
	@RequestMapping(value = "/graffito/{agpId}/csv", method = RequestMethod.GET, produces = "text/csv;charset=UTF-8")
	public String getInscription(@PathVariable String agpId, HttpServletResponse response) {
		String edrId = agpId.replaceFirst("AGP-", "");
		response.addHeader("Content-Disposition", "attachment; filename="+ agpId +".csv");
		return generator.serializeToCSV(graffitiDao.getInscriptionByEDR(edrId));
	}
	
	@ApiOperation(value="Download all graffiti as a CSV file.")
	@RequestMapping(value = "/all/csv", method = RequestMethod.GET, produces = "text/csv;charset=UTF-8")
	public String getInscriptions(HttpServletResponse response) {
		response.addHeader("Content-Disposition", "attachment; filename=all-inscriptions.csv");
		return generator.serializeToCSV(graffitiDao.getAllInscriptions());
	}
	
	@SuppressWarnings("unchecked")
	@ApiOperation(value="Download the filtered graffiti as a CSV file.")
	@RequestMapping(value = "/filtered-results/csv", method = RequestMethod.GET, produces = "text/csv;charset=UTF-8")
	public String getFilteredInscriptions(final HttpServletRequest request, HttpServletResponse response) {
		HttpSession s = request.getSession();
		List<Inscription> results = (List<Inscription>) s.getAttribute("filteredList");
		response.addHeader("Content-Disposition", "attachment; filename=filtered-results.csv");
		return generator.serializeToCSV(results);
	}
	
	@ApiOperation(value="Download all properties as a CSV file.")
	@RequestMapping(value = "/properties/csv", method = RequestMethod.GET, produces = "text/csv;charset=UTF-8")
	public String downloadProperties(final HttpServletRequest request, HttpServletResponse response) {
		
		final List<Property> properties = findspotDao.getProperties();

		for (Property p : properties) {
			p.setPropertyTypes(findspotDao.getPropertyTypeForProperty(p.getId()));
			
		}
		
		response.addHeader("Content-Disposition", "attachment; filename=all-properties.csv");
		return generator.serializePropertiesToCSV(properties);
	}

	@ApiOperation(value="Download all properties in the city as a CSV file.")
	@RequestMapping(value = "/properties/{city}/csv", method = RequestMethod.GET, produces = "text/csv;charset=UTF-8")
	public String downloadPropertiesByCity(@PathVariable String city, final HttpServletRequest request, HttpServletResponse response) {
		
		final List<Property> properties = findspotDao.getPropertiesByCity(city);

		for (Property p : properties) {
			p.setPropertyTypes(findspotDao.getPropertyTypeForProperty(p.getId()));
			
		}
		
		response.addHeader("Content-Disposition", "attachment; filename=" + city +"-properties.csv");
		return generator.serializePropertiesToCSV(properties);
	}
	
	@ApiOperation(value="Download all properties in the city and insula as a CSV file.")
	@RequestMapping(value = "/properties/{city}/{insula}/csv", method = RequestMethod.GET, produces = "text/csv;charset=UTF-8")
	public String downloadPropertiesByCityInsula(@PathVariable String city, @PathVariable String insula, final HttpServletRequest request, HttpServletResponse response) {
		
		final List<Property> properties = findspotDao.getPropertiesByCityAndInsula(city, insula);

		for (Property p : properties) {
			p.setPropertyTypes(findspotDao.getPropertyTypeForProperty(p.getId()));
			
		}
		
		response.addHeader("Content-Disposition", "attachment; filename=" + city + "-" + insula + "-properties.csv");
		return generator.serializePropertiesToCSV(properties);
	}
	
	/**
	@RequestMapping("/property/{city}/{insula}/{property}/csv")
	public Property getProperty(@PathVariable String city, @PathVariable String insula, @PathVariable String property) {
		return findspotDao.getPropertyByCityAndInsulaAndProperty(city, insula, property);
	}
	*/

}
