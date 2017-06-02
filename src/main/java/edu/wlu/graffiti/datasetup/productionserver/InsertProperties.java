package edu.wlu.graffiti.datasetup.productionserver;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.wlu.graffiti.bean.PropertyType;

/**
 * Insert properties from a CSV file into the database; Also insert property
 * type mappings.
 * 
 * @author Sara Sprenkle
 * 
 */
public class InsertProperties {

	final static String newDBURL = "jdbc:postgresql://hopper.cs.wlu.edu/graffiti_agp";

	private static final String DELIMITER = ";";

	private static final String INSERT_PROPERTY_STMT = "INSERT INTO properties "
			+ "(insula_id, property_number, property_name) " + "VALUES (?,?,?)";

	private static final String LOOKUP_INSULA_ID = "SELECT id from insula "
			+ "WHERE modern_city=? AND name=?";

	private static final String LOOKUP_PROP_ID = "SELECT id FROM properties "
			+ "WHERE insula_id=? AND property_number = ?";

	private static final String INSERT_PROPERTY_TYPE_MAPPING = "INSERT INTO propertyToPropertyType "
			+ "VALUES (?,?)";

	private static PreparedStatement selectInsulaStmt;
	private static PreparedStatement selectPropStmt;

	static Connection newDBCon;

	public static void main(String[] args) {
		init();

		try {
			insertProperties("data/pompeii_properties.csv");
			insertProperties("data/herculaneum_properties.csv");

			newDBCon.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	private static void insertProperties(String datafileName) {
		try {
			PreparedStatement pstmt = newDBCon
					.prepareStatement(INSERT_PROPERTY_STMT);

			PreparedStatement insertPTStmt = newDBCon
					.prepareStatement(INSERT_PROPERTY_TYPE_MAPPING);

			List<PropertyType> propertyTypes = getPropertyTypes();

			BufferedReader br = new BufferedReader(new FileReader(datafileName));

			String line;

			while ((line = br.readLine()) != null) {
				String[] data = line.split(DELIMITER);
				String modernCity = data[0].trim();
				String insula = data[1].trim();
				String propertyNumber = "";
				String propertyName = "";

				if (data.length > 2) {
					propertyNumber = data[2].trim();
				}
				if (data.length > 3) {
					propertyName = data[3].trim();
				}

				int insula_id = lookupInsulaId(modernCity, insula);

				pstmt.setInt(1, insula_id);
				pstmt.setString(2, propertyNumber);
				pstmt.setString(3, propertyName);
				try {
					pstmt.executeUpdate();
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("For propertyName: " + propertyName
							+ " propertyNum: " + propertyNumber
							+ " insula_id: " + insula_id);
				}

				int propID = locatePropertyId(insula_id, propertyNumber);

				// handle property tags
				if (data.length > 4) {
					String[] tagArray = data[4].trim().split(",");
					for (String t : tagArray) {
						t = t.trim();
						System.out.println("tag: " + t);
						for (PropertyType propType : propertyTypes) {
							System.out.println("propType: "
									+ propType.getName());
							if (propType.includes(t)) {
								System.out.println("It's a match!");
								insertPTStmt.setInt(1, propID);
								insertPTStmt.setInt(2, propType.getId());
								// Wrapped in try to handle the
								// "duplicate key errors" that so often occur.
								try {
									insertPTStmt.executeUpdate();
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
			br.close();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static int locatePropertyId(int insula_id, String propertyNumber) {
		int propID = 0;
		try {
			selectPropStmt.setInt(1, insula_id);
			selectPropStmt.setString(2, propertyNumber);

			ResultSet propRS = selectPropStmt.executeQuery();
			if (propRS.next()) {
				propID = propRS.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return propID;
	}

	public static int lookupInsulaId(String modernCity, String insula) {
		int insula_id = 0;
		try {
			selectInsulaStmt.setString(1, modernCity);
			selectInsulaStmt.setString(2, insula);

			ResultSet insulaSet = selectInsulaStmt.executeQuery();
			if (insulaSet.next()) {
				insula_id = insulaSet.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return insula_id;
	}

	private static List<PropertyType> getPropertyTypes() {
		List<PropertyType> propTypes = new ArrayList<PropertyType>();

		try {
			PreparedStatement pstmt = newDBCon
					.prepareStatement("SELECT id, name, description FROM propertyTypes");
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				int propTypeId = rs.getInt(1);
				PropertyType pt = new PropertyType();
				pt.setId(propTypeId);
				pt.setName(rs.getString(2));
				pt.setDescription(rs.getString(3));
				propTypes.add(pt);
			}

			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return propTypes;
	}

	private static void init() {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			newDBCon = DriverManager.getConnection(newDBURL, "web", "");
			selectInsulaStmt = newDBCon.prepareStatement(LOOKUP_INSULA_ID);
			selectPropStmt = newDBCon.prepareStatement(LOOKUP_PROP_ID);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
