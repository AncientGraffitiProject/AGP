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
public class ExtractAndTranslateWritingStyle {

	private static final String UPDATE_ANNOTATION_STMT = "UPDATE agp_inscription_annotations "
			+ "SET writing_style_in_english = ? WHERE eagle_id=?";

	final static String newDBURL = "jdbc:postgresql://hopper.cs.wlu.edu/graffiti3";

	final static String SELECT_GRAFFITI = "select writing_style, eagle_id from eagle_inscriptions";

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
				String writingStyle = rs.getString("writing_style");
				String eagle_id = rs.getString("eagle_id");
				String translatedWritingStyle = writingStyle;

				System.out.println(eagle_id + ": " + writingStyle );

				// Writing Style is either
				// "litt. scariph" which should be translated to
				// "Graffito/scratched"
				// OR "cetera/carbone" which should be translated to "charcoal"

				// make this into a switch statement?
				if( writingStyle.startsWith( "litt. scariph") ){
					translatedWritingStyle = "Graffito/incised";
				}
				else if (writingStyle.equals( "cetera/carbone" ) ) {
					translatedWritingStyle = "charcoal";
				}
				else if (writingStyle.equals( "carbone" ) ) {
					translatedWritingStyle = "charcoal";
				}
				else if (writingStyle.startsWith( "cetera, carbone" ) ) {
					translatedWritingStyle = "charcoal";
				}
				else if (writingStyle.startsWith( "cetera carbone" ) ) {
					translatedWritingStyle = "charcoal";
				}
				else if (writingStyle.startsWith( "cetera; lapide rubro" ) ) {
					translatedWritingStyle = "other; 'red rock'";
				}
				else if (writingStyle.startsWith( "cetera lapide rubro" ) ) {
					translatedWritingStyle = "other; 'red rock'";
				}
				else if (writingStyle.startsWith( "cetera; rubrica" ) ) {
					translatedWritingStyle = "other; 'red substance'";
				}
				else if (writingStyle.startsWith( "scalpro" ) ) {
					translatedWritingStyle = "chisel";
				} else {
					translatedWritingStyle = writingStyle;
				}

				updateAnnotation(eagle_id, translatedWritingStyle);

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
	 * @param translatedStyle
	 *            TODO
	 */
	private static void updateAnnotation(String eagle_id, String translatedStyle) {
		try {
			updateAnnotationStatement.setString(1, translatedStyle);
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
