package com.barchart.pivotal.client;

import java.lang.reflect.Type;
import java.util.Map;

import com.barchart.pivotal.util.UtilURL;

/**
 * GitHub API request class that contains the URI and parameters of the request
 * as well as the expected {@link Type} of the response.
 * 
 * The {@link #generateUri()} method should be used to build a full URI that
 * contains both the base uri and the parameters set.
 */
public class PivotalRequest {

	private String uri;

	private Map<String, String> params;

	private Type type;

	private String responseContentType;

	private Type arrayType;

	/**
	 * Create empty request
	 */
	public PivotalRequest() {

	}

	/**
	 * Set type to expect if first token is a beginning of an array
	 * 
	 * @param arrayType
	 * @return this request
	 */
	public PivotalRequest setArrayType(final Type arrayType) {
		this.arrayType = arrayType;
		return this;
	}

	/**
	 * @return arrayType
	 */
	public Type getArrayType() {
		return arrayType;
	}

	/**
	 * @return uri
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * Add request params to URI
	 * 
	 * @param uri
	 */
	protected void addParams(final StringBuilder uri) {
		UtilURL.addParams(getParams(), uri);
	}

	/**
	 * Generate full uri
	 * 
	 * @return uri
	 */
	public String generateUri() {
		final String baseUri = uri;
		if (baseUri == null)
			return null;
		if (baseUri.indexOf('?') != -1)
			return baseUri;
		final StringBuilder params = new StringBuilder();
		addParams(params);
		if (params.length() > 0)
			return baseUri + '?' + params;
		else
			return baseUri;
	}

	/**
	 * @param uri
	 * @return this request
	 */
	public PivotalRequest setUri(final StringBuilder uri) {
		return setUri(uri != null ? uri.toString() : null);
	}

	/**
	 * @param uri
	 * @return this request
	 */
	public PivotalRequest setUri(final String uri) {
		this.uri = uri;
		return this;
	}

	/**
	 * @return params
	 */
	public Map<String, String> getParams() {
		return params;
	}

	/**
	 * @param params
	 * @return this request
	 */
	public PivotalRequest setParams(final Map<String, String> params) {
		this.params = params;
		return this;
	}

	/**
	 * @return type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @param type
	 * @return this request
	 */
	public PivotalRequest setType(final Type type) {
		this.type = type;
		return this;
	}

	/**
	 * @return responseContentType
	 */
	public String getResponseContentType() {
		return responseContentType;
	}

	/**
	 * Set the desired response content type
	 * 
	 * @param responseContentType
	 * @return this request
	 */
	public PivotalRequest setResponseContentType(
			final String responseContentType) {
		this.responseContentType = responseContentType;
		return this;
	}

	@Override
	public int hashCode() {
		final String fullUri = generateUri();
		return fullUri != null ? fullUri.hashCode() : super.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof PivotalRequest))
			return false;
		final String fullUri = generateUri();
		final String objUri = ((PivotalRequest) obj).generateUri();
		return fullUri != null && objUri != null && fullUri.equals(objUri);
	}

	@Override
	public String toString() {
		final String fullUri = generateUri();
		return fullUri != null ? fullUri : super.toString();
	}
}
