package edu.wlu.graffiti.data.setup.main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.text.StringEscapeUtils;

import edu.wlu.graffiti.bean.Insula;
import edu.wlu.graffiti.bean.Property;
import edu.wlu.graffiti.data.setup.HandleFindspotsWithoutAddresses;
import edu.wlu.graffiti.data.setup.Utils;

/**
 * Import the data from EDR.
 * 
 * @author Sara Sprenkle
 * @author Trevor Stalnaker
 */
public class ImportEDRData {

	/* Location of data in the EDR CSV file. */
	private static final int LOCATION_OF_WRITING_STYLE = 9;
	private static final int LOCATION_OF_LANGUAGE = 10;

	private static String DB_DRIVER;
	private static String DB_URL;
	private static String DB_USER;
	private static String DB_PASSWORD;

	private static final String INSERT_INSCRIPTION_STATEMENT = "INSERT INTO edr_inscriptions "
			+ "(edr_id, ancient_city, find_spot, measurements, writing_style, \"language\", date_beginning, date_end, date_explanation) "
			+ "VALUES (?,?,?,?,?,?,?,?,?)";

	private static final String UPDATE_INSCRIPTION_STATEMENT = "UPDATE edr_inscriptions SET "
			+ "ancient_city=?, find_spot=?, measurements=?, writing_style=?, \"language\"=?, date_beginning=?, date_end=?, date_explanation=? "
			+ "WHERE edr_id = ?";

	private static final String CHECK_INSCRIPTION_STATEMENT = "SELECT COUNT(*) FROM edr_inscriptions"
			+ " WHERE edr_id = ?";

	private static final String INSERT_AGP_METADATA = "INSERT INTO agp_inscription_info (edr_id) " + "VALUES (?)";

	public static final String UPDATE_PROPERTY = "UPDATE agp_inscription_info SET "
			+ "property_id = ? WHERE edr_id = ?";

	private static final String UPDATE_CONTENT = "UPDATE edr_inscriptions SET " + "content = ? WHERE edr_id = ?";
	private static final String UPDATE_CONTENT_EPIDOC = "UPDATE agp_inscription_info SET "
			+ "content_epidocified = ? WHERE edr_id = ?";
	private static final String UPDATE_CIL = "UPDATE agp_inscription_info SET " + "cil = ? WHERE edr_id = ?";
	private static final String UPDATE_BIB = "UPDATE edr_inscriptions SET " + "bibliography = ? WHERE edr_id = ?";
	private static final String UPDATE_APPARATUS = "UPDATE edr_inscriptions SET " + "apparatus = ? WHERE edr_id = ?";
	private static final String SET_ON_FACADE = "UPDATE agp_inscription_info SET on_facade = true WHERE edr_id = ?";
	private static final String SELECT_INSULA_AND_PROPERTIES = "select *, insula.id as insula_id, properties.id as property_id from insula, properties where insula_id = insula.id";
	private static final String INSERT_PHOTO_STATEMENT = "INSERT INTO photos (edr_id, photo_id) " + "VALUES (?, ?)";

	private static Connection dbCon;

	private static PreparedStatement insertAGPMetaStmt;
	private static PreparedStatement insertPStmt;

	private static PreparedStatement updatePStmt;
	private static PreparedStatement selPStmt;
	private static PreparedStatement updatePropertyStmt;
	private static PreparedStatement updateApparatusStmt;
	private static PreparedStatement setFacade;
	private static PreparedStatement insertPhotoStmt;

	private static Map<String, HashMap<String, Insula>> cityToInsulaMap;

	private static Map<Integer, HashMap<String, Property>> insulaToPropertyMap;
	
	private static HashMap<String, String> charMap = new HashMap<String, String>();

	private static List<Pattern> patternList;

	private static Pattern bibPattern;

	public static void main(String[] args) {
		init();

		try {
			readPropertiesAndInsula();
			updateInscriptions("data/EDRData/epigr.csv");
			updateContent("data/EDRData/testo_epigr.csv");
			updateBibliography("data/EDRData/editiones.csv");
			updateApparatus("data/EDRData/apparatus.csv");
			updatePhotoInformation("data/EDRData/foto.csv");

			System.out.println("\nOn to Camodeca...\n");

			// do again for Camodeca
			updateInscriptions("data/AGPData/camodeca_epigr.csv");
			updateContent("data/AGPData/camodeca_testo.csv");
			updateBibliography("data/AGPData/camodeca_editiones.csv");
			updateApparatus("data/AGPData/camodeca_apparatus.csv");

			HandleFindspotsWithoutAddresses.updateGraffitiLocations(HandleFindspotsWithoutAddresses.LOCATION_FILE_NAME);
			StorePropertiesFromDatabaseForgeoJsonMap.storeProperties();
			StoreInsulaeFromDatabaseForgeoJsonMap.storeInsulae();
			dbCon.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	private static void readPropertiesAndInsula() throws SQLException {
		System.out.println("Reading in properties and insula");
		cityToInsulaMap = new HashMap<String, HashMap<String, Insula>>();
		insulaToPropertyMap = new HashMap<Integer, HashMap<String, Property>>();

		Statement infoStmt = dbCon.createStatement();

		ResultSet rs = infoStmt.executeQuery(SELECT_INSULA_AND_PROPERTIES);

		while (rs.next()) {
			String modernCity = rs.getString("modern_city");
			String insName = rs.getString("short_name");
			String propNum = rs.getString("property_number");
			String propName = rs.getString("property_name");
			int insID = rs.getInt("insula_id");
			int propID = rs.getInt("property_id");

			if (!cityToInsulaMap.containsKey(modernCity)) {
				cityToInsulaMap.put(modernCity, new HashMap<String, Insula>());
			}
			Insula ins = new Insula(insID, modernCity, insName, "");
			cityToInsulaMap.get(modernCity).put(insName, ins);

			Property p = new Property(propID, propNum, propName, ins);

			if (!insulaToPropertyMap.containsKey(insID)) {
				insulaToPropertyMap.put(insID, new HashMap<String, Property>());
			}

			insulaToPropertyMap.get(insID).put(propNum, p);

		}
		rs.close();
		infoStmt.close();

		for (String city : cityToInsulaMap.keySet()) {
			System.out.println("city: " + city);
			for (String insulaName : cityToInsulaMap.get(city).keySet()) {
				System.out.println("    - " + insulaName + ": " + cityToInsulaMap.get(city).get(insulaName));
			}
		}

	}

	/**
	 * Update the apparatus information in each of the inscription entries.
	 * 
	 * @param apparatusFileName
	 */
	private static void updateApparatus(String apparatusFileName) {
		String eagleID = "";
		try {
			updateApparatusStmt = dbCon.prepareStatement(UPDATE_APPARATUS);

			Reader in = new InputStreamReader(new FileInputStream(apparatusFileName), "UTF-8");
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			for (CSVRecord record : records) {
				eagleID = Utils.cleanData(record.get(0));
				if (record.size() == 1) { // skip if
					continue;
				}
				String apparatus = Utils.cleanData(record.get(1));

				try {
					selPStmt.setString(1, eagleID);

					ResultSet rs = selPStmt.executeQuery();

					int count = 0;

					if (rs.next()) {
						count = rs.getInt(1);
					} else {
						System.err.println(eagleID
								+ ":\nSomething went wrong with the SELECT statement in updating inscriptions!");
					}

					if (count == 1) {
						updateApparatusStmt.setString(1, apparatus);
						updateApparatusStmt.setString(2, eagleID);

						int updated = updateApparatusStmt.executeUpdate();
						if (updated != 1) {
							System.err.println("\nSomething went wrong with apparatus for " + eagleID);
							System.err.println(apparatus);
						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException | SQLException e) {
			System.err.println("\nSomething went wrong with apparatus for " + eagleID);
			e.printStackTrace();
		}
	}

	/**
	 * Updates the bibliography field in the database, using the EDR CSV export
	 * file. Also handles that the AGP link may be in the bibliography and should be
	 * removed.
	 * 
	 * @param bibFileName
	 */
	private static void updateBibliography(String bibFileName) {
		try {
			PreparedStatement updateBibStmt = dbCon.prepareStatement(UPDATE_BIB);
			PreparedStatement updateCilStmt = dbCon.prepareStatement(UPDATE_CIL);

			Reader in = new FileReader(bibFileName);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			for (CSVRecord record : records) {
				String edrID = Utils.cleanData(record.get(0));
				String bib = Utils.cleanData(record.get(1));
				Matcher bibMatch = bibPattern.matcher(bib);

				// handles if the AGP link is in the bibliography
				if (bibMatch.find()) {
					bib = bibMatch.replaceAll("");
				}

				try {
					selPStmt.setString(1, edrID);

					ResultSet rs = selPStmt.executeQuery();

					int count = 0;

					if (rs.next()) {
						count = rs.getInt(1);
					} else {
						System.err.println(
								edrID + ":\nSomething went wrong with the SELECT statement in updating inscriptions!");
					}
					if (count == 1) {
						updateBibStmt.setString(1, bib);
						updateBibStmt.setString(2, edrID);

						int updated = updateBibStmt.executeUpdate();
						if (updated != 1) {
							System.err.println("\nSomething went wrong with bibliography for " + edrID);
							System.err.println(bib);
						} else {
							updateCilStmt.setString(1, extractCIL(bib));
							updateCilStmt.setString(2, edrID);
							updateCilStmt.executeUpdate();
						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Updates the content field of database, based on the EDR CSV export file. Also
	 * creates a "starter" epidoc content field for agp_inscriptions_info based on
	 * the content.
	 * 
	 * @param contentFileName
	 */
	private static void updateContent(String contentFileName) {
		try {
			PreparedStatement updateContentStmt = dbCon.prepareStatement(UPDATE_CONTENT);
			PreparedStatement updateEpidocStmt = dbCon.prepareStatement(UPDATE_CONTENT_EPIDOC);

			Reader in = new InputStreamReader(new FileInputStream(contentFileName), "UTF-8");
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			for (CSVRecord record : records) {
				String eagleID = Utils.cleanData(record.get(0));
				String content = Utils.cleanData(record.get(1));

				try {
					int count = 0;
					content = cleanContent(content);
					selPStmt.setString(1, eagleID);

					ResultSet rs = selPStmt.executeQuery();

					if (rs.next()) {
						count = rs.getInt(1);
					} else {
						System.err.println(eagleID
								+ ":\nSomething went wrong with the SELECT statement in updating inscriptions!");
					}
					if (count == 1) {
						updateContentStmt.setString(1, content);
						updateContentStmt.setString(2, eagleID);

						int updated = updateContentStmt.executeUpdate();
						if (updated != 1) {
							System.err.println("\nSomething went wrong with content for " + eagleID);
							System.err.println(content);
						} else {
							// TODO: put starter Epidoc code.
							updateEpidocStmt.setString(1, transformContentToEpidoc(content));
							updateEpidocStmt.setString(2, eagleID);
							updateEpidocStmt.executeUpdate();
						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	public static String extractCIL(String bib) {
		Pattern pattern = Pattern.compile("CIL\\s04\\,\\s[0-9]{5}[a-zA-Z]*\\s*\\([0-9]\\)");
		Matcher matcher = pattern.matcher(bib);
		if (matcher.find()) {
			return matcher.group(0).split("\\s*\\(")[0];
		}
		return "";
	}

	public static String transformContentToEpidoc(String content) {
		content = normalize(content);
		Pattern pattern;
		Matcher matcher;

		//If any angle brackets exist in text prior to epidocification convert them to entity numbers so they don't mess with parsing later
//		pattern = Pattern.compile("<|>|〈|〉");
//		matcher = pattern.matcher(content);
//		if (matcher.find()) {
//			content = formatAngleBrackets(content);
//		}
		
		//Must use HTML entities to search for angle brackets
		pattern = Pattern.compile("\\&\\#60\\;\\:textus non legitur\\&\\#62\\;");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = addIllegibleText(content);
		}
		
		//Must use HTML entities to search for angle brackets
		pattern = Pattern.compile("\\&\\#60\\;\\:vacat\\&\\#62\\;");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = addBlankSpace(content);
		}
		
		//Must use HTML entities to search for angle brackets
		pattern = Pattern.compile("\\&\\#60\\;\\:ianua\\&\\#62\\;");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = addDoorSpace(content);
		}
		
		//Must be done before we determine line breaks
		pattern = Pattern.compile("\\[([ ]?-[ ]?){6}\\](\\n\\[([ ]?-[ ]?){6}\\])*");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = addLostLinesExtentKnown(content);
		}
		
		if (content.contains(":columna")) { // if content is split across columns, mark those columns
			content = markContentWithColumns(content);
		} else {
			content = addLBTagsToContent(content);
		}
		
		//Must use HTML entities to search for angle brackets
		pattern = Pattern.compile("\\&\\#60\\;\\:([^\\s]+)\\&\\#62\\;");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = addSubaudible(content);
		}

		pattern = Pattern.compile("\\(\\([^\\s\\:]+\\)\\)");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = addSymbols(content);
		}
		
		pattern = Pattern.compile("\\(\\(\\:[^\\[\\]\\:\\<\\>]*\\)\\)");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = markContentWithFigureTags(content);
		}
		
		//Temporary Fix Implemented here!
		//pattern = Pattern.compile("[^\\s\\>]*[ ]?\\(\\:[^\\s\\?]*\\)");
		pattern = Pattern.compile("[^\\s\\>]+[ ]*\\(\\:[^\\t\\n\\?\\)]+\\)");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = addNonStandardSpelling(content);
		}
		
		pattern = Pattern.compile("[^\\s\\>]+[ ]*\\(\\:[^\\t\\n\\?\\)]+\\?\\)");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = addUncertainNonStandardSpelling(content);
		}
		
		pattern = Pattern.compile("([^\\s>]+)\\((\\-[ ]?){3}\\)");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = addUnknownAbbreviationTags(content);
		}
		
		pattern = Pattern.compile("[^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]*\\([^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]+\\)");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = addAbbreviationTags(content);
		}

		pattern = Pattern.compile("[^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]*\\([^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]+\\?\\)");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = addAbbreviationTagsWithUncertainty(content);
		}

//		pattern = Pattern.compile("\\[\\- \\- \\-\\]|\\[\\-\\-\\-\\]");
//		matcher = pattern.matcher(content);
//		if (matcher.find()) {
//			//content = addLostContentTags(content);
//		}
		
//		pattern = Pattern.compile("\\[\\-+\\]");
//		matcher = pattern.matcher(content);
//		if (matcher.find()) {
//			//content = addLostCharactersNumberCertain(content);
//		}

		//pattern = Pattern.compile("\\[\\[[^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]*\\]\\]"); This removed too many nested characters (like [])
		pattern = Pattern.compile("(\\[\\[|〚)([^\\s\\(\\[\\)\\]\\:\\<\\>\\,]|\\[?\\]?)*(\\]\\]|〛)");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = addIntentionallyErasedTags(content);
		}
		
//		pattern = Pattern.compile("\\[\\+([0-9]+)\\?\\+\\]");
//		matcher = pattern.matcher(content);
//		if (matcher.find()) {
//			//content = addLostCharactersNumberUncertain(content);
//		}
		
//		pattern = Pattern.compile("\\[([^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,\\-]||[ ]?)*\\]");
//		matcher = pattern.matcher(content);
//		if (matcher.find()) {
//			//content = addOncePresentButNowErasedTags(content);
//		}
		
//		pattern = Pattern.compile("\\[([^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]||[ ]?)*\\?\\]");
//		matcher = pattern.matcher(content);
//		if (matcher.find()) {
//			//content = addUncertainOncePresentButNowErasedTags(content);
//		}
		
		//With a few tweeks this could be used in place of the other checks, but for now it is an addition
		pattern = Pattern.compile("\\[[^\\[\\]]+\\]");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = addSquareBrackets(content);
		}

		pattern = Pattern.compile("\\++");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = addIllegibleCharacters(content);
		}
		
		pattern = Pattern.compile("([A-Z][\u0332,\u0323,\u0302]?){2,}");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = addIncomprehensibleCharacters(content);
		}
		
		pattern = Pattern.compile("(([^\\s]\u0302)+)([^\\s](\u0323|\u0332)?)");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = addLettersJoinedInLigature(content);
		}
		
		pattern = Pattern.compile("(- - - - - -)|(------)");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = addLostLines(content);
		}
		
		pattern = Pattern.compile("([^\\s]\u0332)+");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = addOnceVisibleNowMissingCharacters(content);
		}
		
		pattern = Pattern.compile("([^\\s]\u0323)+");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = addDamagedCharacters(content);
		}
		
		pattern = Pattern.compile("\\{[^\\s\\{\\}]*\\}");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = addSurplusCharacters(content);
		}
		return content;
	}

	private static String markContentWithFigureTags(String content) {
		String temp;
		Pattern pattern = Pattern.compile("\\(\\(\\:[^\\[\\]\\:\\<\\>]*\\)\\)");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			temp = matcher.group(0);
			content = content.replace(temp, "<figure><figDesc>"
					+ temp.replaceAll("\\(\\(\\:", "").replaceAll("\\)\\)", "") + "</figDesc></figure>");
		}
		return content;
	}

	private static String markContentWithColumns(String content) {
		StringBuilder returnString = new StringBuilder();
		String[] splitContentAcrossColumns = content.split(".*columna.*");
		for (int i = 1; i < splitContentAcrossColumns.length; i++) {
			char letter = (char) ('a' + i - 1);
			returnString.append("<div type='textpart' subtype='column' n='" + letter + "'>");
			returnString.append(addLBTagsToContent(splitContentAcrossColumns[i].trim()));
			returnString.append("</div>");
		}
		return returnString.toString().trim();
	}

	private static String addLBTagsToContent(String content) {
		StringBuilder returnString = new StringBuilder();
		String[] splitContent = content.split("\n *");
		int n = 1;
		for (int i = 0; i < splitContent.length; i++) {
			//returnString.append("<lb n='" + Integer.toString(n) + "'");
			Matcher matcher = Pattern.compile("\\&\\#60\\;\\:ad perpendiculum\\&\\#62\\;").matcher(content);
			if (matcher.find()) {
				if (!Pattern.compile("\\&\\#60\\;\\:ad perpendiculum\\&\\#62\\;").matcher(splitContent[i]).find()) {
					returnString.append("<lb n='" + Integer.toString(n) + "'");
					returnString.append(" style='text-direction:vertical'/>" + splitContent[i].replaceAll("\\&\\#60\\;\\:ad perpendiculum\\&\\#62\\;", ""));
				}
				else {
					n--;
				}
				
			}
			else {
				returnString.append("<lb n='" + Integer.toString(n) + "'/>" + splitContent[i]);
			}
			n++;
		}
		return returnString.toString().trim();
	}

	private static String addAbbreviationTags(String content) {
		String temp;
		Pattern pattern = Pattern.compile("[^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]*\\([^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]+\\)");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			temp = matcher.group(0);
			String abbr = temp.split("\\(")[0];
			String ex = temp.split("\\(")[1].split("\\)")[0];
			content = content.replace(temp, "<expan><abbr>" + abbr + "</abbr><ex>" + ex + "</ex></expan>");
		}
		return content;
	}

	private static String addAbbreviationTagsWithUncertainty(String content) {
		String temp;
		Pattern pattern = Pattern
				.compile("[^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]*\\([^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]+\\?\\)");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			temp = matcher.group(0);
			String abbr = temp.split("\\(")[0];
			String ex = temp.split("\\(")[1].split("\\?\\)")[0];
			content = content.replace(temp, "<expan><abbr>" + abbr + "</abbr><ex cert='low'>" + ex + "</ex></expan>");
		}
		return content;
	}

//	private static String addLostContentTags(String content) {
//		String temp;
//		Pattern pattern = Pattern.compile("\\[\\- \\- \\-\\]|\\[\\-\\-\\-\\]");
//		Matcher matcher = pattern.matcher(content);
//		while (matcher.find()) {
//			temp = matcher.group(0);
//			content = content.replace(temp, temp.replaceAll("\\[\\- \\- \\-\\]|\\[\\-\\-\\-\\]",
//					"<gap reason='lost' extent='unknown' unit='character'/>"));
//		}
//		return content;
//	}

	private static String addIntentionallyErasedTags(String content) {
		String temp;
		//Pattern pattern = Pattern.compile("\\[\\[[^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]*\\]\\]");
		Pattern pattern = Pattern.compile("(\\[\\[|〚)([^\\s\\(\\[\\)\\]\\:\\<\\>\\,]|\\[?\\]?)*(\\]\\]|〛)");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			temp = matcher.group(0);
			content = content.replace(temp, "<del rend='erasure'><supplied reason='lost'>"
					+ temp.replaceAll("\\[\\[|\\]\\]|〚|〛", "") + "</supplied></del>");
		}
		return content;
	}

//	private static String addOncePresentButNowErasedTags(String content) {
//		String temp;
//		Pattern pattern = Pattern.compile("\\[([^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,\\-]||[ ]?)*\\]");
//		Matcher matcher = pattern.matcher(content);
//		while (matcher.find()) {
//			temp = matcher.group(0);
//			content = content.replace(temp,
//					"<supplied reason='lost'>" + temp.replaceAll("\\[|\\]", "") + "</supplied>");
//		}
//		return content;
//	}
	
//	//Epidocifies once present, but now erased tags that are guessed at with uncertainty
//	private static String addUncertainOncePresentButNowErasedTags(String content) {
//		String temp;
//		Pattern pattern = Pattern.compile("\\[([^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]|[ ]?)*\\?\\]");
//		Matcher matcher = pattern.matcher(content);
//		while (matcher.find()) {
//			temp = matcher.group(0);
//			content = content.replace(temp,
//					"<supplied reason='lost' cert='low'>" + temp.replaceAll("\\[|\\]|\\?", "") + "</supplied>");
//		}
//		return content;
//	}
	
	//Epidocifies non-standard spellings
	private static String addNonStandardSpelling(String content) {
		String temp;
		//Pattern pattern = Pattern.compile("[^\\s\\>]*[ ]?\\(\\:[^\\s\\?]*\\)"); //Allow for only one space between elements
		//Pattern pattern = Pattern.compile("[^\\s\\>]*[ ]*\\(\\:[^\\s\\?]*\\)"); //Disallow spaces with parens
		Pattern pattern = Pattern.compile("[^\\s\\>]+[ ]*\\(\\:[^\\t\\n\\?\\)]+\\)");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			temp = matcher.group(0);
			String orig = temp.split("[ ]?\\(\\:")[0];
			String reg = temp.split("\\(\\:")[1].split("\\)")[0];
			content = content.replace(temp, "<choice><reg>" + reg + "</reg><orig>" + orig + "</orig></choice>");
		}
		return content;
	}
	
	//Epidocifies uncertain non-standard spellings
	private static String addUncertainNonStandardSpelling(String content) {
		String temp;
		Pattern pattern = Pattern.compile("[^\\s\\>]+[ ]*\\(\\:[^\\t\\n\\?\\)]+\\?\\)");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			temp = matcher.group(0);
			String orig = temp.split("[ ]?\\(\\:")[0];
			String reg = temp.split("\\(\\:")[1].split("\\)")[0].replaceAll("\\?", "");
			content = content.replace(temp, "<choice><reg cert='low'>" + reg + "</reg><orig>" + orig + "</orig></choice>");
		}
		return content;
	}
	
	//Epidocifies illegible characters
	private static String addIllegibleCharacters(String content) {
		String temp;
		Pattern pattern = Pattern.compile("\\++");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			temp = matcher.group(0);
			String quant = ((Integer)(temp.length())).toString();
			content = content.replaceFirst("\\++", "<gap reason='illegible' quantity='" + quant + "' unit='character'/>");
		}
		return content;
	}
	
	//Epidocifies letters joined in ligature
	private static String addLettersJoinedInLigature(String content) {
		//carot = "\u0302"
		String temp;
		Pattern pattern = Pattern.compile("(([^\\s]\u0302)+)([^\\s](\u0323|\u0332)?)");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			temp = matcher.group(0);
			String tagString = "";
			String[] joinedChars = matcher.group(1).split("\u0302");
			for (int i =0; i < joinedChars.length; i++) {
				tagString += joinedChars[i];
			}
			tagString += matcher.group(3);
			content = content.replaceFirst(temp, "<hi rend='ligature'>" + tagString + "</hi>");
		}
		return content;
	}
	
	//Epidocifies lost lines
	private static String addLostLines(String content) {
		String temp;
		Pattern pattern = Pattern.compile("(- - - - - -)|(------)");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			temp = matcher.group(0);
			content = content.replace(temp, "<gap reason='lost' extent='unknown' unit='line'/>");
		}
		return content;
	}
	
	//Epidocifies characters formerly visible, now missing
	private static String addOnceVisibleNowMissingCharacters(String content) {
		// macron = \u0331 or combining low line = \u0332
		String temp;
		Pattern pattern = Pattern.compile("([^\\s]\u0332)+");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			temp = matcher.group(0);
			content = content.replaceFirst(temp, "<supplied reason='undefined' evidence='previouseditor'>" + 
			temp.replaceAll("\u0332", "") + "</supplied>");
		}
		return content;
	}
	
	//Epidocifies characters damaged or unclear without context
	private static String addDamagedCharacters(String content) {
		// dot = \u0323
		String temp;
		Pattern pattern = Pattern.compile("([^\\s]\u0323)+");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			temp = matcher.group(0);
			content = content.replaceFirst(temp, "<unclear>" + temp.replaceAll("\u0323", "") + "</unclear>");
		}
		return content;
	}
	
	//Epidocifies surplus characters
	private static String addSurplusCharacters(String content) {
		String temp;
		Pattern pattern = Pattern.compile("\\{[^\\s\\{\\}]*\\}");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			temp = matcher.group(0);
			content = content.replace(temp, "<surplus>" + temp.replaceAll("\\{|\\}", "") + "</surplus>");
		}
		return content;
	}
	
//	//Epidocifies an uncertain number of lost characters
//	private static String addLostCharactersNumberUncertain(String content) {
//		String temp;
//		Pattern pattern = Pattern.compile("\\[\\+([0-9]+)\\?\\+\\]");
//		Matcher matcher = pattern.matcher(content);
//		while (matcher.find()) {
//			temp = matcher.group(0);
//			String quant = matcher.group(1);
//			content = content.replace(temp, "<gap reason='lost' quantity='" + quant + "' unit='character' precision='low'/>");
//		}
//		return content;
//	}
	
//	//Epidocifies a certain number of lost characters
//	private static String addLostCharactersNumberCertain(String content) {
//		String temp;
//		Pattern pattern = Pattern.compile("\\[\\-+\\]");
//		Matcher matcher = pattern.matcher(content);
//		while (matcher.find()) {
//			temp = matcher.group(0);
//			String quant = ((Integer) (temp.length()-2)).toString();
//			content = content.replace(temp, "<gap reason='lost' quantity='" + quant + "' unit='character'/>");
//		}
//		return content;
//	}
	
	//Epidocifies an uncertain abbreviation
	private static String addUnknownAbbreviationTags(String content) {
		String temp;
		Pattern pattern = Pattern.compile("([^\\s>]+)\\((\\-[ ]?){3}\\)");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			temp = matcher.group(0);
			String abbr = matcher.group(1);
			content = content.replace(temp, "<abbr>" + abbr + "</abbr>");
		}
		return content;
	}
	
	//Epidocifies incomprehensible characters
	//Ignores Roman Numerals, but doesn't work for Greek Characters
	private static String addIncomprehensibleCharacters(String content) {
		String temp;
		Pattern pattern = Pattern.compile("([A-Z][\u0332,\u0323,\u0302]?){2,}");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			temp = matcher.group(0);
			if (! temp.matches("[C,D,I,L,M,V,X]+")) {
				content = content.replace(temp, "<orig>" + temp.toLowerCase() + "</orig>");
			}
		}
		return content;
	}
	
	//Epidocify '<:textus non legitur>'
	private static String addIllegibleText(String content) {
		String temp;
		Pattern pattern = Pattern.compile("\\&\\#60\\;\\:textus non legitur\\&\\#62\\;");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			temp = matcher.group(0);
			content = content.replace(temp, "<gap reason='illegible' extent='unknown' unit='character'/>");
			
		}
		return content;
	}
	
	//Epidocify '<:vacat>'
	private static String addBlankSpace(String content) {
		String temp;
		Pattern pattern = Pattern.compile("\\&\\#60\\;\\:vacat\\&\\#62\\;");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			temp = matcher.group(0);
			content = content.replace(temp, "<space/>");
			
		}
		return content;
	}
	
	//Epidocify '<:ianua>'
	private static String addDoorSpace(String content) {
		String temp;
		Pattern pattern = Pattern.compile("\\&\\#60\\;\\:ianua\\&\\#62\\;");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			temp = matcher.group(0);
			content = content.replace(temp, "<space type='door'/>");	
		}
		return content;
	}
	
	//Epidocify all other forms of '<:[something]>' as subaudible
	private static String addSubaudible(String content) {
		String temp;
		Pattern pattern = Pattern.compile("\\&\\#60\\;\\:([^\\s]+)\\&\\#62\\;");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			temp = matcher.group(0);
			content = content.replace(temp, "<supplied reason='subaudible'>" + matcher.group(1) + "</supplied>");	
		}
		return content;
	}
	
	//Epidocify non-standard symbols
	private static String addSymbols(String content) {
		String temp;
		Pattern pattern = Pattern.compile("\\(\\([^\\s\\:]+\\)\\)");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			temp = matcher.group(0);
			String symbol = temp.replaceAll("\\(|\\)", "");
			content = content.replace(temp, "<expan><abbr><am><g type='" + symbol + "'/></am></abbr><ex>" + symbol + "</ex></expan>");
		}
		return content;
	}
	
	private static String addLostLinesExtentKnown(String content) {
		//Must be done before we determine line breaks
		Pattern pattern = Pattern.compile("\\[([ ]?-[ ]?){6}\\](\\n\\[([ ]?-[ ]?){6}\\])*");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			String temp = matcher.group(0);
			Matcher nestedMatcher = Pattern.compile("\\[([ ]?-[ ]?){6}\\]").matcher(temp);
			int quant = 0;
			while (nestedMatcher.find()) {
				quant += 1;
			}
			content = content.replace(temp, "<gap reason='lost' extent='" + quant + "' unit='line'/>");
		}
		return content;
	}
	
	//A massive method that epidocifies all content of the form []
	//This allows for more than one type of convention to be used within the brackets
	//Example: [--- accep] or [---d]ece[mbr?---]
	private static String addSquareBrackets(String content) {
		String original, match, temp, quant;
		Pattern pattern = Pattern.compile("\\[[^\\[\\]]+\\]");
		Matcher matcher = pattern.matcher(content);
		Matcher nestedMatcher;
		while (matcher.find()) {
			original = matcher.group(0);
			match = original.replaceAll("\\[|\\]", "");
			//nestedMatcher = Pattern.compile("((\\-\\- \\-|\\-{3})|([^\\-])+)").matcher(match);
			nestedMatcher = Pattern.compile("(((\\-([ ]\\-)*)+)|(\\+([0-9]+)\\?\\+)|(([^\\-])+)|(•([ ]•)*)+)").matcher(match);
			while (nestedMatcher.find()){
				temp = nestedMatcher.group(0);
				if (temp.equals("---") || temp.equals("- - -")) {
					match = match.replaceFirst(temp,"<gap reason='lost' extent='unknown' unit='character'/>");
				}
				// Dot character to replace '-' with '•' once the classicists are ready
				else if(temp.matches("((•[ ]?)+)")) {
					quant = ((Integer) (temp.replaceAll(" ", "").length())).toString();
					match = match.replaceFirst(temp, "<gap reason='lost' quantity='" + quant + "' unit='character'/>");
				}
				else if(temp.matches("((\\-[ ]?)+)")) {
					quant = ((Integer) (temp.replaceAll(" ", "").length())).toString();
					match = match.replaceFirst(temp, "<gap reason='lost' quantity='" + quant + "' unit='character'/>");
				}
				else if(temp.matches("(\\+[0-9]+\\?\\+)")) {
					quant = nestedMatcher.group(6); //This may change if regular express is changed
					match = match.replace(temp, "<gap reason='lost' quantity='" + quant + 
							"' unit='character' precision='low'/>");
				}
				else {
					if(temp.matches("[^\\?]+\\?[ ]?")) {
						match = match.replace(temp, "<supplied reason='lost' cert='low'>" + temp.replaceAll("\\?", "") + "</supplied>");
					}
					else {
						match = match.replace(temp, "<supplied reason='lost'>" + temp + "</supplied>");
					}
				}
			}
			content = content.replace(original, match);
		}
		
		return content;
	}
	
//	//Converts literal angle brackets to their html entity numbers to avoid parsing errors later on
//	//There are also two distinct varieties of brackets that must be rectified
//	private static String formatAngleBrackets(String content) {
//		String temp;
//		Pattern pattern = Pattern.compile("<|>|〈|〉");
//		Matcher matcher = pattern.matcher(content);
//		while (matcher.find()) {
//			temp = matcher.group(0);
//			if (temp.equals("<") || temp.equals("〈")){
//				content = content.replace(temp, "&#60;");
//			}else {
//				content = content.replace(temp, "&#62;");
//			}
//			
//		}
//		return content;
//	}
	
	// Translates Unicode characters to combinational characters
	private static void buildMap() {
		//Translate Sublinear Dots
		String[] unicodeChars = {"Ạ", "Ḅ", "Ḍ", "Ẹ", "Ḥ", "Ị", "Ḳ", "Ḷ", "Ṃ", "Ṇ", "Ọ", "Ṛ", "Ṣ", "Ṭ", "Ụ", "Ṿ", "Ẉ" , "Ỵ", "Ẓ",
				"ạ", "ḅ", "ḍ", "ẹ", "ḥ", "ị", "ḳ", "ḷ", "ṃ", "ṇ", "ọ", "ṛ", "ṣ", "ṭ", "ụ", "ṿ", "ẉ", "ỵ", "ẓ",};
		String[] associatedLetters = {"A", "B", "D", "E", "H", "I", "K", "L", "M", "N", "O", "R", "S", "T", "U", "V", "W", "Y", "Z",
				"a", "b", "d", "e", "h", "i", "k", "l", "m", "n", "o", "r", "s", "t", "u", "v", "w", "y", "z"};
		for (int i = 0; i < unicodeChars.length; i++) {
			charMap.put(unicodeChars[i], associatedLetters[i] + "\u0323");
		}
		//Translate Carots
		String[] unicodeChars_carots = {"Â", "â", "Ĉ", "ĉ", "Ê", "ê", "Ĝ", "ĝ", "Ĥ", "ĥ", "Î", "î", "Ĵ", "ĵ", "Ô", "ô", "Ŝ", "ŝ", 
				"Û", "û", "Ŵ", "ŵ", "X̂",  "x̂", "Ŷ", "ŷ", "Ẑ", "ẑ"};
		String[] associatedLetters_carots = {"A", "a", "C", "c", "E", "e", "G", "g", "H", "h", "I", "i", "J", "j", "O", "o",
				"S", "s", "U", "u", "W", "w", "X", "x", "Y", "y", "Z", "z"};
		for (int i = 0; i < unicodeChars_carots.length; i++) {
			charMap.put(unicodeChars_carots[i], associatedLetters_carots[i] + "\u0302");
		}
	}

	private static String translateUnicode(String str) {
		buildMap();
		//Translate Dots and Carots
		String returnStr = "";
		for (int i = 0; i < str.length(); i++) {
			String temp = Character.toString(str.charAt(i));
			if(charMap.containsKey(temp)) {
				returnStr += charMap.get(temp);
			}else {
				returnStr += str.charAt(i);
			}
		}
		return returnStr;
	}
	
	//Convert HTML encoding into plain strings for processing and run translateDots
	public static String normalize(String content) {
		if (content != null) {
			return translateUnicode(StringEscapeUtils.unescapeHtml4(content)).replaceAll("<|〈", "&#60;").replaceAll(">|〉", "&#62;");
		}
		return "";
		
	}

	/**
	 * Replaces the <br>
	 * tags with newlines. We handle the line breaks in our code.
	 * 
	 * @param content
	 * @return
	 */
	private static String cleanContent(String content) {
		// doing this because some inscriptions have uppercase BR tags in their contents
		// for some reason
		return content.replaceAll("\\<br\\>|\\<BR\\>", "\n");
		// return content.replace("<br>", "\n");
	}

	private static void updateInscriptions(String datafileName) {
		try {
			insertPStmt = dbCon.prepareStatement(INSERT_INSCRIPTION_STATEMENT);
			updatePStmt = dbCon.prepareStatement(UPDATE_INSCRIPTION_STATEMENT);
			selPStmt = dbCon.prepareStatement(CHECK_INSCRIPTION_STATEMENT);

			Reader in = new FileReader(datafileName);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			for (CSVRecord record : records) {
				String eagleID = Utils.cleanData(record.get(0));
				String ancient_city = Utils.cleanData(record.get(3));

				if (!cityToInsulaMap.containsKey(ancient_city)) {
					// System.err.println(eagleID + ": city " + ancient_city + " not found");
					continue;
				}

				String findSpot = Utils.cleanData(record.get(5));
				String dateBeginning = Utils.cleanData(record.get(15));
				String dateEnd = Utils.cleanData(record.get(16));
				String dateExplanation = Utils.cleanData(record.get(22));
				String alt = Utils.cleanData(record.get(17));
				String lat = Utils.cleanData(record.get(18));
				String littAlt = Utils.cleanData(record.get(20));

				String measurements = createMeasurementField(alt, lat, littAlt);

				String writingStyle = Utils.cleanData(record.get(LOCATION_OF_WRITING_STYLE));
				String language = Utils.cleanData(record.get(LOCATION_OF_LANGUAGE));

				selPStmt.setString(1, eagleID);

				ResultSet rs = selPStmt.executeQuery();

				int count = 0;

				if (rs.next()) {
					count = rs.getInt(1);
				} else {
					System.err.println(
							eagleID + ":\nSomething went wrong with the SELECT statement in updating inscriptions!");
				}

				int successUpdate = 0;

				if (count == 0) {
					successUpdate = insertEagleInscription(eagleID, ancient_city, findSpot, measurements, writingStyle,
							language, dateBeginning, dateEnd, dateExplanation);
				} else {
					successUpdate = updateEagleInscription(eagleID, ancient_city, findSpot, measurements, writingStyle,
							language, dateBeginning, dateEnd, dateExplanation);
				}

				// update AGP Metadata
				if (successUpdate == 1) {
					updateAGPMetadata(eagleID, ancient_city, findSpot);
				} else {
					System.err.println("Error updating/inserting " + eagleID);
				}

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
	 * @param edrId
	 * @param ancient_city
	 * @param findSpot
	 * @throws SQLException
	 */
	private static void updateAGPMetadata(String edrId, String ancient_city, String findSpot) throws SQLException {

		insertAGPMetaStmt = dbCon.prepareStatement(INSERT_AGP_METADATA);
		setFacade = dbCon.prepareStatement(SET_ON_FACADE);

		insertAGPMetaStmt.setString(1, edrId);

		try {
			insertAGPMetaStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if (findSpot.contains("facade") || findSpot.contains("Facade")) {
			//Set on_facade to true for inscription
			//setFacade.setString(1, edrId);
			//setFacade.executeUpdate();
			System.err.println(edrId + " is on a facade...  Can't currently handle");
			return;
		}

		String address = convertFindSpotToAddress(findSpot);

		// TODO
		// we're going to skip these because I can't handle them yet.

		if (!address.contains(".")) {
			System.err.println(edrId + ": Couldn't handle address: " + address);
			return;
		}

		String insula = "";
		String propertyNum = "";

		if (ancient_city.equals("Pompeii")) {
			insula = address.substring(0, address.lastIndexOf('.'));
		} else {
			insula = address.substring(0, address.indexOf('.'));
		}
		propertyNum = address.substring(address.lastIndexOf('.') + 1);

		if (!cityToInsulaMap.get(ancient_city).containsKey(insula)) {
			System.err.println(edrId + ": Insula " + insula + " not found in " + ancient_city + ", " + address);
			return;
		}

		int insulaID = cityToInsulaMap.get(ancient_city).get(insula).getId();

		if (!insulaToPropertyMap.get(insulaID).containsKey(propertyNum)) {
			System.err.println(edrId + ": Property " + propertyNum + " in Insula " + insula + " in " + ancient_city
					+ " not found.  Orig Findspot: " + findSpot + " address: " + address);
			return;
		}

		int propertyID = insulaToPropertyMap.get(insulaID).get(propertyNum).getId();
		
		// update property info

		updatePropertyStmt.setInt(1, propertyID);
		updatePropertyStmt.setString(2, edrId);

		int response = updatePropertyStmt.executeUpdate();

		if (response != 1) {
			System.err.println("WHAT? " + edrId);
		}

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

		// Hack to handle Insula Orientalis special addresses
		if (findSpot.contains("Insula Orientalis ")) {
			findSpot = findSpot.replace("Insula Orientalis ", "InsulaOrientalis");
		}

		Matcher matcher = patternList.get(0).matcher(findSpot);
		if (matcher.matches()) {
			return matcher.group(1);
		}

		matcher = patternList.get(1).matcher(findSpot);
		if (matcher.matches()) {
			return matcher.group(2);
		} else {
			return findSpot;
		}
	}

	private static int insertEagleInscription(String eagleID, String ancient_city, String findSpot, String measurements,
			String writingStyle, String language, String dateBeginning, String dateEnd, String dataExplanation)
			throws SQLException {
		insertPStmt.setString(1, eagleID);
		insertPStmt.setString(2, ancient_city);
		insertPStmt.setString(3, findSpot);
		insertPStmt.setString(4, measurements);
		insertPStmt.setString(5, writingStyle);
		insertPStmt.setString(6, language);
		insertPStmt.setString(7, dateBeginning);
		insertPStmt.setString(8, dateEnd);
		insertPStmt.setString(9, dataExplanation);

		try {
			return insertPStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static int updateEagleInscription(String eagleID, String ancient_city, String findSpot, String measurements,
			String writingStyle, String language, String dateBeginning, String dateEnd, String dateExplanation)
			throws SQLException {
		updatePStmt.setString(1, ancient_city);
		updatePStmt.setString(2, findSpot);
		updatePStmt.setString(3, measurements);
		updatePStmt.setString(4, writingStyle);
		updatePStmt.setString(5, language);
		updatePStmt.setString(6, dateBeginning);
		updatePStmt.setString(7, dateEnd);
		updatePStmt.setString(8, dateExplanation);
		updatePStmt.setString(9, eagleID);

		try {
			return updatePStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * Insert the photo information into database
	 * 
	 * @param string
	 */
	private static void updatePhotoInformation(String dataFileName) {
		try {
			Reader in = new FileReader(dataFileName);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			for (CSVRecord record : records) {
				String eagleID = Utils.cleanData(record.get(0));
				String photoID = Utils.cleanData(record.get(1));
				insertPhotoInformation(eagleID, photoID);
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

	private static void insertPhotoInformation(String eagleID, String photoID) throws SQLException {
		insertPhotoStmt.setString(1, eagleID);
		insertPhotoStmt.setString(2, photoID);

		try {
			insertPhotoStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static String createMeasurementField(String alt, String lat, String littAlt) {
		// Example: alt.: 2.50 lat.: 7.50 litt. alt.: 2-2,5

		StringBuffer sb = new StringBuffer();
		sb.append("height: ");
		sb.append(alt);
		sb.append(" width: ");
		sb.append(lat);
		sb.append(" letter height: ");
		sb.append(littAlt);

		return sb.toString();
	}

	private static void init() {
		getConfigurationProperties();

		try {
			Class.forName(DB_DRIVER);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			dbCon = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

			updatePropertyStmt = dbCon.prepareStatement(UPDATE_PROPERTY);
			// updateDescriptionStmt =
			// dbCon.prepareStatement(UPDATE_DESCRIPTION);
			insertPhotoStmt = dbCon.prepareStatement(INSERT_PHOTO_STATEMENT);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		patternList = new ArrayList<Pattern>();
		patternList.add(Pattern.compile("^.* \\((\\w*\\.\\w*.\\w*)\\)"));

		patternList
				.add(Pattern.compile("^\\w+ \\(\\w+\\),? ([\\w'.-]* )* ?\\(?([\\w',-\\.]*)\\)?(, [\\s\\w-,'.\\(\\)]*)?$"));
		// TODO: Need to update the pattern to handle Insula Orientalis I

		bibPattern = Pattern.compile("http://.*/Graffiti/graffito/AGP-EDR\\d{6} \\(\\d\\)");
	}

	public static void getConfigurationProperties() {
		Properties prop = Utils.getConfigurationProperties();

		DB_DRIVER = prop.getProperty("db.driverClassName");
		DB_URL = prop.getProperty("db.url");
		DB_USER = prop.getProperty("db.user");
		DB_PASSWORD = prop.getProperty("db.password");
	}

}
