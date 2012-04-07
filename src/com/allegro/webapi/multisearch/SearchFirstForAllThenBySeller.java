package com.allegro.webapi.multisearch;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.allegro.webapi.SearchResponseType;
import com.allegro.webapi.SellerInfoStruct;

public class SearchFirstForAllThenBySeller extends SearchStrategy {

	public SearchFirstForAllThenBySeller(MultiSearchClient client) {
		super(client);
	}

	@Override
	public Map<SellerInfoStruct, List<SearchResponseType>> search() throws RemoteException {
		HashMap<SellerInfoStruct, List<SearchResponseType>> result = new HashMap<SellerInfoStruct, List<SearchResponseType>>();
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
				if (search3.size() > 0) {
					List<SearchResponseType> auctions = new ArrayList<SearchResponseType>(client.getSearchQueries().size());
					auctions.add(s1);
					auctions.add(search2.get(0));
					auctions.add(search3.get(0));
					result.put(seller, auctions);
				}
			}
		}
		return result;
	}
}
