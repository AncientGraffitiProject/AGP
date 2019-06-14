/**
 * 
 */
package edu.wlu.graffiti.data.setup.main;

import edu.wlu.graffiti.data.setup.AddEDRLinksToApparatus;
import edu.wlu.graffiti.data.setup.ExtractEDRLanguageForAGPInfo;
import edu.wlu.graffiti.data.setup.ExtractWritingStyleForAGPInfo;
import edu.wlu.graffiti.data.setup.InsertFeaturedGraffiti;
import edu.wlu.graffiti.data.setup.InsertFiguralInformation;
import edu.wlu.graffiti.data.setup.InsertTranslations;
import edu.wlu.graffiti.data.setup.UpdateSummaryTranslationCommentaryPlus;

/**
 * Handles translating the EDR info into AGP info as well as importing the [new]
 * AGP data.
 * 
 * @author sprenkle
 */
public class UpdateAGPInfo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// translate from EDR to AGP
		AddEDRLinksToApparatus.addEDRLinksToApparatus();
		ExtractEDRLanguageForAGPInfo.updateAGPLanguage();
		ExtractWritingStyleForAGPInfo.updateWritingStyle();

		InsertFiguralInformation.insertFiguralInfo();
		UpdateSummaryTranslationCommentaryPlus.updateInfo();
		InsertFeaturedGraffiti.insertFeaturedGraffiti();
		InsertTranslations.insertTranslationGraffiti();
	}

}
