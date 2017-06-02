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
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import edu.wlu.graffiti.bean.DrawingTag;

/**
 * Iterates over the properties, looking to see if the property name is one of
 * the defined types. If so, adds a mapping from the property to the property
 * type.
 * 
 * @author Sara Sprenkle
 *
 */
public class InsertDrawingTagMappings {
	final static String newDBURL = "jdbc:postgresql://hopper.cs.wlu.edu/graffiti3";

	private static final String INSERT_DRAWING_TAG_MAPPING = "INSERT INTO graffitotodrawingtags(graffito_id, drawing_tag_id) "
			+ "VALUES (?,?)";

	static Connection newDBCon;

	public static void main(String[] args) {
		init();

		try {
			insertDrawingTypeMappings("data/agp_metadata_spreadsheet.csv");

			newDBCon.close();

		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	private static Map<String, DrawingTag> getDrawingTags() {
		Map<String, DrawingTag> drawingTags = new HashMap<String, DrawingTag>();

		try {
			PreparedStatement pstmt = newDBCon.prepareStatement("SELECT id, name, description FROM drawing_tags");
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				DrawingTag dt = new DrawingTag();
				dt.setId(rs.getInt(1));
				dt.setName(rs.getString(2));
				dt.setDescription(rs.getString(3));
				drawingTags.put(rs.getString(2), dt);
			}

			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return drawingTags;
	}

	private static void insertDrawingTypeMappings(String datafileName) {
		Map<String, DrawingTag> drawingTags = getDrawingTags();
		PreparedStatement pstmt = null;

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
			pstmt = newDBCon.prepareStatement(INSERT_DRAWING_TAG_MAPPING);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (CSVRecord record : records) {
			String eagleID = Utils.cleanData(record.get(0));
			String drawingTag = Utils.cleanData(record.get(4));

			if (!drawingTag.equals("")) {
				String[] tags = drawingTag.split(", ");

				for (String tag : tags) {
					if (drawingTags.containsKey(tag)) {
						DrawingTag dt = drawingTags.get(tag);
						try {
							pstmt.setString(1, eagleID);
							pstmt.setInt(2, dt.getId());
							pstmt.executeUpdate();
						} catch (SQLException e) {
							System.err.println("eagle id: " + eagleID);
							System.err.println(dt);
							e.printStackTrace();
						}
					} else {
						System.err.println(tag + " not one of the tags");
					}
				}

			}
		}
		try {
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
