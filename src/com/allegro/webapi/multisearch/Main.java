package com.allegro.webapi.multisearch;

import com.allegro.webapi.SearchResponseType;
import com.allegro.webapi.SellerInfoStruct;

public class Main {
	public static void main(String[] args) throws Exception {
		MultiSearchClient client = new MultiSearchClient(args[0], args[1],
				args[2]);
		SearchResponseType[] search1 = client.search("mustel* ciemieniuch*");
		for (SearchResponseType s1 : search1) {
			SellerInfoStruct seller = s1.getSItSellerInfo();
			SearchResponseType[] search2 = client.search("bepan*", seller);
			if (search2.length > 0) {
				SearchResponseType[] search3 = client.search(
						"oilatum soft 500", seller);
				if (search3.length > 0)
					System.out
							.println("http://allegro.pl/show_user.php?search="
									+ seller.getSellerName());
			}
		}
	}
}
