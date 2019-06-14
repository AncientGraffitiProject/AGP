package edu.wlu.graffiti.data.setup;

import java.io.FileNotFoundException;
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

import edu.wlu.graffiti.data.setup.main.ImportEDRData;

/**
 * This class updates findspots for graffiti whose findspots aren't the typical
 * addresses.
 * 
 * Can be called as stand-alone script or from other classes.
 * 
 * @author Sara Sprenkle
 * 
 */
public class HandleFindspotsWithoutAddresses {

	private static Connection dbCon;
	public static String LOCATION_FILE_NAME = "data/AGPData/atypical_findspots.csv";

	private static final String SELECT_PROPERTY = "select id from properties where property_name = ?";

	private static PreparedStatement updatePropertyStmt;
	private static PreparedStatement selectPropertyStmt;

	private static String DB_DRIVER;
	private static String DB_URL;
	private static String DB_USER;
	private static String DB_PASSWORD;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		updateGraffitiLocations(LOCATION_FILE_NAME);
	}

	public static void updateGraffitiLocations(String locationFileName) {
		init();
		
		try {
			updatePropertyStmt = dbCon.prepareStatement(ImportEDRData.UPDATE_PROPERTY);
			selectPropertyStmt = dbCon.prepareStatement(SELECT_PROPERTY);
			
			Reader in = new FileReader(locationFileName);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			for (CSVRecord record : records) {
				String edrID = Utils.cleanData(record.get(0));
				String findspot = Utils.cleanData(record.get(1));
				
				selectPropertyStmt.setString(1, findspot);
				
				ResultSet rs = selectPropertyStmt.executeQuery();
				int propertyId = 0;

				if( rs.next() ) {
					propertyId = rs.getInt(1);
				} else {
					System.out.println("Error looking up " + findspot);
					continue;
				}
				
				updatePropertyStmt.setString(2, edrID);
				updatePropertyStmt.setInt(1, propertyId);
				
				updatePropertyStmt.executeUpdate();
			}
			updatePropertyStmt.close();
			dbCon.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
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