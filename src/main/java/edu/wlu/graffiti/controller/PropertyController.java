/*
 * PropertyController -- handles serving property information
 */
package edu.wlu.graffiti.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.wlu.graffiti.bean.Insula;
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

	// Used for mapping the URI to show property information.
	@RequestMapping(value = "/property/{city}/", method = RequestMethod.GET)
	public String cityPage(@PathVariable String city, HttpServletRequest request) {
		// String searches = city;
		// final List<Inscription> resultsList =
		// this.graffitiDao.getInscriptionsByCity(searches);
		// request.setAttribute("resultsLyst", resultsList);
		HttpSession s = request.getSession();
		String returnURL = "/Graffiti/region/" + city;
		s.setAttribute("returnURL", returnURL);
		s.setAttribute("backToResults", false);
		return "displayData";
	}

	@RequestMapping(value = "/property/{city}/{insula}", method = RequestMethod.GET)
	public String insulaPage(@PathVariable String city, @PathVariable String insula, HttpServletRequest request,
			HttpServletResponse response) {
		System.out.println("insulaPage: " + insula);
		int insula_id = getInsulaId(city, insula);
		// final List<Inscription> inscriptions =
		// this.graffitiDao.getInscriptionsByCityAndInsula(city, insula_id);
		// request.setAttribute("resultsLyst", inscriptions);
		HttpSession s = request.getSession();
		String returnURL = "/Graffiti/region/" + city + "/" + insula;
		s.setAttribute("returnURL", returnURL);
		s.setAttribute("backToResults", false);
		return "displayData";
	}

	@RequestMapping(value = "/property/{city}/{insula}/{property}", method = RequestMethod.GET)
	public String propertyPage(@PathVariable String city, @PathVariable String property, @PathVariable String insula,
			HttpServletRequest request) {
		System.out.println("propertyPage: " + property);
		try {
			final Property prop = this.propertyDao.getPropertyByCityAndInsulaAndProperty(city, insula, property);
			request.setAttribute("prop", prop);
			List<String> locationKeys = new ArrayList<>();
			locationKeys.add(prop.getLocationKey());
			System.out.println("Loc Key: " + prop.getLocationKey());
			// request.setAttribute("findLocationKeys", prop.getId());
			// request.setAttribute("findLocationKeys", prop.getLocationKey());
			request.setAttribute("findLocationKeys", locationKeys);
			return "property/propertyInfo";
		} catch (Exception e) {
			request.setAttribute("message", "No property with address " + city + " " + insula + " " + property);
			return "property/error";
		}
	}

	// helper method to get insula id for given insula name
	private int getInsulaId(String city, String insula) {
		final List<Insula> ins = this.insulaDao.getInsulaByCityAndInsula(city, insula.toUpperCase());
		if (ins != null && !ins.isEmpty()) {
			return ins.get(0).getId();
		}
		return -1;
	}

	// Maps to the details page once an individual result has been selected in
	// the results page
	/*
	 * @RequestMapping(value = "/property/{pleiadesid}", method =
	 * RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE) public
	 * String graffitoXML(final HttpServletRequest request, HttpServletResponse
	 * response,
	 * 
	 * @PathVariable String edr) { response.setContentType("application/xml");
	 * final List<Inscription> results =
	 * this.graffitiDao.getInscriptionByEDR(edr); Inscription i = null; if
	 * (results.isEmpty()) { request.setAttribute("error", "Error: " + edr +
	 * " is not a valid EAGLE id."); return "index"; } else { i =
	 * results.get(0); Set<DrawingTag> tags = i.getDrawingTags(); List<String>
	 * names = new ArrayList<String>(); for (DrawingTag tag : tags) {
	 * names.add(tag.getName()); } int num = i.getNumberOfImages();
	 * request.setAttribute("drawingCategories", names);
	 * request.setAttribute("images", getImages(i, num));
	 * request.setAttribute("imagePages", getPages(i, num));
	 * request.setAttribute("thumbnails", getThumbnails(i, num));
	 * request.setAttribute("findLocationKeys", findLocationKeys(results));
	 * request.setAttribute("inscription", i); request.setAttribute("city",
	 * i.getAncientCity()); return "details"; } }
	 */

}
