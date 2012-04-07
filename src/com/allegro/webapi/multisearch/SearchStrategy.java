package com.allegro.webapi.multisearch;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import com.allegro.webapi.SearchResponseType;
import com.allegro.webapi.SellerInfoStruct;

public abstract class SearchStrategy {

	protected MultiSearchClient client;

	public SearchStrategy(MultiSearchClient client) {
		this.client = client;
	}

	public abstract Map<SellerInfoStruct, List<SearchResponseType>> search() throws RemoteException;
	
	// TODO: count exec time

}
