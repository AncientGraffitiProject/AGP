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

public class UpdateLocation {

	private static final String UPDATE_ANNOTATION_STMT = "UPDATE agp_inscription_info "
			+ "SET property_id = ? WHERE edr_id = ? ";

	final static String newDBURL = "jdbc:postgresql://hopper.cs.wlu.edu/graffiti5";

	static Connection newDBCon;

	public static void main(String[] args) {
		init();

		try {
			updateInscriptions("/Users/sprenkle/Desktop/tags.csv");

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

				if (edrID.equals(""))
					continue;

				String location = Utils.cleanData(record.get(4));
				System.out.println(location);
				int property_id = -1;

				switch (location) {
				case "Suburban Baths":
					property_id = 277;
					break;
				case "Rampa":
					property_id = 276;
					break;
				case "Castellum": // Water Tower
					property_id = 273;
					break;
				}

				if (property_id != -1) {

					pstmt.setInt(1, property_id);
					pstmt.setString(2, edrID);
					try {
						System.out.println(edrID + " " + location);
						System.out.println(pstmt.executeUpdate());
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}

			pstmt.close();

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
