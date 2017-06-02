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
 * Inserts the AGP Metadata from a CSV file into the database (agp_inscription_info). Won't update if
 * the entry (based on EDR id) already exists.
 * 
 * @author Sara Sprenkle
 *
 */
public class InsertAGPMetadata {

	private static final String DELIMITER = ";";
	
	// TODO: Update with all the metadata

	private static final String INSERT_ANNOTATION_STMT = "INSERT INTO agp_inscription_info "
			+ "(edr_id, description, translation, writing_style, property_id) "
			+ "VALUES (?,?,?,?,?)";

	final static String newDBURL = "jdbc:postgresql://hopper.cs.wlu.edu/graffiti2";

	static Connection newDBCon;

	public static void main(String[] args) {
		init();

		try {
			insertInscriptions("/Users/sprenkle/Desktop/agp_annotations.csv");

			newDBCon.close();

		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	private static void insertInscriptions(String datafileName) {
		try {
			PreparedStatement pstmt = newDBCon
					.prepareStatement(INSERT_ANNOTATION_STMT);

			BufferedReader br = new BufferedReader(new FileReader(datafileName));

			String line;

			while ((line = br.readLine()) != null) {
				// skip the comments in the CSV file
				if (line.startsWith("#")) {
					continue;
				}
				System.out.println(line);
				String[] data = line.split(DELIMITER);
				String edrID = data[0];
				String description = data[1];
				String translation = data[2];
				String writingStyle = data[3];
				String propertyIDStr = data[4];

				int propertyID = Integer.parseInt(propertyIDStr);

				pstmt.setString(1, edrID);
				pstmt.setString(2, description);
				pstmt.setString(3, translation);
				pstmt.setString(4, writingStyle);
				pstmt.setInt(5, propertyID);
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
