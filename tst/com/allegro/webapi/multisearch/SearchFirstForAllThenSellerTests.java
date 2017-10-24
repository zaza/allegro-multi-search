package com.allegro.webapi.multisearch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.allegro.webapi.ItemInfo;
import com.allegro.webapi.ItemsListType;
import com.allegro.webapi.UserInfoType;
import com.allegro.webapi.multisearch.strategies.SearchFirstForAllThenBySeller;
import com.allegro.webapi.multisearch.strategies.SearchStrategy;
import com.github.zaza.allegro.Item;

public class SearchFirstForAllThenSellerTests {
	
	@Test
	public void testSearch3QueriesSimpleResults() throws RemoteException {
		// given
		MultiSearchClient client = mock(MultiSearchClient.class);
		when(client.search("a", null)).thenReturn(createSearchResponse("a,1"));
		when(client.search("b", 1)).thenReturn(createSearchResponse("b,1"));
		when(client.search("c", 1)).thenReturn(createSearchResponse("c,1"));
		SearchStrategy strategy = new SearchFirstForAllThenBySeller(client);
		strategy.setSearchQueries(new String[] {"a", "b", "c"});
		
		// when
		Map<UserInfoType, List<List<Item>>> result = strategy.execute();
		
		// then
		assertResult(result, "1:a;b;c");
	}
	
	@Test
	public void testSearchNoQueries() throws RemoteException {
		// given
		MultiSearchClient client = mock(MultiSearchClient.class);
		when(client.search("a", null)).thenReturn(createSearchResponse("a,1"));
		SearchStrategy strategy = new SearchFirstForAllThenBySeller(client);
		strategy.setSearchQueries(new String[] {});
		
		// when
		Map<UserInfoType, List<List<Item>>> result = strategy.execute();
		
		// then
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void testSearchSingleQueryNoResult() throws RemoteException {
		// given
		MultiSearchClient client = mock(MultiSearchClient.class);
		when(client.search("a", null)).thenReturn(Collections.<Item>emptyList());
		SearchStrategy strategy = new SearchFirstForAllThenBySeller(client);
		strategy.setSearchQueries(new String[] {"a"});
		
		// when
		Map<UserInfoType, List<List<Item>>> result = strategy.execute();
		
		// then
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void testSearch3QueriesNoSellerMatchingAll() throws RemoteException {
		// given
		MultiSearchClient client = mock(MultiSearchClient.class);
		when(client.search("a", null)).thenReturn(createSearchResponse("a,1", "a,2"));
		when(client.search("b", 1)).thenReturn(createSearchResponse("b,1"));
		when(client.search("b", 2)).thenReturn(Collections.<Item>emptyList());
		when(client.search("c", 1)).thenReturn(Collections.<Item>emptyList());
		SearchStrategy strategy = new SearchFirstForAllThenBySeller(client);
		strategy.setSearchQueries(new String[] {"a", "b", "c"});
		
		// when
		Map<UserInfoType, List<List<Item>>> result = strategy.execute();
		
		// then
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void testSearch3Queries3Sellers2Matching() throws RemoteException {
		// given
		MultiSearchClient client = mock(MultiSearchClient.class);
		when(client.search("a", null)).thenReturn(createSearchResponse("a,1", "a,2", "a,3"));
		when(client.search("b", 1)).thenReturn(createSearchResponse("b,1"));
		when(client.search("b", 2)).thenReturn(createSearchResponse("b,2"));
		when(client.search("b", 3)).thenReturn(createSearchResponse("b,3"));
		when(client.search("c", 1)).thenReturn(createSearchResponse("c,1"));
		when(client.search("c", 2)).thenReturn(Collections.<Item>emptyList());
		when(client.search("c", 3)).thenReturn(createSearchResponse("c,3"));
		SearchStrategy strategy = new SearchFirstForAllThenBySeller(client);
		strategy.setSearchQueries(new String[] {"a", "b", "c"});
		
		// when
		Map<UserInfoType, List<List<Item>>> result = strategy.execute();
		
		// then
		assertResult(result, "1:a;b;c", "3:a;b;c");
	}
	
	@Test
	public void testSearchMoreItemsForSeller() throws RemoteException {
		// given
		MultiSearchClient client = mock(MultiSearchClient.class);
		when(client.search("a", null)).thenReturn(createSearchResponse("a1,1", "a2,1", "a,2"));
		when(client.search("b", 1)).thenReturn(createSearchResponse("b,1"));
		when(client.search("b", 2)).thenReturn(createSearchResponse("b,2"));
		when(client.search("c", 1)).thenReturn(createSearchResponse("c,1"));
		when(client.search("c", 2)).thenReturn(createSearchResponse("c1,2", "c2,2"));
		SearchStrategy strategy = new SearchFirstForAllThenBySeller(client);
		strategy.setSearchQueries(new String[] {"a", "b", "c"});
		
		// when
		Map<UserInfoType, List<List<Item>>> result = strategy.execute();
		
		// then
		assertResult(result, "1:a1,a2;b;c", "2:a;b;c1,c2");
	}
	
	private static List<Item> createSearchResponse(
			String... items) {
		List<Item> result = new ArrayList<Item>();
		for (String item : items) {
			String[] split = item.split(",");
			ItemsListType itemsListType = new ItemsListType();
			itemsListType.setItemTitle(split[0]);
			itemsListType.setSellerInfo(createSeller(Integer.parseInt(split[1])));
			Item searchResponseType = new Item(itemsListType, new ItemInfo());
			result.add(searchResponseType);
		}
		return result;
	}
	
	private static UserInfoType createSeller(int userId) {
		return new UserInfoType(userId, String.valueOf(userId), 0, 0, 0);
	}
	
	private static void assertResult(Map<UserInfoType, List<List<Item>>> result, String... expectedResults) {
		assertFalse(result.isEmpty());
		for (String expectedResult : expectedResults) {
			String[] split = expectedResult.split(":");
			String sellerName = split[0];
			String[] groups = split[1].split(";");
			UserInfoType seller = createSeller(Integer.parseInt(sellerName));
			assertTrue(result.containsKey(seller));
			assertFalse(result.get(seller).isEmpty());
			for (int i = 0; i < groups.length; i++) {
				String[] items = groups[i].split(",");
				assertEquals(items.length, result.get(seller).get(i).size());
				for (int j = 0; j < items.length; j++) {
					assertEquals(items[j], result.get(seller).get(i).get(j).getItemTitle());
				}
			}
		}
	}
}
