/*
 * GraffitiController.java is the main backend controller of the Ancient Graffiti Project. It handles most of the
 * controls regarding the requests.
 * 
 * @editor Trevor Stalnaker
 */
package edu.wlu.graffiti.controller;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.elasticsearch.index.query.QueryBuilders.regexpQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.wlu.graffiti.bean.DrawingTag;
import edu.wlu.graffiti.bean.Inscription;
import edu.wlu.graffiti.bean.Insula;
import edu.wlu.graffiti.bean.Property;
import edu.wlu.graffiti.dao.DrawingTagsDao;
import edu.wlu.graffiti.dao.FindspotDao;
import edu.wlu.graffiti.dao.GraffitiDao;
import edu.wlu.graffiti.dao.InsulaDao;
import edu.wlu.graffiti.dao.PropertyTypesDao;
import edu.wlu.graffiti.data.setup.Utils;
import edu.wlu.graffiti.data.setup.main.ImportEDRData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Controller
@Api(value = "Graffiti", description = "Operations pertaining to the graffiti.")
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

	public static final String WRITING_STYLE_PARAM_NAME = "writing_style";
	public static final String WRITING_STYLE_SEARCH_DESC = "Writing Style";
	public static final String DRAWING_CATEGORY_SEARCH_DESC = "Drawing Category";
	public static final String PROPERTY_TYPE_SEARCH_DESC = "Property Type";

	public static final String WRITING_STYLE_GRAFFITI_INSCRIBED = "Graffito/incised";

	public static final String CITY_FIELD_NAME = "city";
	public static final String LANGUAGE_IN_ENGLISH_FIELD_NAME = "language_in_english";
	public static final String WRITING_STYLE_IN_ENGLISH_FIELD_NAME = "writing_style_in_english";
	public static final String PROPERTY_TYPES_FIELD_NAME = "property.property_types";
	public static final String PROPERTY_ID_FIELD_NAME = "property.property_id";
	public static final String INSULA_NAME_FIELD_NAME = "insula.insula_name";
	public static final String INSULA_ID_FIELD_NAME = "insula.insula_id";

	/** default size in elasticsearch is 10 */
	private static final int NUM_RESULTS_TO_RETURN = 2000;

	/** elastic search configuration properties */
	private static String ES_HOSTNAME;
	private static int ES_PORT_NUM;
	private static String ES_TYPE_NAME;
	private static String ES_INDEX_NAME;
	private static String ES_CLUSTER_NAME;

	@Resource
	private FindspotDao propertyDao;

	private TransportClient client;

	private Settings settings;

	private static String[] searchDescs = { "Content Keyword", "Global Keyword", "CIL Keyword", "City", "Insula", "Property",
			PROPERTY_TYPE_SEARCH_DESC, DRAWING_CATEGORY_SEARCH_DESC, WRITING_STYLE_SEARCH_DESC, "Language" };

	private static String[] searchFields = { "content",
			"content content_translation summary city insula.insula_name property.property_name property.property_types"
					+ "cil description writing_style language edr_id bibliography"
					+ " drawing.description_in_english drawing.description_in_latin drawing.drawing_tags",
			"cil",
			CITY_FIELD_NAME, INSULA_ID_FIELD_NAME, PROPERTY_ID_FIELD_NAME, PROPERTY_TYPES_FIELD_NAME,
			"drawing.drawing_tag_ids", WRITING_STYLE_IN_ENGLISH_FIELD_NAME, LANGUAGE_IN_ENGLISH_FIELD_NAME };

	private Properties prop = null;

	private void init() {
		if (prop == null) {
			prop = Utils.getConfigurationProperties();
			ES_HOSTNAME = prop.getProperty("es.loc");
			ES_PORT_NUM = Integer.parseInt(prop.getProperty("es.port"));
			ES_INDEX_NAME = prop.getProperty("es.index");
			ES_TYPE_NAME = prop.getProperty("es.type");
			ES_CLUSTER_NAME = prop.getProperty("es.cluster_name");
		}
		settings = Settings.builder().put("cluster.name", ES_CLUSTER_NAME).build();
	}

	@RequestMapping(value = "/searchPompeii", method = RequestMethod.GET)
	public String searchMap(final HttpServletRequest request) {
		return "searchPompeii";
	}

	@RequestMapping(value = "/searchHerculaneum", method = RequestMethod.GET)
	public String searchHerc(final HttpServletRequest request) {
		return "searchHerculaneum";
	}

	/*
	 * 
	 * @RequestMapping(value = "/new-featured-graffiti", method =
	 * RequestMethod.GET) public String newFeaturedHits(final HttpServletRequest
	 * request) {
	 * 
	 * final List<Inscription> greatestFiguralHits =
	 * this.graffitiDao.getGreatestFiguralHits(); final List<Inscription>
	 * greatestTranslationHits = this.graffitiDao.getGreatestTranslationHits();
	 * request.setAttribute("figuralHits", greatestFiguralHits);
	 * request.setAttribute("translationHits", greatestTranslationHits);
	 * 
	 * return "newFeaturedGraffiti";
	 * 
	 * }
	 */

	// maps to inputData.jsp page which is used to input inscription to the
	// database using a csv file
	@RequestMapping(value = "/inputData", method = RequestMethod.GET)
	public String inputData(final HttpServletRequest request) {
		return "inputData";
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
		return "displayData";
	}

	@RequestMapping(value = "/region/{city}/{insula}", method = RequestMethod.GET)
	public String insulaPage(@PathVariable String city, @PathVariable String insula, HttpServletRequest request,
			HttpServletResponse response) {
		// System.out.println("insulaPage: " + insula);
		int insula_id = getInsulaId(city, insula);
		final List<Inscription> inscriptions = this.graffitiDao.getInscriptionsByCityAndInsula(city, insula_id);
		request.setAttribute("resultsLyst", inscriptions);
		HttpSession s = request.getSession();
		s.setAttribute("returnURL", ControllerUtils.getFullRequest(request));
		return "displayData";
	}

	@RequestMapping(value = "/region/{city}/{insula}/{property}", method = RequestMethod.GET)
	public String propertyPage(@PathVariable String city, @PathVariable String property, @PathVariable String insula,
			HttpServletRequest request) {
		// System.out.println("propertyPage: " + property);
		int insula_id = getInsulaId(city, insula);
		int property_id = getPropertyId(city, insula, property);
		final List<Inscription> inscriptions = this.graffitiDao.getInscriptionsByCityAndInsulaAndPropertyNumber(city,
				insula_id, property_id);
		request.setAttribute("resultsLyst", inscriptions);
		HttpSession s = request.getSession();
		s.setAttribute("returnURL", ControllerUtils.getFullRequest(request));
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
	@ApiOperation(value = "Opens the details page once an individual result has been selected in the results page. Displays "
			+ "more information pertaining to the selected inscription.")
	@RequestMapping(value = "/graffito/{agpId}", method = RequestMethod.GET)
	public String graffito(final HttpServletRequest request, @PathVariable String agpId) {
		String edr = agpId.replaceFirst("AGP-", "");
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
			String city = i.getAncientCity();
			request.setAttribute("drawingCategories", names);
			request.setAttribute("images", i.getImages());
			request.setAttribute("imagePages", i.getPages());
			request.setAttribute("thumbnails", i.getThumbnails());
			request.setAttribute("findLocationKeys", findLocationKey(i));
			request.setAttribute("insulaLocationKeys", findInsulaLocation(i));
			request.setAttribute("inscription", i);
			request.setAttribute("city", city);
			request.setAttribute("notations", notationsInContent(i));
			request.getSession().setAttribute("returnFromEDR", edr);

			// Decides which jsp page to travel to when user clicks "More
			// Information" on Search page.

			return "details";
		}
	}

	// TODO: add annotation produces = "text/html" for the api docs?
	@ApiOperation(value = "Searches for inscriptions and returns the results. The base URI lists "
			+ "all inscriptions by default. Various parameters can be added to the URI to filter "
			+ "results as the user wishes.", notes = "A detailed overview of possible parameters is as follows: <br/> "
					+ "city={cityName}, where the cities are as follows: [Pompeii, Herculaneum]. <br/>"
					+ "insula={insulaID} <br/>" + "property={propertyID} <br/>" + "property_type={propertyType}<br/>"
					+ "drawing_category={dcID}, where the dcIDs are as follows: [All=0, Boats=1, Geometric designs=2, Animals=3, Erotic Images=4, Other=5, Human figures=6, Gladiators=7, Plants=8]. <br/>"
					+ "writing_style={writingStyle}, where the writing styles are as follows: [Graffito/incised, charcoal, other].<br/>"
					+ "language={language}, where the languages are as follows: [Latin, Greek, Latin/Greek, other].<br/>"
					+ "global={searchString}, where the search string can be any text to search globally for. <br/>"
					+ "content={searchString}, where the search string can be any text to search the content for. <br/>"
					+ "cil={searchString}, where the search string can be any text to search the cil for. <br/>"
					+ "sort_by={sortParameter}, where the sort parameters are as follows: [summary, cil, property.property_id]. <br/>"
					+ "Mutiple parameters passed in the URI can be separated using an ampersand symbol, '&'.")
	@RequestMapping(value = "/results", method = RequestMethod.GET)
	public String search(final HttpServletRequest request) {
		init();
		HttpSession s = request.getSession();

		s.setAttribute("returnURL", ControllerUtils.getFullRequest(request));
		List<Inscription> inscriptions = searchResults(request);
		request.setAttribute("resultsLyst", inscriptions);
		request.setAttribute("searchQueryDesc", "filtering");
		request.setAttribute("findLocationKeys", findLocationKeys(inscriptions));
		request.setAttribute("insulaLocationKeys", findInsulaLocations(inscriptions));
		return "results";
	}

	@ApiOperation(value = "Filters the inscriptions and returns the results without any styling. The base URI lists "
			+ "all inscriptions by default. Various parameters can be added to the URI to filter "
			+ "results as the user wishes.", notes = "A detailed overview of possible parameters is as follows: <br/> "
					+ "city={cityName}, where the cities are as follows: [Pompeii, Herculaneum]. <br/>"
					+ "insula={insulaID} <br/>" + "property={propertyID} <br/>" + "property_type={propertyType}<br/>"
					+ "drawing_category={dcID}, where the dcIDs are as follows: [All=0, Boats=1, Geometric designs=2, Animals=3, Erotic Images=4, Other=5, Human figures=6, Gladiators=7, Plants=8]. <br/>"
					+ "writing_style={writingStyle}, where the writing styles are as follows: [Graffito/incised, charcoal, other].<br/>"
					+ "language={language}, where the languages are as follows: [Latin, Greek, Latin/Greek, other].<br/>"
					+ "global={searchString}, where the search string can be any text to search globally for. <br/>"
					+ "content={searchString}, where the search string can be any text to search the content for. <br/>"
					+ "cil={searchString}, where the search string can be any text to search the cil for. <br/>"
					+ "sort_by={sortParameter}, where the sort parameters are as follows: [summary, cil, property.property_id]. <br/>"
					+ "Mutiple parameters passed in the URI can be separated using an ampersand symbol, '&'.")
	@RequestMapping(value = "/filter", method = RequestMethod.GET)
	public String filterResults(final HttpServletRequest request) {
		init();
		HttpSession s = request.getSession();
		s.setAttribute("returnURL", ControllerUtils.getFullRequest(request));
		List<Inscription> inscriptions = searchResults(request);
		request.setAttribute("resultsLyst", inscriptions);
		request.setAttribute("findLocationKeys", findLocationKeys(inscriptions));
		request.setAttribute("insulaLocationKeys", findInsulaLocations(inscriptions));
		return "filter";
	}

	@RequestMapping(value = "/print", method = RequestMethod.GET)
	public String printResults(final HttpServletRequest request) {
		init();
		HttpSession s = request.getSession();
		s.setAttribute("returnURL", ControllerUtils.getFullRequest(request));
		List<Inscription> inscriptions = searchResults(request);

		request.setAttribute("resultsLyst", inscriptions);
		request.setAttribute("searchQueryDesc", "filtering");
		return "print";
	}

	private List<Inscription> searchResults(final HttpServletRequest request) {
		try {
			client = new PreBuiltTransportClient(settings).addTransportAddress(
					new InetSocketTransportAddress(InetAddress.getByName(ES_HOSTNAME), ES_PORT_NUM));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		SearchResponse response;
		String searchedProperties = "";
		String searchedDrawings = "";

		List<Inscription> inscriptions = new ArrayList<Inscription>();

		// List of parameter strings for each given search term
		List<String> parameters = new ArrayList<String>();
		// List of search terms
		List<String> searchTerms = new ArrayList<String>();
		// List of field names to search for each different search term
		List<String> fieldNames = new ArrayList<String>();

		// Gather all of the request parameters and make an array of those
		// arrays to loop through and check if null
		String[] content = request.getParameterValues("content");
		String[] global = request.getParameterValues("global");
		String[] cil = request.getParameterValues("cil");
		String[] city = request.getParameterValues("city");
		String[] insula = request.getParameterValues("insula");
		String[] property = request.getParameterValues("property");
		String[] propertyType = request.getParameterValues("property_type");
		String[] drawingCategory = request.getParameterValues("drawing_category");
		String[] writingStyle = request.getParameterValues(WRITING_STYLE_PARAM_NAME);
		String[] language = request.getParameterValues("language");

		String[] sortOrder = request.getParameterValues("sort_by");

		String[][] searches = { content, global, cil, city, insula, property, propertyType, drawingCategory, writingStyle,
				language };

		// Determine which parameters have been given; populate the
		// parameters, searchTerms, and fieldNames lists accordingly
		for (int i = 0; i < searches.length; i++) {
			if (searches[i] != null) {
				parameters.add(arrayToString(searches[i]));
				searchTerms.add(searchDescs[i]);
				fieldNames.add(searchFields[i]);
			}
		}
		
		try {
			// This is the main query; does an AND of all sub-queries
			BoolQueryBuilder query = boolQuery();
	
			// For each given search term, we build a sub-query
			// Special cases are Global Keyword, Content Keyword, CIL Keyword, and Property
			// searches; all others are simple match queries
			for (int i = 0; i < searchTerms.size(); i++) {
	
				// System.out.println(searchTerms.get(i) + ": " +
				// parameters.get(i));
	
				// Searches has_figural_component if user selected "All"
				// drawings
				if (searchTerms.get(i).equals(DRAWING_CATEGORY_SEARCH_DESC) && parameters.get(i).contains("All")) {
					// SES: I think that query should be okay because the other
					// drawing categories are numbers.
					// As soon as we have the query "All", then we get all the
					// figural graffiti and we can skip
					// the rest of the drawing-related query.
					BoolQueryBuilder allDrawingsQuery = boolQuery();
					allDrawingsQuery.should(matchQuery("has_figural_component", true));
					query.must(allDrawingsQuery);
				} else if (searchTerms.get(i).equals("Global Keyword")) {
					// Checks content, city, insula name, property name,
					// property types, drawing description, drawing tags,
					// writing style, language, EAGLE id, and bibliography for a
					// keyword match
					BoolQueryBuilder globalQuery;
					globalQuery = boolQuery();
					globalQuery.should(queryStringQuery(parameters.get(i).toLowerCase()).useAllFields(true)); // exact
																								// match
					globalQuery.should(queryStringQuery("*" + parameters.get(i).toLowerCase() + "*").useAllFields(true)); // partial
					query.must(globalQuery);
				} else if (searchTerms.get(i).equals("Content Keyword")) {
					BoolQueryBuilder contentQuery = boolQuery();
					String[] params = parameters.get(i).split(" ");
	
					for (String param : params) {
						BoolQueryBuilder orQuery = boolQuery();
						orQuery.should(matchQuery(fieldNames.get(i), param.toLowerCase())); // exact
																				// match
						orQuery.should((regexpQuery(fieldNames.get(i), ".*" + param.toLowerCase() + ".*"))); // partial
						contentQuery.must(orQuery);
					}
	
					query.must(contentQuery);
				} else if (searchTerms.get(i).equals("CIL Keyword")){
					BoolQueryBuilder cilQuery;
					cilQuery = boolQuery();
					cilQuery.should(matchQuery(fieldNames.get(i), parameters.get(i).toUpperCase())); // exact
																								// match
					query.must(cilQuery);
				} else if (searchTerms.get(i).equals("Property")) {
					BoolQueryBuilder propertiesQuery = boolQuery();
	
					String[] properties = parameters.get(i).split(" ");
	
					for (int j = 0; j < properties.length; j++) {
						QueryBuilder propertyIdQuery;
						BoolQueryBuilder propertyQuery;
	
						String propertyID = properties[j];
						propertyIdQuery = termQuery(fieldNames.get(i), propertyID);
						propertyQuery = boolQuery().must(propertyIdQuery);
						propertiesQuery.should(propertyQuery);
					}
					query.must(propertiesQuery);
				} else if (searchTerms.get(i).equals(WRITING_STYLE_SEARCH_DESC)
						&& parameters.get(i).equalsIgnoreCase("other")) {
					// special handling of the writing style being "other"
					query.mustNot(termQuery(fieldNames.get(i), "charcoal"));
					query.mustNot(termQuery(fieldNames.get(i), WRITING_STYLE_GRAFFITI_INSCRIBED));
				} else {
					BoolQueryBuilder otherQuery = boolQuery();
					String[] params = parameters.get(i).split(" ");
	
					for (String param : params) {
						// System.out.println(searchTerms.get(i) + ": match " +
						// param + " in " + fieldNames.get(i));
						otherQuery.should(termQuery(fieldNames.get(i), param));
					}
					query.must(otherQuery);
				}
			}
	
			if (sortOrder == null || sortOrder[0].equals("relevance")) {
				response = client.prepareSearch(ES_INDEX_NAME).setTypes(ES_TYPE_NAME).setQuery(query)
						.addStoredField("edr_id").setSize(NUM_RESULTS_TO_RETURN).get();
			} else {
				response = client.prepareSearch(ES_INDEX_NAME).setTypes(ES_TYPE_NAME).setQuery(query)
						.addStoredField("edr_id").setSize(NUM_RESULTS_TO_RETURN).addSort(sortOrder[0], SortOrder.ASC).get();
			}
			
			for (SearchHit hit : response.getHits()) {
				inscriptions.add(hitToInscription(hit));
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			return new ArrayList<Inscription>(); 
		}

		// client.close(); // This line slows down searching

		Thread closeClient = new Thread() {
			public void run() {
				client.close();
			}
		};
		closeClient.start();

		HttpSession session = request.getSession();
		
		request.setAttribute("searchedProperties", searchedProperties);
		request.setAttribute("searchedDrawings", searchedDrawings);

		// Used in sidebarSearchMenu.jsp
		request.setAttribute("cities", findspotDao.getCityNames());
		request.setAttribute("drawingCategories", drawingTagsDao.getDrawingTags());
		request.setAttribute("propertyTypes", findspotDao.getPropertyTypes());
		request.setAttribute("insulaList", insulaDao.getInsula());
		request.setAttribute("propertiesList", findspotDao.getProperties());

		session.setAttribute("returnURL", ControllerUtils.getFullRequest(request));

		return inscriptions;
	}

	// Turns an array like ["Pompeii", "Herculaneum"] into a string like
	// "Pompeii Herculaneum" for Elasticsearch match query
	private static String arrayToString(String[] parameters) {
		StringBuilder sb = new StringBuilder();
		sb.append(parameters[0].replace("_", " "));
		for (int i = 1; i < parameters.length; i++) {
			sb.append(" ").append(parameters[i].replace("_", " "));
		}
		return sb.toString();
	}

	private Inscription hitToInscription(SearchHit hit) {
		String edrID = hit.getField("edr_id").getValue();
		Inscription inscription = graffitiDao.getInscriptionByEDR(edrID);
		return inscription;
	}

	private static List<Integer> findLocationKeys(final List<Inscription> inscriptions) {
		final List<Integer> locationKeys = new ArrayList<Integer>();
		if (inscriptions != null) {
			final Set<Integer> locationKeysSet = new TreeSet<Integer>();
			for (final Inscription inscription : inscriptions) {
				locationKeysSet.add(inscription.getSpotKey());
			}
			locationKeys.addAll(locationKeysSet);
		}
		return locationKeys;
	}

	private static List<Integer> findLocationKey(final Inscription inscription) {
		final List<Integer> locationKeys = new ArrayList<Integer>();
		final Set<Integer> locationKeysSet = new TreeSet<Integer>();

		locationKeysSet.add(inscription.getSpotKey());
		locationKeys.addAll(locationKeysSet);
		return locationKeys;
	}
	
	private static List<Integer> findInsulaLocation(final Inscription inscription) {
		final List<Integer> locationKeys = new ArrayList<Integer>();
		locationKeys.add(inscription.getAgp().getProperty().getInsula().getId());
		return locationKeys;
	}
	
	private static List<Integer> findInsulaLocations(final List<Inscription> inscriptions) {
		final List<Integer> locationKeys = new ArrayList<Integer>();
		if (inscriptions != null) {
			final Set<Integer> locationKeysSet = new TreeSet<Integer>();
			for (final Inscription inscription : inscriptions) {
				locationKeysSet.add(inscription.getAgp().getProperty().getInsula().getId());
			}
			locationKeys.addAll(locationKeysSet);
		}
		return locationKeys;
	}

	public GraffitiDao getGraffitiDao() {
		return graffitiDao;
	}

	public void setGraffitiDao(final GraffitiDao graffitiDao) {
		this.graffitiDao = graffitiDao;
	}
	
	private static ArrayList<String> notationsInContent(Inscription i) {
		ArrayList<String> notations = new ArrayList<String>();
		String content = ImportEDRData.normalize(i.getContent());
		Pattern pattern;
		Matcher matcher;
		
		//Contains UpperCase Characters
		pattern = Pattern.compile("([A-Z][\u0332,\u0323,\u0302]?){2,}");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			String temp = matcher.group(0);
			if (!temp.matches("[C,D,I,L,M,V,X]+"))
			notations.add("upper");
		}
		//Abbreviations
		pattern = Pattern.compile("[^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]*\\([^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]+\\)");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			notations.add("abbr");
		}
		//Uncertain Abbreviations
		pattern = Pattern.compile("[^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]*\\([^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]+\\?\\)");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			notations.add("uncert");
			notations.add("abbr");
		}
		//Lost Content
		pattern = Pattern.compile("\\[(\\-[ ]?)+\\]");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			notations.add("lostContent");
			content = content.replaceAll("\\[(\\-[ ]?)+\\]", "");
		}
		//Figural
		pattern = Pattern.compile("\\(\\(\\:[^\\(\\[\\)\\]\\:\\<\\>\\?]*\\)\\)");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			notations.add("fig");
		}
		//IntentionallyErased
		pattern = Pattern.compile("(\\[\\[|〚)([^\\s\\(\\[\\)\\]\\:\\<\\>\\,]|\\[?\\]?)*(\\]\\]|〛)");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			notations.add("intErased");
		}
		//Once Present
		pattern = Pattern.compile("\\[([^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]||[ ]?)*\\]");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			notations.add("oncePres");
		}
		//Uncertain Once Present
		pattern = Pattern.compile("\\[([^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]||[ ]?)*\\?\\]");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			notations.add("oncePres");
			notations.add("uncert");
		}
		//Non-standard Spellings
		pattern = Pattern.compile("[^\\s^>]*[ ]?\\(\\:[^\\s]*\\)");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			notations.add("nonStandSpell");
		}
		//Uncertain Non-standard Spellings
		pattern = Pattern.compile("[^\\s^>]*[ ]?\\(\\:[^\\s\\?]*\\?\\)");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			notations.add("nonStandSpell");
		}
		//Illegible Characters
		pattern = Pattern.compile("\\++");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			notations.add("illegChar");
		}
		//Letters Joined in Ligature
		pattern = Pattern.compile("(([^\\s]\u0302)+)([^\\s](\u0323|\u0332)?)");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			notations.add("lig");
		}
		//Lost line
		pattern = Pattern.compile("- - - - - -");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			notations.add("lostLines");
		}
		//Once visible now missing
		pattern = Pattern.compile("([^\\s]\u0332)+");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			notations.add("underline");
		}
		//Damaged Characters
		pattern = Pattern.compile("([^\\s]\u0323)+");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			notations.add("dots");
		}
		//Uncertain Lost Characters
		pattern = Pattern.compile("\\[\\+([0-9]+)\\?\\+\\]");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			notations.add("uncert");
			notations.add("illegChar");
			notations.add("oncePres");
		}
		
		return notations;
	}

	@RequestMapping(value = "/admin/report", method = RequestMethod.GET)
	public String reportResults(final HttpServletRequest request) {
		init();
		HttpSession s = request.getSession();
		s.setAttribute("returnURL", ControllerUtils.getFullRequest(request));
		List<Inscription> inscriptions = searchResults(request);
		request.setAttribute("resultsLyst", inscriptions);
		request.setAttribute("searchQueryDesc", "filtering");
		return "admin/report";
	}

}