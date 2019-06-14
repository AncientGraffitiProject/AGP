package edu.wlu.graffiti.data.setup;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * This class looks at the apparatus field and adds links to EDR for entries, as
 * appropriate.
 * 
 * Can be called as stand-alone script or from other classes.
 * 
 * @author Sara Sprenkle
 * 
 */

public class AddEDRLinksToApparatus {

	final static String SELECT_GRAFFITI = "SELECT apparatus, edr_id from edr_inscriptions";

	private static final String UPDATE_APPARATUS_TO_DISPLAY = "UPDATE edr_inscriptions SET apparatus_displayed = ? WHERE edr_id = ?";

	private static final String URL_BASE = "http://ancientgraffiti.org/Graffiti/graffito/AGP-";

	private static Connection dbCon;

	private static PreparedStatement updateApparatusStmt;

	private static String DB_DRIVER;
	private static String DB_URL;
	private static String DB_USER;
	private static String DB_PASSWORD;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		addEDRLinksToApparatus();
	}

	public static void addEDRLinksToApparatus() {
		init();

		try {
			// Get list of edrIDs
			PreparedStatement extractData = dbCon.prepareStatement(SELECT_GRAFFITI);
			ResultSet rs = extractData.executeQuery();

			// Update apparatus for each entry where EDR entries are in the
			// apparatus.
			while (rs.next()) {
				String edrid = rs.getString("edr_id");
				String apparatus = rs.getString("apparatus");
				//System.out.println("Updating apparatus for " + edrid);
				String displayApparatus = addLinks(apparatus);
				updateDisplayApparatus(edrid, displayApparatus);
			}
			rs.close();
			extractData.close();
			updateApparatusStmt.close();
			dbCon.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Helper method to modify the apparatus to include links
	 * 
	 * @param apparatus
	 * @return the apparatus so that it contains links to the other entries
	 *         referenced
	 */
	private static String addLinks(String apparatus) {
		if (apparatus.contains("EDR")) {
			String[] components = apparatus.split("\\s");
			StringBuilder displayApparatus = new StringBuilder();
			for (String component : components) {
				if (component.startsWith("EDR")) {
					// know the EDR id is 9 characters
					String edrid = "";
					if( component.length() >= 9 ) {
						edrid = component.substring(0, 9);
					} else {
						edrid = component;
						System.out.println("This edrID is too short to update apparatus: " + component);
					}
					displayApparatus.append(" <a href=\"");
					displayApparatus.append(URL_BASE);
					// clean up the component -- remove punctuation
					displayApparatus.append(edrid);
					displayApparatus.append("\" title=\"See Details\">");
					displayApparatus.append(component);
					displayApparatus.append("</a> ");
				} else {
					displayApparatus.append(component + " ");
				}
			}
			return displayApparatus.toString();
		} else {
			return apparatus;
		}
	}

	/**
	 * @param edr_id
	 * @param apparatusDisplay
	 */
	private static void updateDisplayApparatus(String edr_id, String apparatusDisplay) {
		try {
			updateApparatusStmt.setString(1, apparatusDisplay);
			updateApparatusStmt.setString(2, edr_id);
			updateApparatusStmt.executeUpdate();
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
			updateApparatusStmt = dbCon.prepareStatement(UPDATE_APPARATUS_TO_DISPLAY);
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