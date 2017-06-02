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
 * Update the data in our data from EDR
 * 
 * @author Sara Sprenkle
 *
 */
public class ImportSubsetOfEDRData {

	final static String DB_URL = "jdbc:postgresql://hopper.cs.wlu.edu/graffiti5";

	private static final String UPDATE_INSCRIPTION_STATEMENT = "UPDATE edr_inscriptions SET "
			+ "find_spot=? WHERE edr_id = ?";

	private static final String UPDATE_CONTENT = "UPDATE edr_inscriptions SET " + "content = ? WHERE edr_id = ?";

	static Connection dbCon;

	private static PreparedStatement updatePStmt;
	private static PreparedStatement updateContentStmt;

	public static void main(String[] args) {
		init();

		try {
			updateInscriptions("data/EDRData/epigr.csv");
			updateContent("data/EDRData/testo_epigr.csv");
			dbCon.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	private static void updateContent(String contentFileName) {

		try {
			Reader in = new FileReader(contentFileName);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			for (CSVRecord record : records) {
				String edrID = Utils.cleanData(record.get(0));
				String content = Utils.cleanData(record.get(1));

				try {
					content = cleanContent(content);

					updateContentStmt.setString(1, content);
					updateContentStmt.setString(2, edrID);

					int updated = updateContentStmt.executeUpdate();
					if (updated != 1) {
						System.err.println("\nSomething went wrong with " + edrID);
						System.err.println(content);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Replaces the <br>
	 * tags with newlines. We handle the line breaks in our code.
	 * 
	 * @param content
	 * @return
	 */
	private static String cleanContent(String content) {
		return content.replace("<br>", "\n");
	}

	private static void updateInscriptions(String datafileName) {
		try {

			Reader in = new FileReader(datafileName);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			for (CSVRecord record : records) {
				String eagleID = Utils.cleanData(record.get(0));
				String findSpot = Utils.cleanData(record.get(6));

				System.out.println(eagleID);

				updateEDRInscription(eagleID, findSpot);

			}

			in.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void updateEDRInscription(String eagleID, String findSpot) throws SQLException {
		updatePStmt.setString(1, findSpot);
		updatePStmt.setString(2, eagleID);

		try {
			updatePStmt.executeUpdate();
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
			dbCon = DriverManager.getConnection(DB_URL, "web", "");

			updatePStmt = dbCon.prepareStatement(UPDATE_INSCRIPTION_STATEMENT);
			updateContentStmt = dbCon.prepareStatement(UPDATE_CONTENT);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
