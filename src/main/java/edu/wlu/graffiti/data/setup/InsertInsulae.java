package edu.wlu.graffiti.data.setup;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InsertInsulae {

	private static final String DELIMITER = ";";

	private static final String INSERT_PROPERTY_STMT = "INSERT INTO insula "
			+ "(modern_city, name) VALUES (?,?)";

	final static String newDBURL = "jdbc:postgresql://hopper.cs.wlu.edu/graffiti3";

	static Connection newDBCon;

	public static void main(String[] args) {
		init();

		try {
			insertProperties("data/herculaneum_insulae.csv");
			insertProperties("data/pompeii_insulae.csv");
			newDBCon.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	private static void insertProperties(String datafileName) {
		try {
			PreparedStatement pstmt = newDBCon
					.prepareStatement(INSERT_PROPERTY_STMT);

			BufferedReader br = new BufferedReader(new FileReader(datafileName));

			String line;

			while ((line = br.readLine()) != null) {
				String[] data = line.split(DELIMITER);
				String modernCity = data[0];
				String insula = data[1];

				pstmt.setString(1, modernCity);
				pstmt.setString(2, insula);
				try {
					System.out.println(modernCity + " " + insula); 
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
