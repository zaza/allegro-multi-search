package com.allegro.webapi.multisearch;

import java.util.List;

import com.allegro.webapi.SearchResponseType;
import com.allegro.webapi.SellerInfoStruct;

public class Main {
	public static void main(String[] args) throws Exception {
		MultiSearchClient client = new MultiSearchClient(args[0], args[1],
				args[2]);
		List<SearchResponseType> search1 = client.search(
				"mustel* ciemieniuch*", null);
		for (SearchResponseType s1 : search1) {
			SellerInfoStruct seller = s1.getSItSellerInfo();
			List<SearchResponseType> search2 = client.search("bepan*", seller);
			if (search2.size() > 0) {
				List<SearchResponseType> search3 = client.search(
						"oilatum soft 500", seller);
				if (search3.size() > 0)
					System.out
							.println("http://allegro.pl/show_user.php?search="
									+ seller.getSellerName());
			}
		}
	}
}
