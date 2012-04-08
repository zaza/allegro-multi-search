package com.allegro.webapi.multisearch.strategies;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import com.allegro.webapi.SearchResponseType;
import com.allegro.webapi.SellerInfoStruct;
import com.allegro.webapi.multisearch.MultiSearchClient;

public abstract class SearchStrategy {

	protected MultiSearchClient client;

	public SearchStrategy(MultiSearchClient client) {
		this.client = client;
	}

	final public Map<SellerInfoStruct, List<SearchResponseType>> execute()
			throws RemoteException {
		long start = System.currentTimeMillis();
		try {
			return search();
		} finally {
			long end = System.currentTimeMillis();
			System.out.println("Executed in " + (end - start) + " ms.");
		}
	}

	protected abstract Map<SellerInfoStruct, List<SearchResponseType>> search()
			throws RemoteException;
}
