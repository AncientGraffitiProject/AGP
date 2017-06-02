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
 * This class is meant to extract the find spots from EAGLE and break them down
 * into the pieces we need for the annotations table.
 * 
 * @author sprenkle
 * 
 */
public class ExtractFindSpotAnnotations {

	private static final String UPDATE_ANNOTATION_STMT = "UPDATE agp_inscription_annotations "
			+ "SET modern_city_find_spot = ?, property_name_find_spot = ?,"
			+ "insula_find_spot = ?, property_number_find_spot = ? "
			+ "WHERE eagle_id=?";

	final static String newDBURL = "jdbc:postgresql://hopper.cs.wlu.edu/graffiti_agp";

	final static String SELECT_GRAFFITI = "select find_spot, eagle_id from eagle_inscriptions";

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
				String find_spot = rs.getString("find_spot");
				String eagle_id = rs.getString("eagle_id");

				System.out.println(find_spot + " " + eagle_id);

				// Format of find_spot is
				// Pompei (Napoli), Casa dei Quattro Stili (I.8.17)
				String[] dataPieces = find_spot.split(",");
				String modernCity = dataPieces[0];
				find_spot = dataPieces[1];
				find_spot = find_spot.trim();

				dataPieces = find_spot.split("\\(");

				String propertyName = dataPieces[0].trim();

				// remove the last character, the ), from the address
				find_spot = dataPieces[1].substring(0,
						dataPieces[1].length() - 1);

				String[] address = find_spot.split("\\.");
				String insula;
				int propertyNumber;

				// Handle Pompei vs Herculaneum addresses
				if (modernCity.startsWith("Pompei")) {
					insula = address[0] + "." + address[1];
					propertyNumber = Integer.parseInt(address[2]);
				} else {
					insula = address[0];
					propertyNumber = Integer.parseInt(address[1]);
				}

				updateAnnotation(eagle_id, modernCity, propertyName, insula,
						propertyNumber);

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
	 * Insert a blank annotation for the graffito
	 * 
	 * @param eagle_id
	 * @param modernCity
	 *            TODO
	 * @param propertyName
	 *            TODO
	 * @param insula
	 *            TODO
	 * @param propertyNumber
	 *            TODO
	 */
	private static void updateAnnotation(String eagle_id, String modernCity,
			String propertyName, String insula, int propertyNumber) {
		try {
			updateAnnotationStatement.setString(1, modernCity);
			updateAnnotationStatement.setString(2, propertyName);
			updateAnnotationStatement.setString(3, insula);
			updateAnnotationStatement.setInt(4, propertyNumber);
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
			newDBCon = DriverManager.getConnection(newDBURL, "sprenkle", "");

			updateAnnotationStatement = newDBCon
					.prepareStatement(UPDATE_ANNOTATION_STMT);

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
