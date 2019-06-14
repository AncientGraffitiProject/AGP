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
 * This class extract the writing_style from the EDR inscription database table
 * and translate the Italian writing style into English for the AGP Info. Reads
 * database information from the configuration.properties file.
 * Can be called as a standalone script or from another script.
 * 
 * @author Sara Sprenkle
 * 
 */
public class ExtractEDRLanguageForAGPInfo {

	private static final String UPDATE_ANNOTATION_STMT = "UPDATE agp_inscription_info "
			+ "SET lang_in_english = ? WHERE edr_id=?";

	private final static String SELECT_GRAFFITI = "select language, edr_id from edr_inscriptions";

	private static Connection dbCon;

	private static String DB_DRIVER;
	private static String DB_URL;
	private static String DB_USER;
	private static String DB_PASSWORD;

	private static PreparedStatement updateAGPInfoStmt;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		updateAGPLanguage();
	}

	public static void updateAGPLanguage() {
		init();

		try {

			// Extract the data from original DB and insert it into the new DB
			PreparedStatement extractData = dbCon.prepareStatement(SELECT_GRAFFITI);

			ResultSet rs = extractData.executeQuery();

			while (rs.next()) {
				String language = rs.getString("language");
				String edr_id = rs.getString("edr_id");
				String translatedLanguage = language;

				//System.out.println(edr_id + ": " + language);

				if (language.equals("graeca")) {
					translatedLanguage = "Greek";
				} else if (language.equals("latina")) {
					translatedLanguage = "Latin";
				} else if (language.equals("latina-graeca")) {
					translatedLanguage = "Latin/Greek";
				} else if (language.equals("alia")) {
					translatedLanguage = "other";
				} else {
					translatedLanguage = language;
				}

				updateAnnotation(edr_id, translatedLanguage);

			}

			rs.close();
			extractData.close();
			updateAGPInfoStmt.close();
			dbCon.close();

		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * 
	 * 
	 * @param edr_id
	 * @param translatedLang
	 */
	private static void updateAnnotation(String edr_id, String translatedLang) {
		try {
			updateAGPInfoStmt.setString(1, translatedLang);
			updateAGPInfoStmt.setString(2, edr_id);
			updateAGPInfoStmt.executeUpdate();
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
			updateAGPInfoStmt = dbCon.prepareStatement(UPDATE_ANNOTATION_STMT);
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
