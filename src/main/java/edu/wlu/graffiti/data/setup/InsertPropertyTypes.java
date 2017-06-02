package edu.wlu.graffiti.data.setup;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Creates the table of property types, based on the data/propertyTypes.prop
 * file
 * 
 * @author Sara Sprenkle
 *
 */
public class InsertPropertyTypes {

	private static final String INSERT_PROPERTY_TYPE = "INSERT INTO propertyTypes "
			+ "(name, description) VALUES (?,?)";

	final static String newDBURL = "jdbc:postgresql://hopper.cs.wlu.edu/graffiti3";

	static Connection newDBCon;

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
			Properties prop = new Properties();

			prop.load(new FileReader("data/propertyTypes.prop"));
			Enumeration<Object> propKeys = prop.keys();

			while (propKeys.hasMoreElements()) {

				Object propType = propKeys.nextElement();
				Object propValue = prop.get(propType);
				System.out.println(propType + ": " + propValue);

				pstmt.setString(1, (String) propType);
				pstmt.setString(2, (String) propValue);
				try {
					pstmt.executeUpdate();
				} catch (SQLException e) {
					// handles duplicate key issue
					e.printStackTrace();
				}
			}
			pstmt.close();
		} catch (IOException | SQLException e) {
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
