/*
 * GraffitiController.java is the main backend controller of the Ancient Graffiti Project. It handles most of the
 * controls regarding the requests.
 */
package edu.wlu.graffiti.controller;

import java.io.*;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.wlu.graffiti.bean.Inscription;
import edu.wlu.graffiti.dao.DrawingTagsDao;
import edu.wlu.graffiti.dao.GraffitiDao;
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

	// Maps to the search.jsp page currently receives information from
	// regions.txt file for the dropdown menu
	// that holds the information for each property in an ancient city (i.e.
	// Pompeii)
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public String searchForm(final HttpServletRequest request) {
		final ArrayList<String> regions = getRegionsFromFile();
		request.setAttribute("regions", regions);

		request.setAttribute("drawingTags", drawingTagsDao.getDrawingTags());
		request.setAttribute("propertyTypes",
				propertyTypesDao.getPropertyTypes());

		return "search";
	}

	private ArrayList<String> getRegionsFromFile() {
		final ArrayList<String> regions = new ArrayList<String>();
		final InputStream inStream = Thread.currentThread()
				.getContextClassLoader().getResourceAsStream("regionNames.txt");
		final BufferedReader br = new BufferedReader(new InputStreamReader(
				inStream));
		String line = "";
		try {
			while ((line = br.readLine()) != null) {
				regions.add(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return regions;
	}

	@RequestMapping(value = "/AdminFunctions", method = RequestMethod.GET)
	public String adminFunctions(final HttpServletRequest request) {
		return "AdminFunctions";
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
			BufferedReader file_in = new BufferedReader(new InputStreamReader(
					request.getInputStream()));
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

		this.graffitiDao.insertInscription(inscriptions);

		return "inputDataComplete";
	}

	/*
	 * @RequestMapping(value = "/updateTest", method = RequestMethod.GET) public
	 * String updateTest(final HttpServletRequest request) { final String edr =
	 * request.getParameter("EDR"); final List<Inscription> inscriptions =
	 * this.graffitiDao .getInscriptionsByEDR(edr);
	 * 
	 * System.out.println(inscriptions.get(0).getEagleId());
	 * request.setAttribute("inscriptions", inscriptions); return "updateTest";
	 * }
	 */

	/*
	 * Handles the backend of editing that is done in updateTest.jsp and sends
	 * the relevant information down to graffitiDao to make changes in the
	 * database.Currently clears the drawing tags for the inscription because
	 * the database does not allow multiple drawing tags, but also to prevent
	 * having multiple instances of the same drawing tags listed in
	 * graffitotodrawingstags table in the database.
	 */
	@RequestMapping(value = "/editComplete", method = RequestMethod.GET)
	public String editComplete(final HttpServletRequest request) {
		final ArrayList<String> fields = new ArrayList<String>();
		final String[] dt = request.getParameterValues("drawing");
		fields.add(request.getParameter("ancientCity"));
		fields.add(request.getParameter("findspot"));
		fields.add(request.getParameter("measurements"));
		fields.add(request.getParameter("language"));
		fields.add(request.getParameter("content"));
		fields.add(request.getParameter("bibliography"));
		fields.add(request.getParameter("writingStyle"));
		fields.add(request.getParameter("url"));
		fields.add(request.getParameter("eagleId"));

		this.graffitiDao.clearDrawingTags(request.getParameter("eagleId"));
		this.graffitiDao.insertDrawingTags(request.getParameter("eagleId"), dt);
		this.graffitiDao.updateInscription(fields);

		return "editComplete";
	}

	// maps to insertData.jsp which is a page for the user to add new
	// inscriptions one at a time.
	@RequestMapping(value = "/insertData", method = RequestMethod.GET)
	public String insertData(final HttpServletRequest request) {
		return "insertData";
	}

	// Handles information that is input through insertData.jsp and thus adds a
	// new inscription into the database.
	@RequestMapping(value = "/insertDataComplete", method = RequestMethod.GET)
	public String insertDataComplete(final HttpServletRequest request) {
		final ArrayList<ArrayList<String>> inscriptions = new ArrayList<ArrayList<String>>();
		final ArrayList<String> fields = new ArrayList<String>();
		fields.add(request.getParameter("EDR"));
		fields.add(request.getParameter("ancientCity"));
		fields.add(request.getParameter("findspot"));
		fields.add(request.getParameter("measurements"));
		fields.add(request.getParameter("language"));
		fields.add(request.getParameter("content"));
		fields.add(request.getParameter("bibliography"));
		fields.add(request.getParameter("writingStyle"));
		fields.add(request.getParameter("url"));

		inscriptions.add(fields);

		this.graffitiDao.insertInscription(inscriptions);

		return "insertDataComplete";
	}

	// Used for mapping the URI to show inscriptions. It has a similar structure
	// to the mapping done to /result below
	@RequestMapping(value = "/region/{city}", method = RequestMethod.GET)
	public String cityPage(@PathVariable String city, HttpServletRequest request) {
		final Set<Inscription> resultSet = new TreeSet<Inscription>();
		String searches = city;
		final String[] searchParams = searches.split(" ");
		System.out.println(searches);
		final List<Inscription> inscriptions = this.graffitiDao
				.getInscriptionsByCity(searches);
		resultSet.addAll(inscriptions);
		final List<Inscription> resultsList = new ArrayList<Inscription>();
		resultsList.addAll(resultSet);
		request.setAttribute("resultsLyst", resultsList);
		return "displayData";
	}

	@RequestMapping(value = "/region/{city}/{insula}", method = RequestMethod.GET)
	public String insulaPage(@PathVariable String city,
			@PathVariable String insula, HttpServletRequest request) {
		System.out.println("insulaPage: " + insula);
		final Set<Inscription> resultSet = new TreeSet<Inscription>();
		final List<Inscription> inscriptions2 = this.graffitiDao
				.getInscriptionsByCityAndInsula(city, insula);
		resultSet.addAll(inscriptions2);
		final List<Inscription> resultsList = new ArrayList<Inscription>();
		resultsList.addAll(resultSet);
		request.setAttribute("resultsLyst", resultsList);
		return "displayData";
	}

	@RequestMapping(value = "/region/{city}/{insula}/{property}", method = RequestMethod.GET)
	public String propertyPage(@PathVariable String city,
			@PathVariable String property, @PathVariable String insula,
			HttpServletRequest request) {
		System.out.println("propertyPage: " + property);
		final List<Inscription> inscriptions = this.graffitiDao
				.getInscriptionsByCityAndInsulaAndPropertyNumber(city, insula,
						property);
		request.setAttribute("resultsLyst", inscriptions);
		return "displayData";
	}

	@RequestMapping(value = "/region/{city}/{insula}/{property}/{id}", method = RequestMethod.GET)
	public String dataPage(@PathVariable String city,
			@PathVariable String property, @PathVariable String insula,
			@PathVariable int id, HttpServletRequest request) {
		final List<Inscription> allInscriptions = this.graffitiDao
				.getInscriptionsByCityAndInsulaAndPropertyNumber(city, insula,
						property);
		final List<Inscription> resultsList2 = new ArrayList<Inscription>();

		if (id < allInscriptions.size()) {
			Inscription whichInsc = allInscriptions.get(id);
			resultsList2.add(whichInsc);
		}
		request.setAttribute("resultsLyst", resultsList2);

		return "displayData";
	}

	// The default page is sent to index.jsp
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String indexPage() {
		return "index";
	}

	/*
	 * The main backend
	 */
	@RequestMapping(value = "/results", method = RequestMethod.GET)
	public String search(final HttpServletRequest request) {
		String searches = null;
		String searchQueryDesc = "";
		final Set<Inscription> resultSet = new TreeSet<Inscription>();
		final String loc = request.getParameter("loc");
		if (loc != null && !loc.isEmpty()) {
			searches = request.getParameter("loc");
			if( request.getParameter("location") == null ) {
				final List<Inscription> inscriptions = this.graffitiDao
						.getInscriptionsByCityAndInsula("Pompeii", searches);
				resultSet.addAll(inscriptions);
			}
			else if (request.getParameter("location") != null
					&& !request.getParameter("location").isEmpty()) {
				final String[] locations = request
						.getParameterValues("location");
				Arrays.sort(locations);
				searches = "";
				for (String location : locations) {
					searches += location + " ";
				}
				searchQueryDesc = "in locations " + searches;
				
				final String[] searchParams = searches.split(" ");

				for (final String searchParam : searchParams) {
					// TODO: GENERALIZE -- won't work for Herculaneum
					// Modify to use the city, insula, and property
					String[] pieces = searchParam.split("\\.");
					String insula = pieces[0] + "." + pieces[1];
					String property = pieces[2];
					final List<Inscription> inscriptions = this.graffitiDao
							.getInscriptionsByFindSpot(insula, property);
					resultSet.addAll(inscriptions);
				}
			}

		} 
		else {
		
			if (request.getParameter("property") != null && ! request.getParameter("property").isEmpty()) {
				searches = request.getParameter("property");
				final String[] searchParams = searches.split(" ");
				int propertyType = 1;
				for (final String searchParam : searchParams) {
					System.out.println(searchParam);
					propertyType = Integer.parseInt(searchParam);
					final List<Inscription> inscriptions = this.graffitiDao
							.getInscriptionsByPropertyType(propertyType);

					resultSet.addAll(inscriptions);
				}
				// look up by type info
				String propName = propertyTypesDao
						.getPropertyName(propertyType);
				searchQueryDesc = "in property type " + propName;
			}

			if (request.getParameter("drawing") != null && !request.getParameter("drawing").isEmpty()) {
				searches = request.getParameter("drawing");
				final List<Inscription> inscriptions = this.graffitiDao
						.getInscriptionByDrawing(searches);
				resultSet.addAll(inscriptions);
				searchQueryDesc = "for drawings";
			}

			if (request.getParameter("query") != null && !request.getParameter("query").isEmpty()) {
				searches = request.getParameter("query");
				final String[] searchParams = searches.split(" ");
				for (final String searchParam : searchParams) {
					// System.out.println(searchParam);
					final List<Inscription> inscriptions = this.graffitiDao
							.getInscriptionsByContent(searchParam);
					resultSet.addAll(inscriptions);
				}
				searchQueryDesc = "for content containing \"" + searches + "\"";
			}

			if (request.getParameter("keyword") != null && !request.getParameter("keyword").isEmpty()) {
				searches = request.getParameter("keyword");
				final String[] searchParams = searches.split(" ");
				for (final String searchParam : searchParams) {
					// System.out.println(searchParam);
					final List<Inscription> inscriptions = this.graffitiDao
							.getInscriptions(searchParam);
					resultSet.addAll(inscriptions);
				}
				searchQueryDesc = "for keyword containing \"" + searches + "\"";
			}

			if (searches == null || searches.isEmpty()) {
				final ArrayList<String> regions = getRegionsFromFile();
				request.setAttribute("regions", regions);

				request.setAttribute("drawingTags",
						drawingTagsDao.getDrawingTags());
				request.setAttribute("propertyTypes",
						propertyTypesDao.getPropertyTypes());
				return "search";
			}
		} 
		final List<Inscription> resultsList = new ArrayList<Inscription>();
		resultsList.addAll(resultSet);
		if (resultsList.size() > 0) {
			request.setAttribute("mapName", resultsList.get(0).getAncientCity());
		}
		request.setAttribute("resultsLyst", resultsList);
		request.setAttribute("findLocationKeys", findLocationKeys(resultsList));
		request.setAttribute("searchQueryDesc", searchQueryDesc);
		return "results";
	}

	private static List<String> findLocationKeys(
			final List<Inscription> inscriptions) {
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
