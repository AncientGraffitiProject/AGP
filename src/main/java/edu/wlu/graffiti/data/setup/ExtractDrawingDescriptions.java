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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

/**
 * This class is responsible for
 * 
 * @author Sara Sprenkle
 * 
 */
public class ExtractDrawingDescriptions {

	final static String DB_URL = "jdbc:postgresql://hopper.cs.wlu.edu/graffiti5";

	final static String SELECT_GRAFFITI = "select content, eagle_id from eagle_inscriptions";

	private static final String UPDATE_ANNOTATION_STMT = "UPDATE agp_inscription_annotations "
			+ "SET description = ?, description_translation = ? WHERE eagle_id=?";

	static Connection dbCon;

	private static PreparedStatement updateAnnotationStatement;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		init();

		try {
			// Extract the data from original DB and insert it into the new DB
			// PreparedStatement extractData =
			// dbCon.prepareStatement(SELECT_GRAFFITI);

			Reader in = null;
			Iterable<CSVRecord> records;
			try {
				in = new FileReader("data/drawings_info.csv");
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

			for (CSVRecord record : records) {
				String eagle_id = Utils.cleanData(record.get(0));
				String description = Utils.cleanData(record.get(1));
				String translation = Utils.cleanData(record.get(2));


				/*
				 * ResultSet rs = extractData.executeQuery();
				 * 
				 * Pattern contentPattern = Pattern .compile(
				 * "^\\w+ \\(\\w+\\),? (\\w* )* ?\\(?([\\w.-]*+)\\)?(, [\\w\\s,.\\(\\)]*)?"
				 * );
				 * 
				 * while (rs.next()) { String content = rs.getString("content");
				 * String eagle_id = rs.getString("eagle_id");
				 * 
				 * Matcher matcher = contentPattern.matcher(content);
				 * 
				 * if (matcher.matches()) { String translation =
				 * matcher.group(2); } else { System.out.println(
				 * "ERROR MATCHING CONTENT!"); }
				 * 
				 * System.out.println(content + " " + eagle_id);
				 */

				updateAnnotation(eagle_id, description, translation);

			}

			// rs.close();
			// extractData.close();
			updateAnnotationStatement.close();
			dbCon.close();

		} catch (SQLException e1) {
			e1.printStackTrace();
		}

	}

	/**
	 * 
	 * @param eagle_id
	 * @param description
	 * @param translation
	 */
	private static void updateAnnotation(String eagle_id, String description, String translation) {
		try {
			updateAnnotationStatement.setString(1, description);
			updateAnnotationStatement.setString(2, translation);
			updateAnnotationStatement.setString(3, eagle_id);
			updateAnnotationStatement.executeUpdate();
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

			updateAnnotationStatement = dbCon.prepareStatement(UPDATE_ANNOTATION_STMT);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
