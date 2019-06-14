package edu.wlu.graffiti.data.setup.main;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

/**
 * Creates the table of property types, based on the data/propertyTypes.prop
 * file
 * 
 * @author Sara Sprenkle
 *
 */
public class InsertPropertyTypes {

	private static final String INSERT_PROPERTY_TYPE = "INSERT INTO propertyTypes "
			+ "(name, commentary, parent_id, is_parent) VALUES (?,?,?,?)";

	static Connection newDBCon;
	private static String DB_DRIVER;
	private static String DB_URL;
	private static String DB_USER;
	private static String DB_PASSWORD;
	
	private static final String LOOKUP_PROPTYPE_ID = "SELECT id FROM propertytypes WHERE name=?";
	
	private static PreparedStatement selectPropTypeStmt;

	public static void main(String[] args) {
		init();

		try {
			insertPropertyTypes();

			newDBCon.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	private static void insertPropertyTypes() {
		try {
			PreparedStatement pstmt = newDBCon
					.prepareStatement(INSERT_PROPERTY_TYPE);
			selectPropTypeStmt = newDBCon.prepareStatement(LOOKUP_PROPTYPE_ID);

			Reader in = new FileReader("data/property_types.csv");
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			
			//loop through parent property types
			for (CSVRecord record : records) {
				String parentName = Utils.cleanData(record.get(0));
				String[] subcategories = Utils.cleanData(record.get(1)).split(",");
				String commentary = Utils.cleanData(record.get(2));
				int parent_id = 0;
				boolean is_parent = true;
				
				
				pstmt.setString(1, parentName);
				pstmt.setString(2, commentary);
				pstmt.setInt(3,  parent_id);
				pstmt.setBoolean(4,  is_parent);
				
				try {
					System.out.println(parentName + " " + parent_id);
					pstmt.executeUpdate();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				//loop through children of this parent property type
				commentary = "";
				is_parent = false;
				for(String sub : subcategories) {
					//if parent has children in subcategory portion of csv
					if (!sub.equals("")) {
						parent_id = locatePropertyTypeId(parentName);
						pstmt.setString(1, sub);
						pstmt.setString(2, commentary);
						pstmt.setInt(3,  parent_id);
						pstmt.setBoolean(4,  is_parent);
						try {
							System.out.println(sub + " " + parent_id);
							pstmt.executeUpdate();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
			}

			in.close();
			pstmt.close();
		} catch (IOException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private static int locatePropertyTypeId(String name) {
		int propID = 0;
		try {
			selectPropTypeStmt.setString(1, name);

			ResultSet propRS = selectPropTypeStmt.executeQuery();
			if (propRS.next()) {
				propID = propRS.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return propID;
	}

	private static void init() {
		getConfigurationProperties();

		try {
			Class.forName(DB_DRIVER);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			newDBCon = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
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
