package edu.wlu.graffiti.data.setup;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

/**
 * Inserts Description and Translations of figural graffiti from spreadsheet into database
 * 
 * @author Sara Sprenkle
 *
 */
public class InsertDrawingDescriptionAndTranslations {
	private static final int LOCATION_OF_LATIN_DESCRIPTION = 10;

	private static final int LOCATION_OF_ENGLISH_DESCRIPTION = 11;

	final static String newDBURL = "jdbc:postgresql://hopper.cs.wlu.edu/graffiti5";

	private static final String UPDATE_DESCRIPTION = "UPDATE figural_graffiti_info SET description_in_english=? WHERE edr_id=?";
	private static final String UPDATE_DESCRIPTION_TRANSLATION = "UPDATE figural_graffiti_info SET description_in_latin=? WHERE edr_id=?";

	static Connection newDBCon;

	public static void main(String[] args) {
		init();

		try {
			updateTranslations("/Users/sprenkle/Desktop/descriptions.csv");

			newDBCon.close();

		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	private static void updateTranslations(String datafileName) {
		PreparedStatement descriptionUpdate = null;
		PreparedStatement translationStmt = null;

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
			descriptionUpdate = newDBCon.prepareStatement(UPDATE_DESCRIPTION);
			translationStmt = newDBCon.prepareStatement(UPDATE_DESCRIPTION_TRANSLATION);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (CSVRecord record : records) {
			String edrID = Utils.cleanData(record.get(0));
			String description_in_english = Utils.cleanData(record.get(LOCATION_OF_ENGLISH_DESCRIPTION));
			String description_in_latin = Utils.cleanData(record.get(LOCATION_OF_LATIN_DESCRIPTION));
			
			try {
				descriptionUpdate.setString(2, edrID);
				translationStmt.setString(2, edrID);
				
				descriptionUpdate.setString(1, description_in_english);
				translationStmt.setString(1, description_in_latin);
				
				descriptionUpdate.executeUpdate();
				translationStmt.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		try {
			descriptionUpdate.close();
			translationStmt.close();
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
