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

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import edu.wlu.graffiti.dao.FindspotDao;

/**
 * Updating the location of the
 * 
 * @author Sara Sprenkle
 *
 */
public class UpdateGraffitiLocations {
	private static final int LOCATION_OF_EDR_ID = 0;

	private static final int LOCATION_OF_CITY = 4;

	private static final int LOCATION_OF_INSULA = 6;

	private static final int LOCATION_OF_MAIN_ENTRANCE = 7;

	final static String newDBURL = "jdbc:postgresql://hopper.cs.wlu.edu/graffiti5";

	private static final String UPDATE_PROPERTY = "UPDATE agp_inscription_info SET property_id = ? WHERE edr_id=?";

	private static final String LOOKUP_PROPERTY = FindspotDao.SELECT_BY_CITY_AND_INSULA_AND_PROPERTY_STATEMENT;

	static Connection newDBCon;

	public static void main(String[] args) {
		init();

		try {
			updateProperty("/Users/sprenkle/Desktop/descriptions.csv");

			newDBCon.close();

		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	private static void updateProperty(String datafileName) {
		PreparedStatement propertyInfo = null;
		PreparedStatement updateProperty = null;

		Reader in = null;
		Iterable<CSVRecord> records;
		try {
			in = new FileReader(datafileName);
			records = CSVFormat.EXCEL.parse(in);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}

		try {
			propertyInfo = newDBCon.prepareStatement(LOOKUP_PROPERTY);
			updateProperty = newDBCon.prepareStatement(UPDATE_PROPERTY);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (CSVRecord record : records) {
			String edrID = Utils.cleanData(record.get(LOCATION_OF_EDR_ID));
			String city = Utils.cleanData(record.get(LOCATION_OF_CITY));
			String insula = Utils.cleanData(record.get(LOCATION_OF_INSULA));
			String door = Utils.cleanData(record.get(LOCATION_OF_MAIN_ENTRANCE));
			int propertyID;

			try {
				propertyInfo.setString(1, city);
				propertyInfo.setString(2, insula);
				propertyInfo.setString(3, door);
				
				ResultSet rs = propertyInfo.executeQuery();
				
				if( rs.next() ) {
					propertyID = rs.getInt("id");
					
				} else {
					System.out.println("something went wrong ... no property for");
					System.out.println(city + " " + insula + " " + door + " " + edrID);
					continue;
				}
				
				//check by 
				
				updateProperty.setInt(1, propertyID);
				updateProperty.setString(2, edrID);
				
				//updateProperty.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		try {
			propertyInfo.close();
			updateProperty.close();
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
			newDBCon = DriverManager.getConnection(newDBURL, "web", "");
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
