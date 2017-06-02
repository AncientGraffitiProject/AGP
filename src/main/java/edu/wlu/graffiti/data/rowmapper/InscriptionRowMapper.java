package edu.wlu.graffiti.data.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import edu.wlu.graffiti.bean.AGPInfo;
import edu.wlu.graffiti.bean.FiguralInfo;
import edu.wlu.graffiti.bean.GreatestHitsInfo;
import edu.wlu.graffiti.bean.Inscription;
import edu.wlu.graffiti.bean.Property;

public final class InscriptionRowMapper implements RowMapper<Inscription> {
	public Inscription mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
		final Inscription inscription = new Inscription();
		final AGPInfo agp = new AGPInfo();
		final FiguralInfo figinfo = new FiguralInfo();
		final GreatestHitsInfo ghInfo = new GreatestHitsInfo();
		
		inscription.setId(resultSet.getInt("local_id"));
		inscription.setEdrId(resultSet.getString("edr_id"));
		inscription.setAncientCity(resultSet.getString("ANCIENT_CITY"));

		inscription.setEDRFindSpot(resultSet.getString("find_spot"));
		inscription.setMeasurements(resultSet.getString("MEASUREMENTS"));
		inscription.setLanguage(resultSet.getString("language"));
		inscription.setContent(resultSet.getString("CONTENT"));
		inscription.setBibliography(resultSet.getString("BIBLIOGRAPHY"));
		inscription.setWritingStyle(resultSet.getString("writing_style"));
		inscription.setApparatus(resultSet.getString("APPARATUS"));
		inscription.setApparatusDisplay(resultSet.getString("apparatus_displayed"));
		inscription.setNumberOfImages(resultSet.getInt("NUMBEROFIMAGES"));
		inscription.setStartImageId(resultSet.getInt("start_image_id"));
		inscription.setStopImageId(resultSet.getInt("stop_image_id"));

		agp.setSummary(resultSet.getString("summary"));
		agp.setCommentary(resultSet.getString("comment"));
		agp.setContentTranslation(resultSet.getString("content_translation"));
		agp.setEdrId(inscription.getEdrId());
		agp.setCil(resultSet.getString("CIL"));
		agp.setLangner(resultSet.getString("langner"));
		agp.setWritingStyleInEnglish(resultSet.getString("writing_style_in_english"));
		agp.setLanguageInEnglish(resultSet.getString("lang_in_english"));
		
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
		agp.setEpidoc(resultSet.getString("epidoc"));

		if (agp.hasFiguralComponent()) {
			figinfo.setDescriptionInEnglish(resultSet.getString("description_in_english"));
			figinfo.setDescriptionInLatin(resultSet.getString("description_in_latin"));
		}
		
		if( agp.isGreatestHitFigural() || agp.isGreatestHitTranslation() ) {
			ghInfo.setCommentary(resultSet.getString("commentary"));
			ghInfo.setPreferredImage(resultSet.getString("preferred_image"));
		}

		agp.setFiguralInfo(figinfo);
		agp.setGreatestHitsInfo(ghInfo);

		inscription.setAgp(agp);
		return inscription;
	}
}