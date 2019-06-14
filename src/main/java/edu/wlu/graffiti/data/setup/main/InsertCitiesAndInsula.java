package edu.wlu.graffiti.data.setup.main;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import edu.wlu.graffiti.data.setup.Utils;

public class InsertCitiesAndInsula {

	private static final String INSERT_PROPERTY_STMT = "INSERT INTO insula " + "(modern_city, short_name, full_name) VALUES (?,?,?)";
	private static final String INSERT_CITY_STMT = "INSERT INTO cities " + "(name, pleiades_id) VALUES (?,?)";

	private static Connection newDBCon;

	private static String DB_DRIVER;
	private static String DB_URL;
	private static String DB_USER;
	private static String DB_PASSWORD;

	public static void main(String[] args) {
		init();

		try {
			insertCities("data/cities.csv");
			insertProperties("data/herculaneum_insulae.csv");
			insertProperties("data/pompeii_insulae.csv");
			newDBCon.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	private static void insertCities(String datafileName) {
		try {
			PreparedStatement pstmt = newDBCon.prepareStatement(INSERT_CITY_STMT);

			Reader in = new FileReader(datafileName);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			for (CSVRecord record : records) {
				String modernCity = Utils.cleanData(record.get(0));
				String pleiadesID = Utils.cleanData(record.get(1));
			
				pstmt.setString(1, modernCity);
				pstmt.setString(2, pleiadesID);
				try {
					System.out.println(modernCity + " " + pleiadesID);
					pstmt.executeUpdate();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			in.close();
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

	private static void insertProperties(String datafileName) {
		try {
			PreparedStatement pstmt = newDBCon.prepareStatement(INSERT_PROPERTY_STMT);

			Reader in = new FileReader(datafileName);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			for (CSVRecord record : records) {
				String modernCity = Utils.cleanData(record.get(0));
				String insula = Utils.cleanData(record.get(1));
				String fullname = Utils.cleanData(record.get(2));
				//String pleaides_id = Utils.cleanData(record.get(3));
			
				pstmt.setString(1, modernCity);
				pstmt.setString(2, insula);
				pstmt.setString(3, fullname);
				//pstmt.setString(4, pleaides_id);
				try {
					System.out.println(modernCity + " " + insula);
					pstmt.executeUpdate();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			in.close();
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
