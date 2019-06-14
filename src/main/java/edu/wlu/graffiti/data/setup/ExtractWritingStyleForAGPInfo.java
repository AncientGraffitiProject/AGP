/**
 * 
 */

package edu.wlu.graffiti.data.setup;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * This class extracts the writing_style from the EDR inscriptions and translate
 * the Italian writing style into English for AGP. Can be called as standalone
 * application or from another script.
 * Reads database information from the configuration.properties file.
 * 
 * @author sprenkle
 * 
 */
public class ExtractWritingStyleForAGPInfo {

	private static final String UPDATE_ANNOTATION_STMT = "UPDATE agp_inscription_info "
			+ "SET writing_style_in_english = ? WHERE edr_id=?";

	final static String SELECT_GRAFFITI = "select writing_style, edr_id from edr_inscriptions";

	private static Connection dbCon;
	private static String DB_DRIVER;
	private static String DB_URL;
	private static String DB_USER;
	private static String DB_PASSWORD;

	private static PreparedStatement updateWritingStyleStmt;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		updateWritingStyle();
	}

	public static void updateWritingStyle() {
		init();

		try {

			PreparedStatement extractData = dbCon.prepareStatement(SELECT_GRAFFITI);

			ResultSet rs = extractData.executeQuery();

			while (rs.next()) {
				String writingStyle = rs.getString("writing_style");
				String edr_id = rs.getString("edr_id");
				String translatedWritingStyle = writingStyle;

				//System.out.println(edr_id + ": " + writingStyle);

				// Writing Style is either
				// "litt. scariph" which should be translated to
				// "Graffito/scratched"
				// OR "cetera/carbone" which should be translated to "charcoal"

				// make this into a switch statement?
				if (writingStyle.startsWith("litt. scariph")) {
					translatedWritingStyle = "Graffito/incised";
				} else if (writingStyle.equals("cetera/carbone")) {
					translatedWritingStyle = "charcoal";
				} else if (writingStyle.equals("carbone")) {
					translatedWritingStyle = "charcoal";
				} else if (writingStyle.startsWith("cetera, carbone")) {
					translatedWritingStyle = "charcoal";
				} else if (writingStyle.startsWith("cetera carbone")) {
					translatedWritingStyle = "charcoal";
				} else if (writingStyle.startsWith("cetera; lapide rubro")) {
					translatedWritingStyle = "other; 'red rock'";
				} else if (writingStyle.startsWith("cetera lapide rubro")) {
					translatedWritingStyle = "other; 'red rock'";
				} else if (writingStyle.startsWith("cetera; rubrica")) {
					translatedWritingStyle = "other; 'red substance'";
				} else if (writingStyle.startsWith("scalpro")) {
					translatedWritingStyle = "chisel";
				} else {
					translatedWritingStyle = writingStyle;
				}

				updateAnnotation(edr_id, translatedWritingStyle);

			}

			rs.close();
			extractData.close();
			updateWritingStyleStmt.close();
			dbCon.close();

		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * 
	 * 
	 * @param edr_id
	 * @param translatedStyle
	 *            TODO
	 */
	private static void updateAnnotation(String edr_id, String translatedStyle) {
		try {
			updateWritingStyleStmt.setString(1, translatedStyle);
			updateWritingStyleStmt.setString(2, edr_id);
			updateWritingStyleStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
			updateWritingStyleStmt = dbCon.prepareStatement(UPDATE_ANNOTATION_STMT);
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
