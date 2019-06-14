/**
 * 
 */
package edu.wlu.graffiti.data.setup.main;

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
	public static String cleanData(String string) {
		return string.replace("\"", "").trim();
	}

	public static Properties getConfigurationProperties() {
		InputStream inputStream = null;
		Properties prop = null;
		//System.out.println("Stage 1 utils");
		try {
			prop = new Properties();
			String propFileName = "configuration.properties";
			//System.out.println("Stage 2 utils");
			inputStream = Utils.class.getClassLoader().getResourceAsStream(propFileName);
			//System.out.println("Stage 2.5 util");
			//System.out.println(inputStream);
			prop.load(inputStream);
			//System.out.println(prop);
			inputStream.close();
			//System.out.println("Stage 3 utils");
		} catch (FileNotFoundException f) {
			System.err.println("property file 'configuration.properties' not found in the classpath");
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}

		return prop;
	}

}
