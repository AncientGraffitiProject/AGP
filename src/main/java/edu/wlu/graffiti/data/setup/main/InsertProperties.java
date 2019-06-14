package edu.wlu.graffiti.data.setup.main;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.math.NumberUtils;

import edu.wlu.graffiti.bean.PropertyType;
import edu.wlu.graffiti.data.setup.Utils;

/**
 * Insert properties from a CSV file into the database; Also insert property
 * type mappings and OSM ids
 * 
 * @author Sara Sprenkle
 * 
 */
public class InsertProperties {

	private static final int OSM_WAY_ID_LOC = 10;
	private static final int OSM_ID_LOC = 9;
	private static final int PROPERTY_TYPE_LOC = 7;
	private static final int PARCO_ARCHELOGICO_LINK_LOC = 12;
	private static final int HERC_PANORAMAS_LINK_LOC = 11;
	private static String DB_DRIVER;
	private static String DB_URL;
	private static String DB_USER;
	private static String DB_PASSWORD;

	private static final String INSERT_PROPERTY_STMT = "INSERT INTO properties "
			+ "(insula_id, property_number, additional_properties, property_name, english_property_name, italian_property_name) "
			+ "VALUES (?,?,?,?,?,?)";

	private static final String INSERT_PROPERTY_LINK = "INSERT INTO property_links "
			+ "(property_id, link_name, link) values (?,?,?)";
	private static final String UPDATE_OSM_ID = "UPDATE properties SET osm_id = ? WHERE id = ?";
	private static final String UPDATE_OSM_WAY_ID = "UPDATE properties SET osm_way_id = ? WHERE id = ?";

	private static final String LOOKUP_INSULA_ID = "SELECT id from insula WHERE modern_city=? AND short_name=?";

	private static final String LOOKUP_PROP_ID = "SELECT id FROM properties "
			+ "WHERE insula_id=? AND property_number = ?";

	private static final String INSERT_PROPERTY_TYPE_MAPPING = "INSERT INTO propertyToPropertyType VALUES (?,?)";

	private static final String GET_PROPERTY_TYPES = "SELECT id, name, commentary FROM propertyTypes";
	
	private static String HERCULANEUM_PANORAMAS = "Herculaneum Panoramas";
	private static String PARCO_ARCHEOLOGICO = "Parco Archeologico";

	private static PreparedStatement selectInsulaStmt;
	private static PreparedStatement selectPropStmt;

	static Connection newDBCon;
	private static PreparedStatement propertyLinkStmt;

	public static void main(String[] args) {
		init();

		try {
			insertProperties("data/pompeii_properties.csv");
			insertProperties("data/herculaneum_properties.csv");

			newDBCon.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	private static void insertProperties(String datafileName) {
		int count = 0;
		try {
			PreparedStatement pstmt = newDBCon.prepareStatement(INSERT_PROPERTY_STMT);
			PreparedStatement insertPTStmt = newDBCon.prepareStatement(INSERT_PROPERTY_TYPE_MAPPING);
			PreparedStatement osmStmt = newDBCon.prepareStatement(UPDATE_OSM_ID);
			PreparedStatement osmWayStmt = newDBCon.prepareStatement(UPDATE_OSM_WAY_ID);
			List<PropertyType> propertyTypes = getPropertyTypes();

			Reader in = new FileReader(datafileName);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			for (CSVRecord record : records) {
				count += 1;
				System.out.println(count);
				String modernCity = record.get(0).trim();
				String insula = record.get(1).trim();
				String propertyNumber = "";
				String additionalProperties = "";
				String propertyName = "";
				String englishPropName = "";
				String italianPropName = "";

				if (modernCity.isEmpty()) {
					System.err.println("Likely blank line, continuing ...");
					continue;
				}

				if (record.size() > 2) {
					propertyNumber = record.get(2).trim();
				}
				if (record.size() > 3) {
					additionalProperties = record.get(3).trim();
				}
				if (record.size() > 4) {
					propertyName = record.get(4).trim();
				}
				if (record.size() > 5) {
					englishPropName = record.get(5).trim();
				}
				if (record.size() > 6) {
					italianPropName = record.get(6).trim();
				}
				int insula_id = lookupInsulaId(modernCity, insula);

				if (insula_id == 0) {
					System.err.println("Failed when looking up insula: " + modernCity + " " + insula + "\n" + record);
					System.err.println("Skipping...");
					continue;
				}

				pstmt.setInt(1, insula_id);
				pstmt.setString(2, propertyNumber);
				pstmt.setString(3, additionalProperties);
				pstmt.setString(4, propertyName);
				pstmt.setString(5, englishPropName);
				pstmt.setString(6, italianPropName);

				try {
					pstmt.executeUpdate();
				} catch (SQLException e) {
					System.err.println("Error for propertyName: " + propertyName + " propertyNum: " + propertyNumber
							+ " insula_id: " + insula_id + " italian property name " + italianPropName
							+ " additional properties " + additionalProperties);
					e.printStackTrace();
				}

				int propID = locatePropertyId(insula_id, propertyNumber);

				if (propID == 0) {
					System.err.println("Property not in DB: " + insula_id + " " + propertyNumber + " " + propertyName);
					System.err.println("Skipping...");
					continue;
				}

				// handle property tags
				if (record.size() > PROPERTY_TYPE_LOC) {
					String[] tagArray = record.get(PROPERTY_TYPE_LOC).trim().split(",");
					addPropertyTags(insertPTStmt, propertyTypes, propID, tagArray);
				}

				// handle adding OSM ids and OSM WAY ids
				if (record.size() > OSM_ID_LOC) {

					String osmId = record.get(OSM_ID_LOC).trim();
					if (!osmId.isEmpty() && NumberUtils.isCreatable(osmId)) {
						osmStmt.setInt(2, propID);
						osmStmt.setString(1, osmId);
						osmStmt.executeUpdate();
					}

					if (record.size() > OSM_WAY_ID_LOC) {
						String osmWayId = record.get(OSM_WAY_ID_LOC).trim();
						if (!osmWayId.isEmpty() && NumberUtils.isCreatable(osmWayId)) {
							osmWayStmt.setInt(2, propID);
							osmWayStmt.setString(1, osmWayId);
							osmWayStmt.executeUpdate();
						}
					}
				}

				// handle links about the properties
				if (record.size() > HERC_PANORAMAS_LINK_LOC) {
					String hercPanaLink = record.get(HERC_PANORAMAS_LINK_LOC).trim();
					if (!hercPanaLink.isEmpty()) {
						String links[] = hercPanaLink.split("\\s+");
						for (String link : links) {
							insertLink(propID, HERCULANEUM_PANORAMAS, link);
						}
					}

					String parcoArcheologicLink = record.get(PARCO_ARCHELOGICO_LINK_LOC).trim();
					if (!parcoArcheologicLink.isEmpty()) {
						String links[] = parcoArcheologicLink.split("\\s+");
						for (String link : links) {
							insertLink(propID, PARCO_ARCHEOLOGICO, link);
						}
					}
				}

			}
			in.close();
			pstmt.close();
			insertPTStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(count + " properties");
	}

	public static void addPropertyTags(PreparedStatement insertPTStmt, List<PropertyType> propertyTypes, int propID,
			String[] tagArray) throws SQLException {
		for (String tag : tagArray) {
			tag = tag.trim();
			for (PropertyType propType : propertyTypes) {
				if (propType.includes(tag)) {
					// System.out.println("Match! " +
					// propType.getName() + " for propID " +
					// propID);
					insertPTStmt.setInt(1, propID);
					insertPTStmt.setInt(2, propType.getId());
					// Wrapped in try to handle the "duplicate key
					// errors" that so often occur.
					try {
						insertPTStmt.executeUpdate();
					} catch (SQLException e) {
						System.err.println("Duplicate entry, likely caused by synonyms in the property types.");
						e.printStackTrace();
					}
				}
			}
		}
	}

	private static int insertLink(int property_id, String linkname, String link) {
		try {
			propertyLinkStmt.setInt(1, property_id);
			propertyLinkStmt.setString(2, linkname);
			propertyLinkStmt.setString(3, link);
			return propertyLinkStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	private static int locatePropertyId(int insula_id, String propertyNumber) {
		int propID = 0;
		try {
			selectPropStmt.setInt(1, insula_id);
			selectPropStmt.setString(2, propertyNumber);

			ResultSet propRS = selectPropStmt.executeQuery();
			if (propRS.next()) {
				propID = propRS.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return propID;
	}

	public static int lookupInsulaId(String modernCity, String insula) {
		int insula_id = 0;
		try {
			selectInsulaStmt.setString(1, modernCity);
			selectInsulaStmt.setString(2, insula);

			ResultSet insulaSet = selectInsulaStmt.executeQuery();
			if (insulaSet.next()) {
				insula_id = insulaSet.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return insula_id;
	}

	private static List<PropertyType> getPropertyTypes() {
		List<PropertyType> propTypes = new ArrayList<PropertyType>();

		try {
			PreparedStatement pstmt = newDBCon.prepareStatement(GET_PROPERTY_TYPES);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				int propTypeId = rs.getInt(1);
				PropertyType pt = new PropertyType();
				pt.setId(propTypeId);
				pt.setName(rs.getString(2));
				pt.setDescription(rs.getString(3));
				propTypes.add(pt);
			}

			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return propTypes;
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
			selectInsulaStmt = newDBCon.prepareStatement(LOOKUP_INSULA_ID);
			selectPropStmt = newDBCon.prepareStatement(LOOKUP_PROP_ID);
			propertyLinkStmt = newDBCon.prepareStatement(INSERT_PROPERTY_LINK);
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
