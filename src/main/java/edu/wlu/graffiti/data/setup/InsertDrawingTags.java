package edu.wlu.graffiti.data.setup;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;

public class InsertDrawingTags {

	private static final String INSERT_DRAWING_TAG = "INSERT INTO drawing_tags "
			+ "(name, description) " + "VALUES (?,?)";

	final static String newDBURL = "jdbc:postgresql://hopper.cs.wlu.edu/graffiti3";

	static Connection newDBCon;

	public static void main(String[] args) {
		init();

		try {
			insertDrawingTags();

			newDBCon.close();

		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	private static void insertDrawingTags() {
		try {
			PreparedStatement pstmt = newDBCon
					.prepareStatement(INSERT_DRAWING_TAG);
			Properties drawingTags = new Properties();

			drawingTags.load(new FileReader("data/drawingTags.prop"));
			Enumeration<Object> propKeys = drawingTags.keys();

			while (propKeys.hasMoreElements()) {

				Object drawingType = propKeys.nextElement();
				Object drawingDescription = drawingTags.get(drawingType);
				System.out.println(drawingType + ": " + drawingDescription);

				pstmt.setString(1, (String) drawingType);
				pstmt.setString(2, (String) drawingDescription);
				pstmt.executeUpdate();
			}
			pstmt.close();
		} catch (IOException | SQLException e) {
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
