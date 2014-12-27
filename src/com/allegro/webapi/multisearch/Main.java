package com.allegro.webapi.multisearch;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import javax.xml.rpc.ServiceException;

import com.allegro.webapi.SearchResponseType;
import com.allegro.webapi.SellerInfoStruct;
import com.allegro.webapi.multisearch.strategies.SearchFirstForAllThenBySeller;

public class Main {
	public static void main(String[] args) throws Exception {
		search(args[0], args[1], args[2], args[3], System.out);
	}

	static void search(String username, String password, String key,
			String query, PrintStream out) throws RemoteException, NoSuchAlgorithmException, UnsupportedEncodingException, ServiceException {
		MultiSearchClient client = new MultiSearchClient(username, password, key, out);
		SearchFirstForAllThenBySeller strategy = new SearchFirstForAllThenBySeller(client);
		strategy.setSearchQueries(query.split(","));
		client.setSearchStrategy(strategy);
		Map<SellerInfoStruct, List<List<SearchResponseType>>> result = client.search();
		if (result.isEmpty())
			out.println("No seller found offering all the products.");
		for (SellerInfoStruct seller : result.keySet())
			out.println("http://allegro.pl/show_user.php?search=" + seller.getSellerName());
	}
}
