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
 *
 * @author Hammad Ahmad
 */

public class StoreInsulaeFromDatabaseForgeoJsonMap {

	private static final String POMPEII_INSULA_DATA_TXT_FILE = "src/main/resources/map_starter_text/pompeiiInsulaData.txt";
	private static final String POMPEII_INSULA_JAVASCRIPT_DATA_FILE_LOC = "src/main/webapp/resources/js/pompeiiInsulaData.js";
	private static final String POMPEII_INIT_JAVASCRIPT_LOC = "src/main/resources/map_starter_text/pompeiiInsulaDataFirst.txt";
	private static final String POMPEII_GEOJSON_FILE_LOC = "src/main/resources/geoJSON/pompeii_cityblocks.json";
	
	final static String SELECT_PROPERTIES_ON_INSULA  = "SELECT count(*) FROM agp_inscription_info"
			+ " WHERE property_id IN (" + "SELECT properties.id FROM properties "
			+ "LEFT JOIN insula ON properties.insula_id=insula.id "
			+ "LEFT JOIN cities ON insula.modern_city=cities.name "
			+ "WHERE UPPER(modern_city) = UPPER(?) and insula.short_name = ? " + ")";
	
	final static String GET_INSULA_ID = "SELECT id, full_name from insula where short_name = ?";

	static Connection dbCon;

	private static PreparedStatement selectPropertiesStatement;
	private static PreparedStatement getIdStatement;

	@Resource
	private static GraffitiDao graffitiDaoObject;

	public static void main(String args[]) throws SQLException {
		storeInsulae();
	}

	public static void storeInsulae() throws SQLException {
		init();

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
			selectPropertiesStatement = dbCon.prepareStatement(SELECT_PROPERTIES_ON_INSULA);
			getIdStatement = dbCon.prepareStatement(GET_INSULA_ID);
		} catch (SQLException e) {
			e.printStackTrace();
		}

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
			PrintWriter pompeiiTextWriter = new PrintWriter(POMPEII_INSULA_DATA_TXT_FILE, "UTF-8");

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

				JsonNode nameNode = featureNode.findValue("NAME");
				if (nameNode == null) {
					continue;
				}

				String name = nameNode.textValue();
				if (name == null || !name.contains(".")) {
					continue;
				}

				// Parse the geometry and get rid of the z coordinates
				Polygon p = parseGeometryAndRemoveCoordinates(featureNode);

				try {
					// Get the insula names from the database and store.
					selectPropertiesStatement.setString(1, "Pompeii");
					selectPropertiesStatement.setString(2, name);
					
					getIdStatement.setString(1, name);

					ResultSet propertyResultSet = selectPropertiesStatement.executeQuery();
					ResultSet idResultSet = getIdStatement.executeQuery();
					
					int id=-1;
					String fullName = "";
					
					if(idResultSet.next()) {
						id = idResultSet.getInt("id");
						fullName = idResultSet.getString("full_name");
					}

					if (propertyResultSet.next()) {

						int numberOfGraffiti = propertyResultSet.getInt(1);

						ObjectNode insulaNode = (ObjectNode) featureNode;
						ObjectNode insulae = (ObjectNode) insulaNode.path("properties");
						
						if(id == -1) {
							continue;
						}
						
						insulae.put("insula_id", id);
						insulae.put("insula_short_name", name);
						insulae.put("insula_full_name", fullName);
						insulae.put("number_of_graffiti", numberOfGraffiti);

						JsonNode updatedInsulae = insulae;
						insulaNode.set("insulae", updatedInsulae);

						String jsonPoly = new ObjectMapper().writeValueAsString(p);
						JsonNode updatedGeometry = new ObjectMapper().readTree(jsonPoly);

						insulaNode.replace("geometry", updatedGeometry);

						pompeiiTextWriter.println(insulaNode + ",");
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
	 * An independent method for copying from pompeiiPropertyData.txt to
	 * pompeiiPropertyData.js with necessary js-specific components. Copies the
	 * data from pompeiiPropertyData.txt to updateEschebach.js in between the [
	 * ] First, creates and writes to a textFile. Then, saves it as a .js file
	 * by renaming it.
	 * 
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	private static void copyToJavascriptFiles() {
		writeJavaScriptPropertyFile(POMPEII_INSULA_JAVASCRIPT_DATA_FILE_LOC, POMPEII_INIT_JAVASCRIPT_LOC,
				POMPEII_INSULA_DATA_TXT_FILE);
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
