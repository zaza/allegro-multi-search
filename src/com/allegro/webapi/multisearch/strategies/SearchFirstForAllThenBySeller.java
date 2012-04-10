package com.allegro.webapi.multisearch.strategies;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.allegro.webapi.SearchResponseType;
import com.allegro.webapi.SellerInfoStruct;
import com.allegro.webapi.multisearch.MultiSearchClient;

public class SearchFirstForAllThenBySeller extends SearchStrategy {

	public SearchFirstForAllThenBySeller(MultiSearchClient client) {
		super(client);
	}

	@Override
	protected Map<SellerInfoStruct, List<List<SearchResponseType>>> search()
			throws RemoteException {
		HashMap<SellerInfoStruct, List<List<SearchResponseType>>> result = new HashMap<SellerInfoStruct, List<List<SearchResponseType>>>();
		if (getSearchQueries().iterator().hasNext()) {
			List<SearchResponseType> searchResult = client.search(getSearchQueries().get(0), null);
			Map<SellerInfoStruct, List<SearchResponseType>> searchResultBySeller = groupBySeller(searchResult);
			SELLER: for (SellerInfoStruct seller : searchResultBySeller.keySet()) {
				List<List<SearchResponseType>> searchQueriesResult = new ArrayList<List<SearchResponseType>>(getSearchQueries().size());
				searchQueriesResult.add(searchResultBySeller.get(seller));
				for (int i = 1; i < getSearchQueries().size(); i++) {
					List<SearchResponseType> search2 = client.search(getSearchQueries().get(i), seller);
					if (search2.size() > 0) {
						searchQueriesResult.add(search2);
					} else {
						continue SELLER;
					}
				}
				if (searchQueriesResult.size() == getSearchQueries().size())
					result.put(seller, searchQueriesResult);
			}
		}
		return result;
	}

	private Map<SellerInfoStruct, List<SearchResponseType>> groupBySeller(
			List<SearchResponseType> searchResult) {
		HashMap<SellerInfoStruct, List<SearchResponseType>> result = new HashMap<SellerInfoStruct, List<SearchResponseType>>();
		for (SearchResponseType s : searchResult) {
			SellerInfoStruct seller = s.getSItSellerInfo();
			List<SearchResponseType> auctions = result.containsKey(seller) ? result.get(seller)	: new ArrayList<SearchResponseType>();
			auctions.add(s);
			result.put(seller, auctions);
		}
		return result;
	}
}
