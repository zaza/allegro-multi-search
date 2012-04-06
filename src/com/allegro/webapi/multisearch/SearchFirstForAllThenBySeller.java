package com.allegro.webapi.multisearch;

import java.rmi.RemoteException;
import java.util.List;

import com.allegro.webapi.SearchResponseType;
import com.allegro.webapi.SellerInfoStruct;

public class SearchFirstForAllThenBySeller extends SearchStrategy {

	public SearchFirstForAllThenBySeller(MultiSearchClient client) {
		super(client);
	}

	@Override
	public void search() throws RemoteException {
		// TODO: remove get by index
		List<SearchResponseType> search1 = client.search(client
				.getSearchQueries().get(0), null);
		for (SearchResponseType s1 : search1) {
			SellerInfoStruct seller = s1.getSItSellerInfo();
			List<SearchResponseType> search2 = client.search(client
					.getSearchQueries().get(1), seller);
			if (search2.size() > 0) {
				List<SearchResponseType> search3 = client.search(client
						.getSearchQueries().get(2), seller);
				if (search3.size() > 0)
					System.out
							.println("http://allegro.pl/show_user.php?search="
									+ seller.getSellerName());
			}
		}
	}
}
