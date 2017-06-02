package edu.wlu.graffiti.data.setup;

import java.util.Iterator;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.wlu.graffiti.dao.FindspotDao;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



/**
 * 
 * @author Alicia Martinez
 *
 */
public class DatabaseToJson {

	final static String newDBURL = "jdbc:postgresql://hopper.cs.wlu.edu/graffiti5";

	final static String SELECT_PROPERTY = FindspotDao.SELECT_BY_CITY_AND_INSULA_AND_PROPERTY_STATEMENT;
	
	final static String GET_PROPERTY_TYPE = "SELECT * FROM properties, propertytypes," 
			+ " propertytopropertytype WHERE properties.id = propertytopropertytype.property_id"
			+ " AND propertytypes.id = propertytopropertytype.property_type AND properties.id = ?";

	static Connection newDBCon;

	private static PreparedStatement selectPropertyStatement;
	private static PreparedStatement getPropertyTypeStatement;
	
	public static void main(String args[]) {
		
		init();
		
		try {
			// creates the file we will later write the updated graffito to
			PrintWriter writer = new PrintWriter("src/main/webapp/resources/js/updatedEschebach.txt", "UTF-8");
			
			// creates necessary objects to parse the original eschebach file
			ObjectMapper mapper = new ObjectMapper();
			JsonFactory jasonFactory = new JsonFactory();
			JsonParser jsonParser = jasonFactory.createParser(
					new File("src/main/resources/geoJSON/eschebach.json"));
			
			// this accesses the 'features' level of the eschebach document
			JsonNode root = mapper.readTree(jsonParser);
			JsonNode featuresNode = root.path("features");
						
			// iterates over the node
			Iterator<JsonNode> iterator = featuresNode.elements();
			while (iterator.hasNext()) {
				JsonNode field = iterator.next();
				String fieldText = field.toString();
				
				// converts the above string into an InputStream so I can use it in
				// the json parser to iterate through the different tokens
				InputStream stream = new ByteArrayInputStream(fieldText.getBytes(StandardCharsets.UTF_8));
				
				JsonParser parseLine = jasonFactory.createParser(stream);
				while (parseLine.nextToken() != JsonToken.END_OBJECT) {
					String fieldname = parseLine.getCurrentName();
					
					// when the token is the PRIMARY_DO field, we go to the next
					// token and that is the value
					if("PRIMARY_DO".equals(fieldname)) {
						parseLine.nextToken();
						String primarydo = parseLine.getText();
						if (!primarydo.contains(".")) {continue;}
						String[] parts = primarydo.split("\\.");
						
						String pt1 = parts[0];
						String pt2 = parts[1];
						String pt3 = parts[2];
						
						String insulaName = pt1 + "." + pt2;
						String propertyNum = pt3;
						try {
							selectPropertyStatement.setString(1, "Pompeii");
							selectPropertyStatement.setString(2, insulaName);
							selectPropertyStatement.setString(3, propertyNum);
							ResultSet rs = selectPropertyStatement.executeQuery();
							
							if (rs.next()) {
								int propertyId = rs.getInt("id");
								String propertyName = rs.getString("property_name");
								String addProperties = rs.getString("additional_properties");
								String italPropName = rs.getString("italian_property_name");
								String insulaDescription = rs.getString("description");
								String insulaPleiadesId = rs.getString("insula_pleiades_id");
								String propPleiadesId = rs.getString("property_pleiades_id");
								
								getPropertyTypeStatement.setInt(1, propertyId);
								ResultSet resultset = getPropertyTypeStatement.executeQuery();
								String propertyType = "";
								if (resultset.next()) {
									propertyType = resultset.getString("name");	
								}
								
								ObjectNode graffito = (ObjectNode)field;
								ObjectNode properties = (ObjectNode)graffito.path("properties");
								properties.put("Property_Id", propertyId);
								properties.put("Property_Name", propertyName);
								properties.put("Additional_Properties", addProperties);
								properties.put("Italian_Property_Name", italPropName);
								properties.put("Insula_Description", insulaDescription);
								properties.put("Insula_Pleiades_Id", insulaPleiadesId);
								properties.put("Property_Pleiades_Id", propPleiadesId);
								properties.put("Property_Type", propertyType);
								
								JsonNode updatedProps = (JsonNode)properties;
								graffito.set("properties", updatedProps);
								
								// write the newly updated graffito to text file
								System.out.println(graffito);
								writer.println(graffito + ",");
							}
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
 				}
			}
			writer.close();
		}
		
		catch (JsonParseException e) {e.printStackTrace();}
		catch (JsonMappingException e) {e.printStackTrace();}
		catch (IOException e) {e.printStackTrace();}
	}
	
	private static void init() {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			newDBCon = DriverManager.getConnection(newDBURL, "web", "");

			selectPropertyStatement = newDBCon
					.prepareStatement(SELECT_PROPERTY);
			
			getPropertyTypeStatement = newDBCon.prepareStatement(GET_PROPERTY_TYPE);

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
}
