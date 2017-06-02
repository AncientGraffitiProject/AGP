package edu.wlu.graffiti.data.setup;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * Class to update the measurements field that I had screwed up.
 * 
 * @author Sara Sprenkle
 *
 */
public class UpdateMeasurementsField {

	private static final String UPDATE_INSCRIPTION_STATEMENT = "UPDATE edr_inscriptions SET "
			+ "measurements=? WHERE edr_id = ?";
	private static final String SELECT_MEASUREMENTS = "SELECT edr_id, measurements FROM edr_inscriptions";

	final static String DB_URL = "jdbc:postgresql://hopper.cs.wlu.edu/graffiti5";

	static Connection dbCon;

	private static PreparedStatement updateMeasurements;
	private static PreparedStatement selectMeasurements;

	public static void main(String[] args) {

		init();
		try {
			updateMeasurements();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void updateMeasurements() throws SQLException {

		ResultSet rs = selectMeasurements.executeQuery();

		while (rs.next()) {
			String edrID = rs.getString("edr_id");
			String measurements = rs.getString("measurements");

			String fixedMeasurements = measurements.replace('\n', ' ');

			updateMeasurements.setString(1, fixedMeasurements);
			updateMeasurements.setString(2, edrID);
			
			updateMeasurements.execute();
			
		}
		rs.close();
		selectMeasurements.close();
		updateMeasurements.close();

	}

	private static void init() {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			dbCon = DriverManager.getConnection(DB_URL, "web", "");

			selectMeasurements = dbCon.prepareStatement(SELECT_MEASUREMENTS);
			updateMeasurements = dbCon.prepareStatement(UPDATE_INSCRIPTION_STATEMENT);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
