package edu.wlu.graffiti.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;

import edu.wlu.graffiti.bean.City;
import edu.wlu.graffiti.bean.DrawingTag;
import edu.wlu.graffiti.bean.GreatestHitsInfo;
import edu.wlu.graffiti.bean.Inscription;
import edu.wlu.graffiti.bean.Insula;
import edu.wlu.graffiti.bean.Property;
import edu.wlu.graffiti.data.rowmapper.DrawingTagRowMapper;
import edu.wlu.graffiti.data.rowmapper.GreatestHItsInfoRowMapper;
import edu.wlu.graffiti.data.rowmapper.InscriptionRowMapper;
import edu.wlu.graffiti.data.setup.UpdateGreatestHitsInfo;

public class GraffitiDao extends JdbcTemplate {

	private static final String DURING_TEST_LIMIT = ""; //" LIMIT 50";

	private static final String ALL_DRAWINGS = "0";

	private static final String ORDER_BY_EDR_INSCRIPTIONS_ID_ASC = " ORDER BY edr_inscriptions.edr_id ASC;";

	public static final String SELECT_STATEMENT = "SELECT *, " + "edr_inscriptions.id as local_id, "
			+ "properties.id AS property_id, insula.short_name AS insula_name " + "FROM edr_inscriptions "
			+ "LEFT JOIN agp_inscription_info ON edr_inscriptions.edr_id=agp_inscription_info.edr_id "
			+ "LEFT JOIN figural_graffiti_info ON edr_inscriptions.edr_id=figural_graffiti_info.edr_id "
			+ "LEFT JOIN greatest_hits_info ON edr_inscriptions.edr_id=greatest_hits_info.edr_id "
			+ "LEFT JOIN properties ON agp_inscription_info.property_id=properties.id "
			+ "LEFT JOIN insula ON properties.insula_id=insula.id";

	private static final String FIND_BY_ALL = SELECT_STATEMENT + " WHERE UPPER(edr_inscriptions.edr_id) "
			+ "LIKE UPPER(?) OR UPPER(ANCIENT_CITY) LIKE UPPER(?) OR " + "UPPER(FIND_SPOT) LIKE UPPER(?) OR "
			+ "UPPER(MEASUREMENTS) LIKE UPPER(?) OR " + "UPPER(edr_inscriptions.WRITING_STYLE) LIKE UPPER(?) OR "
			+ "UPPER(writing_style_in_english) LIKE UPPER(?) OR " + "UPPER(LANGUAGE) LIKE UPPER(?) OR "
			+ "UPPER(lang_in_english) LIKE UPPER(?) OR " + "UPPER(CONTENT) LIKE UPPER(?) OR "
			+ "UPPER(BIBLIOGRAPHY) LIKE UPPER(?) OR " + "NUMBEROFIMAGES = ? " + ORDER_BY_EDR_INSCRIPTIONS_ID_ASC;

	private static final String FIND_BY_FIND_SPOT = SELECT_STATEMENT + " WHERE insula.id = ? AND properties.id = ?  "
			+ ORDER_BY_EDR_INSCRIPTIONS_ID_ASC;

	// need to assign property ids to the inscriptions and cross with that info.

	private static final String FIND_BY_PROPERTY_TYPE = "select *, "
			+ "properties.id AS property_id, edr_inscriptions.id as local_id "
			+ "from edr_inscriptions, agp_inscription_info, figural_graffiti_info, propertyTypes, properties, propertytopropertytype, insula "
			+ "where propertyTypes.id=? and propertyTypes.id=propertytopropertytype.property_type "
			+ "and properties.id=propertytopropertytype.property_id and properties.id=agp_inscription_info.property_id "
			+ "and edr_inscriptions.edr_id=agp_inscription_info.edr_id and properties.insula_id = insula.id "
			+ "AND edr_inscriptions.edr_id=figural_graffiti_info.edr_id " + ORDER_BY_EDR_INSCRIPTIONS_ID_ASC;

	private static final String FIND_BY_CITY = SELECT_STATEMENT + " WHERE UPPER(ANCIENT_CITY) LIKE UPPER(?) "
			+ ORDER_BY_EDR_INSCRIPTIONS_ID_ASC;

	private static final String FIND_BY_CITY_AND_INSULA = SELECT_STATEMENT + " WHERE UPPER(ANCIENT_CITY) = UPPER(?) "
			+ "AND insula.id = ? " + ORDER_BY_EDR_INSCRIPTIONS_ID_ASC;

	private static final String FIND_BY_CITY_AND_INSULA_AND_PROPERTY = SELECT_STATEMENT
			+ " WHERE UPPER(ANCIENT_CITY) = UPPER(?) " + "AND insula.id = ? and properties.id = ? "
			+ ORDER_BY_EDR_INSCRIPTIONS_ID_ASC;

	private static final String FIND_BY_CONTENT = SELECT_STATEMENT + " WHERE UPPER(CONTENT) LIKE UPPER(?) "
			+ ORDER_BY_EDR_INSCRIPTIONS_ID_ASC;

	private static final String FIND_BY_EDR = SELECT_STATEMENT + " WHERE UPPER(edr_inscriptions.edr_id) = UPPER(?) "
			+ ORDER_BY_EDR_INSCRIPTIONS_ID_ASC;

	private static final String SELECT_ALL_DRAWING_INSCRIPTIONS = "SELECT *, " + "edr_inscriptions.id as local_id, "
			+ "properties.id AS property_id " + "FROM edr_inscriptions "
			+ "LEFT JOIN agp_inscription_info ON edr_inscriptions.edr_id=agp_inscription_info.edr_id "
			+ "LEFT JOIN properties ON agp_inscription_info.property_id=properties.id "
			+ "LEFT JOIN figural_graffiti_info ON edr_inscriptions.edr_id=figural_graffiti_info.edr_id "
			+ "LEFT JOIN greatest_hits_info ON edr_inscriptions.edr_id=greatest_hits_info.edr_id "
			+ "WHERE has_figural_component = true AND " + "edr_inscriptions.edr_id=agp_inscription_info.edr_id";

	private static final String SELECT_INSCRIPTIONS_BY_DRAWING_TAG = "SELECT *, " + "edr_inscriptions.id AS local_id, "
			+ "properties.id AS property_id "
			+ "FROM graffitotodrawingtags, edr_inscriptions "
			+ "LEFT JOIN agp_inscription_info ON edr_inscriptions.edr_id=agp_inscription_info.edr_id "
			+ "LEFT JOIN properties ON agp_inscription_info.property_id=properties.id "
			+ "LEFT JOIN figural_graffiti_info ON edr_inscriptions.edr_id=figural_graffiti_info.edr_id "
			+ "LEFT JOIN greatest_hits_info ON edr_inscriptions.edr_id=greatest_hits_info.edr_id "
			+ "WHERE has_figural_component = true AND drawing_tag_id=(?) AND edr_inscriptions.edr_id=graffitotodrawingtags.graffito_id AND "
			+ "edr_inscriptions.edr_id=agp_inscription_info.edr_id";

	private static final String SELECT_DRAWING_TAGS = "SELECT drawing_tags.id, name, description "
			+ "FROM graffitotodrawingtags, drawing_tags "
			+ "WHERE graffito_id = ? AND drawing_tags.id = graffitotodrawingtags.drawing_tag_id;";

	private static final String SELECT_GREATEST_FIGURAL_HITS = SELECT_STATEMENT
			+ " WHERE is_greatest_hit_figural = True" + ORDER_BY_EDR_INSCRIPTIONS_ID_ASC;

	private static final String SELECT_GREATEST_TRANSLATION_HITS = SELECT_STATEMENT
			+ " WHERE is_greatest_hit_translation = True" + ORDER_BY_EDR_INSCRIPTIONS_ID_ASC;

	@Resource
	private FindspotDao propertyDao;

	
	@Cacheable("inscriptions")
	public List<Inscription> getAllInscriptions() {
		List<Inscription> results = query(SELECT_STATEMENT + DURING_TEST_LIMIT, new InscriptionRowMapper());
		addOtherInfo(results);
		return results;
	}

	public List<Inscription> getGreatestFiguralHits() {
		List<Inscription> results = query(SELECT_GREATEST_FIGURAL_HITS, new InscriptionRowMapper());
		addOtherInfo(results);
		return results;
	}

	public List<Inscription> getGreatestTranslationHits() {
		List<Inscription> results = query(SELECT_GREATEST_TRANSLATION_HITS, new InscriptionRowMapper());
		addOtherInfo(results);
		return results;
	}

	public List<Inscription> getInscriptions(final String searchArg) {
		final Object[] searchArgs = new String[9];
		Arrays.fill(searchArgs, "%" + searchArg + "%");
		List<Inscription> results = query(FIND_BY_ALL, new InscriptionRowMapper(), searchArgs);
		addOtherInfo(results);
		return results;
	}

	public List<Inscription> getInscriptionsByPropertyType(final int propertyType) {
		List<Inscription> results = query(FIND_BY_PROPERTY_TYPE, new InscriptionRowMapper(), propertyType);
		addOtherInfo(results);
		return results;
	}

	public List<Inscription> getInscriptionsByFindSpot(final int insula_id, final int property_id) {
		List<Inscription> results = query(FIND_BY_FIND_SPOT, new InscriptionRowMapper(), insula_id, property_id);
		addOtherInfo(results);
		return results;
	}

	public List<Inscription> getInscriptionsByContent(final String searchArg) {
		List<Inscription> results = query(FIND_BY_CONTENT, new InscriptionRowMapper(), "%" + searchArg + "%");
		addOtherInfo(results);
		return results;
	}

	@Cacheable(cacheNames="inscriptions", key="#edrId")
	public Inscription getInscriptionByEDR(final String edrId) {
		List<Inscription> results = query(FIND_BY_EDR, new InscriptionRowMapper(), edrId);
		if( results.size() == 0 ) {
			return null;
		}
		Inscription result = results.get(0);
		addOtherInfo(result);
		return result;
	}

	public List<Inscription> getInscriptionByDrawing(String drawingTagId) {
		List<Inscription> results = null;
		if (drawingTagId.equals(ALL_DRAWINGS)) {
			results = query(SELECT_ALL_DRAWING_INSCRIPTIONS, new InscriptionRowMapper());
		} else {
			results = query(SELECT_INSCRIPTIONS_BY_DRAWING_TAG, new InscriptionRowMapper(),
					Integer.parseInt(drawingTagId));
		}

		addOtherInfo(results);

		return results;
	}

	/* Possibly should be removed; no longer used?
	public List<Inscription> getInscriptionById(String id) {
		List<Inscription> results = query(FIND_BY_ID, new InscriptionRowMapper(), id);
		addOtherInfo(results);
		return results;
	}
	*/

	/**
	 * Adds the drawing tag information to the Inscription object
	 * @param inscription
	 */
	@Cacheable("drawingTags")
	private void retrieveDrawingTagsForInscription(Inscription inscription) {
		List<DrawingTag> drawingTags = query(SELECT_DRAWING_TAGS, new DrawingTagRowMapper(), inscription.getEdrId());
		inscription.getAgp().getFiguralInfo().addDrawingTags(drawingTags);
	}

	private void addPropertyToInscription(Inscription inscription) {
		// TODO: SPECIAL HANDLING until we have the info fixed.
		if (inscription.getAgp().getProperty().getId() == 0) {
			// System.out.println("AGP Property_ID = 0");
			Property property = new Property();
			property.setId(0);
			property.setPropertyName("Not found");
			property.setPropertyNumber("0");
			Insula insula = new Insula();
			insula.setModernCity(inscription.getAncientCity());
			insula.setShortName("Unknown");
			City city = new City();
			city.setName(inscription.getAncientCity());
			insula.setCity(city);
			property.setInsula(insula);
			inscription.getAgp().setProperty(property);
		} else {
			Property property = propertyDao.getPropertyById(inscription.getAgp().getProperty().getId());
			inscription.getAgp().setProperty(property);
		}
	}

	//@Cacheable("inscriptions")
	public List<Inscription> getInscriptionsByCity(final String city) {
		List<Inscription> results = query(FIND_BY_CITY, new InscriptionRowMapper(), city);
		addOtherInfo(results);
		return results;
	}

	//@Cacheable("inscriptions")
	public List<Inscription> getInscriptionsByCityAndInsula(String city, int insula_id) {
		List<Inscription> results = query(FIND_BY_CITY_AND_INSULA, new InscriptionRowMapper(), city, insula_id);
		addOtherInfo(results);
		return results;
	}

	//@Cacheable("inscriptions")
	public List<Inscription> getInscriptionsByCityAndInsulaAndPropertyNumber(String city, int insula_id,
			int property_id) {
		List<Inscription> results = query(FIND_BY_CITY_AND_INSULA_AND_PROPERTY, new InscriptionRowMapper(), city,
				insula_id, property_id);
		addOtherInfo(results);
		return results;
	}

	/**
	 * Adds additional information to the inscription
	 * @param inscription
	 */
	private void addOtherInfo(Inscription inscription) {
		retrieveDrawingTagsForInscription(inscription);
		addPropertyToInscription(inscription);
	}

	/**
	 * Adds additional information to each inscription
	 * @param inscriptions
	 */
	private void addOtherInfo(List<Inscription> inscriptions) {
		for (Inscription i : inscriptions) {
			addOtherInfo(i);
		}
	}

	// update edr inscription function
	@CacheEvict(value="inscriptions", key="#edrID")
	public void updateEdrInscription(ArrayList<String> fields, String edrID) {
		String sql = "UPDATE edr_inscriptions "
				+ "SET ancient_city=(?),find_spot=(?), language=(?), content=(?),bibliography=(?), writing_style=(?), apparatus=(?) "
				+ "where edr_id=(?)";
		fields.add(edrID);
		update(sql, fields.toArray());
	}

	// update agp_inscription_info
	@CacheEvict(value="inscriptions", key="#edrID")
	public void updateAgpInscription(List<Object> fields, String edrID) {
		String sql = "UPDATE agp_inscription_info "
				+ "SET summary=?, content_translation= ?, cil=?, langner=?, height_from_ground=(?),graffito_height=(?), "
				+ "graffito_length=?, letter_height_min=?, letter_height_max=?, individual_letter_heights=?, "
				+ " comment=?, has_figural_component = ?,  is_greatest_hit_figural=?, is_greatest_hit_translation=? "
				+ "where edr_id=(?)";
		fields.add(edrID);
		update(sql, fields.toArray());
	}

	// update greatest hits info
	public void updateGreatestHitsInfo(String edrID, String commentary, String preferredImage) {

		String selectSQL = UpdateGreatestHitsInfo.SELECT_GH_INFO;
		String updateSQL = UpdateGreatestHitsInfo.UPDATE_GH_INFO;
		String insertSQL = UpdateGreatestHitsInfo.INSERT_GH_INFO;

		List<GreatestHitsInfo> inscriptions = query(selectSQL, new GreatestHItsInfoRowMapper(), edrID);

		if (inscriptions.size() == 1) {
			// entry already exists, so update it.
			update(updateSQL, commentary, preferredImage, edrID);

		} else {
			// insert
			update(insertSQL, edrID, commentary, preferredImage);
		}
	}

	// update graffito2drawingtags
	public void updateDrawingTags(List<String> fields) {
		String sql = "UPDATE graffito2drawingtags " + "SET drawing_tag_id=(?) " + "where edr_id=(?)";
		update(sql, fields.toArray());
	}

	// insert edr inscription
	public void insertEdrInscription(List<ArrayList<String>> inscriptions) {
		String sql = "INSERT INTO edr_inscriptions "
				+ "(edr_id,ancient_city,find_spot, language, content,bibliography, writing_style, apparatus)"
				+ " VALUES (?,?,?,?,?,?,?,?)";
		for (ArrayList<String> fields : inscriptions) {
			update(sql, fields.toArray());
		}
	}

	// insert agp_inscription_info
	public void insertAgpInscription(ArrayList<ArrayList<String>> inscriptions) {
		String sql = "INSERT INTO agp_inscription_info "
				+ "(edr_id,floor_to_graffito_height, content_translation, graffito_height, graffito_length, letter_height_min, letter_height_max, cil)"
				+ " VALUES (?,?,?,?,?,?,?,?)";
		for (ArrayList<String> fields : inscriptions) {
			update(sql, fields.toArray());
		}
	}

	public void clearDrawingTags(String edr) {
		String sql = "DELETE FROM graffitotodrawingtags " + "WHERE graffito_id=(?)";

		update(sql, edr);

	}

	// insert drawing tags
	public void insertDrawingTags(String edr, String[] dts) {
		String sql = "INSERT INTO graffitotodrawingtags " + "(graffito_id,drawing_tag_id)" + " VALUES (?,?)";
		for (String dt : dts) {
			update(sql, new Object[] { edr, Integer.parseInt(dt) });
		}
	}

	public void updateDrawingInfo(String drawingDescriptionLatin, String drawingDescriptionEnglish, String edrId) {
		String checkSQL = "SELECT count(edr_id) FROM figural_graffiti_info WHERE edr_id = ?";
		Integer count = queryForObject(checkSQL, Integer.class, edrId);

		if (count == 0) {
			String isql = "INSERT INTO figural_graffiti_info (edr_id) values (?) ";
			update(isql, edrId);
		}

		String sql = "UPDATE figural_graffiti_info SET  description_in_latin = ?, description_in_english = ? "
				+ "WHERE edr_id = ?";
		update(sql, new Object[] { drawingDescriptionLatin, drawingDescriptionEnglish, edrId });

	}

}
