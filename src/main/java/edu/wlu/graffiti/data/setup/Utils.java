/**
 * 
 */
package edu.wlu.graffiti.data.setup;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author sprenkle
 *
 */
public class Utils {

	/**
	 * Cleans the data coming from the CSV file, removing the quotes and the
	 * extra spaces.
	 * 
	 * @param string
	 * @return
	 */
	static String cleanData(String string) {
		return string.replace("\"", "").trim();
	}

	public static Properties getConfigurationProperties() {
		InputStream inputStream = null;
		Properties prop = null;

		try {
			prop = new Properties();
			String propFileName = "configuration.properties";

			inputStream = Utils.class.getClassLoader().getResourceAsStream(propFileName);

			prop.load(inputStream);

			inputStream.close();
		} catch (FileNotFoundException f) {
			System.err.println("property file 'configuration.properties' not found in the classpath");
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}

		return prop;
	}

}
