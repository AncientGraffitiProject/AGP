/**
 * 
 */
package edu.wlu.graffiti.data.setup;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * @author sprenkle
 * 
 */
public class CopyGraffiti {

	final static String newDBURL = "jdbc:postgresql://hopper.cs.wlu.edu/graffiti3";
	final static String origDBURL = "jdbc:postgresql://hopper.cs.wlu.edu/graffiti2";

	final static String SELECT_EAGLE = "SELECT * FROM eagle_inscriptions";
	final static String SELECT_ANNOTATIONS = "SELECT * FROM agp_inscription_annotations";
	final static String SELECT_PROPERTIES = "SELECT properties.id as myid, property_number, property_name, insula FROM properties, insula where properties.insula_id = insula.id";
	final static String SELECT_PROP_BY_NAME = "SELECT properties.id FROM properties, insula where property_number = ? and property_name = ? "
			+ " and properties.insula_id = insula.id and insula.name = ?";

	private static final String INSERT_ANNOTATION_STMT = "INSERT INTO agp_inscription_annotations "
			+ "(eagle_id, floor_to_graffito_height, description, comment, translation, writing_style,"
			+ "height_from_ground, graffito_height, graffito_length, letter_height_min, letter_height_max, property_id) "
			+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

	private static PreparedStatement selectAnnoStmt;
	private static PreparedStatement insertAnnoEntry;

	static Connection newDBCon;
	static Connection origDBCon;

	private static HashMap<Integer, Integer> origToNewPropIds = new HashMap<Integer, Integer>();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		init();
		try {
			setUpPropertiesMapping();
			insertAGPMetadata();

			newDBCon.close();
			origDBCon.close();

		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	private static void setUpPropertiesMapping() throws SQLException {
		PreparedStatement origPropStmt = origDBCon
				.prepareStatement(SELECT_PROPERTIES);
		PreparedStatement newPropStmt = newDBCon
				.prepareStatement(SELECT_PROP_BY_NAME);

		ResultSet origProperties = origPropStmt.executeQuery();

		int newPropID = 0;

		while (origProperties.next()) {
			String propName = origProperties.getString("property_name");
			String propNum = origProperties.getString("property_number");
			String insulaName = origProperties.getString("insula");
			int propId = origProperties.getInt("myid");

			newPropStmt.setString(1, propNum);
			newPropStmt.setString(2, propName);
			newPropStmt.setString(3, insulaName);

			ResultSet newPropRS = newPropStmt.executeQuery();
			if (newPropRS.next()) {
				newPropID = newPropRS.getInt(1);
				origToNewPropIds.put(propId, newPropID);
			} else {
				System.out.println("Uh oh!");
				System.out.println(propName + " " + propNum + " " + insulaName
						+ "*");
			}
			newPropRS.close();
		}

		origProperties.close();
		origPropStmt.close();
		newPropStmt.close();
	}

	private static void insertAGPMetadata() throws SQLException {
		selectAnnoStmt = origDBCon.prepareStatement(SELECT_ANNOTATIONS);
		
		insertAnnoEntry = newDBCon.prepareStatement(INSERT_ANNOTATION_STMT);

		ResultSet annotations = selectAnnoStmt.executeQuery();

		while (annotations.next()) {
			// get info out of DB
			String eagleId = annotations.getString(2);
			String floorHeight = annotations.getString(3);
			String description = annotations.getString(4);
			String comment = annotations.getString(5);
			String translation = annotations.getString(6);
			String writingStyle = annotations.getString(7);
			String heightGround = annotations.getString(8);
			String height = annotations.getString(9);
			String length = annotations.getString(10);
			String letterHeightMin = annotations.getString(11);
			String letterHeightMax = annotations.getString(12);
			int propID = annotations.getInt(13);

			// put into new DB
			insertAnnoEntry.setString(1, eagleId);
			insertAnnoEntry.setString(2, floorHeight);
			insertAnnoEntry.setString(3, description);
			insertAnnoEntry.setString(4, comment);
			insertAnnoEntry.setString(5, translation);
			insertAnnoEntry.setString(6, writingStyle);
			insertAnnoEntry.setString(7, heightGround);
			insertAnnoEntry.setString(8, height);
			insertAnnoEntry.setString(9, length);
			insertAnnoEntry.setString(10, letterHeightMin);
			insertAnnoEntry.setString(11, letterHeightMax);
			insertAnnoEntry.setInt(12, origToNewPropIds.get(propID));
			
			
			int success = insertAnnoEntry.executeUpdate();
			if( success != 1 ) {
				System.out.println("Oops! on " + eagleId);
			}
		}
		
		annotations.close();
		insertAnnoEntry.close();
		selectAnnoStmt.close();

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
			selectAnnoStmt = origDBCon.prepareStatement(SELECT_ANNOTATIONS);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
