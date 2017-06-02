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

/**
 * Iterates over the properties, looking to see if the property name is one of
 * the defined types. If so, adds a mapping from the property to the property
 * type.
 * 
 * @author Sara Sprenkle
 *
 */
public class UpdateGreatestHitsInfo {
	final static String newDBURL = "jdbc:postgresql://hopper.cs.wlu.edu/graffiti5";

	public static final String UPDATE_AGP = "UPDATE agp_inscription_info SET is_greatest_hit_translation = ?, is_greatest_hit_figural=? WHERE edr_id = ?";
	public static final String UPDATE_GH_INFO = "UPDATE greatest_hits_info SET commentary = ?, preferred_image = ? WHERE edr_id=?";
	public static final String INSERT_GH_INFO = "INSERT INTO greatest_hits_info VALUES (?, ?, ?)";
	public static final String SELECT_GH_INFO = "SELECT * from greatest_hits_info where edr_id = ?";

	static Connection newDBCon;

	public static void main(String[] args) {
		init();

		try {
			updateGreatestHits("/Users/sprenkle/Desktop/tags.csv");

			newDBCon.close();

		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	private static void updateGreatestHits(String datafileName) {
		PreparedStatement agpInfoUpdate = null;
		PreparedStatement greatestHitsUpdate = null;
		PreparedStatement greatestHitsInsert = null;
		PreparedStatement selectGH = null;

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

		try {
			agpInfoUpdate = newDBCon.prepareStatement(UPDATE_AGP);
			selectGH = newDBCon.prepareStatement(SELECT_GH_INFO);
			greatestHitsInsert = newDBCon.prepareStatement(INSERT_GH_INFO);
			greatestHitsUpdate = newDBCon.prepareStatement(UPDATE_GH_INFO);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (CSVRecord record : records) {
			String edrID = Utils.cleanData(record.get(0));
			String isTextualGraffiti = Utils.cleanData(record.get(11));
			String isFiguralGraffiti = Utils.cleanData(record.get(13));
			String ghCommentary = Utils.cleanData(record.get(12));
			String preferredImage = Utils.cleanData(record.get(14));

			boolean isFiguralGH = false;
			boolean isTranslationGH = false;
			
			System.out.println(edrID);

			if (isFiguralGraffiti.equalsIgnoreCase("Y")) {
				isFiguralGH = true;
			}

			if (isTextualGraffiti.equalsIgnoreCase("Y")) {
				isTranslationGH = true;
			}

			try {
				agpInfoUpdate.setString(3, edrID);

				agpInfoUpdate.setBoolean(1, isTranslationGH);
				agpInfoUpdate.setBoolean(2, isFiguralGH);

				agpInfoUpdate.executeUpdate();

				// Update the DB, as appropriate:

				if (isFiguralGH || isTranslationGH) {
					selectGH.setString(1, edrID);

					ResultSet rs = selectGH.executeQuery();

					if (rs.next()) {
						// already in the DB
						greatestHitsUpdate.setString(1, ghCommentary);
						greatestHitsUpdate.setString(2, preferredImage);
						greatestHitsUpdate.setString(3, edrID);
						greatestHitsUpdate.executeUpdate();

					} else {
						// insert
						greatestHitsInsert.setString(1, edrID);
						greatestHitsInsert.setString(2, ghCommentary);
						greatestHitsInsert.setString(3, preferredImage);
						greatestHitsInsert.executeUpdate();
					}

				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		try {
			agpInfoUpdate.close();
			selectGH.close();

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
