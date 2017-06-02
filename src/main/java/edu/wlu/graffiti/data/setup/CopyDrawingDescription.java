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
 * This class is meant to ease copying the description of images from graffiti
 * to appropriate column in graffiti2
 * 
 * @author sprenkle
 * 
 */
public class CopyDrawingDescription {

	final static String origDBURL = "jdbc:postgresql://hopper.cs.wlu.edu/graffiti";
	final static String newDBURL = "jdbc:postgresql://hopper.cs.wlu.edu/graffiti2";

	static Connection origDBCon;
	static Connection newDBCon;

	static PreparedStatement blankContentData;
	private static PreparedStatement updateDescriptionStmt;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		init();

		try {

			// Extract the data from original DB and insert it into the new DB
			PreparedStatement extractData = origDBCon
					.prepareStatement("SELECT content, eagle_id from inscriptions WHERE content LIKE '((:%))'");

			ResultSet rs = extractData.executeQuery();

			while (rs.next()) {
				String content = rs.getString(1);
				String eagle_id = rs.getString(2);

				// set content in graffiti2 db to ''
				deleteContent(eagle_id);

				// set description in graffiti2 db to content, without the ((:
				// ... ))

				content = content.replaceFirst("\\(\\(:", "");
				content = content.replaceFirst("\\)\\)", "");

				System.out.print("Inserting eagle_id with description: ");
				System.out.println(eagle_id + " " + content);
				updateDescription(eagle_id, content);

			}

			rs.close();
			extractData.close();
			blankContentData.close();
			updateDescriptionStmt.close();
			newDBCon.close();
			origDBCon.close();

		} catch (SQLException e1) {
			e1.printStackTrace();
		}

	}

	/**
	 * Update the description of the graffito with the description
	 * 
	 * @param eagle_id
	 * @param description
	 */
	private static void updateDescription(String eagle_id, String description) {
		// MAY need to change this from an INSERT to an update
		try {
			updateDescriptionStmt.setString(2, eagle_id);
			updateDescriptionStmt.setString(1, description);

			updateDescriptionStmt.executeUpdate();
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
			origDBCon = DriverManager.getConnection(origDBURL, "web", "");
			newDBCon = DriverManager.getConnection(newDBURL, "web", "");

			blankContentData = newDBCon
					.prepareStatement("UPDATE eagle_inscriptions SET content='' WHERE eagle_id = ?");

			//updateDescriptionStmt = newDBCon
			//		.prepareStatement("INSERT INTO agp_inscription_annotations (eagle_id, description) VALUES (?, ? )");

			updateDescriptionStmt = newDBCon
					.prepareStatement("UPDATE agp_inscription_annotations SET description = ? WHERE eagle_id = ?");

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Blanks the content of the inscription with this eagle_id
	 * 
	 * @param eagle_id
	 * @param dbCon
	 */
	private static void deleteContent(String eagle_id) {

		try {
			blankContentData.setString(1, eagle_id);
			blankContentData.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
