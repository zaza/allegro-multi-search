package com.allegro.webapi.multisearch.strategies;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.allegro.webapi.UserInfoType;
import com.allegro.webapi.multisearch.MultiSearchClient;
import com.github.zaza.allegro.Item;

public class SearchFirstForAllThenBySeller extends SearchStrategy {

	public SearchFirstForAllThenBySeller(MultiSearchClient client) {
		super(client);
	}

	@Override
	protected Map<UserInfoType, List<List<Item>>> search() throws RemoteException {
		Map<UserInfoType, List<List<Item>>> result = new HashMap<>();
		if (getSearchQueries().iterator().hasNext()) {
			List<Item> searchResult = client.search(getSearchQueries().get(0), null);
			Map<UserInfoType, List<Item>> searchResultBySeller = groupBySeller(searchResult);
			SELLER: for (UserInfoType seller : searchResultBySeller.keySet()) {
				List<List<Item>> searchQueriesResult = new ArrayList<List<Item>>(getSearchQueries().size());
				searchQueriesResult.add(searchResultBySeller.get(seller));
				for (int i = 1; i < getSearchQueries().size(); i++) {
					List<Item> search2 = client.search(getSearchQueries().get(i), seller.getUserId());
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

	private Map<UserInfoType, List<Item>> groupBySeller(List<Item> searchResult) {
		HashMap<UserInfoType, List<Item>> result = new HashMap<>();
		for (Item s : searchResult) {
			UserInfoType seller = s.getSellerInfo();
			List<Item> auctions = result.containsKey(seller) ? result.get(seller)	: new ArrayList<Item>();
			auctions.add(s);
			result.put(seller, auctions);
		}
		return result;
	}
}
