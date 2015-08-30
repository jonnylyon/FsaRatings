package jl.fsaratings.rest;

import java.io.IOException;

import org.json.JSONObject;

/**
 * Rest Handler interface, provided to allow easy mocking for test purposes.
 * @author jonny.lyon
 *
 */
public interface RestHandler {

	/**
	 * Performs a GET HTTP request on the URL specified, with the headers required
	 * for the FSA API.  Converts Json response into JSONObject, and returns this.
	 *
	 * @param endpointUrl The FSA API endpoint URL
	 * @return The JSONObject representing the response
	 * @throws IOException if an error occurs retrieving data from the external API
	 */
	JSONObject getEndpointResponse(String endpointUrl) throws IOException;
}
