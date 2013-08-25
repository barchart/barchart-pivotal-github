package com.barchart.web.util;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.barchart.pivotal.util.UtilGson;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * Google short url service with cache.
 */
// https://developers.google.com/url-shortener/v1/getting_started
public class GooGl {

	public String id;
	public String kind;
	public String longUrl;

	/**
	 * Render as JSON.
	 */
	@Override
	public String toString() {
		return UtilGson.getGson().toJson(this);
	}

	/** [ id -> longUrl ] */
	private final static BiMap<String, String> linkMap = HashBiMap.create();

	private static final String SERVICE_URL = "https://www.googleapis.com/urlshortener/v1/url";

	private static final ThreadLocal<WebClient> CLIENT = new ThreadLocal<WebClient>() {
		@Override
		protected WebClient initialValue() {
			final WebClient client = new WebClient();
			client.getOptions().setJavaScriptEnabled(false);
			return client;
		}
	};

	/**
	 * 
	 */
	public static String longURL(final String shortUrl) throws IOException {

		if (!linkMap.containsKey(shortUrl)) {

			final WebClient client = CLIENT.get();

			final WebRequest request = new WebRequest(new URL(SERVICE_URL),
					HttpMethod.GET);
			final List<NameValuePair> paramList = new ArrayList<NameValuePair>();
			paramList.add(new NameValuePair("shortUrl", shortUrl));
			request.setRequestParameters(paramList);

			final WebResponse response = client.getPage(request)
					.getWebResponse();

			final GooGl target = UtilGson.getGson().fromJson(
					response.getContentAsString(), GooGl.class);

			linkMap.put(shortUrl, target.longUrl);
		}

		return linkMap.get(shortUrl);
	}

	/**
	 * 
	 */
	public static String shortURL(final String longUrl) throws IOException {

		if (!linkMap.containsValue(longUrl)) {

			final WebClient client = CLIENT.get();

			final GooGl source = new GooGl();
			source.longUrl = longUrl;

			final WebRequest request = new WebRequest(new URL(SERVICE_URL),
					HttpMethod.POST);
			request.setAdditionalHeader("Content-Type", "application/json");
			request.setRequestBody(source.toString());

			final WebResponse response = client.getPage(request)
					.getWebResponse();

			final GooGl target = UtilGson.getGson().fromJson(
					response.getContentAsString(), GooGl.class);

			linkMap.put(target.id, longUrl);
		}

		return linkMap.inverse().get(longUrl);
	}

}
