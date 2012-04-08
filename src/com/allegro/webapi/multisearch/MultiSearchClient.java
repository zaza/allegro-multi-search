package com.allegro.webapi.multisearch;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.xml.rpc.ServiceException;
import javax.xml.rpc.holders.IntHolder;
import javax.xml.rpc.holders.LongHolder;
import javax.xml.rpc.holders.StringHolder;

import org.apache.axis.encoding.Base64;

import com.allegro.webapi.AllegroWebApiPortType;
import com.allegro.webapi.AllegroWebApiServiceLocator;
import com.allegro.webapi.SearchOptType;
import com.allegro.webapi.SearchResponseType;
import com.allegro.webapi.SellerInfoStruct;
import com.allegro.webapi.holders.ArrayOfCategoriesStructHolder;
import com.allegro.webapi.holders.ArrayOfExcludedWordsHolder;
import com.allegro.webapi.holders.ArrayOfSearchResponseHolder;
import com.allegro.webapi.multisearch.strategies.SearchStrategy;

public class MultiSearchClient {
	private AllegroWebApiPortType port;
	private StringHolder sessionHandlePart;
	private SearchStrategy searchStrategy;
	
	private List<String> searchQueries;

	public MultiSearchClient(String username, String password, String key)
			throws RemoteException, ServiceException, NoSuchAlgorithmException,
			UnsupportedEncodingException {

		// Make a service
		AllegroWebApiServiceLocator service = new AllegroWebApiServiceLocator();

		// Now use the service to get a stub which implements the SDI.
		port = service.getAllegroWebApiPort();

		// Make the actual call
		final String userLogin = username;
		final String userPassword = password;
		final int countryCode = 1;
		final String webapiKey = key;
		long localVerKey = readAllegroKey();

		StringHolder info = new StringHolder();
		LongHolder currentVerKey = new LongHolder();

		System.out.print("Receving key version... ");
		port.doQuerySysStatus(1, countryCode, webapiKey, info, currentVerKey);
		System.out.println("done. Current version key=" + currentVerKey.value);
		
		if (localVerKey != currentVerKey.value) {
			System.err.println("Warning: key versions don't match!");
			localVerKey = currentVerKey.value;
		}

		sessionHandlePart = new StringHolder();
		LongHolder userId = new LongHolder();
		LongHolder serverTime = new LongHolder();
		System.out.print("Logging in... ");
		port.doLoginEnc(userLogin, encryptAndEncodePassword(userPassword),
				countryCode, webapiKey, localVerKey, sessionHandlePart, userId,
				serverTime);
		System.out.println("done.");
	}

	private String encryptAndEncodePassword(String password)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(password.getBytes("UTF-8"));
		return Base64.encode(md.digest());
	}
	
	public void setSearchQueries(String... searchQueries) {
		this.searchQueries = Arrays.asList(searchQueries);
	}
	
	public List<String> getSearchQueries() {
		return this.searchQueries;
	}
	
	public void setSearchStrategy(SearchStrategy searchStrategy) {
		this.searchStrategy = searchStrategy;
	}
	
	public Map<SellerInfoStruct, List<SearchResponseType>> search() throws RemoteException {
		return searchStrategy.execute();
	}
	
	public List<SearchResponseType> search(final String phrase, final SellerInfoStruct seller) throws RemoteException {
		int offset = 0;
		int limit = 25;

		int sellerId = seller == null ? 0 : seller.getSellerId();
		
		IntHolder searchCount = new IntHolder();
		IntHolder searchCountFeatured = new IntHolder();
		ArrayOfSearchResponseHolder searchArray = new ArrayOfSearchResponseHolder();
		ArrayOfExcludedWordsHolder searchExcludedWords = new ArrayOfExcludedWordsHolder();
		ArrayOfCategoriesStructHolder searchCategories = new ArrayOfCategoriesStructHolder();
		
		List<SearchResponseType> searchResponseItems = new ArrayList<SearchResponseType>();
		do {
			port.doSearch(sessionHandlePart.value, new SearchOptType(phrase, 0,
					0, 0, 0, 0, offset, "", 0, 0, 0, limit, 0, sellerId), searchCount,
					searchCountFeatured, searchArray, searchExcludedWords,
					searchCategories);
			searchResponseItems.addAll(Arrays.asList(searchArray.value));
			offset += limit;
		} while (searchArray.value.length > 0);

		if (searchCount.value > 0) {
			System.out.print("Found " + searchCount.value + " items for '"
					+ phrase + "'");
			if (seller != null)
				System.out.println(" by '" + seller.getSellerName()+"'.");
			else
				System.out.println(".");
		}

		return searchResponseItems;
	}	
	
	private long readAllegroKey() {
		DataInputStream in = null;
		try {
			FileInputStream fstream = new FileInputStream("allegro.key");
			in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				return Long.parseLong(strLine);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return -1;
	}
}
