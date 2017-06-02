package edu.wlu.graffiti.data.setup;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;


/**
 * This class checks the existence of image urls for each inscription
 * and updates the numberofimages attribute accordingly.
 * 
 * @author whitej
 * 
 */

public class UpdateNumberOfImages {

	final static String SELECT_GRAFFITI = "SELECT edr_id, numberofimages from edr_inscriptions";
	
	private static final String UPDATE_NUMBEROFIMAGES = "UPDATE edr_inscriptions SET numberofimages = ?, start_image_id = ?, stop_image_id = ? WHERE edr_id = ?";
	
	static Connection newDBCon;
	
	private static PreparedStatement updateNumberStatement;

	private static String DB_DRIVER;

	private static String DB_URL;

	private static String DB_USER;

	private static String DB_PASSWORD;

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		init();

		try{
			int numOfUpdates = 0;
			
			//Get list of edrIDs
			PreparedStatement extractData = newDBCon.prepareStatement(SELECT_GRAFFITI);
			ResultSet rs = extractData.executeQuery();
			
			//Update numberofimages for each 
			while (rs.next()){
				String id = rs.getString("edr_id");
				int oldNum = rs.getInt("numberofimages");
				String baseUrl = "http://www.edr-edr.it/foto_epigrafi/immagini_uso/" + id.substring(3,6) + "/" + id.substring(3);
				int newNum = 0;
				int startNum = 0;
				int stopNum = 0;
				if (urlExists(baseUrl + ".jpg")){
					startNum = 0;
					newNum = 1;
					while (urlExists(baseUrl + "-" + newNum + ".jpg")){
						newNum++;
					}
					stopNum = newNum-1;
				}
				else if ( urlExists(baseUrl + "-1.jpg")) {
					startNum = 1;
					newNum = 2;
					while (urlExists(baseUrl + "-" + newNum + ".jpg")){
						newNum++;
					}
					stopNum = newNum -1;
					newNum --; // so that the number of images is correct
				}
				//if (oldNum != newNum){
					updateNumbers(id, newNum, startNum, stopNum);
					System.out.println("Inscription with edr id " + id + " updated.");
					System.out.println("Old number: " + oldNum);
					System.out.println("New number: " + newNum);
					numOfUpdates++;
				/*}
				else {
					System.out.println("Inscription with edr id " + id + " NOT updated.");
					System.out.println("Number of images: " + oldNum);
				}
				*/
			}
			System.out.println(numOfUpdates + " changes made to numberofimages.");
			rs.close();
			extractData.close();
			updateNumberStatement.close();
			newDBCon.close();
		}
		catch (SQLException e1){
			e1.printStackTrace();
		}
	}
	
	/**
	 * Helper method to check that the image url exists
	 * @param url
	 * @return true if the url is valid, false otherwise
	 */
	private static boolean urlExists(String url){
		URL u = null;
		HttpURLConnection huc = null;
		int code = 0;
		try {
			u = new URL(url);
			huc = (HttpURLConnection) u.openConnection();
			huc.setRequestMethod("GET");
			huc.connect();
			code = huc.getResponseCode();
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (code == 200);
	}
	
	/**
	 * @param edr_id
	 * @param numberofimages
	 */
	private static void updateNumbers(String edr_id, int numberofimages, int startNum, int stopNum) {
		try {
			updateNumberStatement.setInt(1, numberofimages);
			updateNumberStatement.setInt(2, startNum);
			updateNumberStatement.setInt(3, stopNum);
			updateNumberStatement.setString(4, edr_id);
			updateNumberStatement.executeUpdate();
		} catch (SQLException e) {
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
			updateNumberStatement = newDBCon.prepareStatement(UPDATE_NUMBEROFIMAGES);
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