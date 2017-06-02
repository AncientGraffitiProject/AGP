package edu.wlu.graffiti.data.setup;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.wlu.graffiti.bean.Property;

public class UpdatePropertyId {

	private static final String UPDATE_PROPERTY_ID = "UPDATE agp_inscription_annotations "
			+ "SET property_id = ? WHERE insula_find_spot = ? "
			+ "AND property_number_find_spot = ?";

	final static String newDBURL = "jdbc:postgresql://hopper.cs.wlu.edu/graffiti2";

	static Connection newDBCon;

	public static void main(String[] args) {
		init();

		try {
			updatePropertyIDs();

			newDBCon.close();

		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	private static List<Property> getProperties() {
		List<Property> props = new ArrayList<Property>();

		try {
			PreparedStatement pstmt = newDBCon
					.prepareStatement("SELECT id, insula_id, property_number FROM properties");
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				int propID = rs.getInt(1);
				Property pt = new Property();
				pt.setId(propID);
				//pt.setInsula(rs.getString(2));
				pt.setPropertyNumber(rs.getString(3));
				props.add(pt);
			}

			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return props;
	}

	private static void updatePropertyIDs() {
		List<Property> properties = getProperties();

		try {
			PreparedStatement pstmt = newDBCon
					.prepareStatement(UPDATE_PROPERTY_ID);
			// Iterate over the properties
			// Look at the find spot description to determine if it's one of the
			// property types
			for (Property p : properties) {
				pstmt.setInt(1, p.getId());
				//pstmt.setString(2, p.getInsula());
				pstmt.setString(3, p.getPropertyNumber());
				int num = pstmt.executeUpdate();
				System.out.println("Updated " + num + " entries");
			}
			pstmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
