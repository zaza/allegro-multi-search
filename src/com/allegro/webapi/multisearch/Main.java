package com.allegro.webapi.multisearch;

public class Main {
	public static void main(String[] args) throws Exception {
		MultiSearchClient client = new MultiSearchClient(args[0], args[1],
				args[2]);
		client.setSearchQueries(args[3].split(","));
		client.setSearchStrategy(new SearchFirstForAllThenBySeller(client));
		client.search();
	}
}
