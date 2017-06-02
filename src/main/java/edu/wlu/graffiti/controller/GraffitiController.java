/*
sudo * GraffitiController.java is the main backend controller of the Ancient Graffiti Project. It handles most of the
 * controls regarding the requests.
 */
package edu.wlu.graffiti.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.wlu.graffiti.bean.DrawingTag;
import edu.wlu.graffiti.bean.Inscription;
import edu.wlu.graffiti.bean.Insula;
import edu.wlu.graffiti.bean.Property;
import edu.wlu.graffiti.dao.DrawingTagsDao;
import edu.wlu.graffiti.dao.GraffitiDao;
import edu.wlu.graffiti.dao.InsulaDao;
import edu.wlu.graffiti.dao.FindspotDao;
import edu.wlu.graffiti.dao.PropertyTypesDao;

@Controller
public class GraffitiController {

	@Resource
	// The @Resource injects an instance of the GraffitiDao at runtime. The
	// GraffitiDao instance is defined in graffiti-servlet.xml.
	private GraffitiDao graffitiDao;

	@Resource
	private DrawingTagsDao drawingTagsDao;

	@Resource
	private PropertyTypesDao propertyTypesDao;

	@Resource
	private FindspotDao findspotDao;

	@Resource
	private InsulaDao insulaDao;

	// Maps to the search.jsp page currently receives information from
	// regions.txt file for the dropdown menu
	// that holds the information for each property in an ancient city (i.e.
	// Pompeii)
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public String searchForm(final HttpServletRequest request) {
		String city = request.getParameter("city");
		String message;
		HttpSession s = request.getSession();
		s.setAttribute("returnFromEDR", "");

		if (city != null && !city.isEmpty()) {
			if (city.toLowerCase().equals("pompeii")) {
				request.setAttribute("city", "Pompeii");
				city = "Pompeii";
				message = "Click on the map to search for graffiti in a particular city-block.";
			} else if (city.toLowerCase().equals("herculaneum")) {
				request.setAttribute("city", "Herculaneum");
				city = "Herculaneum";
				message = "Click on one or more properties within the map, then hit the \"Search\" button below.";
			} else {
				request.setAttribute("error", "Error: " + city + " is not a valid city.");
				return "index";
			}

			final InputStream inStream = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(city.toLowerCase() + "_map.xml");

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			Document doc;

			List<String> coords = new ArrayList<String>();
			List<String> regionNames = new ArrayList<String>();
			List<String> regionIds = new ArrayList<String>();

			try {
				dBuilder = dbFactory.newDocumentBuilder();
				doc = dBuilder.parse(inStream);
				doc.getDocumentElement().normalize();

				NodeList areaList = doc.getElementsByTagName("area");

				for (int i = 0; i < areaList.getLength(); i++) {

					Element area = (Element) areaList.item(i);
					String regionCoords = area.getAttribute("coords");
					String regionName = area.getAttribute("alt");
					int regionId;

					if (city.equals("Pompeii")) { // use insula ids
						regionId = this.insulaDao.getInsulaByCityAndInsula(city, regionName).get(0).getId();
						regionIds.add("i" + String.valueOf(regionId));
					} else { // use property ids
						String insulaAndProp = regionName.split(" ")[0];
						try {
							int ind = insulaAndProp.lastIndexOf(".");
							String insula = insulaAndProp.substring(0, ind);
							String property_number = insulaAndProp.substring(ind + 1);
							// System.out.println(insulaAndProp + " " + city + "
							// " + insula + " " + property_number);
							regionId = this.findspotDao
									.getPropertyByCityAndInsulaAndProperty(city, insula, property_number).getId();
							regionIds.add("p" + String.valueOf(regionId));
						} catch (IndexOutOfBoundsException e) {
							// not one of the "regular" properties
							// String uniqueId = insulaAndProp + i;
							// System.out.println(city + " " + regionName);
							regionId = this.findspotDao.getPropertyByCityAndProperty(city, regionName).getId();
							regionIds.add("p" + regionId);
						}
					}
					coords.add(regionCoords);
					regionNames.add(regionName);
				}
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException | IOException e) {
				e.printStackTrace();
			}
			request.setAttribute("coords", coords);
			request.setAttribute("regionNames", regionNames);
			request.setAttribute("regionIds", regionIds);
			request.setAttribute("message", message);
			request.setAttribute("displayImage", request.getContextPath() + "/resources/images/" + city + ".jpg");
			s.setAttribute("returnURL", ControllerUtils.getFullRequest(request));
			return "search";
		} else {
			return "index";
		}
	}

	@RequestMapping(value = "/AdminFunctions", method = RequestMethod.GET)
	public String adminFunctions(final HttpServletRequest request) {
		return "admin/AdminFunctions";
	}

	@RequestMapping(value = "/featured-graffiti", method = RequestMethod.GET)
	public String featuredHits(final HttpServletRequest request) {

		final List<Inscription> greatestFiguralHits = this.graffitiDao.getGreatestFiguralHits();
		final List<Inscription> greatestTranslationHits = this.graffitiDao.getGreatestTranslationHits();
		request.setAttribute("figuralHits", greatestFiguralHits);
		request.setAttribute("translationHits", greatestTranslationHits);

		return "featuredGraffiti";

	}

	@RequestMapping(value = "/admin/editGraffito", method = RequestMethod.GET)
	public String editGraffito(final HttpServletRequest request) {

		return "admin/editGraffito";

	}

	// Update a graffito page - sharmas
	@RequestMapping(value = "/admin/updateGraffito", method = RequestMethod.GET)
	public String updateGraffito(final HttpServletRequest request) {

		String id = request.getParameter("edrID");
		if (id == null || id.equals("")) {
			request.setAttribute("msg", "Please enter an EDR number");
			return "admin/editGraffito";
		}

		request.getSession().setAttribute("edrID", id);

		Inscription element = graffitiDao.getInscriptionByEDR(id);

		if (element == null) {
			request.setAttribute("msg", "Not a valid EDR number");
			return "admin/editGraffito";
		}

		request.setAttribute("graffito", element);

		return "admin/updateGraffito";

	}

	// Update a graffito controller
	@RequestMapping(value = "/admin/updateGraffito", method = RequestMethod.POST)
	public String adminUpdateGraffito(final HttpServletRequest request) {

		// updating AGP Inscription Information
		String edrID = (String) request.getSession().getAttribute("edrID");

		// updating AGP Inscriptions
		String summary = request.getParameter("summary");
		String commentary = request.getParameter("commentary");
		String cil = request.getParameter("cil");
		String langner = request.getParameter("langner");
		String floor_to_graffito_height = request.getParameter("floor_to_graffito_height");
		String content_translation = request.getParameter("content_translation");
		String graffito_height = request.getParameter("graffito_height");
		String graffito_length = request.getParameter("graffito_length");
		String letter_height_min = request.getParameter("letter_height_min");
		String letter_height_max = request.getParameter("letter_height_max");
		String charHeights = request.getParameter("character_heights");
		String figural = request.getParameter("figural");
		String ghFig = request.getParameter("gh_fig");
		String ghTrans = request.getParameter("gh_trans");

		boolean hasFiguralComponent = false;
		boolean isfeaturedHitFig = false;
		boolean isfeaturedHitTrans = false;

		if (figural != null) {
			hasFiguralComponent = true;
		}
		if (ghFig != null) {
			isfeaturedHitFig = true;
		}
		if (ghTrans != null) {
			isfeaturedHitTrans = true;
		}

		List<Object> agpOneDimArrList = new ArrayList<Object>();
		agpOneDimArrList.add(summary);
		agpOneDimArrList.add(content_translation);
		agpOneDimArrList.add(cil);
		agpOneDimArrList.add(langner);
		agpOneDimArrList.add(floor_to_graffito_height);
		agpOneDimArrList.add(graffito_height);
		agpOneDimArrList.add(graffito_length);
		agpOneDimArrList.add(letter_height_min);
		agpOneDimArrList.add(letter_height_max);
		agpOneDimArrList.add(charHeights);
		agpOneDimArrList.add(commentary);
		agpOneDimArrList.add(hasFiguralComponent);
		agpOneDimArrList.add(isfeaturedHitFig);
		agpOneDimArrList.add(isfeaturedHitTrans);

		graffitiDao.updateAgpInscription(agpOneDimArrList, edrID);

		if (hasFiguralComponent) {
			String drawingDescriptionLatin = request.getParameter("drawing_description_latin");
			String drawingDescriptionEnglish = request.getParameter("drawing_description_english");

			graffitiDao.updateDrawingInfo(drawingDescriptionLatin, drawingDescriptionEnglish, edrID);

		}

		if (isfeaturedHitFig || isfeaturedHitTrans) {
			String ghCommentary = request.getParameter("gh_commentary");
			String ghPreferredImage = request.getParameter("gh_preferred_image");
			graffitiDao.updateGreatestHitsInfo(edrID, ghCommentary, ghPreferredImage);
		}

		// updating drawing tags
		String[] drawingTags = request.getParameterValues("drawingCategory");
		graffitiDao.clearDrawingTags(edrID);

		if (drawingTags != null) {
			graffitiDao.insertDrawingTags(edrID, drawingTags);
		}

		request.setAttribute("msg", "The graffito has been successfully updated in the database");

		Inscription element = graffitiDao.getInscriptionByEDR(edrID);

		request.setAttribute("graffito", element);

		return "admin/updateGraffito";

	}

	// maps to inputData.jsp page which is used to input inscription to the
	// database using a csv file
	@RequestMapping(value = "/inputData", method = RequestMethod.GET)
	public String inputData(final HttpServletRequest request) {
		return "inputData";
	}

	/*
	 * reads in the file inputed from inputData.jsp and currently reads each
	 * line and check to see if it contains the string "EDR" to check if that
	 * line is a valid line holding information for a new inscription. Thus it
	 * is required for the inscriptions in the csv file to have an EDR number
	 * starting with EDR
	 */
	@RequestMapping(value = "/inputDataComplete", method = RequestMethod.POST)
	public String inputDataComplete(final HttpServletRequest request) {

		final ArrayList<ArrayList<String>> inscriptions = new ArrayList<ArrayList<String>>();
		try {
			BufferedReader file_in = new BufferedReader(new InputStreamReader(request.getInputStream()));
			String line = new String("");
			int count = 0;
			while ((line = file_in.readLine()) != null) {
				System.out.println(count);
				if (line.contains("EDR")) {
					System.out.println(line);
					inscriptions.add(separateFields(line));
				}
				count++;
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		this.graffitiDao.insertEdrInscription(inscriptions);

		return "inputDataComplete";
	}

	// Used for mapping the URI to show inscriptions. It has a similar structure
	// to the mapping done to /result below
	@RequestMapping(value = "/region/{city}", method = RequestMethod.GET)
	public String cityPage(@PathVariable String city, HttpServletRequest request) {
		String searches = city;
		final List<Inscription> resultsList = this.graffitiDao.getInscriptionsByCity(searches);
		request.setAttribute("resultsLyst", resultsList);
		HttpSession s = request.getSession();
		s.setAttribute("returnURL", ControllerUtils.getFullRequest(request));
		s.setAttribute("backToResults", false);
		return "displayData";
	}

	@RequestMapping(value = "/region/{city}/{insula}", method = RequestMethod.GET)
	public String insulaPage(@PathVariable String city, @PathVariable String insula, HttpServletRequest request,
			HttpServletResponse response) {
		System.out.println("insulaPage: " + insula);
		int insula_id = getInsulaId(city, insula);
		final List<Inscription> inscriptions = this.graffitiDao.getInscriptionsByCityAndInsula(city, insula_id);
		request.setAttribute("resultsLyst", inscriptions);
		HttpSession s = request.getSession();
		s.setAttribute("returnURL", ControllerUtils.getFullRequest(request));
		s.setAttribute("backToResults", false);
		return "displayData";
	}

	@RequestMapping(value = "/region/{city}/{insula}/{property}", method = RequestMethod.GET)
	public String propertyPage(@PathVariable String city, @PathVariable String property, @PathVariable String insula,
			HttpServletRequest request) {
		System.out.println("propertyPage: " + property);
		int insula_id = getInsulaId(city, insula);
		int property_id = getPropertyId(city, insula, property);
		final List<Inscription> inscriptions = this.graffitiDao.getInscriptionsByCityAndInsulaAndPropertyNumber(city,
				insula_id, property_id);
		request.setAttribute("resultsLyst", inscriptions);
		HttpSession s = request.getSession();
		s.setAttribute("returnURL", ControllerUtils.getFullRequest(request));
		s.setAttribute("backToResults", false);
		return "displayData";
	}

	@RequestMapping(value = "/region/{city}/{insula}/{property}/{id}", method = RequestMethod.GET)
	public String dataPage(@PathVariable String city, @PathVariable String property, @PathVariable String insula,
			@PathVariable int id, HttpServletRequest request) {
		int insula_id = getInsulaId(city, insula);
		int property_id = getPropertyId(city, insula, property);
		final List<Inscription> allInscriptions = this.graffitiDao.getInscriptionsByCityAndInsulaAndPropertyNumber(city,
				insula_id, property_id);
		final List<Inscription> resultsList2 = new ArrayList<Inscription>();

		if (id < allInscriptions.size()) {
			Inscription whichInsc = allInscriptions.get(id);
			resultsList2.add(whichInsc);
		}
		request.setAttribute("resultsLyst", resultsList2);
		HttpSession s = request.getSession();
		s.setAttribute("returnURL", ControllerUtils.getFullRequest(request));
		s.setAttribute("backToResults", false);
		return "displayData";
	}

	// helper method to get insula id for given insula name
	private int getInsulaId(String city, String insula) {
		final List<Insula> ins = this.insulaDao.getInsulaByCityAndInsula(city, insula.toUpperCase());
		if (ins != null && !ins.isEmpty()) {
			return ins.get(0).getId();
		}
		return -1;
	}

	// helper method to get property id for given property number
	private int getPropertyId(String city, String insula, String property_number) {
		final Property prop = this.findspotDao.getPropertyByCityAndInsulaAndProperty(city, insula.toUpperCase(),
				property_number);
		if (prop != null) {
			return prop.getId();
		}
		return -1;
	}

	// The default page is sent to index.jsp
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String indexPage(final HttpServletRequest request) {

		request.setAttribute("drawingCategories", drawingTagsDao.getDrawingTags());
		request.setAttribute("propertyTypes", findspotDao.getPropertyTypes());

		return "index";
	}

	// Maps to the details page once an individual result has been selected in
	// the results page
	@RequestMapping(value = "/graffito/AGP-{edr}", method = RequestMethod.GET)
	public String graffito(final HttpServletRequest request, @PathVariable String edr) {
		final Inscription i = this.graffitiDao.getInscriptionByEDR(edr);
		if (i == null) {
			request.setAttribute("error", "Error: " + edr + " is not a valid EDR id.");
			return "index";
		} else {
			Set<DrawingTag> tags = i.getAgp().getFiguralInfo().getDrawingTags();
			List<String> names = new ArrayList<String>();
			for (DrawingTag tag : tags) {
				names.add(tag.getName());
			}
			request.setAttribute("drawingCategories", names);
			request.setAttribute("images", i.getImages());
			request.setAttribute("imagePages", i.getPages());
			request.setAttribute("thumbnails", i.getThumbnails());
			request.setAttribute("findLocationKeys", findLocationKeys(i));
			request.setAttribute("inscription", i);
			request.setAttribute("city", i.getAncientCity());
			return "details";
		}
	}

	/*
	 * The main backend
	 */
	@RequestMapping(value = "/results", method = RequestMethod.GET)
	public String search(final HttpServletRequest request) {

		String searchQueryDesc;
		final Set<Inscription> resultSet = new TreeSet<Inscription>();
		String checkboxIds = ""; // Used to check the boxes of the searched
									// properties
		String searchedProperties = ""; // Used to create the blue filter labels
		String searchedDrawings = ""; // Used to create blue filter labels

		String browse = request.getParameter("browse");
		String searchOptions = request.getParameter("searchOptions");
		String drawing = request.getParameter("drawing");
		if (browse != null && browse.equals("true")) {
			List<Inscription> inscriptions = this.graffitiDao.getAllInscriptions();
			resultSet.addAll(inscriptions);
			searchQueryDesc = "browsing all inscriptions";

			request.setAttribute("city", "Pompeii");
			request.setAttribute("insula", "Herculaneum"); // TODO fix
															// this...temporary
															// fix to highlight
															// Herculaneum map
															// on browse page
		} else if (searchOptions != null && searchOptions.equals("true")) {
			List<Inscription> inscriptions = this.graffitiDao.getAllInscriptions();
			resultSet.addAll(inscriptions);
			searchQueryDesc = "";

			request.setAttribute("city", "Pompeii");
			request.setAttribute("insula", "Herculaneum"); // TODO fix
															// this...temporary
															// fix to highlight
															// Herculaneum map
															// on browse page
		} else if (drawing != null && !drawing.equals("")) {
			if (drawing.toLowerCase().equals("all")) {
				searchQueryDesc = "for all drawings";
				List<Inscription> inscriptions = this.graffitiDao.getInscriptionByDrawing("0");
				resultSet.addAll(inscriptions);
				checkboxIds += "dc0;";
				searchedDrawings += "All;";
			} else {
				List<DrawingTag> drawingTags = this.drawingTagsDao.getDrawingTags();
				String tag = null;
				searchQueryDesc = "for drawing tag ";
				try {
					for (DrawingTag dt : drawingTags) {
						if (dt.getId() == Integer.parseInt(drawing)) {
							tag = dt.getName();
							List<Inscription> inscriptions = this.graffitiDao.getInscriptionByDrawing(drawing);
							resultSet.addAll(inscriptions);
							searchQueryDesc += tag;
							checkboxIds += "dc" + dt.getId() + ";";
							searchedDrawings += dt.getName() + ";";
						}
					}
				} catch (NumberFormatException e) {
					// do nothing; user entered invalid id
				}
				if (tag == null) {
					request.setAttribute("error", "Error: " + drawing + " is not a valid drawing tag id.");
					return "index";
				}
			}
			request.setAttribute("city", "Pompeii");
			request.setAttribute("insula", "Herculaneum"); // TODO fix
															// this...temporary
															// fix to highlight
															// Herculaneum map
															// on drawing tag
															// page
		} else {
			String prop = "";
			final String[] properties = request.getParameterValues("property");
			final int[] propertyIds = new int[properties.length];
			try {
				for (int i = 0; i < properties.length; i++) {
					prop = properties[i];
					propertyIds[i] = Integer.parseInt(properties[i]);
				}
			} catch (NumberFormatException e) {
				if (prop.equals(""))
					request.setAttribute("error", "Error: no property id entered.");
				else
					request.setAttribute("error", "Error: " + prop + " is not a valid property id.");
				return "index";
			}
			Arrays.sort(propertyIds);

			searchQueryDesc = "in propert";

			int id = 0;

			try {
				if (propertyIds.length == 1) {
					id = propertyIds[0];
					Property property = this.findspotDao.getPropertyById(propertyIds[0]);
					searchQueryDesc += "y " + property.getInsula().getShortName() + "." + property.getPropertyNumber();
				} else {
					Property property;
					property = this.findspotDao.getPropertyById(propertyIds[0]);
					searchQueryDesc += "ies " + property.getInsula().getShortName() + "."
							+ property.getPropertyNumber();
					for (int i = 1; i < properties.length; i++) {
						id = propertyIds[i];
						property = this.findspotDao.getPropertyById(propertyIds[i]);
						String propName = property.getInsula().getShortName() + "." + property.getPropertyNumber();
						if (i + 1 != properties.length)
							searchQueryDesc += ", " + propName;
						else if (i == 1)
							searchQueryDesc += " and " + propName;
						else
							searchQueryDesc += ", and " + propName;
					}
					request.setAttribute("city", property.getInsula().getCity().getName());
				}

				for (int i = 0; i < propertyIds.length; i++) {
					Property property = this.findspotDao.getPropertyById(propertyIds[i]);
					checkboxIds += "p" + propertyIds[i] + ";";
					searchedProperties += property.getInsula().getShortName() + "." + property.getPropertyNumber()
							+ ";";
					final List<Inscription> inscriptions = this.graffitiDao
							.getInscriptionsByFindSpot(property.getInsula().getId(), property.getId());
					resultSet.addAll(inscriptions);
					request.setAttribute("city", property.getInsula().getCity().getName());
				}

			} catch (IndexOutOfBoundsException e) {
				request.setAttribute("error", "Error: " + id + " is not a valid property id.");
				return "index";
			}
		}
		final List<Inscription> resultsList = new ArrayList<Inscription>();
		final HttpSession session = request.getSession();

		resultsList.addAll(resultSet);
		if (resultsList.size() > 0) {
			request.setAttribute("mapName", resultsList.get(0).getAncientCity());
		}
		request.setAttribute("resultsLyst", resultsList);
		request.setAttribute("findLocationKeys", findLocationKeys(resultsList));
		request.setAttribute("searchQueryDesc", searchQueryDesc);
		session.setAttribute("checkboxIds", checkboxIds);
		request.setAttribute("searchedProperties", searchedProperties);
		request.setAttribute("searchedDrawings", searchedDrawings);

		// Used in sidebarSearchMenu.jsp
		request.setAttribute("cities", findspotDao.getCityNames());
		request.setAttribute("drawingCategories", drawingTagsDao.getDrawingTags());
		request.setAttribute("propertyTypes", findspotDao.getPropertyTypes());
		request.setAttribute("insulaList", insulaDao.getInsula());
		request.setAttribute("propertiesList", findspotDao.getProperties());

		session.setAttribute("returnURL", ControllerUtils.getFullRequest(request));

		// Checks if user was sent from "Back to Results" button; used to filter
		// results after returning to page

		if (session.getAttribute("backToResults") != null) {
			boolean backToResults = (boolean) session.getAttribute("backToResults");
			if (backToResults) {
				session.setAttribute("backToResults", false);
				session.setAttribute("returnFromEDR", session.getAttribute("edr"));
			} else {
				session.setAttribute("requestURL", "");
			}
		} else {
			session.setAttribute("backToResults", false);
			session.setAttribute("requestURL", "");
			session.setAttribute("returnFromEDR", "");
		}
		return "results";
	}

	@RequestMapping(value = "/backToResults", method = RequestMethod.GET)
	public void backToResults(final HttpServletRequest request) {
		System.out.println("Back to Results: " + request.getParameter("edr"));
		HttpSession s = request.getSession();
		s.setAttribute("backToResults", true);
		s.setAttribute("returnFromEDR", request.getParameter("edr"));
	}

	private static List<String> findLocationKeys(final List<Inscription> inscriptions) {
		final List<String> locationKeys = new ArrayList<String>();
		if (inscriptions != null) {
			final Set<String> locationKeysSet = new TreeSet<String>();
			for (final Inscription inscription : inscriptions) {
				locationKeysSet.add(inscription.getSpotKey());
				locationKeysSet.add(inscription.getGenSpotKey());
			}
			locationKeys.addAll(locationKeysSet);
		}
		return locationKeys;
	}

	private static List<String> findLocationKeys(final Inscription inscription) {
		final List<String> locationKeys = new ArrayList<String>();
		final Set<String> locationKeysSet = new TreeSet<String>();

		locationKeysSet.add(inscription.getSpotKey());
		locationKeysSet.add(inscription.getGenSpotKey());
		locationKeys.addAll(locationKeysSet);
		return locationKeys;
	}

	private static ArrayList<String> separateFields(String line) {
		final ArrayList<String> fields = new ArrayList<String>();
		while (line.contains(",")) {
			fields.add(line.substring(0, line.indexOf(",")));
			line = line.substring(line.indexOf(",") + 1);
		}
		fields.add(line);
		return fields;
	}

	public GraffitiDao getGraffitiDao() {
		return graffitiDao;
	}

	public void setGraffitiDao(final GraffitiDao graffitiDao) {
		this.graffitiDao = graffitiDao;
	}
}
