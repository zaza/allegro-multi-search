package com.allegro.webapi.multisearch;

import java.util.List;
import java.util.Map;

import com.allegro.webapi.SearchResponseType;
import com.allegro.webapi.SellerInfoStruct;
import com.allegro.webapi.multisearch.strategies.SearchFirstForAllThenBySeller;

public class Main {
	public static void main(String[] args) throws Exception {
		MultiSearchClient client = new MultiSearchClient(args[0], args[1],
				args[2]);
		client.setSearchQueries(args[3].split(","));
		client.setSearchStrategy(new SearchFirstForAllThenBySeller(client));
		Map<SellerInfoStruct, List<SearchResponseType>> result = client
				.search();
		for (SellerInfoStruct seller : result.keySet()) {
			System.out.println("http://allegro.pl/show_user.php?search="
					+ seller.getSellerName());
		}
	}
}
