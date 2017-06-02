import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class JacksonTester2 {
	public static void main(String args[]) {
		
		JacksonTester2 tester = new JacksonTester2();
	
		try {
				ObjectMapper mapper = new ObjectMapper();
				
				JsonFactory jasonFactory = new JsonFactory();
				JsonParser jsonParser = jasonFactory.createParser(
						new File("src/main/resources/geoJSON/eschebach.json"));
				
				// this accesses the 'features' level of the eschebach document
				JsonNode root = mapper.readTree(jsonParser);
				JsonNode featuresNode = root.path("features");	
				
				// this will iterate through all the elements in featuresNode
				// the elements are JSON objects (graffiti)
				Iterator<JsonNode> iterator = featuresNode.elements();
				while (iterator.hasNext()) {
					ObjectNode graffito = (ObjectNode)iterator.next();
					ObjectNode properties = (ObjectNode)graffito.path("properties");
					properties.put("NEW_FIELD", "random text");
					JsonNode graffitoProp = (JsonNode)properties;
					graffito.set("properties", graffitoProp);
					System.out.print(graffito);
				}	
		}
		
		catch (JsonParseException e) {e.printStackTrace(); }
		catch (JsonMappingException e) {e.printStackTrace(); }
		catch (IOException e) {e.printStackTrace(); }
	}
	
}


