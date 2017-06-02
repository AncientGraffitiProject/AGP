package edu.wlu.graffiti.data.setup;

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

public class UpdateSummaryTranslationCommentary {

	private static final String CSV_LOCATION = "/home/sprenkle/translations.csv";

	private static final String UPDATE_ANNOTATION_STMT = "UPDATE agp_inscription_info "
			+ "SET summary = ?, content_translation = ?, comment = ?, langner = ? WHERE edr_id = ? ";

	static Connection newDBCon;
	
	private static String DB_DRIVER;

	private static String DB_URL;

	private static String DB_USER;

	private static String DB_PASSWORD;

	public static void main(String[] args) {
		init();

		try {
			updateInscriptions(CSV_LOCATION);

			newDBCon.close();

		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	private static void updateInscriptions(String datafileName) {
		try {
			PreparedStatement pstmt = newDBCon.prepareStatement(UPDATE_ANNOTATION_STMT);

			Reader in = null;
			Iterable<CSVRecord> records;
			try {
				in = new FileReader(datafileName);
				records = CSVFormat.EXCEL.parse(in);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
				return;
			} catch (IOException e1) {
				e1.printStackTrace();
				return;
			}

			for (CSVRecord record : records) {
				String edrID = Utils.cleanData(record.get(0));
				
				if( edrID.equals(""))
					continue;
				
				String langner = Utils.cleanData(record.get(3));
				String summary = Utils.cleanData(record.get(6));
				String translation = Utils.cleanData(record.get(7));
				String commentary = Utils.cleanData(record.get(8));
				System.out.println(edrID + ":" + summary);
				

				pstmt.setString(1, summary);
				pstmt.setString(2, translation);
				pstmt.setString(3, commentary);
				pstmt.setString(4, langner);
				pstmt.setString(5, edrID);
				try {
					System.out.println(pstmt.executeUpdate());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			pstmt.close();

		} catch (SQLException e) {
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
