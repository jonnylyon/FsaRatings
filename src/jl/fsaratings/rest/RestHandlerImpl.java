package jl.fsaratings.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

/**
 * This class handles the REST calls and interacts directly with the external
 * web service.  No automated testing is provided for this class, but functionality
 * is kept very specific to the web calls.
 *
 * @author jonny.lyon
 *
 */
public class RestHandlerImpl implements RestHandler {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONObject getEndpointResponse(String endpointUrl) throws IOException {
		String fullOutput = "";

		URL url = new URL(endpointUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("x-api-version", "2");

		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Endpoint returned HTTP error code " + conn.getResponseCode());
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

		String output;
		while ((output = br.readLine()) != null) {
			fullOutput += "\n" + output;
		}
		conn.disconnect();

		return new JSONObject(fullOutput);
	}

}
