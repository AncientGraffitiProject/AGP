package edu.wlu.graffiti.data.setup;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Inserts the inscription from a CSV file into the database (edr_inscriptions table). Won't update if
 * the entry (based on EDR id) already exists.
 * 
 * @author Sara Sprenkle
 *
 */
public class InsertEDRInscriptions {

	private static final String DELIMITER = ";";

	private static final String INSERT_INSCRIPTION_STATEMENT = "INSERT INTO edr_inscriptions "
			+ "(edr_id, ancient_city, find_spot, measurements, writing_style, \"language\", content, bibliography) "
			+ "VALUES (?,?,?,?,?,?,?,?)";

	final static String newDBURL = "jdbc:postgresql://hopper.cs.wlu.edu/graffiti2";

	static Connection newDBCon;

	public static void main(String[] args) {
		init();

		try {
			insertInscriptions("/Users/sprenkle/Desktop/eagle_inscriptions.csv");

			newDBCon.close();

		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	private static void insertInscriptions(String datafileName) {
		try {
			PreparedStatement pstmt = newDBCon
					.prepareStatement(INSERT_INSCRIPTION_STATEMENT);

			BufferedReader br = new BufferedReader(new FileReader(datafileName));

			String line;

			while ((line = br.readLine()) != null) {
				// skip the comments in the CSV file
				if( line.startsWith("#")) {
					continue;
				}
				System.out.println(line);
				String[] data = line.split(DELIMITER);
				String edrID = data[0];
				String ancientCity = data[1];
				String findSpot = data[2];
				String measurements = data[3];
				String writingStyle = data[4];
				String language = data[5];
				String content = data[6];
				String bibliography = data[7];


				pstmt.setString(1, edrID);
				pstmt.setString(2, ancientCity);
				pstmt.setString(3, findSpot);
				pstmt.setString(4, measurements);
				pstmt.setString(5, writingStyle);
				pstmt.setString(6, language);
				pstmt.setString(7, content);
				pstmt.setString(8, bibliography);
				try {
					pstmt.executeUpdate();
				} catch (SQLException e) {
					e.printStackTrace();
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
