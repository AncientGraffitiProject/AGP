package edu.wlu.graffiti.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import edu.wlu.graffiti.bean.AgpInscription;
import edu.wlu.graffiti.bean.DrawingTag;
import edu.wlu.graffiti.bean.Inscription;
import edu.wlu.graffiti.bean.User;

public class GraffitiDao extends JdbcTemplate {

	private static final String ORDER_BY_EAGLE_INSCRIPTIONS_ID_ASC = "ORDER BY eagle_inscriptions.ID ASC;";

	private static final String SELECT_STATEMENT = "SELECT *, "
			+ "agp_inscription_annotations.description AS agp_inscription_annotations_description"
			+ " FROM eagle_inscriptions "
			+ "LEFT JOIN agp_inscription_annotations ON eagle_inscriptions.eagle_id=agp_inscription_annotations.eagle_id "
			+ "LEFT JOIN properties ON agp_inscription_annotations.property_id=properties.id";

	private static final String FIND_BY_ALL = SELECT_STATEMENT
			+ " WHERE UPPER(eagle_inscriptions.EAGLE_ID) "
			+ "LIKE UPPER(?) OR UPPER(ANCIENT_CITY) LIKE UPPER(?) OR "
			+ "UPPER(FIND_SPOT) LIKE UPPER(?) OR "
			+ "UPPER(MEASUREMENTS) LIKE UPPER(?) OR "
			+ "UPPER(eagle_inscriptions.WRITING_STYLE) LIKE UPPER(?) OR "
			+ "UPPER(LANGUAGE) LIKE UPPER(?) OR "
			+ "UPPER(CONTENT) LIKE UPPER(?) OR "
			+ "UPPER(BIBLIOGRAPHY) LIKE UPPER(?) OR "
			+ "UPPER(IMAGE) LIKE UPPER(?) "
			+ ORDER_BY_EAGLE_INSCRIPTIONS_ID_ASC;

	private static final String FIND_BY_FIND_SPOT = SELECT_STATEMENT
			+ " WHERE properties.insula = ? AND properties.property_number = ?  "
			+ ORDER_BY_EAGLE_INSCRIPTIONS_ID_ASC;

	// need to assign property ids to the inscriptions and cross with that info.

	private static final String FIND_BY_PROPERTY_TYPE = 
			"select *, agp_inscription_annotations.description AS agp_inscription_annotations_description "
			+ "from eagle_inscriptions, agp_inscription_annotations, propertyTypes, properties, propertytopropertytype "
			+ "where propertyTypes.id=? and propertyTypes.id=propertytopropertytype.property_type "
			+ "and properties.id=propertytopropertytype.property_id and properties.id=agp_inscription_annotations.property_id"
			+ " and eagle_inscriptions.eagle_id=agp_inscription_annotations.eagle_id "
			+ ORDER_BY_EAGLE_INSCRIPTIONS_ID_ASC;

	private static final String FIND_BY_CITY = SELECT_STATEMENT
			+ " WHERE UPPER(ANCIENT_CITY) LIKE UPPER(?) "
			+ ORDER_BY_EAGLE_INSCRIPTIONS_ID_ASC;

	private static final String FIND_BY_CITY_AND_INSULA = SELECT_STATEMENT
			+ " WHERE UPPER(ANCIENT_CITY) = UPPER(?) "
			+ "AND properties.insula = ? "
			+ ORDER_BY_EAGLE_INSCRIPTIONS_ID_ASC;
	
	private static final String FIND_BY_CITY_AND_INSULA_AND_PROPERTY = SELECT_STATEMENT
			+ " WHERE UPPER(ANCIENT_CITY) = UPPER(?) "
			+ "AND properties.insula = ? and properties.property_number = ? "
			+ ORDER_BY_EAGLE_INSCRIPTIONS_ID_ASC;

	private static final String FIND_BY_CONTENT = SELECT_STATEMENT
			+ " WHERE UPPER(CONTENT) LIKE UPPER(?) "
			+ ORDER_BY_EAGLE_INSCRIPTIONS_ID_ASC;

	private static final String FIND_BY_EDR = SELECT_STATEMENT
			+ " WHERE UPPER(eagle_inscriptions.EAGLE_ID) LIKE UPPER(?) "
			+ ORDER_BY_EAGLE_INSCRIPTIONS_ID_ASC;

	private static final String SELECT_ALL_DRAWING_INSCRIPTIONS = "SELECT *, "
			+ "agp_inscription_annotations.description AS agp_inscription_annotations_description "
			+ "FROM graffitotodrawingtags,drawing_tags, eagle_inscriptions "
			+ "LEFT JOIN agp_inscription_annotations ON eagle_inscriptions.eagle_id=agp_inscription_annotations.eagle_id "
			+ "LEFT JOIN properties ON agp_inscription_annotations.property_id=properties.id "
			+ "WHERE eagle_inscriptions.eagle_id=graffitotodrawingtags.graffito_id AND drawing_tag_id=drawing_tags.id AND "
			+ "eagle_inscriptions.eagle_id=agp_inscription_annotations.eagle_id";

	private static final String SELECT_INSCRIPTIONS_BY_DRAWING_TAG = "SELECT *, "
			+ "agp_inscription_annotations.description AS agp_inscription_annotations_description "
			+ "FROM graffitotodrawingtags,drawing_tags, eagle_inscriptions "
			+ "LEFT JOIN agp_inscription_annotations ON eagle_inscriptions.eagle_id=agp_inscription_annotations.eagle_id "
			+ "LEFT JOIN properties ON agp_inscription_annotations.property_id=properties.id "
			+ "WHERE drawing_tag_id=(?) AND eagle_inscriptions.eagle_id=graffitotodrawingtags.graffito_id AND drawing_tag_id=drawing_tags.id AND "
			+ "eagle_inscriptions.eagle_id=agp_inscription_annotations.eagle_id";

	private static final String SELECT_DRAWING_TAGS = "SELECT drawing_tags.id, name, description "
			+ "FROM graffitotodrawingtags, drawing_tags "
			+ "WHERE graffito_id = ? AND drawing_tags.id = graffitotodrawingtags.drawing_tag_id;";

	private static final class InscriptionRowMapper implements
			RowMapper<Inscription> {
		public Inscription mapRow(final ResultSet resultSet, final int rowNum)
				throws SQLException {
			final Inscription inscription = new Inscription();
			final AgpInscription agp = new AgpInscription();
			inscription.setId(resultSet.getInt("ID"));
			inscription.setEagleId(resultSet.getString("EAGLE_ID"));
			inscription.setAncientCity(resultSet.getString("ANCIENT_CITY"));

			String propertyName = resultSet.getString("property_name");
			String insula = resultSet.getString("insula");
			String property_number = resultSet.getString("property_number");

			String findSpot = propertyName + " (" + insula + "."
					+ property_number + ")";

			inscription.setfindSpot(findSpot);
			inscription.setMeasurements(resultSet.getString("MEASUREMENTS"));
			inscription.setLanguage(resultSet.getString("LANGUAGE"));
			inscription.setContent(resultSet.getString("CONTENT"));
			inscription.setBibliography(resultSet.getString("BIBLIOGRAPHY"));
			inscription.setWritingStyle(resultSet.getString("WRITING_STYLE"));
			inscription.setUrl(resultSet.getString("IMAGE"));
			agp.setDescription(resultSet
					.getString("agp_inscription_annotations_description"));
			agp.setComment(resultSet.getString("comment"));
			agp.setTranslation(resultSet.getString("translation"));
			agp.setModern_city(resultSet.getString("modern_city"));
			agp.setProperty_name(propertyName);
			agp.setInsula(insula);
			agp.setPropertyNumber(property_number);
			inscription.setAgp(agp);
			return inscription;
		}
	}

	private static final class DrawingTagRowMapper implements
			RowMapper<DrawingTag> {
		public DrawingTag mapRow(final ResultSet resultSet, final int rowNum)
				throws SQLException {
			final DrawingTag drawingtag = new DrawingTag();
			drawingtag.setId(resultSet.getInt("ID"));
			drawingtag.setName(resultSet.getString("name"));
			drawingtag.setDescription(resultSet.getString("description"));
			return drawingtag;
		}
	}

	public List<Inscription> getInscriptions(final String searchArg) {
		final Object[] searchArgs = new String[9];
		Arrays.fill(searchArgs, "%" + searchArg + "%");
		List<Inscription> results = query(FIND_BY_ALL,
				new InscriptionRowMapper(), searchArgs);
		for (Inscription inscription : results) {
			addDrawingTagsToInscription(inscription);
		}
		return results;
	}

	public List<Inscription> getInscriptionsByPropertyType(
			final int propertyType) {
		List<Inscription> results = query(FIND_BY_PROPERTY_TYPE,
				new InscriptionRowMapper(), propertyType);
		for (Inscription inscription : results) {
			addDrawingTagsToInscription(inscription);
		}
		return results;
	}

	public List<Inscription> getInscriptionsByFindSpot(final String insula, final String propertyNumber) {
		List<Inscription> results = query(FIND_BY_FIND_SPOT,
				new InscriptionRowMapper(), insula, propertyNumber);
		for (Inscription inscription : results) {
			addDrawingTagsToInscription(inscription);
		}
		return results;
	}
	
	public List<Inscription> getInscriptionsByContent(final String searchArg) {
		List<Inscription> results = query(FIND_BY_CONTENT,
				new InscriptionRowMapper(), "%" + searchArg + "%");
		for (Inscription inscription : results) {
			addDrawingTagsToInscription(inscription);
		}
		return results;
	}

	public List<Inscription> getInscriptionsByEDR(final String searchArg) {
		List<Inscription> results = query(FIND_BY_EDR,
				new InscriptionRowMapper(), "%" + searchArg + "%");
		for (Inscription inscription : results) {
			addDrawingTagsToInscription(inscription);
		}
		return results;
	}

	public List<Inscription> getInscriptionByDrawing(String drawingTagId) {
		List<Inscription> results = null;
		// The 0 means "All drawings"
		if (drawingTagId.equals("0")) {
			results = query(SELECT_ALL_DRAWING_INSCRIPTIONS,
					new InscriptionRowMapper());
		} else {
			results = query(SELECT_INSCRIPTIONS_BY_DRAWING_TAG,
					new InscriptionRowMapper(), Integer.parseInt(drawingTagId));
		}

		for (Inscription inscription : results) {
			addDrawingTagsToInscription(inscription);
		}

		return results;
	}

	private void addDrawingTagsToInscription(Inscription inscription) {
		List<DrawingTag> drawingTags = query(SELECT_DRAWING_TAGS,
				new DrawingTagRowMapper(), inscription.getEagleId());
		inscription.addDrawingTags(drawingTags);
	}
	

	public List<Inscription> getInscriptionsByCity(final String city) {
		List<Inscription> results = query(FIND_BY_CITY,
				new InscriptionRowMapper(), city );
		for (Inscription inscription : results) {
			addDrawingTagsToInscription(inscription);
		}
		return results;
	}

	public List<Inscription> getInscriptionsByCityAndInsula(String city,
			String insula) {
		List<Inscription> results = query(FIND_BY_CITY_AND_INSULA,
				new InscriptionRowMapper(), city, insula);
		for (Inscription inscription : results) {
			addDrawingTagsToInscription(inscription);
		}
		return results;
	}
	
	public List<Inscription> getInscriptionsByCityAndInsulaAndPropertyNumber(String city,
			String insula, String propertyNumber) {
		List<Inscription> results = query(FIND_BY_CITY_AND_INSULA_AND_PROPERTY,
				new InscriptionRowMapper(), city, insula, propertyNumber);
		for (Inscription inscription : results) {
			addDrawingTagsToInscription(inscription);
		}
		return results;
	}

	public void updateInscription(ArrayList<String> fields) {
		String sql = "UPDATE eagle_inscriptions "
				+ "SET ancient_city=(?),find_spot=(?),measurements=(?), language=(?), content=(?),bibliography=(?), writing_style=(?), image=(?) "
				+ "where eagle_id=(?)";
		update(sql, fields.toArray());
	}

	public void insertInscription(ArrayList<ArrayList<String>> inscriptions) {
		String sql = "INSERT INTO eagle_inscriptions "
				+ "(eagle_id,ancient_city,find_spot,measurements, language, content,bibliography, writing_style, image)"
				+ " VALUES (?,?,?,?,?,?,?,?,?)";
		for (ArrayList<String> fields : inscriptions) {
			update(sql, fields.toArray());
		}
	}

	public void clearDrawingTags(String edr) {
		String sql = "DELETE FROM graffitotodrawingtags "
				+ "WHERE graffito_id=(?)";

		update(sql, edr);

	}

	public void insertDrawingTags(String edr, String[] dts) {
		String sql = "INSERT INTO graffitotodrawingtags "
				+ "(graffito_id,drawing_tag_id)" + " VALUES (?,?)";
		for (String dt : dts) {
			update(sql, new Object[] { edr, Integer.parseInt(dt) });
		}
	}

	public void insertUser(ArrayList<String> user) {
		String sql = "INSERT INTO users " + "(username,password,name)"
				+ " VALUES (?,?,?)";

		update(sql, user.toArray());
	}

	public List<User> getPendingUsers() {
		List<User> results = query(
				"SELECT username FROM users WHERE enabled is NULL",
				new UserRowMapper());
		return results;
	}

	public void approveUsers(Object[] users) {
		String sql = "UPDATE users " + "SET enabled='true'"
				+ "where username=(?)";

		for (int x = 0; x < users.length; x++) {
			update(sql, users[x]);
		}
	}

	private static final class UserRowMapper implements RowMapper<User> {
		public User mapRow(final ResultSet resultSet, final int rowNum)
				throws SQLException {
			final User us = new User();
			us.setUserName(resultSet.getString("username"));
			return us;
		}
	}

}
