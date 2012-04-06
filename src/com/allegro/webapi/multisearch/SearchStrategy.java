package com.allegro.webapi.multisearch;

import java.rmi.RemoteException;

public abstract class SearchStrategy {

	protected MultiSearchClient client;

	public SearchStrategy(MultiSearchClient client) {
		this.client = client;
	}

	public abstract void search() throws RemoteException;
	
	// TODO: count exec time

}
