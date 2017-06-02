package edu.wlu.graffiti.controller;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.wlu.graffiti.bean.Inscription;
import edu.wlu.graffiti.dao.DrawingTagsDao;
import edu.wlu.graffiti.dao.FindspotDao;
import edu.wlu.graffiti.dao.GraffitiDao;
import edu.wlu.graffiti.data.setup.Utils;

@Controller
public class FilterController {

	public static final String WRITING_STYLE_PARAM_NAME = "Writing_Style";
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
	private DrawingTagsDao drawingTagsDao;

	@Resource
	private FindspotDao propertyDao;

	@Resource
	private GraffitiDao graffitiDao;

	private TransportClient client;

	private Settings settings;

	private static String[] searchDescs = { "Content Keyword", "Global Keyword", "City", "Insula", "Property",
			PROPERTY_TYPE_SEARCH_DESC, DRAWING_CATEGORY_SEARCH_DESC, WRITING_STYLE_SEARCH_DESC, "Language" };

	private static String[] searchFields = { "content",
			"content city insula.insula_name property.property_name property.property_types"
					+ "cil description writing_style language edr_id bibliography"
					+ " drawing.description_in_english drawing.description_in_latin drawing.drawing_tags",
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
		settings = Settings.settingsBuilder().put("cluster.name", ES_CLUSTER_NAME).build();
	}

	@RequestMapping(value = "/filter", method = RequestMethod.GET)
	public String filterResults(final HttpServletRequest request) {
		init();
		HttpSession s = request.getSession();
		s.setAttribute("returnURL", ControllerUtils.getFullRequest(request));
		s.setAttribute("returnFromEDR", "");
		List<Inscription> inscriptions = searchResults(request);
		// request.setAttribute("findLocationKeys",
		// findLocationKeys(inscriptions));

		request.setAttribute("resultsLyst", inscriptions);
		request.setAttribute("searchQueryDesc", "filtering");
		return "filter";
	}

	@RequestMapping(value = "/admin/report", method = RequestMethod.GET)
	public String reportResults(final HttpServletRequest request) {
		init();
		HttpSession s = request.getSession();
		s.setAttribute("returnURL", ControllerUtils.getFullRequest(request));
		List<Inscription> inscriptions = searchResults(request);
		request.setAttribute("resultsLyst", inscriptions);
		request.setAttribute("searchQueryDesc", "filtering");
		s.setAttribute("returnFromEDR", "");
		return "admin/report";
	}

	private List<Inscription> searchResults(final HttpServletRequest request) {
		System.out.println("We're in FilterController: " + request.getQueryString());

		try {
			client = new TransportClient.Builder().settings(settings).build().addTransportAddress(
					new InetSocketTransportAddress(InetAddress.getByName(ES_HOSTNAME), ES_PORT_NUM));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		SearchResponse response;

		List<Inscription> inscriptions = new ArrayList<Inscription>();

		String queryAll = request.getParameter("query_all");

		if (queryAll != null && queryAll.equals("true")) {
			return graffitiDao.getAllInscriptions();
		} else {
			// List of parameter strings for each given search term
			List<String> parameters = new ArrayList<String>();
			// List of search terms
			List<String> searchTerms = new ArrayList<String>();
			// List of field names to search for each different search term
			List<String> fieldNames = new ArrayList<String>();

			// Gather all of the request parameters and make an array of those
			// arrays to loop through and check if null
			String[] content = request.getParameterValues("Content");
			String[] global = request.getParameterValues("Global");
			String[] city = request.getParameterValues("City");
			String[] insula = request.getParameterValues("Insula");
			String[] property = request.getParameterValues("Property");
			String[] propertyType = request.getParameterValues("Property_Type");
			String[] drawingCategory = request.getParameterValues("Drawing_Category");
			String[] writingStyle = request.getParameterValues(WRITING_STYLE_PARAM_NAME);
			String[] language = request.getParameterValues("Language");

			String[][] searches = { content, global, city, insula, property, propertyType, drawingCategory,
					writingStyle, language };

			String checkboxIds = ""; // Used to check the boxes of the searched
			// properties

			// Determine which parameters have been given; populate the
			// parameters, searchTerms, and fieldNames lists accordingly
			for (int i = 0; i < searches.length; i++) {
				if (searches[i] != null) {
					parameters.add(arrayToString(searches[i]));
					searchTerms.add(searchDescs[i]);
					fieldNames.add(searchFields[i]);
				}
			}

			// This is the main query; does an AND of all sub-queries
			BoolQueryBuilder query = boolQuery();

			// For each given search term, we build a sub-query
			// Special cases are Global Keyword, Content Keyword, and Property
			// searches; all others are simple match queries
			for (int i = 0; i < searchTerms.size(); i++) {

				System.out.println(searchTerms.get(i) + ": " + parameters.get(i));

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
					checkboxIds += "dc0;";
				} else if (searchTerms.get(i).equals("Global Keyword")) {
					// Checks content, city, insula name, property name,
					// property types, drawing description, drawing tags,
					// writing style, language, EAGLE id, and bibliography for a
					// keyword match
					BoolQueryBuilder globalQuery;
					QueryBuilder fuzzyQuery;
					QueryBuilder exactQuery;

					String[] a = fieldNames.get(i).split(" ");
					globalQuery = boolQuery();
					fuzzyQuery = multiMatchQuery(parameters.get(i), a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7],
							a[8]).fuzziness("AUTO");
					exactQuery = multiMatchQuery(parameters.get(i), a[9], a[10]);

					// For EDR id and bibliography, users want exact results.

					globalQuery.should(fuzzyQuery);
					globalQuery.should(exactQuery);

					query.must(globalQuery);
				} else if (searchTerms.get(i).equals("Content Keyword")) {
					BoolQueryBuilder contentQuery = boolQuery();
					String[] params = parameters.get(i).split(" ");

					for (String param : params) {
						contentQuery.must(matchQuery(fieldNames.get(i), param).fuzziness("AUTO"));
					}
					query.must(contentQuery);
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

						checkboxIds += "p" + propertyID + ";";
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
						System.out.println(searchTerms.get(i) + ": match " + param + " in " + fieldNames.get(i));
						otherQuery.should(termQuery(fieldNames.get(i), param));
					}
					query.must(otherQuery);
				}
			}

			response = client.prepareSearch(ES_INDEX_NAME).setTypes(ES_TYPE_NAME).setQuery(query)
					.addFields("id", CITY_FIELD_NAME, INSULA_ID_FIELD_NAME, INSULA_NAME_FIELD_NAME,
							PROPERTY_ID_FIELD_NAME, "property.property_number", "property.property_name",
							PROPERTY_TYPES_FIELD_NAME, "drawing.description_in_english", "drawing.description_in_latin",
							"drawing.drawing_tag_ids", "content", "edr_id", "bibliography",
							WRITING_STYLE_IN_ENGLISH_FIELD_NAME, LANGUAGE_IN_ENGLISH_FIELD_NAME, "cil", "description",
							"lagner", "comment", "content_translation", "measurements")
					.setSize(NUM_RESULTS_TO_RETURN).addSort("edr_id", SortOrder.ASC).execute().actionGet();

			for (SearchHit hit : response.getHits()) {
				inscriptions.add(hitToInscription(hit));
			}
			client.close();
			HttpSession session = request.getSession();
			session.setAttribute("checkboxIds", checkboxIds);
			System.out.println("checkboxIDs: " + checkboxIds);
			return inscriptions;
		}

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
		String edrID = hit.field("edr_id").value();

		Inscription inscription = graffitiDao.getInscriptionByEDR(edrID);

		return inscription;
	}
}