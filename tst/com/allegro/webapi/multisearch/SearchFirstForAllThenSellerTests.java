package com.allegro.webapi.multisearch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.allegro.webapi.SearchResponseType;
import com.allegro.webapi.SellerInfoStruct;
import com.allegro.webapi.multisearch.strategies.SearchFirstForAllThenBySeller;
import com.allegro.webapi.multisearch.strategies.SearchStrategy;

public class SearchFirstForAllThenSellerTests {
	
	private final static SellerInfoStruct A = createSeller("A");
	private final static SellerInfoStruct B = createSeller("B");
	private final static SellerInfoStruct C = createSeller("C");
	
	@Test
	public void testSearch3QueriesSimpleResults() throws RemoteException {
		// given
		MultiSearchClient client = mock(MultiSearchClient.class);
		when(client.search("a", null)).thenReturn(createSearchResponse("a,A"));
		when(client.search("b", A)).thenReturn(createSearchResponse("b,A"));
		when(client.search("c", A)).thenReturn(createSearchResponse("c,A"));
		SearchStrategy strategy = new SearchFirstForAllThenBySeller(client);
		strategy.setSearchQueries(new String[] {"a", "b", "c"});
		
		// when
		Map<SellerInfoStruct, List<List<SearchResponseType>>> result = strategy.execute();
		
		// then
		assertResult(result, "A:a;b;c");
	}
	
	@Test
	public void testSearchNoQueries() throws RemoteException {
		// given
		MultiSearchClient client = mock(MultiSearchClient.class);
		when(client.search("a", null)).thenReturn(createSearchResponse("a,A"));
		SearchStrategy strategy = new SearchFirstForAllThenBySeller(client);
		strategy.setSearchQueries(new String[] {});
		
		// when
		Map<SellerInfoStruct, List<List<SearchResponseType>>> result = strategy.execute();
		
		// then
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void testSearchSingleQueryNoResult() throws RemoteException {
		// given
		MultiSearchClient client = mock(MultiSearchClient.class);
		when(client.search("a", null)).thenReturn(Collections.<SearchResponseType>emptyList());
		SearchStrategy strategy = new SearchFirstForAllThenBySeller(client);
		strategy.setSearchQueries(new String[] {"a"});
		
		// when
		Map<SellerInfoStruct, List<List<SearchResponseType>>> result = strategy.execute();
		
		// then
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void testSearch3QueriesNoSellerMatchingAll() throws RemoteException {
		// given
		MultiSearchClient client = mock(MultiSearchClient.class);
		when(client.search("a", null)).thenReturn(createSearchResponse("a,A", "a,B"));
		when(client.search("b", A)).thenReturn(createSearchResponse("b,A"));
		when(client.search("b", B)).thenReturn(Collections.<SearchResponseType>emptyList());
		when(client.search("c", A)).thenReturn(Collections.<SearchResponseType>emptyList());
		SearchStrategy strategy = new SearchFirstForAllThenBySeller(client);
		strategy.setSearchQueries(new String[] {"a", "b", "c"});
		
		// when
		Map<SellerInfoStruct, List<List<SearchResponseType>>> result = strategy.execute();
		
		// then
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void testSearch3Queries3Sellers2Matching() throws RemoteException {
		// given
		MultiSearchClient client = mock(MultiSearchClient.class);
		when(client.search("a", null)).thenReturn(createSearchResponse("a,A", "a,B", "a,C"));
		when(client.search("b", A)).thenReturn(createSearchResponse("b,A"));
		when(client.search("b", B)).thenReturn(createSearchResponse("b,B"));
		when(client.search("b", C)).thenReturn(createSearchResponse("b,C"));
		when(client.search("c", A)).thenReturn(createSearchResponse("c,A"));
		when(client.search("c", B)).thenReturn(Collections.<SearchResponseType>emptyList());
		when(client.search("c", C)).thenReturn(createSearchResponse("c,C"));
		SearchStrategy strategy = new SearchFirstForAllThenBySeller(client);
		strategy.setSearchQueries(new String[] {"a", "b", "c"});
		
		// when
		Map<SellerInfoStruct, List<List<SearchResponseType>>> result = strategy.execute();
		
		// then
		assertResult(result, "A:a;b;c", "C:a;b;c");
	}
	
	@Test
	public void testSearchMoreItemsForSeller() throws RemoteException {
		// given
		MultiSearchClient client = mock(MultiSearchClient.class);
		when(client.search("a", null)).thenReturn(createSearchResponse("a1,A", "a2,A", "a,B"));
		when(client.search("b", A)).thenReturn(createSearchResponse("b,A"));
		when(client.search("b", B)).thenReturn(createSearchResponse("b,B"));
		when(client.search("c", A)).thenReturn(createSearchResponse("c,A"));
		when(client.search("c", B)).thenReturn(createSearchResponse("c1,B", "c2,B"));
		SearchStrategy strategy = new SearchFirstForAllThenBySeller(client);
		strategy.setSearchQueries(new String[] {"a", "b", "c"});
		
		// when
		Map<SellerInfoStruct, List<List<SearchResponseType>>> result = strategy.execute();
		
		// then
		assertResult(result, "A:a1,a2;b;c", "B:a;b;c1,c2");
	}
	
	private static List<SearchResponseType> createSearchResponse(
			String... items) {
		List<SearchResponseType> result = new ArrayList<SearchResponseType>();
		for (String item : items) {
			String[] split = item.split(",");
			SearchResponseType searchResponseType = new SearchResponseType();
			searchResponseType.setSItName(split[0]);
			searchResponseType.setSItSellerInfo(createSeller(split[1]));
			result.add(searchResponseType);
		}
		return result;
	}
	
	private static SellerInfoStruct createSeller(String sellerName) {
		return new SellerInfoStruct(1, sellerName, 0, 0);
	}
	
	private static void assertResult(Map<SellerInfoStruct, List<List<SearchResponseType>>> result, String... expectedResults) {
		assertFalse(result.isEmpty());
		for (String expectedResult : expectedResults) {
			String[] split = expectedResult.split(":");
			String sellerName = split[0];
			String[] groups = split[1].split(";");
			SellerInfoStruct seller = createSeller(sellerName);
			assertTrue(result.containsKey(seller));
			assertFalse(result.get(seller).isEmpty());
			for (int i = 0; i < groups.length; i++) {
				String[] items = groups[i].split(",");
				assertEquals(items.length, result.get(seller).get(i).size());
				for (int j = 0; j < items.length; j++) {
					assertEquals(items[j], result.get(seller).get(i).get(j).getSItName());
				}
			}
		}
	}
}
