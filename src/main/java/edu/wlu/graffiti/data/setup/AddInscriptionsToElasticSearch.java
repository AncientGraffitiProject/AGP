package edu.wlu.graffiti.data.setup;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;

import edu.wlu.graffiti.bean.DrawingTag;
import edu.wlu.graffiti.bean.Inscription;
import edu.wlu.graffiti.bean.Property;
import edu.wlu.graffiti.bean.PropertyType;
import edu.wlu.graffiti.dao.DrawingTagsDao;
import edu.wlu.graffiti.dao.GraffitiDao;
import edu.wlu.graffiti.dao.FindspotDao;
import edu.wlu.graffiti.data.rowmapper.DrawingTagRowMapper;
import edu.wlu.graffiti.data.rowmapper.InscriptionRowMapper;
import edu.wlu.graffiti.data.rowmapper.PropertyRowMapper;
import edu.wlu.graffiti.data.rowmapper.PropertyTypeRowMapper;

/**
 * This class gathers the inscriptions from the database and indexes them in the
 * Elasticsearch node
 * 
 * Run this file whenever there are changes made to the database to reflect the
 * changes in the Elasticsearch index
 * 
 * @author whitej
 * @author sprenkle - refactored to decrease duplicate code with row mapping;
 *         revised to use the updated DB schema; updated for Elasticsearch 2.x
 *
 */

public class AddInscriptionsToElasticSearch {

	private static String DB_PASSWORD;
	private static String DB_USER;
	private static String DB_DRIVER;
	private static String ELASTIC_SEARCH_LOC;
	private static String ES_TYPE_NAME;
	private static String ES_INDEX_NAME;
	private static int ES_PORT;
	private static String ES_CLUSTER_NAME;

	private static String DB_URL;

	private static final String SELECT_ALL_INSCRIPTIONS = GraffitiDao.SELECT_STATEMENT;

	private static Connection newDBCon;

	private static TransportClient client;

	private static final InscriptionRowMapper INSCRIPTION_ROW_MAPPER = new InscriptionRowMapper();
	private static final PropertyTypeRowMapper PROPERTY_TYPE_ROW_MAPPER = new PropertyTypeRowMapper();
	private static final DrawingTagRowMapper DRAWING_TAG_ROW_MAPPER = new DrawingTagRowMapper();
	private static final PropertyRowMapper PROPERTY_ROW_MAPPER = new PropertyRowMapper();

	/**
	 * Gathers all inscriptions from the database and maps the result set to
	 * inscription objects. Deletes all current documents from the Elasticsearch
	 * node. Indexes each inscription as a JSON document
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		init();

		try {
			// Clear out index before adding inscriptions

			boolean exists = client.admin().indices().prepareExists(ES_INDEX_NAME).execute().actionGet().isExists();

			if (exists) {
				DeleteIndexResponse delete = client.admin().indices().delete(new DeleteIndexRequest(ES_INDEX_NAME))
						.actionGet();
				if (!delete.isAcknowledged()) {
					System.out.println(ES_INDEX_NAME + " index wasn't deleted");
				} else {
					System.out.println(ES_INDEX_NAME + " index successfully deleted");
				}
			}

			client.admin().indices().create(new CreateIndexRequest(ES_INDEX_NAME)).actionGet();
			createMapping();

			PreparedStatement getInscriptions = newDBCon.prepareStatement(SELECT_ALL_INSCRIPTIONS);
			PreparedStatement getProperty = newDBCon.prepareStatement(FindspotDao.SELECT_BY_PROPERTY_ID_STATEMENT);

			ResultSet rs = getInscriptions.executeQuery();

			int rowNum = 0;
			int count = 0;

			List<Inscription> inscriptions = new ArrayList<Inscription>();

			while (rs.next()) {
				Inscription i = INSCRIPTION_ROW_MAPPER.mapRow(rs, rowNum);

				getProperty.setInt(1, i.getAgp().getProperty().getId());

				ResultSet propResults = getProperty.executeQuery();
				if (propResults.next()) {
					Property property = PROPERTY_ROW_MAPPER.mapRow(propResults, 1);
					i.getAgp().setProperty(property);
				}
				inscriptions.add(i);
				propResults.close();
				rowNum++;
				
				XContentBuilder inscriptionBuilder = createContentBuilder(i);

				IndexResponse response = client.prepareIndex(ES_INDEX_NAME, ES_TYPE_NAME).setSource(inscriptionBuilder)
						.execute().actionGet();

				// System.out.println(inscriptionBuilder.string());

				if (response.isCreated()) {
					count++;
					// System.out.println(response.getId() + " " +
					// response.getVersion());
				} else {
					System.out.println("Failed to index document " + count);
				}
			}
			rs.close();
			getInscriptions.close();
			newDBCon.close();

			System.out.println("Looking at " + rowNum + " inscriptions");

			System.out.println(count + " documents indexed");
			client.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static XContentBuilder createContentBuilder(Inscription i) throws IOException {
		Map<String, Object> property = new HashMap<String, Object>();
		Map<String, Object> insula = new HashMap<String, Object>();
		List<Integer> propertyTypes = new ArrayList<>();

		if (i.getAgp().getProperty() != null && i.getAgp().getProperty().getInsula() != null ) {
			propertyTypes = getPropertyTypes(i.getAgp().getProperty().getId());
			insula.put("insula_id", i.getAgp().getProperty().getInsula().getId());
			insula.put("insula_name", i.getAgp().getProperty().getInsula().getShortName());

			property.put("property_id", i.getAgp().getProperty().getId());
			property.put("property_name", i.getAgp().getProperty().getPropertyName());
			property.put("property_number", i.getAgp().getProperty().getPropertyNumber());
			property.put("property_types", propertyTypes);
		}

		Map<String, Object> drawing = new HashMap<String, Object>();
		if (i.getAgp().hasFiguralComponent()) {
			drawing.put("description_in_english", i.getAgp().getFiguralInfo().getDescriptionInEnglish());
			drawing.put("description_in_latin", i.getAgp().getFiguralInfo().getDescriptionInLatin());
			List<Integer> drawingTagIds = getDrawingTagIds(i.getEdrId());
			List<String> drawingTags = getDrawingTags(i.getEdrId());
			drawing.put("drawing_tag_ids", drawingTagIds);
			drawing.put("drawing_tags", drawingTags);
		}

		XContentBuilder inscriptionBuilder = jsonBuilder().startObject().field("id", i.getId())
				.field("city", i.getAncientCity()).field("insula", insula).field("property", property)
				.field("drawing", drawing).field("summary", i.getAgp().getSummary())
				.field("writing_style", i.getWritingStyle()).field("language", i.getLanguage())
				.field("content", i.getContent()).field("edr_id", i.getEdrId())
				.field("bibliography", i.getBibliography()).field("comment", i.getAgp().getCommentary())
				.field("content_translation", i.getAgp().getContentTranslation())
				.field("cil", i.getAgp().getCil())
				.field("has_figural_component", i.getAgp().hasFiguralComponent())
				.field("langner", i.getAgp().getLangner()).field("measurements", i.getMeasurements())
				.field("writing_style_in_english", i.getAgp().getWritingStyleInEnglish())
				.field("language_in_english", i.getAgp().getLanguageInEnglish()).endObject();
		return inscriptionBuilder;
	}

	public static void getConfigurationProperties() {
		Properties prop = Utils.getConfigurationProperties();

		DB_DRIVER = prop.getProperty("db.driverClassName");
		DB_URL = prop.getProperty("db.url");
		DB_USER = prop.getProperty("db.user");
		DB_PASSWORD = prop.getProperty("db.password");
		ELASTIC_SEARCH_LOC = prop.getProperty("es.loc");
		ES_PORT = Integer.parseInt(prop.getProperty("es.port"));
		ES_INDEX_NAME = prop.getProperty("es.index");
		ES_TYPE_NAME = prop.getProperty("es.type");
		ES_CLUSTER_NAME = prop.getProperty("es.cluster_name");
	}

	/**
	 * Returns the list of property type names for an inscription's property in
	 * order to store this information in an Elasticsearch index
	 * 
	 * @param propertyId
	 *            the property id of the inscription's property
	 * @return the list of property type names for an inscription's property
	 */
	private static List<Integer> getPropertyTypes(int propertyId) {
		List<Integer> propertyTypes = new ArrayList<Integer>();

		String selectByPropertyId = FindspotDao.SELECT_PROP_TYPES_BY_PROP_ID;

		try {
			PreparedStatement getPropertyTypesStatement = newDBCon.prepareStatement(selectByPropertyId);
			getPropertyTypesStatement.setInt(1, propertyId);
			ResultSet rs = getPropertyTypesStatement.executeQuery();

			int rowNum = 0;

			while (rs.next()) {
				PropertyType pt = PROPERTY_TYPE_ROW_MAPPER.mapRow(rs, rowNum);
				propertyTypes.add(pt.getId());
				rowNum++;
			}
			rs.close();
			getPropertyTypesStatement.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return propertyTypes;
	}

	/**
	 * Returns the list of drawing tag names for an inscription's drawing in
	 * order to store this information in an Elasticsearch index
	 * 
	 * @param graffitoId
	 *            the inscription's EAGLE id
	 * @return the list of drawing tag names for an inscription's drawing
	 */
	private static List<String> getDrawingTags(String graffitoId) {
		List<String> drawingTags = new ArrayList<String>();
		String selectByGraffitoId = DrawingTagsDao.SELECT_BY_EDR_ID;

		try {
			PreparedStatement getDrawingTagsStatement = newDBCon.prepareStatement(selectByGraffitoId);
			getDrawingTagsStatement.setString(1, graffitoId);
			ResultSet rs = getDrawingTagsStatement.executeQuery();

			int rowNum = 0;

			while (rs.next()) {
				DrawingTag dt = DRAWING_TAG_ROW_MAPPER.mapRow(rs, rowNum);
				drawingTags.add(dt.getName());
				rowNum++;
			}
			rs.close();
			getDrawingTagsStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return drawingTags;
	}

	/**
	 * Returns the list of drawing tag names for an inscription's drawing in
	 * order to store this information in an Elasticsearch index
	 * 
	 * @param graffitoId
	 *            the inscription's EAGLE id
	 * @return the list of drawing tag names for an inscription's drawing
	 */
	private static List<Integer> getDrawingTagIds(String graffitoId) {
		List<Integer> drawingTagIds = new ArrayList<Integer>();
		String selectByGraffitoId = DrawingTagsDao.SELECT_BY_EDR_ID;

		try {
			PreparedStatement getDrawingTagsStatement = newDBCon.prepareStatement(selectByGraffitoId);
			getDrawingTagsStatement.setString(1, graffitoId);
			ResultSet rs = getDrawingTagsStatement.executeQuery();

			int rowNum = 0;

			while (rs.next()) {
				DrawingTag dt = DRAWING_TAG_ROW_MAPPER.mapRow(rs, rowNum);
				drawingTagIds.add(dt.getId());
				rowNum++;
			}
			rs.close();
			getDrawingTagsStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return drawingTagIds;
	}

	/**
	 * initializes the database and Elasticsearch connections
	 * 
	 * @throws IOException
	 */
	private static void init() {

		getConfigurationProperties();

		Settings settings = Settings.settingsBuilder().put("cluster.name", ES_CLUSTER_NAME).build();

		try {
			client = new TransportClient.Builder().settings(settings).build().addTransportAddress(
					new InetSocketTransportAddress(InetAddress.getByName(ELASTIC_SEARCH_LOC), ES_PORT));
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}

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

	/**
	 * Creates the mapping for the inscription type. Doesn't analyze the fields
	 * with special characters that require an exact match
	 * 
	 * @throws IOException
	 */
	private static void createMapping() throws IOException {
		XContentBuilder mapping = jsonBuilder().startObject().startObject(ES_TYPE_NAME).startObject("properties")
				.startObject("id").field("type", "long").endObject().startObject("city").field("type", "string")
				.field("index", "not_analyzed").endObject().startObject("insula") // insula
																					// (name
																					// not
																					// analyzed)
				.startObject("properties").startObject("insula_id").field("type", "long").endObject()
				.startObject("insula_name").field("type", "string").field("index", "not_analyzed").endObject()
				.endObject().endObject().startObject("property") // property
				.startObject("properties").startObject("property_id").field("type", "long").endObject()
				.startObject("property_name").field("type", "string").endObject().startObject("property_number")
				.field("type", "string").endObject().startObject("property_types").field("type", "integer").endObject()
				.endObject().endObject().startObject("drawing") // drawing
				.startObject("properties").startObject("description_in_english").field("type", "string").endObject()
				.startObject("description_in_latin").field("type", "string").endObject().startObject("drawing_tags")
				.field("type", "string").endObject().startObject("drawing_tag_ids").field("type", "integer").endObject()
				.endObject().endObject().startObject("writing_style_in_english").field("type", "string")
				.field("index", "not_analyzed").endObject().startObject("language_in_english") // language,
				// not
				// analyzed
				.field("type", "string").field("index", "not_analyzed").endObject().startObject("content")
				.field("type", "string").endObject().startObject("summary").field("type", "string").endObject()
				.startObject("edr_id").field("type", "string").endObject().startObject("bibliography")
				.field("type", "string").endObject().startObject("cil").field("type", "string").endObject()
				.startObject("comment").field("type", "string").endObject().startObject("content_translation")
				.field("type", "string").endObject().startObject("description_in_english").field("type", "string")
				.endObject().startObject("measurements").field("type", "string").endObject().endObject().endObject()
				.endObject();

		client.admin().indices().preparePutMapping(ES_INDEX_NAME).setType(ES_TYPE_NAME).setSource(mapping).execute()
				.actionGet();
	}

}