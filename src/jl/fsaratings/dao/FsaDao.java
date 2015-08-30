package jl.fsaratings.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONObject;

public class FsaDao {

	private static final String AUTHORITIES_ENDPOINT = "http://api.ratings.food.gov.uk/Authorities/basic";
	private static final String ESTABLISHMENTS_ENDPOINT = "http://api.ratings.food.gov.uk/Establishments?localAuthorityId={authorityId}";

	public static Map<String, Long> fetchAuthorityNamesWithIds() {
		JSONObject json = getEndpointResponse(AUTHORITIES_ENDPOINT);

		JSONArray authorities = json.getJSONArray("authorities");

		Map<String, Long> result = new TreeMap<>();

		// making assumption that authority names are unique...
		for (int i = 0; i < authorities.length(); i++) {
			JSONObject authority = authorities.getJSONObject(i);
			result.put(authority.getString("Name"), authority.getLong("LocalAuthorityId"));
		}

		return result;
	}

	public static Map<String, Double> fetchRatingPercentagesForAuthority(Long authorityId) {
		JSONObject json = getEndpointResponse(ESTABLISHMENTS_ENDPOINT.replace("{authorityId}", authorityId.toString()));

		JSONArray establishments = json.getJSONArray("establishments");

		Map<String, Integer> countPerRatingType = new HashMap<>();
		Integer totalCount = 0;

		for (int i = 0; i < establishments.length(); i++) {
			JSONObject establishment = establishments.getJSONObject(i);
			String ratingValue = establishment.getString("RatingValue");
			if (countPerRatingType.containsKey(ratingValue)) {
				countPerRatingType.put(ratingValue, countPerRatingType.get(ratingValue) + 1);
			} else {
				countPerRatingType.put(ratingValue, 1);
			}
			totalCount += 1;
		}

		Map<String, Double> result = new HashMap<>();

		for (Map.Entry<String, Integer> entry : countPerRatingType.entrySet()) {
			Integer ratingCount = entry.getValue() * 100;
			result.put(entry.getKey(), (double)(entry.getValue() * 100) / totalCount);
		}

		return result;
	}

	private static JSONObject getEndpointResponse(String endpointUrl) {
		String fullOutput = "";

		try {
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
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new JSONObject(fullOutput);
	}
}
