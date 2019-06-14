package edu.wlu.graffiti.data.setup;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

/**
 * Class to update the measurements field that I had screwed up.
 * 
 * @author Sara Sprenkle
 *
 */
public class ImportMeasurementsFields {

	private static final String UPDATE_INSCRIPTION_STATEMENT = "UPDATE agp_inscription_info SET "
			+ "height_from_ground=?, graffito_height=?, graffito_length=?, letter_height_min=?, letter_height_max=?, letter_with_flourishes_height_min=?, letter_with_flourishes_height_max=?  WHERE edr_id = ?";

	final static String DB_URL = "jdbc:postgresql://hopper.cs.wlu.edu/graffiti5";

	static Connection dbCon;

	private static PreparedStatement updateMeasurements;

	public static void main(String[] args) {

		String datafileName = "data/agp_measurements.csv";

		init();
		try {
			updateMeasurements(datafileName);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void updateMeasurements(String datafileName) throws SQLException {
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
		
		records.iterator().next(); // skip the first row?
		
		Set<String> cils = new HashSet<>();
		
		for (CSVRecord record : records) {
			String cil = Utils.cleanData(record.get(0));
			
			String edrID = Utils.cleanData(record.get(1));
			if( edrID.equals("") || edrID.equals("0")) {
				continue;
			}
			edrID = "EDR" + edrID;
			String height_from_ground = Utils.cleanData(record.get(6));
			String graffito_height = Utils.cleanData(record.get(2));
			String graffito_length = Utils.cleanData(record.get(3));
			String letterRange = Utils.cleanData(record.get(4));
			String letterRangeWithFlourishes = Utils.cleanData(record.get(5));
			String letterMin = "", letterMax = "";
			String letterFlMin = "", letterFlMax = "";
			if (letterRange.contains("-")) {
				String[] letterComponents = letterRange.split("-");
				letterMin = letterComponents[0];
				letterMax = letterComponents[1];
			}
			if( letterRangeWithFlourishes.contains("-")) {
				String[] letterComponents = letterRangeWithFlourishes.split("-");
				letterFlMin = letterComponents[0];
				letterFlMax = letterComponents[1];
			}
			updateMeasurements.setString(1, height_from_ground);
			updateMeasurements.setString(2, graffito_height);
			updateMeasurements.setString(3, graffito_length);
			updateMeasurements.setString(4, letterMin);
			updateMeasurements.setString(5, letterMax);
			updateMeasurements.setString(6, letterFlMin);
			updateMeasurements.setString(7, letterFlMax);
			updateMeasurements.setString(8, edrID);

			int rowsUpdated = updateMeasurements.executeUpdate();
			if( rowsUpdated > 0 ) {
				//System.out.println(edrID);
				;
			}
			
			if( cils.contains(cil)) {
				System.out.println("duplicate: " + cil);
			}
			
			cils.add(cil);
 
		}
		updateMeasurements.close();

	}

	private static void init() {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			dbCon = DriverManager.getConnection(DB_URL, "web", "");

			updateMeasurements = dbCon.prepareStatement(UPDATE_INSCRIPTION_STATEMENT);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
