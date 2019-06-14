/**
 * 
 */
package edu.wlu.graffiti.data.setup.main;


/**
 * Populate the database with the (not-fixed) data
 * 
 *  
 * @author sprenkle
 */
public class PopulateDatabase {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		InsertCitiesAndInsula.main(args);
		InsertProperties.main(args);
		ImportEDRData.main(args);
		UpdateAGPInfo.main(args);
		// if you want to get rid of the inscriptions who don't have findspots
		// CleanUpDBForDeployment.main(args);
	}

}
