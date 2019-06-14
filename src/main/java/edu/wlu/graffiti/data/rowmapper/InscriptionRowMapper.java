package edu.wlu.graffiti.data.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import edu.wlu.graffiti.bean.AGPInfo;
import edu.wlu.graffiti.bean.FiguralInfo;
import edu.wlu.graffiti.bean.GreatestHitsInfo;
import edu.wlu.graffiti.bean.Inscription;
import edu.wlu.graffiti.bean.Property;

/**
 * Maps the inscription info in the DB to the Inscription bean.
 * 
 * @author Sara Sprenkle
 * @author Hammad Ahmad
 * @author Trevor Stalnaker
 *
 */
public final class InscriptionRowMapper implements RowMapper<Inscription> {
	public Inscription mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
		final Inscription inscription = new Inscription();
		final AGPInfo agp = new AGPInfo();
		final FiguralInfo figinfo = new FiguralInfo();
		final GreatestHitsInfo ghInfo = new GreatestHitsInfo();

		inscription.setId(resultSet.getInt("local_id"));
		inscription.setEdrId(resultSet.getString("edr_id"));
		inscription.setAncientCity(resultSet.getString("ANCIENT_CITY"));
		inscription.setDateBeginning(resultSet.getString("date_beginning"));
		inscription.setDateEnd(resultSet.getString("date_end"));
		inscription.setDateExplanation(resultSet.getString("date_explanation"));

		inscription.setEDRFindSpot(resultSet.getString("find_spot"));
		inscription.setMeasurements(resultSet.getString("MEASUREMENTS"));
		inscription.setLanguage(resultSet.getString("language"));
		inscription.setContent(resultSet.getString("CONTENT"));
		inscription.setBibliography(resultSet.getString("BIBLIOGRAPHY"));
		inscription.setWritingStyle(resultSet.getString("writing_style"));
		inscription.setApparatus(resultSet.getString("APPARATUS"));
		inscription.setApparatusDisplay(resultSet.getString("apparatus_displayed"));

		agp.setCaption(resultSet.getString("caption"));
		agp.setCommentary(resultSet.getString("comment"));
		agp.setContentTranslation(resultSet.getString("content_translation"));
		agp.setEdrId(inscription.getEdrId());
		agp.setCil(resultSet.getString("cil"));
		agp.setLangner(resultSet.getString("langner"));
		agp.setWritingStyleInEnglish(resultSet.getString("writing_style_in_english"));
		agp.setLanguageInEnglish(resultSet.getString("lang_in_english"));
		agp.setEpidoc(resultSet.getString("content_epidocified"));

		Property p = new Property(resultSet.getInt("property_id"));
		agp.setProperty(p);

		agp.setHasFiguralComponent(resultSet.getBoolean("has_figural_component"));
		agp.setGraffitoHeight(resultSet.getString("graffito_height"));
		agp.setGraffitoLength(resultSet.getString("graffito_length"));
		agp.setHeightFromGround(resultSet.getString("height_from_ground"));
		agp.setGreatestHitFigural(resultSet.getBoolean("is_greatest_hit_figural"));
		agp.setGreatestHitTranslation(resultSet.getBoolean("is_greatest_hit_translation"));
		agp.setIndividualLetterHeights(resultSet.getString("individual_letter_heights"));
		agp.setMaxLetterHeight(resultSet.getString("letter_height_max"));
		agp.setMinLetterHeight(resultSet.getString("letter_height_min"));
		agp.setMaxLetterWithFlourishesHeight(resultSet.getString("letter_with_flourishes_height_max"));
		agp.setMinLetterWithFlourishesHeight(resultSet.getString("letter_with_flourishes_height_min"));
		agp.setEpidoc(resultSet.getString("content_epidocified"));
		agp.setThemed(resultSet.getBoolean("is_themed"));

		if (agp.hasFiguralComponent()) {
			figinfo.setDescriptionInEnglish(resultSet.getString("description_in_english"));
			figinfo.setDescriptionInLatin(resultSet.getString("description_in_latin"));
		}

		if (agp.isThemed()) { // agp.isGreatestHitFigural() || agp.isGreatestHitTranslation() ) { //isThemed
								// is probably more appropriate
			ghInfo.setCommentary(resultSet.getString("gh_commentary"));
			ghInfo.setPreferredImage(resultSet.getString("preferred_image"));
		}

		agp.setFiguralInfo(figinfo);
		agp.setGreatestHitsInfo(ghInfo);

		inscription.setAgp(agp);
		return inscription;
	}
}