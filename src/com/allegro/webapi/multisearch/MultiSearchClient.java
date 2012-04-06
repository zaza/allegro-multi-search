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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.rpc.ServiceException;
import javax.xml.rpc.holders.IntHolder;
import javax.xml.rpc.holders.LongHolder;
import javax.xml.rpc.holders.StringHolder;

import org.apache.axis.encoding.Base64;

import com.allegro.webapi.AllegroWebApiPortType;
import com.allegro.webapi.AllegroWebApiServiceLocator;
import com.allegro.webapi.ItemInfo;
import com.allegro.webapi.MyAccountStruct2;
import com.allegro.webapi.SearchOptType;
import com.allegro.webapi.SearchResponseType;
import com.allegro.webapi.SellerInfoStruct;
import com.allegro.webapi.SellerShipmentDataStruct;
import com.allegro.webapi.holders.ArrayOfCategoriesStructHolder;
import com.allegro.webapi.holders.ArrayOfExcludedWordsHolder;
import com.allegro.webapi.holders.ArrayOfSearchResponseHolder;

public class MultiSearchClient {
	private AllegroWebApiPortType port;
	private StringHolder sessionHandlePart;

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
	
	public SearchResponseType[] search(final String phrase) throws RemoteException {
//		System.out.println("Searching for '" + phrase + "'... ");
		IntHolder searchCount = new IntHolder();
		IntHolder searchCountFeatured = new IntHolder();
		ArrayOfSearchResponseHolder searchArray = new ArrayOfSearchResponseHolder();
		ArrayOfExcludedWordsHolder searchExcludedWords = new ArrayOfExcludedWordsHolder();
		ArrayOfCategoriesStructHolder searchCategories = new ArrayOfCategoriesStructHolder();
		
		port.doSearch(sessionHandlePart.value, new SearchOptType(phrase, 0,
				0, 0, 0, 0, 0, "", 0, 0, 0, 0, 0, 0), searchCount,
				searchCountFeatured, searchArray, searchExcludedWords,
				searchCategories);

		System.out.println("Found " + searchCount.value + " items for '"
				+ phrase + "'.");
		return searchArray.value;
	}
	
	public SearchResponseType[] search(final String phrase, final SellerInfoStruct seller) throws RemoteException {
//		System.out.println("Searching for '" + phrase + "' by '"
//				+ seller.getSellerName() + "'... ");
		IntHolder searchCount = new IntHolder();
		IntHolder searchCountFeatured = new IntHolder();
		ArrayOfSearchResponseHolder searchArray = new ArrayOfSearchResponseHolder();
		ArrayOfExcludedWordsHolder searchExcludedWords = new ArrayOfExcludedWordsHolder();
		ArrayOfCategoriesStructHolder searchCategories = new ArrayOfCategoriesStructHolder();
		
		port.doSearch(sessionHandlePart.value, new SearchOptType(phrase, 0,
				0, 0, 0, 0, 0, "", 0, 0, 0, 0, 0, seller.getSellerId()), searchCount,
				searchCountFeatured, searchArray, searchExcludedWords,
				searchCategories);

		if (searchCount.value > 0)
			System.out.println("Found " + searchCount.value + " items for '"
					+ phrase + "' by '" + seller.getSellerName()+"'.");
		return searchArray.value;
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
			if (in!=null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return -1;
	}
}
