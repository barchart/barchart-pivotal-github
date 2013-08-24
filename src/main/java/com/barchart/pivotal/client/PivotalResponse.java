package com.barchart.pivotal.client;

import java.net.HttpURLConnection;

/**
 * GitHub API response class that provides the parsed response body as well as
 * any links to the first, previous, next, and last responses.
 */
public class PivotalResponse {

	/**
	 * HTTP response
	 */
	protected final HttpURLConnection connection;

	/**
	 * Response body
	 */
	protected final Object body;

	/**
	 * Links to other pages
	 */
	protected PivotalPageLinks links;

	/**
	 * Create response
	 * 
	 * @param connection
	 * @param body
	 */
	public PivotalResponse(final HttpURLConnection connection, final Object body) {
		this.connection = connection;
		this.body = body;
	}

	/**
	 * Get header value
	 * 
	 * @param name
	 * @return value
	 */
	public String getHeader(final String name) {
		return connection.getHeaderField(name);
	}

	/**
	 * Get page links
	 * 
	 * @return links
	 */
	protected PivotalPageLinks getLinks() {
		if (links == null)
			links = new PivotalPageLinks(this);
		return links;
	}

	/**
	 * Get link uri to first page
	 * 
	 * @return possibly null uri
	 */
	public String getFirst() {
		return getLinks().getFirst();
	}

	/**
	 * Get link uri to previous page
	 * 
	 * @return possibly null uri
	 */
	public String getPrevious() {
		return getLinks().getPrev();
	}

	/**
	 * Get link uri to next page
	 * 
	 * @return possibly null uri
	 */
	public String getNext() {
		return getLinks().getNext();
	}

	/**
	 * Get link uri to last page
	 * 
	 * @return possibly null uri
	 */
	public String getLast() {
		return getLinks().getLast();
	}

	/**
	 * Parsed response body
	 * 
	 * @return body
	 */
	public Object getBody() {
		return body;
	}

}
