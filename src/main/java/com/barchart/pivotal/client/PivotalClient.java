package com.barchart.pivotal.client;

import static com.barchart.pivotal.PivotalConstants.*;
import static com.google.gson.stream.JsonToken.*;
import static java.net.HttpURLConnection.*;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.pivotal.util.UtilGson;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;

/**
 * Client class for interacting with GitHub HTTP/JSON API.
 */
public class PivotalClient {

	private static final Logger log = LoggerFactory
			.getLogger(PivotalClient.class);

	/**
	 * Create API v3 client from URL.
	 * <p>
	 * This creates an HTTPS-based client with a host that contains the host
	 * value of the given URL prefixed with 'api' if the given URL is github.com
	 * or gist.github.com
	 * 
	 * @param url
	 * @return client
	 */
	public static PivotalClient createClient(final String url) {
		try {
			String host = new URL(url).getHost();
			if (HOST_DEFAULT.equals(host))
				host = HOST_API;
			return new PivotalClient(host);
		} catch (final IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Content-Type header
	 */
	protected static final String HEADER_CONTENT_TYPE = "Content-Type"; //$NON-NLS-1$

	/**
	 * Accept header
	 */
	protected static final String HEADER_ACCEPT = "Accept"; //$NON-NLS-1$

	/**
	 * Authorization header
	 */
	protected static final String HEADER_AUTHORIZATION = "Authorization"; //$NON-NLS-1$

	/**
	 * Authorization header
	 */
	protected static final String HEADER_TOKEN = "X-TrackerToken"; //$NON-NLS-1$

	/**
	 * User-Agent header
	 */
	protected static final String HEADER_USER_AGENT = "User-Agent"; //$NON-NLS-1$

	/**
	 * METHOD_GET
	 */
	protected static final String METHOD_GET = "GET"; //$NON-NLS-1$

	/**
	 * METHOD_PUT
	 */
	protected static final String METHOD_PUT = "PUT"; //$NON-NLS-1$

	/**
	 * METHOD_POST
	 */
	protected static final String METHOD_POST = "POST"; //$NON-NLS-1$

	/**
	 * METHOD_DELETE
	 */
	protected static final String METHOD_DELETE = "DELETE"; //$NON-NLS-1$

	/**
	 * Default user agent request header value
	 */
	protected static final String USER_AGENT = "PivotalJava/1.0.0"; //$NON-NLS-1$

	/**
	 * 422 status code for unprocessable entity
	 */
	protected static final int HTTP_UNPROCESSABLE_ENTITY = 422;

	/**
	 * Base URI
	 */
	protected final String baseUri;

	/**
	 * Prefix to apply to base URI
	 */
	protected final String prefix;

	/**
	 * {@link Gson} instance
	 */
	protected Gson gson = UtilGson.getGson();

	private String username;

	private String token;

	private String credentials;

	private String userAgent = USER_AGENT;

	private int bufferSize = 8192;

	private int requestLimit = -1;

	private int remainingRequests = -1;

	/**
	 * Create default client
	 */
	public PivotalClient() {
		this(HOST_API);
	}

	/**
	 * Create client for host name
	 * 
	 * @param hostname
	 */
	public PivotalClient(final String hostname) {
		this(hostname, -1, PROTOCOL_HTTPS);
	}

	/**
	 * Create client for host, port, and scheme
	 * 
	 * @param hostname
	 * @param port
	 * @param scheme
	 */
	public PivotalClient(final String hostname, final int port,
			final String scheme) {
		final StringBuilder uri = new StringBuilder(scheme);
		uri.append("://"); //$NON-NLS-1$
		uri.append(hostname);
		if (port > 0)
			uri.append(':').append(port);
		baseUri = uri.toString();

		prefix = SEGMENT_V5_API;
	}

	/**
	 * Set whether or not serialized data should include fields that are null.
	 * 
	 * @param serializeNulls
	 * @return this client
	 */
	public PivotalClient setSerializeNulls(final boolean serializeNulls) {
		gson = UtilGson.getGson(serializeNulls);
		return this;
	}

	/**
	 * Set the value to set as the user agent header on every request created.
	 * Specifying a null or empty agent parameter will reset this client to use
	 * the default user agent header value.
	 * 
	 * @param agent
	 * @return this client
	 */
	public PivotalClient setUserAgent(final String agent) {
		if (agent != null && agent.length() > 0)
			userAgent = agent;
		else
			userAgent = USER_AGENT;
		return this;
	}

	/**
	 * Configure request with standard headers
	 * 
	 * @param request
	 * @return configured request
	 */
	protected HttpURLConnection configureRequest(final HttpURLConnection request) {
		request.setRequestProperty(HEADER_TOKEN, token);
		request.setRequestProperty(HEADER_USER_AGENT, userAgent);
		return request;
	}

	/**
	 * Configure request URI
	 * 
	 * @param uri
	 * @return configured URI
	 */
	protected String configureUri(final String uri) {
		if (prefix == null || uri.startsWith(prefix))
			return uri;
		else
			return prefix + uri;
	}

	/**
	 * Create connection to URI
	 * 
	 * @param uri
	 * @return connection
	 * @throws IOException
	 */
	protected HttpURLConnection createConnection(final String uri)
			throws IOException {
		final URL url = new URL(createUri(uri));
		return (HttpURLConnection) url.openConnection();
	}

	/**
	 * Create connection to URI
	 * 
	 * @param uri
	 * @param method
	 * @return connection
	 * @throws IOException
	 */
	protected HttpURLConnection createConnection(final String uri,
			final String method) throws IOException {
		final HttpURLConnection connection = createConnection(uri);
		connection.setRequestMethod(method);
		return configureRequest(connection);
	}

	/**
	 * Create a GET request connection to the URI
	 * 
	 * @param uri
	 * @return connection
	 * @throws IOException
	 */
	protected HttpURLConnection createGet(final String uri) throws IOException {
		return createConnection(uri, METHOD_GET);
	}

	/**
	 * Create a POST request connection to the URI
	 * 
	 * @param uri
	 * @return connection
	 * @throws IOException
	 */
	protected HttpURLConnection createPost(final String uri) throws IOException {
		return createConnection(uri, METHOD_POST);
	}

	/**
	 * Create a PUT request connection to the URI
	 * 
	 * @param uri
	 * @return connection
	 * @throws IOException
	 */
	protected HttpURLConnection createPut(final String uri) throws IOException {
		return createConnection(uri, METHOD_PUT);
	}

	/**
	 * Create a DELETE request connection to the URI
	 * 
	 * @param uri
	 * @return connection
	 * @throws IOException
	 */
	protected HttpURLConnection createDelete(final String uri)
			throws IOException {
		return createConnection(uri, METHOD_DELETE);
	}

	/**
	 * Set OAuth2 token
	 * 
	 * @param token
	 * @return this client
	 */
	public PivotalClient setToken(final String token) {
		this.token = token;
		return this;
	}

	/**
	 * Set buffer size used to send the request and read the response
	 * 
	 * @param bufferSize
	 * @return this client
	 */
	public PivotalClient setBufferSize(final int bufferSize) {
		if (bufferSize < 1)
			throw new IllegalArgumentException(
					"Buffer size must be greater than zero"); //$NON-NLS-1$

		this.bufferSize = bufferSize;
		return this;
	}

	/**
	 * Get the user that this client is currently authenticating as
	 * 
	 * @return user or null if not authentication
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Convert object to a JSON string
	 * 
	 * @param object
	 * @return JSON string
	 * @throws IOException
	 */
	protected String toJson(final Object object) throws IOException {
		try {
			return gson.toJson(object);
		} catch (final JsonParseException jpe) {
			final IOException ioe = new IOException(
					"Parse exception converting object to JSON"); //$NON-NLS-1$
			ioe.initCause(jpe);
			throw ioe;
		}
	}

	/**
	 * Parse JSON to specified type
	 * 
	 * @param <V>
	 * @param stream
	 * @param type
	 * @return parsed type
	 * @throws IOException
	 */
	protected <V> V parseJson(final InputStream stream, final Type type)
			throws IOException {
		return parseJson(stream, type, null);
	}

	/**
	 * Parse JSON to specified type
	 * 
	 * @param <V>
	 * @param stream
	 * @param type
	 * @param listType
	 * @return parsed type
	 * @throws IOException
	 */
	protected <V> V parseJson(final InputStream stream, final Type type,
			final Type listType) throws IOException {
		final BufferedReader reader = new BufferedReader(new InputStreamReader(
				stream, CHARSET_UTF8), bufferSize);
		if (listType == null)
			try {
				return gson.fromJson(reader, type);
			} catch (final JsonParseException jpe) {
				final IOException ioe = new IOException(
						"Parse exception converting JSON to object"); //$NON-NLS-1$
				ioe.initCause(jpe);
				throw ioe;
			} finally {
				try {
					reader.close();
				} catch (final IOException ignored) {
					// Ignored
				}
			}
		else {
			final JsonReader jsonReader = new JsonReader(reader);
			try {
				if (jsonReader.peek() == BEGIN_ARRAY)
					return gson.fromJson(jsonReader, listType);
				else
					return gson.fromJson(jsonReader, type);
			} catch (final JsonParseException jpe) {
				final IOException ioe = new IOException(
						"Parse exception converting JSON to object"); //$NON-NLS-1$
				ioe.initCause(jpe);
				throw ioe;
			} finally {
				try {
					jsonReader.close();
				} catch (final IOException ignored) {
					// Ignored
				}
			}
		}
	}

	/**
	 * Does status code denote an error
	 * 
	 * @param code
	 * @return true if error, false otherwise
	 */
	protected boolean isError(final int code) {
		switch (code) {
		case HTTP_BAD_REQUEST:
		case HTTP_UNAUTHORIZED:
		case HTTP_FORBIDDEN:
		case HTTP_NOT_FOUND:
		case HTTP_CONFLICT:
		case HTTP_GONE:
		case HTTP_UNPROCESSABLE_ENTITY:
		case HTTP_INTERNAL_ERROR:
			return true;
		default:
			return false;
		}
	}

	/**
	 * Does status code denote a non-error response?
	 * 
	 * @param code
	 * @return true if okay, false otherwise
	 */
	protected boolean isOk(final int code) {
		switch (code) {
		case HTTP_OK:
		case HTTP_CREATED:
		case HTTP_ACCEPTED:
			return true;
		default:
			return false;
		}
	}

	/**
	 * Is the response empty?
	 * 
	 * @param code
	 * @return true if empty, false otherwise
	 */
	protected boolean isEmpty(final int code) {
		return HTTP_NO_CONTENT == code;
	}

	/**
	 * Parse error from response
	 * 
	 * @param response
	 * @return request error
	 * @throws IOException
	 */
	protected RequestError parseError(final InputStream response)
			throws IOException {
		return parseJson(response, RequestError.class);
	}

	/**
	 * Get body from response inputs stream
	 * 
	 * @param request
	 * @param stream
	 * @return parsed body
	 * @throws IOException
	 */
	protected Object getBody(final PivotalRequest request,
			final InputStream stream) throws IOException {

		final Type type = request.getType();

		if (type != null) {
			return parseJson(stream, type, request.getArrayType());
		} else {
			return null;
		}
	}

	/**
	 * Create error exception from response and throw it
	 * 
	 * @param response
	 * @param code
	 * @param status
	 * @return non-null newly created {@link IOException}
	 */
	protected IOException createException(final InputStream response,
			final int code, final String status) {
		if (isError(code)) {
			final RequestError error;
			try {
				error = parseError(response);
			} catch (final IOException e) {
				return e;
			}
			if (error != null)
				return new RequestException(error, code);
		} else
			try {
				response.close();
			} catch (final IOException ignored) {
				// Ignored
			}
		String message;
		if (status != null && status.length() > 0)
			message = status + " (" + code + ')'; //$NON-NLS-1$
		else
			message = "Unknown error occurred (" + code + ')'; //$NON-NLS-1$
		return new IOException(message);
	}

	/**
	 * Post to URI
	 * 
	 * @param uri
	 * @throws IOException
	 */
	public void post(final StringBuilder uri) throws IOException {
		post(uri, null, null);
	}

	/**
	 * Put to URI
	 * 
	 * @param uri
	 * @throws IOException
	 */
	public void put(final String uri) throws IOException {
		put(uri, null, null);
	}

	/**
	 * Delete resource at URI. This method will throw an {@link IOException}
	 * when the response status is not a 204 (No Content).
	 * 
	 * @param uri
	 * @throws IOException
	 */
	public void delete(final String uri) throws IOException {
		delete(uri, null);
	}

	/**
	 * Send parameters to output stream of request
	 * 
	 * @param request
	 * @param params
	 * @throws IOException
	 */
	protected void sendParams(final HttpURLConnection request,
			final Object params) throws IOException {
		request.setDoOutput(true);
		if (params != null) {
			request.setRequestProperty(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON
					+ "; charset=" + CHARSET_UTF8); //$NON-NLS-1$
			final byte[] data = toJson(params).getBytes(CHARSET_UTF8);
			request.setFixedLengthStreamingMode(data.length);
			final BufferedOutputStream output = new BufferedOutputStream(
					request.getOutputStream(), bufferSize);
			try {
				output.write(data);
				output.flush();
			} finally {
				try {
					output.close();
				} catch (final IOException ignored) {
					// Ignored
				}
			}
		} else {
			request.setFixedLengthStreamingMode(0);
			request.setRequestProperty("Content-Length", "0");
		}
	}

	private <V> V sendJson(final HttpURLConnection request,
			final Object params, final Type type) throws IOException {
		sendParams(request, params);
		final int code = request.getResponseCode();
		updateRateLimits(request);
		if (isOk(code))
			if (type != null)
				return parseJson(getStream(request), type);
			else
				return null;
		if (isEmpty(code))
			return null;
		throw createException(getStream(request), code,
				request.getResponseMessage());
	}

	/**
	 * Create full URI from path
	 * 
	 * @param path
	 * @return uri
	 */
	protected String createUri(final String path) {
		return baseUri + configureUri(path);
	}

	/**
	 * Get response stream from GET to URI. It is the responsibility of the
	 * calling method to close the returned stream.
	 * 
	 * @param request
	 * @return stream
	 * @throws IOException
	 */
	public InputStream getStream(final PivotalRequest request)
			throws IOException {
		return getResponseStream(createGet(request.generateUri()));
	}

	/**
	 * Get response stream from POST to URI. It is the responsibility of the
	 * calling method to close the returned stream.
	 * 
	 * @param uri
	 * @param params
	 * @return stream
	 * @throws IOException
	 */
	public InputStream postStream(final String uri, final Object params)
			throws IOException {
		final HttpURLConnection connection = createPost(uri);
		sendParams(connection, params);
		return getResponseStream(connection);
	}

	/**
	 * Get response stream for request
	 * 
	 * @param request
	 * @return stream
	 * @throws IOException
	 */
	protected InputStream getResponseStream(final HttpURLConnection request)
			throws IOException {
		final InputStream stream = getStream(request);
		final int code = request.getResponseCode();
		updateRateLimits(request);
		if (isOk(code))
			return stream;
		else
			throw createException(stream, code, request.getResponseMessage());
	}

	/**
	 * Get stream from request
	 * 
	 * @param request
	 * @return stream
	 * @throws IOException
	 */
	protected InputStream getStream(final HttpURLConnection request)
			throws IOException {
		if (request.getResponseCode() < HTTP_BAD_REQUEST)
			return request.getInputStream();
		else {
			final InputStream stream = request.getErrorStream();
			return stream != null ? stream : request.getInputStream();
		}
	}

	/**
	 * Get response from URI and bind to specified type
	 * 
	 * @param request
	 * @return response
	 * @throws IOException
	 */
	public PivotalResponse get(final PivotalRequest request) throws IOException {

		final HttpURLConnection connection = createGet(request.generateUri());

		final String accept = request.getResponseContentType();

		if (accept != null) {
			connection.setRequestProperty(HEADER_ACCEPT, accept);
		}

		final int code = connection.getResponseCode();

		// updateRateLimits(httpRequest);

		if (isOk(code)) {
			log.info("code : {}", code);
			return new PivotalResponse(connection, getBody(request,
					getStream(connection)));
		}

		if (isEmpty(code)) {
			return new PivotalResponse(connection, null);
		}

		throw createException(getStream(connection), code,
				connection.getResponseMessage());

	}

	/**
	 * Post data to URI
	 * 
	 * @param <V>
	 * @param uri
	 * @param params
	 * @param type
	 * @return response
	 * @throws IOException
	 */
	public <V> V post(final StringBuilder uri, final Object params,
			final Type type) throws IOException {
		final HttpURLConnection request = createPost(uri.toString());
		return sendJson(request, params, type);
	}

	/**
	 * Put data to URI
	 * 
	 * @param <V>
	 * @param uri
	 * @param params
	 * @param type
	 * @return response
	 * @throws IOException
	 */
	public <V> V put(final String uri, final Object params, final Type type)
			throws IOException {
		final HttpURLConnection request = createPut(uri);
		return sendJson(request, params, type);
	}

	/**
	 * Delete resource at URI. This method will throw an {@link IOException}
	 * when the response status is not a 204 (No Content).
	 * 
	 * @param uri
	 * @param params
	 * @throws IOException
	 */
	public void delete(final String uri, final Object params)
			throws IOException {
		final HttpURLConnection request = createDelete(uri);
		if (params != null)
			sendParams(request, params);
		final int code = request.getResponseCode();
		updateRateLimits(request);
		if (!isEmpty(code))
			throw new RequestException(parseError(getStream(request)), code);
	}

	/**
	 * Update rate limits present in response headers
	 * 
	 * @param request
	 * @return this client
	 */
	protected PivotalClient updateRateLimits(final HttpURLConnection request) {
		final String limit = request.getHeaderField("X-RateLimit-Limit");
		if (limit != null && limit.length() > 0)
			try {
				requestLimit = Integer.parseInt(limit);
			} catch (final NumberFormatException nfe) {
				requestLimit = -1;
			}
		else
			requestLimit = -1;

		final String remaining = request
				.getHeaderField("X-RateLimit-Remaining");
		if (remaining != null && remaining.length() > 0)
			try {
				remainingRequests = Integer.parseInt(remaining);
			} catch (final NumberFormatException nfe) {
				remainingRequests = -1;
			}
		else
			remainingRequests = -1;

		return this;
	}

	/**
	 * Get number of requests remaining before rate limiting occurs
	 * <p>
	 * This will be the value of the 'X-RateLimit-Remaining' header from the
	 * last request made
	 * 
	 * @return remainingRequests or -1 if not present in the response
	 */
	public int getRemainingRequests() {
		return remainingRequests;
	}

	/**
	 * Get number of requests that {@link #getRemainingRequests()} counts down
	 * from as each request is made
	 * <p>
	 * This will be the value of the 'X-RateLimit-Limit' header from the last
	 * request made
	 * 
	 * @return requestLimit or -1 if not present in the response
	 */
	public int getRequestLimit() {
		return requestLimit;
	}
}
