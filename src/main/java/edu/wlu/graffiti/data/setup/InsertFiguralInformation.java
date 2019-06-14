package edu.wlu.graffiti.data.setup;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import edu.wlu.graffiti.bean.DrawingTag;

/**
 * Inserts Description, Translations, and Drawing Tags of figural graffiti from spreadsheet
 * into database
 * 
 * @author Sara Sprenkle
 *
 */
public class InsertFiguralInformation {

	// These are the locations of the data within the CSV file
	private static final int LOCATION_OF_EDR_ID = 0;
	private static final int LOCATION_OF_DRAWING_TAGS = 9;
	private static final int LOCATION_OF_LATIN_DESCRIPTION = 11;
	private static final int LOCATION_OF_ENGLISH_DESCRIPTION = 12;

	private static final String UPDATE_DESCRIPTION = "UPDATE figural_graffiti_info SET description_in_english=? WHERE edr_id=?";
	private static final String UPDATE_DESCRIPTION_TRANSLATION = "UPDATE figural_graffiti_info SET description_in_latin=? WHERE edr_id=?";
	private static final String INSERT_DRAWING_TAG_MAPPING = "INSERT INTO graffitotodrawingtags(graffito_id, drawing_tag_id) "
			+ "VALUES (?,?)";
	private static final String UPDATE_FIGURAL_COMPONENT = "UPDATE agp_inscription_info SET has_figural_component=True WHERE edr_id=?";

	private static Connection dbCon;
	private static String DB_DRIVER;
	private static String DB_URL;
	private static String DB_USER;
	private static String DB_PASSWORD;

	public static void main(String[] args) {
		insertFiguralInfo();
	}

	public static void insertFiguralInfo() {
		init();

		try {
			updateFiguralInfo("data/AGPData/herc_figural.csv");
			updateFiguralInfo("data/AGPData/pompeii_figural.csv");

			dbCon.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	private static void updateFiguralInfo(String datafileName) {
		PreparedStatement descriptionUpdate = null;
		PreparedStatement translationStmt = null;
		PreparedStatement dtStmt = null;
		PreparedStatement figCompStmt = null;
		Map<String, DrawingTag> drawingTags = getDrawingTags();

		Iterable<CSVRecord> records;

		try {
			Reader in = new InputStreamReader(new FileInputStream(datafileName), "UTF-8");
			records = CSVFormat.EXCEL.parse(in);
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}

		try {
			descriptionUpdate = dbCon.prepareStatement(UPDATE_DESCRIPTION);
			translationStmt = dbCon.prepareStatement(UPDATE_DESCRIPTION_TRANSLATION);
			dtStmt = dbCon.prepareStatement(INSERT_DRAWING_TAG_MAPPING);
			figCompStmt = dbCon.prepareStatement(UPDATE_FIGURAL_COMPONENT);


			for (CSVRecord record : records) {
				String edrID = Utils.cleanData(record.get(LOCATION_OF_EDR_ID));
				String description_in_english = Utils.cleanData(record.get(LOCATION_OF_ENGLISH_DESCRIPTION));
				String description_in_latin = Utils.cleanData(record.get(LOCATION_OF_LATIN_DESCRIPTION));
				String drawingTag = Utils.cleanData(record.get(LOCATION_OF_DRAWING_TAGS));

				try {
					descriptionUpdate.setString(2, edrID);
					translationStmt.setString(2, edrID);

					descriptionUpdate.setString(1, description_in_english);
					translationStmt.setString(1, description_in_latin);
					
					figCompStmt.setString(1, edrID);

					descriptionUpdate.executeUpdate();
					translationStmt.executeUpdate();
					figCompStmt.executeUpdate();
				} catch (SQLException e) {
					e.printStackTrace();
				}

				if (!drawingTag.isEmpty()) {
					String[] tags = drawingTag.split(", ");

					for (String tag : tags) {
						if (drawingTags.containsKey(tag)) {
							DrawingTag dt = drawingTags.get(tag);
							try {
								dtStmt.setString(1, edrID);
								dtStmt.setInt(2, dt.getId());
								dtStmt.executeUpdate();
							} catch (SQLException e) {
								System.err.println("EDR id: " + edrID);
								System.err.println(dt);
								e.printStackTrace();
							}
						} else {
							System.err.println(tag + " not one of the tags for " + edrID);
						}
					}
				}
			}
			descriptionUpdate.close();
			translationStmt.close();
			dtStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private static Map<String, DrawingTag> getDrawingTags() {
		Map<String, DrawingTag> drawingTags = new HashMap<String, DrawingTag>();

		try {
			PreparedStatement pstmt = dbCon.prepareStatement("SELECT id, name, description FROM drawing_tags");
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				DrawingTag dt = new DrawingTag();
				dt.setId(rs.getInt(1));
				dt.setName(rs.getString(2));
				dt.setDescription(rs.getString(3));
				drawingTags.put(rs.getString(2), dt);
			}

			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return drawingTags;
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
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public static void getConfigurationProperties() {
		Properties prop = Utils.getConfigurationProperties();

		DB_DRIVER = prop.getProperty("db.driverClassName");
		DB_URL = prop.getProperty("db.url");
		DB_USER = prop.getProperty("db.user");
		DB_PASSWORD = prop.getProperty("db.password");
	}

}
