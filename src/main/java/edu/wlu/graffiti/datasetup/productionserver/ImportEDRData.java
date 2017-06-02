package edu.wlu.graffiti.datasetup.productionserver;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import edu.wlu.graffiti.bean.Insula;
import edu.wlu.graffiti.bean.Property;

/**
 * Import the data from EDR
 * 
 * (Next: handle updates)
 * 
 * @author Sara Sprenkle
 *
 */
public class ImportEDRData {

	private static final String DB_USER_NAME = "sprenkle";
	private static final String DB_PASSWORD = "";


	final static String DB_URL = "jdbc:postgresql://hopper.cs.wlu.edu/graffiti_agp";

	private static final String INSERT_INSCRIPTION_STATEMENT = "INSERT INTO eagle_inscriptions "
			+ "(eagle_id, ancient_city, find_spot, measurements, writing_style, \"language\") "
			+ "VALUES (?,?,?,?,?,?)";

	private static final String UPDATE_INSCRIPTION_STATEMENT = "UPDATE eagle_inscriptions SET "
			+ "ancient_city=?, find_spot=?, measurements=?, writing_style=?, \"language\"=? "
			+ "WHERE eagle_id = ?";

	private static final String CHECK_INSCRIPTION_STATEMENT = "SELECT COUNT(*) FROM eagle_inscriptions"
			+ " WHERE eagle_id = ?";

	private static final String INSERT_AGP_METADATA = "INSERT INTO agp_inscription_annotations (eagle_id) "
			+ "VALUES (?)";

	private static final String UPDATE_CONTENT = "UPDATE eagle_inscriptions SET "
			+ "content = ? WHERE eagle_id = ?";

	private static final String UPDATE_BIB = "UPDATE eagle_inscriptions SET "
			+ "bibliography = ? WHERE eagle_id = ?";

	private static final String UPDATE_DESCRIPTION = "UPDATE agp_inscription_annotations SET "
			+ "description = ? WHERE eagle_id = ?";

	static Connection dbCon;

	private static PreparedStatement insertAGPMetaStmt;
	private static PreparedStatement insertPStmt;

	private static PreparedStatement updatePStmt;
	private static PreparedStatement selPStmt;
	private static PreparedStatement updatePropertyStmt;
	private static PreparedStatement updateContentStmt;
	private static PreparedStatement updateDescriptionStmt;
	private static PreparedStatement updateBibStmt;
	private static PreparedStatement updateApparatusStmt;

	private static Map<String, HashMap<String, Insula>> cityToInsulaMap;

	private static Map<Integer, HashMap<String, Property>> insulaToPropertyMap;

	private static List<Pattern> patternList;

	public static void main(String[] args) {
		init();

		try {
			//updateInscriptions("data/EDRData/epigr.csv");
			
			updateInscriptions("data/EDRData/lupanare_epigr.csv");
		//	updateContent("data/EDRData/testo_epigr.csv");
			//updateBibliography("data/EDRData/editiones.csv");
			//updateApparatus("data/EDRData/apparatus.csv");

			dbCon.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}


	private static void updateApparatus(String apparatusFileName) {
		try {
			Reader in = new FileReader(apparatusFileName);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			for (CSVRecord record : records) {
				String eagleID = cleanData(record.get(0));
				String apparatus = cleanData(record.get(1));

				try {

					updateApparatusStmt.setString(1, apparatus);
					updateApparatusStmt.setString(2, eagleID);

					int updated = updateApparatusStmt.executeUpdate();
					if (updated != 1) {
						System.out.println("Something went wrong with apparatus for "
								+ eagleID);
						System.out.println(apparatus);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void updateBibliography(String bibFileName) {
		try {
			Reader in = new FileReader(bibFileName);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			for (CSVRecord record : records) {
				String eagleID = cleanData(record.get(0));
				String bib = cleanData(record.get(1));

				try {
					updateBibStmt.setString(1, bib);
					updateBibStmt.setString(2, eagleID);

					int updated = updateBibStmt.executeUpdate();
					if (updated != 1) {
						System.out.println("Something went wrong with bib for "
								+ eagleID);
						System.out.println(bib);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void updateContent(String contentFileName) {

		try {
			Reader in = new FileReader(contentFileName);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			for (CSVRecord record : records) {
				String eagleID = cleanData(record.get(0));
				String content = cleanData(record.get(1));

				// this needs to be generalized

				if (content.startsWith("((:")) {
					String desc = content.substring(3, content.length() - 2);
					try {
						updateDescriptionStmt.setString(1, desc);
						updateDescriptionStmt.setString(2, eagleID);
						int updated = updateDescriptionStmt.executeUpdate();
						if (updated != 1) {
							System.out.println("Something went wrong with description for "
									+ eagleID);
							System.out.println(desc);
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}

				} else {
					try {
						content = cleanContent(content);

						updateContentStmt.setString(1, content);
						updateContentStmt.setString(2, eagleID);

						int updated = updateContentStmt.executeUpdate();
						if (updated != 1) {
							System.out.println("Something went wrong with content for "
									+ eagleID);
							System.out.println(content);
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}

				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Replaces the <br>
	 * tags with newlines. We handle the line breaks in our code.
	 * 
	 * @param content
	 * @return
	 */
	private static String cleanContent(String content) {
		return content.replace("<br>", "\n");
	}

	private static void updateInscriptions(String datafileName) {
		try {

			Reader in = new FileReader(datafileName);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			for (CSVRecord record : records) {
				String eagleID = cleanData(record.get(0));
				String ancient_city = cleanData(record.get(3));
				String findSpot = cleanData(record.get(5));
				String alt = cleanData(record.get(17));
				String lat = cleanData(record.get(18));
				String crass = cleanData(record.get(19));
				String littAlt = cleanData(record.get(20));

				String measurements = createMeasurementField(alt, lat, crass,
						littAlt);

				String writingStyle = cleanData(record.get(9));
				String language = cleanData(record.get(10));

				System.out.println(eagleID);

				selPStmt.setString(1, eagleID);

				ResultSet rs = selPStmt.executeQuery();

				int count = 0;

				if (rs.next()) {
					count = rs.getInt(1);
				} else {
					System.out
							.println("Something went wrong with the SELECT statement!");
				}

				if (count == 0) {
					insertEagleInscription(eagleID, ancient_city, findSpot,
							measurements, writingStyle, language);
				} else {
					updateEagleInscription(eagleID, ancient_city, findSpot,
							measurements, writingStyle, language);
				}

				// update AGP Metadata
				updateAGPMetadata(eagleID, ancient_city, findSpot);

			}

			in.close();
			insertPStmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Update the AGP Metadata for this inscription
	 * 
	 * @param eagleID
	 * @param ancient_city
	 * @param findSpot
	 * @throws SQLException
	 */
	private static void updateAGPMetadata(String eagleID, String ancient_city,
			String findSpot) throws SQLException {

		insertAGPMetaStmt.setString(1, eagleID);

		try {
			insertAGPMetaStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		String address = convertFindSpotToAddress(findSpot);
		System.out.println("*" + address);

		// TODO
		// we're going to skip these because I can't handle them yet.

		if (!address.contains(".")) {
			return;
		}

		String insula = DB_PASSWORD;
		String propertyNum = DB_PASSWORD;

		if (ancient_city.startsWith("Pompei")) {
			ancient_city = "Pompeii";
		}

		if (ancient_city.equals("Pompeii")) {
			insula = address.substring(0, address.lastIndexOf('.'));
			propertyNum = address.substring(address.lastIndexOf('.') + 1);
		} else {
			insula = address.substring(0, address.indexOf('.'));
			propertyNum = address.substring(address.lastIndexOf('.') + 1);
		}

		System.out.println("city: " + ancient_city);
		System.out.println("findspot: " + findSpot);
		System.out.println("insula: " + insula);
		System.out.println("property: " + propertyNum);

		// Look up ids from the HashMaps
		// handle alternative spellings
/*
		if (!cityToInsulaMap.get(ancient_city).containsKey(insula)) {
			System.out.println("Insula " + insula + " not found");
			return;
		}

		int insulaID = cityToInsulaMap.get(ancient_city).get(insula).getId();

		if (!insulaToPropertyMap.get(insulaID).containsKey(propertyNum)) {
			System.out.println("Property " + propertyNum + " in Insula "
					+ insula + " not found");
			return;
		}

		int propertyID = insulaToPropertyMap.get(insulaID).get(propertyNum)
				.getId();

		// update property info

		updatePropertyStmt.setInt(1, propertyID);
		updatePropertyStmt.setString(2, eagleID);

		int response = updatePropertyStmt.executeUpdate();

		if (response != 1) {
			System.out.println("WHAT?" + eagleID);
		}
		*/

	}

	/**
	 * Parses the findspot for the address
	 * 
	 * @param findSpot
	 * @return
	 */
	private static String convertFindSpotToAddress(String findSpot) {
		// Example: Pompei (Napoli) VII.12.18-20, Lupanare, cella b
		// Example: Ercolano (Napoli), Insula III.11, Casa del Tramezzo di Legno

		Matcher matcher = patternList.get(0).matcher(findSpot);
		if (matcher.matches()) {
			return matcher.group(2);
		} else {
			return findSpot;
		}
	}

	private static void insertEagleInscription(String eagleID,
			String ancient_city, String findSpot, String measurements,
			String writingStyle, String language) throws SQLException {
		insertPStmt.setString(1, eagleID);
		insertPStmt.setString(2, ancient_city);
		insertPStmt.setString(3, findSpot);
		insertPStmt.setString(4, measurements);
		insertPStmt.setString(5, writingStyle);
		insertPStmt.setString(6, language);

		try {
			insertPStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void updateEagleInscription(String eagleID,
			String ancient_city, String findSpot, String measurements,
			String writingStyle, String language) throws SQLException {
		updatePStmt.setString(1, ancient_city);
		updatePStmt.setString(2, findSpot);
		updatePStmt.setString(3, measurements);
		updatePStmt.setString(4, writingStyle);
		updatePStmt.setString(5, language);
		updatePStmt.setString(6, eagleID);

		try {
			updatePStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static String createMeasurementField(String alt, String lat,
			String crass, String littAlt) {
		// Example: alt.: 2.50 lat.: 7.50 Crass./Diam.: 0.00 litt. alt.: 2-2,5

		StringBuffer sb = new StringBuffer();
		sb.append("alt.: ");
		sb.append(alt);
		sb.append(" lat.: ");
		sb.append(lat);
		sb.append(" Crass./Diam.: ");
		sb.append(crass);
		sb.append(" litt. alt.: ");
		sb.append(littAlt);

		return sb.toString();
	}

	/**
	 * Cleans the data coming from the CSV file, removing the quotes and the
	 * extra spaces.
	 * 
	 * @param string
	 * @return
	 */
	private static String cleanData(String string) {
		return string.replace("\"", DB_PASSWORD).trim();
	}

	private static void init() {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			dbCon = DriverManager.getConnection(DB_URL, DB_USER_NAME, DB_PASSWORD);

			insertAGPMetaStmt = dbCon.prepareStatement(INSERT_AGP_METADATA);
			insertPStmt = dbCon.prepareStatement(INSERT_INSCRIPTION_STATEMENT);
			updatePStmt = dbCon.prepareStatement(UPDATE_INSCRIPTION_STATEMENT);
			selPStmt = dbCon.prepareStatement(CHECK_INSCRIPTION_STATEMENT);

			updateContentStmt = dbCon.prepareStatement(UPDATE_CONTENT);
			updateDescriptionStmt = dbCon.prepareStatement(UPDATE_DESCRIPTION);
			updateBibStmt = dbCon.prepareStatement(UPDATE_BIB);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		patternList = new ArrayList<Pattern>();
		patternList
				.add(Pattern
						.compile("^\\w+ \\(\\w+\\),? (\\w* )* ?\\(?([\\w.-]*+)\\)?(, [\\w\\s,.\\(\\)]*)?"));
	}
}
