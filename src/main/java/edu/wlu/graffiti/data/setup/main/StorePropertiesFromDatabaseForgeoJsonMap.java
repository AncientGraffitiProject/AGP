package edu.wlu.graffiti.data.setup.main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import javax.annotation.Resource;

import org.geojson.GeoJsonObject;
import org.geojson.LngLatAlt;
import org.geojson.Polygon;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.wlu.graffiti.dao.FindspotDao;
import edu.wlu.graffiti.dao.GraffitiDao;
import edu.wlu.graffiti.data.setup.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This program translates each property and its features from the SQL database
 * and json shape files to pompeiiPropertyData.txt,pompeiiPropertyData.js,
 * herculaneumPropertyData.txt, and herculaneumPropertyData.js. Then, the data
 * can be used to efficiently provide data to geoJson for use in the maps of
 * Pompeii and Herculaneum.
 * 
 * @author Alicia Martinez - v1.0
 * @author Kelly McCaffrey -Created functionality for getting the number of
 *         Graffiti and automating the process of copying to
 *         pompeiiPropertyData.js -Also added all functionality for the
 *         Herculaneum map and insula information such as insula name and insula
 *         id to the .txt and .js files.
 * @author Sara Sprenkle - refactored code to make it easier to change later;
 */

public class StorePropertiesFromDatabaseForgeoJsonMap {

	private static final String HERCULANEUM_JAVASCRIPT_DATA_FILE = "src/main/webapp/resources/js/herculaneumPropertyData.js";
	private static final String OSM_ID_KEY = "osm_id";
	private static final String OSM_WAY_ID_KEY = "osm_way_id";
	private static final String HERCULANEUM_JSON_FILE = "src/main/resources/geoJSON/herculaneum.json";
	private static final String HERCULANEUM_PROPERTY_TEXT_FILE = "src/main/resources/map_starter_text/herculaneumPropertyData.txt";
	private static final String POMPEII_PROPERTY_DATA_TXT_FILE = "src/main/resources/map_starter_text/pompeiiPropertyData.txt";
	private static final String POMPEII_JAVASCRIPT_DATA_FILE_LOC = "src/main/webapp/resources/js/pompeiiPropertyData.js";
	private static final String POMPEII_INIT_JAVASCRIPT_LOC = "src/main/resources/map_starter_text/pompeiiPropertyDataFirst.txt";
	private static final String HERCULANEUM_INIT_JAVASCRIPT_LOC = "src/main/resources/map_starter_text/HerculaneumDataFirst.txt";
	private static final String POMPEII_GEOJSON_FILE_LOC = "src/main/resources/geoJSON/pompeii_buildings_and_corpustopo.geojson";

	final static String SELECT_PROPERTY = FindspotDao.SELECT_BY_CITY_AND_INSULA_AND_PROPERTY_STATEMENT;

	final static String SELECT_BY_OSM_WAY_ID = FindspotDao.SELECT_BY_OSM_WAY_ID_STATEMENT;

	final static String SELECT_BY_OSM_ID = FindspotDao.SELECT_BY_OSM_ID_STATEMENT;

	final static String SELECT_BY_CITY_AND_INSULA = FindspotDao.SELECT_BY_PROPERTY_ID_STATEMENT;

	final static String GET_NUMBER = GraffitiDao.FIND_BY_PROPERTY;

	final static String GET_PROPERTY_TYPE = "SELECT * FROM properties, propertytypes,"
			+ " propertytopropertytype WHERE properties.id = propertytopropertytype.property_id"
			+ " AND propertytypes.id = propertytopropertytype.property_type AND properties.id = ?";

	static Connection dbCon;

	private static PreparedStatement selectPropertyStatement;
	private static PreparedStatement getPropertyTypeStatement;
	private static PreparedStatement getNumberStatement;
	private static PreparedStatement osmWayIdSelectionStatement;
	private static PreparedStatement osmIdSelectionStatement;
	private static PreparedStatement selectCityAndInsulaStatement;

	@Resource
	private static GraffitiDao graffitiDaoObject;

	public static void main(String args[]) throws SQLException {
		storeProperties();
	}

	public static void storeProperties() throws SQLException {
		init();

		storeHerculaneum();

		storePompeii();

		copyToJavascriptFiles();
	}

	private static void init() {

		// Sets database url using the configuration file.
		Properties prop = Utils.getConfigurationProperties();
		try {
			Class.forName(prop.getProperty("db.driverClassName"));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			dbCon = DriverManager.getConnection(prop.getProperty("db.url"), prop.getProperty("db.user"),
					prop.getProperty("db.password"));
			selectCityAndInsulaStatement = dbCon.prepareStatement(SELECT_BY_CITY_AND_INSULA);
			osmWayIdSelectionStatement = dbCon.prepareStatement(SELECT_BY_OSM_WAY_ID);
			osmIdSelectionStatement = dbCon.prepareStatement(SELECT_BY_OSM_ID);
			selectPropertyStatement = dbCon.prepareStatement(SELECT_PROPERTY);
			getPropertyTypeStatement = dbCon.prepareStatement(GET_PROPERTY_TYPE);
			getNumberStatement = dbCon.prepareStatement(GET_NUMBER);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Stores the data for Herculaneum in herculaneumPropertyData.txt
	 * @throws SQLException 
	 */
	private static void storeHerculaneum() throws SQLException {
		try {
			// creates the file we will later write the updated graffito to.

			PrintWriter herculaneumTextWriter = new PrintWriter(HERCULANEUM_PROPERTY_TEXT_FILE, "UTF-8");

			ObjectMapper herculaneumMapper = new ObjectMapper();
			JsonFactory herculaneumJsonFactory = new JsonFactory();
			JsonParser herculaneumJsonParser = herculaneumJsonFactory.createParser(new File(HERCULANEUM_JSON_FILE));

			JsonNode herculaneumRoot = herculaneumMapper.readTree(herculaneumJsonParser);
			JsonNode herculaneumFeaturesNode = herculaneumRoot.path("features");

			Iterator<JsonNode> herculaneumIterator = herculaneumFeaturesNode.elements();

			while (herculaneumIterator.hasNext()) {
				
				JsonNode hercProperty = herculaneumIterator.next();

				JsonNode osmWayNode = hercProperty.findValue(OSM_WAY_ID_KEY);
				JsonNode osmNode = hercProperty.findValue(OSM_ID_KEY);

				if (osmWayNode != null && osmWayNode.textValue() != null) {
					String osm_way_id = osmWayNode.textValue();
					try {
						osmWayIdSelectionStatement.setString(1, osm_way_id);

						ResultSet propertyRS = osmWayIdSelectionStatement.executeQuery();

						if (propertyRS.next()) {
							writeHerculaneumPropertyInfoToFile(herculaneumTextWriter, hercProperty, propertyRS);
						} else {
							System.out.println("Property with osm way id " + osm_way_id + " not in database.");
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				} else if (osmNode != null) {
					String osm_id = osmNode.textValue();
					try {
						osmIdSelectionStatement.setString(1, osm_id);

						ResultSet propertyRS = osmIdSelectionStatement.executeQuery();

						if (propertyRS.next()) {
							writeHerculaneumPropertyInfoToFile(herculaneumTextWriter, hercProperty, propertyRS);
						} else {
							System.out.println("Property with osm id " + osm_id + " not in database.");
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
			herculaneumTextWriter.close();
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeHerculaneumPropertyInfoToFile(PrintWriter herculaneumTextWriter, JsonNode hercProperty, ResultSet rs)
			throws SQLException {
		int propertyId = rs.getInt("id");
		
		String propertyNumber = rs.getString("property_number");
		String propertyName = rs.getString("property_name");
		String addProperties = rs.getString("additional_properties");
		String italPropName = rs.getString("italian_property_name");
		String insulaId = rs.getString("insula_id");
		String propertyAddress = "";
		
		ObjectNode graffito = (ObjectNode) hercProperty;
		ObjectNode properties = (ObjectNode) graffito.path("properties");
		
		selectCityAndInsulaStatement.setInt(1, propertyId);

		ResultSet insulaResultSet = selectCityAndInsulaStatement.executeQuery();

		if (insulaResultSet.next()) {
			String shortInsulaName = insulaResultSet.getString("short_name");
			String fullInsulaName = insulaResultSet.getString("full_name");

			properties.put("short_insula_name", shortInsulaName);
			properties.put("full_insula_name", fullInsulaName);
			propertyAddress += shortInsulaName + "." + propertyNumber;
		}
		
		// String insulaDescription =
		// rs.getString("description");
		// String insulaPleiadesId =
		// rs.getString("insula_pleiades_id");
		// String propPleiadesId =
		// rs.getString("property_pleiades_id");

		getNumberStatement.setInt(1, propertyId);
		ResultSet numberOnPropResultSet = getNumberStatement.executeQuery();
		int numberOfGraffitiOnProperty = 0;
		if (numberOnPropResultSet.next()) {
			numberOfGraffitiOnProperty = numberOnPropResultSet.getInt(1);
		}

		getPropertyTypeStatement.setInt(1, propertyId);
		ResultSet resultset = getPropertyTypeStatement.executeQuery();
		String propertyType = "";
		if (resultset.next()) {
			propertyType = resultset.getString("name");
		}

		
		properties.put("Property_Id", propertyId);
		properties.put("Number_Of_Graffiti", numberOfGraffitiOnProperty);
		properties.put("Property_Name", propertyName);
		properties.put("Additional_Properties", addProperties);
		properties.put("Italian_Property_Name", italPropName);
		properties.put("insula_id", insulaId);
		properties.put("Property_Address", propertyAddress);
		
		// These are not in the database.
		// properties.put("Insula_Description",
		// insulaDescription);
		// properties.put("Insula_Pleiades_Id",
		// insulaPleiadesId);
		// properties.put("Property_Pleiades_Id",
		// propPleiadesId);

		properties.put("Property_Type", propertyType);
		JsonNode updatedProps = properties;
		graffito.set("properties", updatedProps);
		// write the newly updated graffito to text file
		herculaneumTextWriter.println(graffito + ",");
	}

	/**
	 * Parses the data file Stores the data for Pompeii in
	 * herculaneumPropertyData.txt
	 * 
	 * @throws IOException
	 * @throws JsonProcessingException
	 */
	private static void storePompeii() {

		try {
			PrintWriter pompeiiTextWriter = new PrintWriter(POMPEII_PROPERTY_DATA_TXT_FILE, "UTF-8");

			// creates necessary objects to parse the original GeoJSON file
			ObjectMapper pompeiiMapper = new ObjectMapper();
			JsonFactory pompeiiJsonFactory = new JsonFactory();
			JsonParser pompeiiJsonParser = pompeiiJsonFactory.createParser(new File(POMPEII_GEOJSON_FILE_LOC));
			// this accesses the 'features' level of the GeoJSON document
			JsonNode pompeiiRoot = pompeiiMapper.readTree(pompeiiJsonParser);
			JsonNode pompeiiFeaturesNode = pompeiiRoot.path("features");

			// iterates over the features node
			Iterator<JsonNode> featureIterator = pompeiiFeaturesNode.elements();

			while (featureIterator.hasNext()) {
				JsonNode featureNode = featureIterator.next();

				JsonNode primaryDONode = featureNode.findValue("PRIMARY_DOOR");
				if (primaryDONode == null) {
					continue;
				}

				String primaryDO = primaryDONode.textValue();
				if (primaryDO == null || !primaryDO.contains(".")) {
					continue;
				}

				String[] parts = primaryDO.split("\\.");

				String pt1 = parts[0];
				String pt2 = parts[1];
				String pt3;
				if(parts.length == 3) {
					pt3 = parts[2];
				} else {
					pt3 = "";
				}
				
				String insulaName = pt1 + "." + pt2;
				String propertyNum = pt3;

				// Parse the geometry and get rid of the z coordinates
				Polygon p = parseGeometryAndRemoveCoordinates(featureNode);

				try {
					// Get the insula names from the database and store.
					selectPropertyStatement.setString(1, "Pompeii");
					selectPropertyStatement.setString(2, insulaName);
					selectPropertyStatement.setString(3, propertyNum);

					ResultSet propertyResultSet = selectPropertyStatement.executeQuery();

					if (propertyResultSet.next()) {

						int propertyId = propertyResultSet.getInt("id");
						selectCityAndInsulaStatement.setInt(1, propertyId);

						ResultSet insulaResultSet = selectCityAndInsulaStatement.executeQuery();

						ObjectNode propertyNode = (ObjectNode) featureNode;
						ObjectNode properties = (ObjectNode) propertyNode.path("properties");

						if (insulaResultSet.next()) {
							String shortInsulaName = insulaResultSet.getString("short_name");
							String fullInsulaName = insulaResultSet.getString("full_name");

							properties.put("short_insula_name", shortInsulaName);
							properties.put("full_insula_name", fullInsulaName);
						}

						String propertyName = propertyResultSet.getString("property_name");
						String addProperties = propertyResultSet.getString("additional_properties");
						String italPropName = propertyResultSet.getString("italian_property_name");
						String insulaDescription = propertyResultSet.getString("description");
						String insulaPleiadesId = propertyResultSet.getString("insula_pleiades_id");
						String propPleiadesId = propertyResultSet.getString("property_pleiades_id");
						String insulaId = propertyResultSet.getString("insula_id");

						getNumberStatement.setInt(1, propertyId);
						ResultSet numberOnPropResultSet = getNumberStatement.executeQuery();
						int numberOfGraffitiOnProperty = 0;
						if (numberOnPropResultSet.next()) {
							numberOfGraffitiOnProperty = numberOnPropResultSet.getInt(1);
						}

						getPropertyTypeStatement.setInt(1, propertyId);
						ResultSet resultset = getPropertyTypeStatement.executeQuery();
						String propertyType = "";
						if (resultset.next()) {
							propertyType = resultset.getString("name");
						}

						properties.put("Property_Id", propertyId);
						properties.put("Number_Of_Graffiti", numberOfGraffitiOnProperty);
						properties.put("Property_Name", propertyName);
						properties.put("Additional_Properties", addProperties);
						properties.put("Italian_Property_Name", italPropName);
						properties.put("Insula_Description", insulaDescription);
						properties.put("Insula_Pleiades_Id", insulaPleiadesId);
						properties.put("Property_Pleiades_Id", propPleiadesId);
						properties.put("Property_Type", propertyType);
						properties.put("insula_id", insulaId);

						JsonNode updatedProps = properties;
						propertyNode.set("properties", updatedProps);

						String jsonPoly = new ObjectMapper().writeValueAsString(p);
						JsonNode updatedGeometry = new ObjectMapper().readTree(jsonPoly);

						propertyNode.replace("geometry", updatedGeometry);

						pompeiiTextWriter.println(propertyNode + ",");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			pompeiiTextWriter.close();
		} catch (JsonParseException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Simplifies a geoJSON polygon object by removing the Z-coordinates and
	 * keeping only the x and y coordinates
	 * 
	 * @param featureNode
	 * @return Polygon with Z-coordinate removed
	 */
	private static Polygon parseGeometryAndRemoveCoordinates(JsonNode featureNode) throws JsonProcessingException {
		JsonNode geometryNode = featureNode.findValue("geometry");
		JsonParser coordParse = geometryNode.traverse();
		Polygon p = null;

		GeoJsonObject object;
		try {
			object = new ObjectMapper().readValue(coordParse, GeoJsonObject.class);

			if (object instanceof Polygon) {
				p = (Polygon) object;

				// go through the coordinates, removing the z-coordinate
				List<List<LngLatAlt>> newCoordList = new ArrayList<List<LngLatAlt>>();
				for (List<LngLatAlt> coordList : p.getCoordinates()) {
					List<LngLatAlt> aList = new ArrayList<LngLatAlt>();
					for (LngLatAlt coord : coordList) {
						LngLatAlt newCoord = new LngLatAlt(coord.getLongitude(), coord.getLatitude());
						aList.add(newCoord);
					}
					newCoordList.add(aList);
				}
				p.setCoordinates(newCoordList);
			}
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return p;
	}

	/**
	 * An independent function for copying from pompeiiPropertyData.txt to
	 * pompeiiPropertyData.js with necessary js-specific components. Copies the
	 * data from pompeiiPropertyData.txt to updateEschebach.js in between the [
	 * ] First, creates and writes to a textFile. Then, saves it as a .js file
	 * by renaming it.
	 * 
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	private static void copyToJavascriptFiles() {
		writeJavaScriptPropertyFile(POMPEII_JAVASCRIPT_DATA_FILE_LOC, POMPEII_INIT_JAVASCRIPT_LOC,
				POMPEII_PROPERTY_DATA_TXT_FILE);
		writeJavaScriptPropertyFile(HERCULANEUM_JAVASCRIPT_DATA_FILE, HERCULANEUM_INIT_JAVASCRIPT_LOC,
				HERCULANEUM_PROPERTY_TEXT_FILE);
	}

	public static void writeJavaScriptPropertyFile(String finalJavaScriptFileLoc, String initJavaScriptFileLoc,
			String dataFile) {
		try {
			PrintWriter jsWriter = new PrintWriter(finalJavaScriptFileLoc, "UTF-8");
			// Writes the beginning part of the js file, which it fetches from
			// another text file
			Scanner initJSReader = new Scanner(new File(initJavaScriptFileLoc));
			while (initJSReader.hasNext()) {
				String content = initJSReader.nextLine();
				jsWriter.println(content);
			}
			// Copies from pompeiiPropertyData.txt to pompeiiPropertyData.js for
			// the
			// body portion of the file.
			Scanner dataReader = new Scanner(new File(dataFile));
			String content;
			while (dataReader.hasNext()) {
				content = dataReader.nextLine();
				jsWriter.println(content);
			}
			jsWriter.println("]};");
			initJSReader.close();
			dataReader.close();
			jsWriter.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}

}
