package com.allegro.webapi.multisearch;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import javax.xml.rpc.ServiceException;

import com.allegro.webapi.UserInfoType;
import com.allegro.webapi.multisearch.strategies.SearchStrategy;
import com.github.zaza.allegro.AllegroClient;
import com.github.zaza.allegro.Item;

public class MultiSearchClient {
	private SearchStrategy searchStrategy;
	private final PrintStream out;
	private AllegroClient client;

	public MultiSearchClient(String username, String password, String key)
			throws RemoteException, ServiceException, NoSuchAlgorithmException,
			UnsupportedEncodingException {
		this(username, password, key, System.out);
	}

	public MultiSearchClient(String username, String password, String key, PrintStream out)
			throws RemoteException, ServiceException, NoSuchAlgorithmException,
			UnsupportedEncodingException {
		client = new AllegroClient(key);
		this.out = out;
	}

	public void setSearchStrategy(SearchStrategy searchStrategy) {
		this.searchStrategy = searchStrategy;
	}
	
	public Map<UserInfoType, List<List<Item>>> search() throws RemoteException {
		return searchStrategy.execute();
	}
	
	public List<Item> search(final String phrase, final Integer sellerId) throws RemoteException {
		List<Item> items = client.searchByString(phrase).userId(sellerId).search();

		if (!items.isEmpty()) {
			print("Found " + items.size() + " items for '"	+ phrase + "'");
			if (sellerId != null)
				println(" by '" + items.get(0).getSellerInfo().getUserLogin() +"'.");
			else
				println(".");
		}

		return items;
	}	
	
	public void print(String s) {
		out.print(s);
	}

	public void println(String s) {
		out.println(s);
	}
}
