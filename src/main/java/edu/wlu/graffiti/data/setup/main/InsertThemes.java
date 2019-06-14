package edu.wlu.graffiti.data.setup.main;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;

import edu.wlu.graffiti.data.setup.Utils;

/**
 * 
 * @author Hammad Ahmad
 *
 */
public class InsertThemes {

	private static final String INSERT_THEMES = "INSERT INTO themes " + "(name, description) "
			+ "VALUES (?, ?)";

	private static String DB_DRIVER;
	private static String DB_URL;
	private static String DB_USER;
	private static String DB_PASSWORD;
	static Connection newDBCon;

	public static void main(String[] args) {
		init();

		try {
			insertThemes();

			newDBCon.close();

		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	private static void insertThemes() {
		try {
			PreparedStatement pstmt = newDBCon.prepareStatement(INSERT_THEMES);
			Properties themes = new Properties();

			themes.load(new FileReader("data/themes.prop"));
			Enumeration<Object> propKeys = themes.keys();

			while (propKeys.hasMoreElements()) {

				Object themeType = propKeys.nextElement();
				Object themeDescription = themes.get(themeType);
				System.out.println(themeType + ": " + themeDescription);

				pstmt.setString(1, (String) themeType);
				pstmt.setString(2, (String) themeDescription);
				pstmt.executeUpdate();
			}
			pstmt.close();
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
