package edu.wlu.graffiti.data.setup;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Properties;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

/**
 * Imports data from the featuredGraffiti.csv file into the database.
 * 
 * @author Hammad Ahmad
 * @author Trevor Stalnaker
 * 
 * Each element in the CSV file has four elements: edr_id, theme(s), commentary, preferred_image.
 * Separate themes with the "/" character to provide more than one, white space doesn't matter here.
 * Capitalization doesn't matter for themes.
 * The preferred_image can be provided as the last 6 digits of the edr_id.
 * 
 * Example:EDR152963, FIGURAL / travel/Grammar, example commentary, 152963
 */

public class InsertFeaturedGraffiti {

	private static final String INSERT_FEATURED_GRAFFITI = "INSERT INTO graffititothemes " + "(graffito_id, theme_id) "
			+ "VALUES (?, ?)";
	private static final String SET_INSCRIPTION_AS_THEMED = "UPDATE agp_inscription_info " + "SET is_themed=true "
			+ "where edr_id=(?)";
	private static final String GET_THEME_ID = "SELECT theme_id FROM themes WHERE name = ?";
	
	private static final String INSERT_GREATEST_HITS_INFO = "INSERT INTO greatest_hits_info "
			+ "(edr_id, commentary, preferred_image) " + "VALUES (?, ?, ?)";
	
	private static final String SET_GREATEST_FIGURAL_HIT = "UPDATE agp_inscription_info " + "SET is_greatest_hit_figural=true "
			+ "WHERE edr_id=(?)";

	public static final String UPDATE_GH_INFO = "UPDATE greatest_hits_info SET commentary = ?, preferred_image = ? WHERE edr_id=?";
	public static final String SELECT_GH_INFO = "SELECT * from greatest_hits_info where edr_id = ?";
	public static final String INSERT_GH_INFO = "INSERT INTO greatest_hits_info VALUES (?, ?, ?)";

	private static String DB_DRIVER;
	private static String DB_URL;
	private static String DB_USER;
	private static String DB_PASSWORD;
	static Connection newDBCon;

	public static void main(String[] args) {
		insertFeaturedGraffiti();
	}

	public static void insertFeaturedGraffiti() {
		
		init();

		try {
			
			//Create Prepared Statements
			PreparedStatement pstmt = newDBCon.prepareStatement(INSERT_FEATURED_GRAFFITI);
			PreparedStatement pstmt2 = newDBCon.prepareStatement(SET_INSCRIPTION_AS_THEMED);
			PreparedStatement pstmt3 = newDBCon.prepareStatement(GET_THEME_ID);
			PreparedStatement pstmt4 = newDBCon.prepareStatement(INSERT_GREATEST_HITS_INFO);			
			PreparedStatement pstmt5 = newDBCon.prepareStatement(SET_GREATEST_FIGURAL_HIT);

			//Read in the data from the CSV File
			Reader in = new FileReader("data/AGPData/featured_graffiti.csv");
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			for (CSVRecord record : records) {

				//Clean data in CSV File and save to Strings
				String edrId = Utils.cleanData(record.get(0));
				String theme = Utils.cleanData(record.get(2));
				String commentary = Utils.cleanData(record.get(3));
				String image = Utils.cleanData(record.get(4));
				
				LinkedList<Integer> themeIds = new LinkedList<Integer>();
				int themeId;
				
				//Split theme names about the '/' character
				for (String t : theme.split("/")) {
				
					//Normalize theme string to ensure theme is found if it exists
					t = t.replaceAll("\\s", ""); //Removes white spaces from the string
					t = String.valueOf(t.charAt(0)).toUpperCase() + t.substring(1, t.length()).toLowerCase();
					
					//Find the theme_id for the given record using normalized theme string
					pstmt3.setString(1, t);
					ResultSet rs = pstmt3.executeQuery();
					
					//Add theme_ids to themeIds if they exist and are not already present in list
					while (rs.next()) {
						themeId = rs.getInt("theme_id");
						if (!themeIds.contains(themeId) && themeId != 0) {
							themeIds.add(themeId);
						}
					}
				}
				//Verify that there is at least one theme_id and update database
				if (themeIds.size() != 0) {
					
					//Insert data into greatest_hits_info
					pstmt4.setString(1, edrId);
					pstmt4.setString(2, commentary.replaceAll("\n", "<br/>"));
					pstmt4.setString(3, image);
					pstmt4.executeUpdate();
					
						//Code for testing that file was read properly					
//							System.out.println("EDR: " + edrId);
//							System.out.println("Themes: " + themeIds);
//							System.out.println("Commentary: " + commentary);
//							System.out.println("Image: " + image);
//							System.out.println("-----------------------------------------");
					
					//Set Inscription as themed
					pstmt2.setString(1, edrId);
					pstmt2.executeUpdate();
					
					//Iterate through themes and update the database
					for (int id : themeIds) {
						
						//Insert into graffititothemes table
						pstmt.setString(1, (String) edrId);
						pstmt.setInt(2, id);
						pstmt.executeUpdate();
					
						//Sets is_figural_greatest_hit to true in the database for the 
						//given edr_id if figural is the theme
						if (id == 27) {
							pstmt5.setString(1, edrId);
							pstmt5.executeUpdate();
						}
					}
				}//else {System.out.println("It failed");} //Used for testing purposes

			}
			
			//Close statements, connections, and file readers
			pstmt.close();
			pstmt2.close();
			pstmt3.close();
			pstmt4.close();
			pstmt5.close();
			in.close();
			newDBCon.close();
			
		} catch (IOException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void init() {
		getConfigurationProperties();

		try {
			Class.forName(DB_DRIVER);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			newDBCon = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void getConfigurationProperties() {
		Properties prop = Utils.getConfigurationProperties();

		DB_DRIVER = prop.getProperty("db.driverClassName");
		DB_URL = prop.getProperty("db.url");
		DB_USER = prop.getProperty("db.user");
		DB_PASSWORD = prop.getProperty("db.password");
	}

}
