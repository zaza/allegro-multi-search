package com.allegro.webapi.multisearch.strategies;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.allegro.webapi.SearchResponseType;
import com.allegro.webapi.SellerInfoStruct;
import com.allegro.webapi.multisearch.MultiSearchClient;

public abstract class SearchStrategy {

	protected MultiSearchClient client;
	private List<String> searchQueries;

	public SearchStrategy(MultiSearchClient client) {
		this.client = client;
	}

	public void setSearchQueries(String... searchQueries) {
		this.searchQueries = Arrays.asList(searchQueries);
	}

	public List<String> getSearchQueries() {
		return this.searchQueries;
	}

	final public Map<SellerInfoStruct, List<List<SearchResponseType>>> execute()
			throws RemoteException {
		long start = System.currentTimeMillis();
		try {
			return search();
		} finally {
			long end = System.currentTimeMillis();
			client.println("Executed in " + (end - start) + " ms.");
		}
	}

	protected abstract Map<SellerInfoStruct, List<List<SearchResponseType>>> search()
			throws RemoteException;
}
