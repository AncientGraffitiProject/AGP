package edu.wlu.graffiti.data.setup;

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

/**
 * Imports data from the translation_graffiti.csv file into the database.
 * 
 * @author Trevor Stalnaker
 * 
 */

public class InsertTranslations {
	
	private static final String SET_TRANSLATION_GREATEST_HIT = "UPDATE agp_inscription_info SET is_greatest_hit_translation=true WHERE edr_id=?";

	private static String DB_DRIVER;
	private static String DB_URL;
	private static String DB_USER;
	private static String DB_PASSWORD;
	static Connection newDBCon;

	public static void main(String[] args) {
		insertTranslationGraffiti();
	}

	public static void insertTranslationGraffiti() {
		
		init();

		try {
			
			//Create Prepared Statements
			PreparedStatement pstmt = newDBCon.prepareStatement(SET_TRANSLATION_GREATEST_HIT);

			//Read in the data from the CSV File
			Reader in = new FileReader("data/AGPData/translation_graffiti.csv");
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			for (CSVRecord record : records) {
				//Clean data in CSV File and save to Strings
				String edrId = Utils.cleanData(record.get(0));
				pstmt.setString(1, edrId);
				pstmt.executeUpdate();
			}
			//Close statements, connections, and file readers
			pstmt.close();
			in.close();
			newDBCon.close();
			
		} catch (IOException | SQLException e) {
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
