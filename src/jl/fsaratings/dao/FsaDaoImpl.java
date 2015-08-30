package jl.fsaratings.dao;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONObject;

import jl.fsaratings.rest.RestHandler;

/**
 * Implements FsaDao and handles all of the API data retrieval
 *
 * @author jonny.lyon
 *
 */
public class FsaDaoImpl implements FsaDao {

	/**
	 * Pulls in the URL to the authorities endpoint from an externalised property file
	 */
	private static final String AUTHORITIES_ENDPOINT = Messages.getString("FsaDao.fsa.api.authorities");

	/**
	 * Pulls in the URL to the establishments endpoint from an externalised property file
	 */
	private static final String ESTABLISHMENTS_ENDPOINT = Messages.getString("FsaDao.fsa.api.establishments");

	private final RestHandler restHandler;

	/**
	 * Constructor for FsaDaoImpl
	 *
	 * @param restHandler A class to handle the rest calls and return JSONObjects with the response data
	 */
	public FsaDaoImpl(RestHandler restHandler) {
		this.restHandler = restHandler;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Long> fetchAuthorityNamesWithIds() throws IOException {
		JSONObject json = restHandler.getEndpointResponse(AUTHORITIES_ENDPOINT);

		JSONArray authorities = json.getJSONArray("authorities");

		Map<String, Long> result = new TreeMap<>();

		// making assumption that authority names are unique...
		for (int i = 0; i < authorities.length(); i++) {
			JSONObject authority = authorities.getJSONObject(i);
			result.put(authority.getString("Name"), authority.getLong("LocalAuthorityId"));
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Double> fetchRatingPercentagesForAuthority(Long authorityId) throws IOException {
		JSONObject json = restHandler.getEndpointResponse(ESTABLISHMENTS_ENDPOINT.replace("{authorityId}", authorityId.toString()));

		JSONArray establishments = json.getJSONArray("establishments");

		Map<String, Integer> countPerRatingType = new LinkedHashMap<>();
		// making the assumption that all establishments within one authority share
		// the same scheme type (FHIS or FHRS); therefore just checking the first
		// establishment to infer available ratings.
		if (establishments.length() > 0) {
			String schemeType = establishments.getJSONObject(0).getString("SchemeType");
			if (schemeType.equals("FHIS")) {
				countPerRatingType = initialCountsForScotland();
			} else if (schemeType.equals("FHRS")) {
				countPerRatingType = initialCountsForNotScotland();
			}
		}

		// wanted ratings excludes 'unwanted' ratings such as Awaiting Inspection
		Integer totalWantedRatingsCount = 0;
		for (int i = 0; i < establishments.length(); i++) {
			JSONObject establishment = establishments.getJSONObject(i);
			String ratingValue = establishment.getString("RatingValue");
			if (countPerRatingType.containsKey(ratingValue)) {
				countPerRatingType.put(ratingValue, countPerRatingType.get(ratingValue) + 1);
				totalWantedRatingsCount++;
			}
		}

		// create a new LinkedHashMap with the same indexes as the previous countPerRatingType
		// LinkedHashMap, calculating the percentages applicable to each rating type
		Map<String, Double> result = new LinkedHashMap<>();
		if (totalWantedRatingsCount > 0) {
			for (Map.Entry<String, Integer> entry : countPerRatingType.entrySet()) {
				Integer ratingCount = entry.getValue() * 100;
				result.put(entry.getKey(), (double)(entry.getValue() * 100) / totalWantedRatingsCount);
			}
		}

		return result;
	}

	/**
	 * Constructs an initial map of zero counts containing all of the 'wanted' rating types for Scotland.
	 * Excludes 'Awaiting Inspection', 'Pass and Eat Safe' and all others not specified in the tech test
	 * document.
	 *
	 * LinkedHashMap is used so that the order of indexes is retained.
	 *
	 * @return a map of Rating Type to initial count (0)
	 */
	private Map<String, Integer> initialCountsForScotland() {
		Map<String, Integer> initial = new LinkedHashMap<>();
		initial.put("Pass", 0);
		initial.put("Improvement Required", 0);
		initial.put("Exempt", 0);
		return initial;
	}

	/**
	 * Constructs an initial map of zero counts containing all of the 'wanted' rating types for not-Scotland.
	 * Excludes 'Awaiting Inspection' and all others not specified in the tech test document.
	 *
	 * LinkedHashMap is used so that the order of indexes is retained.
	 *
	 * @return a map of Rating Type to initial count (0)
	 */
	private Map<String, Integer> initialCountsForNotScotland() {
		Map<String, Integer> initial = new LinkedHashMap<>();
		initial.put("5", 0);
		initial.put("4", 0);
		initial.put("3", 0);
		initial.put("2", 0);
		initial.put("1", 0);
		initial.put("0", 0);
		initial.put("Exempt", 0);
		return initial;
	}
}
