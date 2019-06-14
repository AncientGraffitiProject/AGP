package edu.wlu.graffiti.test;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

/**
 * Tests match query by content keyword
 * 
 * @author cooperbaird
 */
public class TestQueriesUpdated
{
	private static TransportClient client;
	
	public static void main(String[] args)
	{
		Settings settings = Settings.builder().put("cluster.name", "agp-cluster").build();
		SearchResponse response;
		
		try
		{
			client = new PreBuiltTransportClient(settings).addTransportAddress(
					new InetSocketTransportAddress(InetAddress.getByName("carl.cs.wlu.edu"), 9300));
		}
		catch(UnknownHostException e)
		{
			e.printStackTrace();
		}
		
		// Test search by content keyword
		
		QueryBuilder query = matchQuery("content", "LXXII").fuzziness("AUTO"); // keyword: LXXII
		
		response = client.prepareSearch("agp")
				.setTypes("inscription")
				.setQuery(query)
				.addStoredField("edr_id") // field has to be stored in AddInscriptionsToElasticSearch first
				.setSize(66)
				.addSort("id", SortOrder.ASC)
				.execute()
				.actionGet();
		
		int count = 1;
		
		for(SearchHit hit : response.getHits())
		{
			String id = hit.getField("edr_id").getValue();
			System.out.println("Result " + count + ": id=" + id);
			count++;
		}
		
		count -= 1;
		System.out.println("\n" + count + " total results for this search.");
		
		client.close();
	}
}
