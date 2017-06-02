/**
 * 
 */

package edu.wlu.graffiti.data.setup;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class is meant to extract the writing_style from the EAGLE inscriptions
 * and translate the Italian writing style into English.
 * 
 * @author sprenkle
 * 
 */
public class ExtractAndTranslateLanguage {

	private static final String UPDATE_ANNOTATION_STMT = "UPDATE agp_inscription_annotations "
			+ "SET lang_in_english = ? WHERE eagle_id=?";

	final static String newDBURL = "jdbc:postgresql://hopper.cs.wlu.edu/graffiti3";

	final static String SELECT_GRAFFITI = "select language, eagle_id from edr_inscriptions";

	static Connection newDBCon;

	private static PreparedStatement updateAnnotationStatement;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		init();

		try {

			// Extract the data from original DB and insert it into the new DB
			PreparedStatement extractData = newDBCon
					.prepareStatement(SELECT_GRAFFITI);

			ResultSet rs = extractData.executeQuery();

			while (rs.next()) {
				String language = rs.getString("language");
				String eagle_id = rs.getString("eagle_id");
				String translatedLanguage = language;

				System.out.println(eagle_id + ": " + language );

				// Writing Style is either
				// "litt. scariph" which should be translated to
				// "Graffito/scratched"
				// OR "cetera/carbone" which should be translated to "charcoal"

				// make this into a switch statement?
				if( language.equals( "graeca") ){
					translatedLanguage = "Greek";
				}
				else if (language.equals( "latina" ) ) {
					translatedLanguage = "Latin";
				}
				else if (language.equals( "latina-graeca" ) ) {
					translatedLanguage = "Latin/Greek";
				}
				else if (language.equals( "alia" ) ) {
					translatedLanguage = "other";
				} else {
					translatedLanguage = language;
				}

				updateAnnotation(eagle_id, translatedLanguage);

			}

			rs.close();
			extractData.close();
			updateAnnotationStatement.close();
			newDBCon.close();

		} catch (SQLException e1) {
			e1.printStackTrace();
		}

	}

	/**
	 * 
	 * 
	 * @param eagle_id
	 * @param translatedLang
	 */
	private static void updateAnnotation(String eagle_id, String translatedLang) {
		try {
			updateAnnotationStatement.setString(1, translatedLang);
			updateAnnotationStatement.setString(2, eagle_id);
			updateAnnotationStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void init() {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			newDBCon = DriverManager.getConnection(newDBURL, "web", "");

			updateAnnotationStatement = newDBCon
					.prepareStatement(UPDATE_ANNOTATION_STMT);

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
