/**
 * 
 */
package edu.wlu.graffiti.data.setup.main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import edu.wlu.graffiti.data.setup.Utils;

/**
 * Remove the graffiti whose properties/findspots are not found
 * 
 * @author sprenkle
 */
public class CleanUpDBForDeployment {

	public static String DELETE_AGP_INFO = "delete from agp_inscription_info where property_id IS NULL and edr_id in (select edr_inscriptions.edr_id from edr_inscriptions where ancient_city='Pompeii');";
	public static String DELETE_INSCRIPTIONS = "delete from edr_inscriptions where edr_id in (select edr_inscriptions.edr_id from edr_inscriptions left join agp_inscription_info on (edr_inscriptions.edr_id = agp_inscription_info.edr_id) where agp_inscription_info.edr_id IS Null);";
	private static String DB_DRIVER;
	private static String DB_URL;
	private static String DB_USER;
	private static String DB_PASSWORD;
	private static Connection dbCon;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		init();

		removeInscriptionsWithoutKnownFindSpot();

		try {
			StoreInsulaeFromDatabaseForgeoJsonMap.main(args);
			StorePropertiesFromDatabaseForgeoJsonMap.main(args);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static void removeInscriptionsWithoutKnownFindSpot() {
		try {
			PreparedStatement deleteAGPInfo = dbCon.prepareStatement(DELETE_AGP_INFO);
			deleteAGPInfo.execute();
			deleteAGPInfo.close();
			
			PreparedStatement deleteInscriptions = dbCon.prepareStatement(DELETE_INSCRIPTIONS);
			deleteInscriptions.execute();
			deleteInscriptions.close();
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
			dbCon = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

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
