/**
 * 
 */

package edu.wlu.graffiti.datasetup.productionserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class is meant to extract the measurements from the EAGLE inscriptions
 * and put them in the agp_annotations table
 * 
 * @author sprenkle
 * 
 */
public class ExtractMeasurements {

	private static final String UPDATE_ANNOTATION_STMT = "UPDATE agp_inscription_annotations "
			+ "SET graffito_height = ?, graffito_length = ?, "
			+ "letter_height_min = ? , letter_height_max = ? "
			+ " WHERE eagle_id=?";

	final static String newDBURL = "jdbc:postgresql://hopper.cs.wlu.edu/graffiti2";

	final static String SELECT_GRAFFITI = "select measurements, eagle_id from eagle_inscriptions";

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
				String measurements = rs.getString("measurements");
				String eagle_id = rs.getString("eagle_id");
				
				if( measurements == null ) {
					continue;
				} else if ( ! measurements.contains("alt.:")) {
					continue;
				}

				System.out.println(measurements + " " + eagle_id);
				
				// parse measurements into its components
				// Example measurements: 
				// alt.: 0.00   lat.: 0.00   Crass./Diam.: 0.00   litt. alt.: 3,0-6,0
				
				String[] measurementsPieces = measurements.split("\\s+");
				
				String height = measurementsPieces[1];
				
				// the height probably isn't actually 0, just unknown
				if( height.equals("0.00")) {  
					height = "";
				}
				
				String width = measurementsPieces[3];
				
				if( width.equals("0.00")) {
					width = "";
				}
				
				String letterInfo = measurementsPieces[8];
				String minLetterHeight = "";
				String maxLetterHeight = "";
				
				if( ! letterInfo.equals("?")) {
					// change to English
					letterInfo = letterInfo.replace(',','.');
					// try to find the min and max
					if( letterInfo.contains("-")) {
						String[] minmax = letterInfo.split("-");
						minLetterHeight = minmax[0];
						maxLetterHeight = minmax[1];
					} else {
						minLetterHeight = maxLetterHeight = letterInfo;
					}
				}
				
				System.out.printf("%s %s %s %s\n", height, width, minLetterHeight, maxLetterHeight);
				

				updateAnnotation(eagle_id, height, width, minLetterHeight, maxLetterHeight);

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
	 */
	private static void updateAnnotation(String eagle_id,
			String graffitoHeight, String graffitoWidth,
			String minLetterHeight, String maxLetterHeight) {
		try {
			updateAnnotationStatement.setString(1, graffitoHeight);
			updateAnnotationStatement.setString(2, graffitoWidth);
			updateAnnotationStatement.setString(3, minLetterHeight);
			updateAnnotationStatement.setString(4, maxLetterHeight);
			updateAnnotationStatement.setString(5, eagle_id);
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
