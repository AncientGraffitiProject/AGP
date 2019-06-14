package edu.wlu.graffiti.test;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

public class TestSearchQueries {
	
	private static TransportClient client;

	public static void main(String[] args){
		
		Settings settings = Settings.builder().put("cluster.name", "agp-cluster").build();
		
		try {
			client = new PreBuiltTransportClient(settings).addTransportAddress(
					new InetSocketTransportAddress(InetAddress.getByName("carl.cs.wlu.edu"), 9300));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//client = new TransportClient();
		//client.addTransportAddress(new InetSocketTransportAddress("servo.cs.wlu.edu", 9300));
		
		SearchResponse response;
		
		String content = "quartila III";
		String global = "casa III";
		String city = "Herculaneum";
		String insula = "I.8 V";
		String property = "I.8.17 V.35";
		String propertyType = "House Shop";
		String drawingTag = "Geometric designs";
		String writingStyle = "cetera,carbone";// litt. scariph. ";
		String language = "alia latina";
		
		System.out.println("Running ...");
		
		//String measurements = ''; //Need to wait until measurement data representation is determined
		
		List<String> searches = new ArrayList<String>(Arrays.asList(content, global, city, insula, property, 
																	propertyType, drawingTag, writingStyle, language));
		List<String> searchDescs = new ArrayList<String>(Arrays.asList("Content Keyword", "Global Keyword", "City", "Insula", "Property",
																	"Property Type", "Drawing Tag", "Writing Style", "Language"));
		List<String> fields = new ArrayList<String>(Arrays.asList("content", "content city insula.insula_name property.property_name drawing.description"
																	+ " property.property_types drawing.drawing_tags writing_style language"
																	+ " eagle_id bibliography","city", "insula.insula_name", "property.property_name", 
																	"property.property_types", "drawing.drawing_tags", "writing_style", "language"));
		
		//List<Integer> testIf = new ArrayList<Integer>(Arrays.asList(1));
		//List<Integer> testElseIf = new ArrayList<Integer>(Arrays.asList(4));
		//List<Integer> testElse = new ArrayList<Integer>(Arrays.asList(0,2,3,5,6,7,8));
		
		for (int i = 0; i < searches.size(); i++){
		//for (int i : testIf){
		//for (int i : testElseIf){
		//for (int i : testElse){
			System.out.println("Testing Search by " + searchDescs.get(i) + " (" + searches.get(i) + ")\n");
			
			QueryBuilder query;
			BoolQueryBuilder boolQuery = null;
			
			if (searchDescs.get(i).equals("Global Keyword")){
				String[] a = fields.get(i).split(" ");
				query = multiMatchQuery(searches.get(i), a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8]).fuzziness("AUTO");
				
				response = client.prepareSearch("agp")
						.setTypes("inscription")
						.setQuery(query)
						.addStoredField("id")
						.setSize(66)
						.addSort("id", SortOrder.ASC)
						.execute()
						.actionGet();
			}
			else if (searchDescs.get(i).equals("Property")){
				query = null;
				
				boolQuery = boolQuery();
				
				String[] properties = searches.get(i).split(" ");
				
				for (int j = 0; j < properties.length; j++){
					QueryBuilder insulaQuery;
					QueryBuilder propertyQuery;
					BoolQueryBuilder shouldQuery;
					String insAndProp = properties[j];
					int ind = insAndProp.lastIndexOf(".");
					String insula_name = insAndProp.substring(0, ind);
					String property_number = insAndProp.substring(ind+1);
					insulaQuery = matchQuery("insula.insula_name", insula_name);
					propertyQuery = matchQuery("property.property_number", property_number);
					shouldQuery = boolQuery();
					shouldQuery.must(insulaQuery);
					shouldQuery.must(propertyQuery);
					
					boolQuery.should(shouldQuery);
				}
				response = client.prepareSearch("agp")
						.setTypes("inscription")
						.setQuery(boolQuery)
						.addStoredField("id")
						.setSize(66)
						.addSort("id", SortOrder.ASC)
						.execute()
						.actionGet();
			}
			else {
				query = matchQuery(fields.get(i), searches.get(i)).fuzziness("AUTO");
				response = client.prepareSearch("agp")
						.setTypes("inscription")
						.setQuery(query)
						//.addStoredField("id")
						.setSize(66)
						.addSort("id", SortOrder.ASC)
						.execute()
						.actionGet();
			}

			int count = 1;
			for (SearchHit hit : response.getHits()){
				System.out.println(hit.getSourceAsString());
				int id = hit.getField("id").getValue();
				System.out.println("Result " + count + ": id=" + id);
				count++;
			}
			count -= 1;
			System.out.println("\n" + count + " total results for this search.\n\n--------------------------------\n");
		}
		
		
		
		System.out.println("Testing Combination Search 1");
		//Write tests for various combination searches
		
		BoolQueryBuilder query = boolQuery();
		
		//Search V.35 and I.8.17
		BoolQueryBuilder locationQuery = boolQuery();
		BoolQueryBuilder locationQuery1 = boolQuery();
		BoolQueryBuilder locationQuery2 = boolQuery();
		QueryBuilder propertyQuery1;
		QueryBuilder propertyQuery2;
		QueryBuilder insulaQuery1;
		QueryBuilder insulaQuery2;
		
		insulaQuery1 = matchQuery("insula.insula_name", "I.8");
		insulaQuery2 = matchQuery("insula.insula_name", "V");
		propertyQuery1 = matchQuery("property.property_number", "17");
		propertyQuery2 = matchQuery("property.property_number", "35");
		
		locationQuery1.must(insulaQuery1);
		locationQuery1.must(propertyQuery1);
		locationQuery2.must(insulaQuery2);
		locationQuery2.must(propertyQuery2);
		
		locationQuery.should(locationQuery1);
		locationQuery.should(locationQuery2);
				
		query.must(locationQuery);
		
		//Search for Boats and Gladiators/equipment drawing tags
		QueryBuilder drawingTagQuery = matchQuery("drawing.drawing_tags", "boats Gladiators/equipment");
		
		query.must(drawingTagQuery);
		
		//Search for navis and viri in drawing description
		QueryBuilder drawingDescriptionQuery = matchQuery("drawing.description", "navis viri");
		
		query.must(drawingDescriptionQuery);
		
		response = client.prepareSearch("agp")
				.setTypes("inscription")
				.setQuery(query)
				.addStoredField("id")
				.setSize(66)
				.addSort("id", SortOrder.ASC)
				.execute()
				.actionGet();
		
		int count = 1;
		for (SearchHit hit : response.getHits()){
			int id = hit.field("id").value();
			System.out.println("Result " + count + ": id=" + id);
			count++;
		}
		count -= 1;
		System.out.println("\n" + count + " total results for this search.\n\n--------------------------------\n");
		
		client.close();
	}
	
}