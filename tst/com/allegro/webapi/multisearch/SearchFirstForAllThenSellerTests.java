package com.allegro.webapi.multisearch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.allegro.webapi.SearchResponseType;
import com.allegro.webapi.SellerInfoStruct;

public class SearchFirstForAllThenSellerTests {
	
	private final static SellerInfoStruct A = createSeller("A");
	
	@Test
	public void testSearch3Queries() throws RemoteException {
		// given
		MultiSearchClient client = mock(MultiSearchClient.class);
		when(client.getSearchQueries()).thenReturn(Arrays.asList(new String[] {"a", "b", "c"}));
		when(client.search("a", null)).thenReturn(createSearchResponse("a", "A"));
		when(client.search("b", A)).thenReturn(createSearchResponse("b", "A"));
		when(client.search("c", A)).thenReturn(createSearchResponse("c", "A"));
		SearchFirstForAllThenBySeller strategy = new SearchFirstForAllThenBySeller(client);
		
		// when
		Map<SellerInfoStruct, List<SearchResponseType>> result = strategy.search();
		
		// then
		assertFalse(result.isEmpty());
		assertTrue(result.containsKey(A));
		assertFalse(result.get(A).isEmpty());
		assertEquals("a", result.get(A).get(0).getSItName());
		assertEquals("b", result.get(A).get(1).getSItName());
		assertEquals("c", result.get(A).get(2).getSItName());
	}
	
	@Test
	public void testSearchNoQueries() throws RemoteException {
		// given
		MultiSearchClient client = mock(MultiSearchClient.class);
		when(client.getSearchQueries()).thenReturn(Arrays.asList(new String[] {}));
		SearchFirstForAllThenBySeller strategy = new SearchFirstForAllThenBySeller(client);
		
		// when
		Map<SellerInfoStruct, List<SearchResponseType>> result = strategy.search();
		
		// then
		assertTrue(result.isEmpty());
	}
	
	private static List<SearchResponseType> createSearchResponse(String itemName, String sellerName) {
		SearchResponseType searchResponseType = new SearchResponseType();
		searchResponseType.setSItName(itemName);
		searchResponseType.setSItSellerInfo(createSeller(sellerName));
		List<SearchResponseType> result = new ArrayList<SearchResponseType>();
		result.add(searchResponseType);
		return result;
	}
	
	private static SellerInfoStruct createSeller(String sellerName) {
		return new SellerInfoStruct(1, sellerName, 0, 0);
	}
}
