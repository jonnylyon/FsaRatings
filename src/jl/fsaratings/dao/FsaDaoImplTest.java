package jl.fsaratings.dao;

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import jl.fsaratings.rest.RestHandler;

/**
 * Test class for FsaDaoImplTest class, mocking out RestHandler implementation
 *
 * @author jonny.lyon
 *
 */
public class FsaDaoImplTest {

	/**
	 * Mock RestHandler
	 */
	private RestHandler rest;

	/**
	 * Instance under test
	 */
	private FsaDaoImpl instance;

	/**
	 * set up mocks and test instance
	 */
	@Before
	public void setUp() {
		rest = createStrictMock(RestHandler.class);
		instance = new FsaDaoImpl(rest);
	}

	@Test
	public void testFetchAuthoritiesMapsJsonObjectCorrectly() throws IOException {
		JSONArray authoritiesArray = new JSONArray();
		authoritiesArray.put(createJsonAuthority("Birmingham", 5L));
		authoritiesArray.put(createJsonAuthority("London", 6L));
		authoritiesArray.put(createJsonAuthority("Manchester", 7L));
		JSONObject rootJson = new JSONObject();
		rootJson.put("authorities", authoritiesArray);

		expect(rest.getEndpointResponse(Messages.getString("FsaDao.fsa.api.authorities"))).andReturn(rootJson).once();
		replay(rest);

		Map<String, Long> actual = instance.fetchAuthorityNamesWithIds();

		Map<String, Long> expected = new TreeMap<>();
		expected.put("Birmingham", 5L);
		expected.put("London", 6L);
		expected.put("Manchester", 7L);

		assertEquals(actual, expected);
	}

	@Test
	public void testFetchAutoritiesMapsJsonObjectCorrectlyWhenAuthoritiesCountIsZero() throws IOException {
		JSONArray authoritiesArray = new JSONArray();
		JSONObject rootJson = new JSONObject();
		rootJson.put("authorities", authoritiesArray);

		expect(rest.getEndpointResponse(Messages.getString("FsaDao.fsa.api.authorities"))).andReturn(rootJson).once();
		replay(rest);

		Map<String, Long> actual = instance.fetchAuthorityNamesWithIds();

		Map<String, Long> expected = new TreeMap<>();

		assertEquals(actual, expected);
	}

	@Test
	public void testFetchAuthoritiesThrowsIOExceptionStraightUp() throws IOException {
		expect(rest.getEndpointResponse(Messages.getString("FsaDao.fsa.api.authorities"))).andThrow(new IOException("mocked exception")).once();
		replay(rest);

		try {
			Map<String, Long> actual = instance.fetchAuthorityNamesWithIds();
			fail("IOException expected");
		} catch (IOException e) {
			// do nothing;
		}
	}

	@Test
	public void testFetchPercentagesMapsJsonCorrectlyIgnoringUnwantedRatingsForScotland() throws IOException {
		JSONArray establishmentsArray = new JSONArray();
		establishmentsArray.put(createJsonEstablishment("Pass", "FHIS"));
		establishmentsArray.put(createJsonEstablishment("Pass", "FHIS"));
		establishmentsArray.put(createJsonEstablishment("Pass", "FHIS"));
		establishmentsArray.put(createJsonEstablishment("Pass", "FHIS"));
		establishmentsArray.put(createJsonEstablishment("Pass", "FHIS"));
		establishmentsArray.put(createJsonEstablishment("Exempt", "FHIS"));
		establishmentsArray.put(createJsonEstablishment("Exempt", "FHIS"));
		establishmentsArray.put(createJsonEstablishment("Exempt", "FHIS"));
		establishmentsArray.put(createJsonEstablishment("Awaiting Inspection", "FHIS"));
		establishmentsArray.put(createJsonEstablishment("Awaiting Inspection", "FHIS"));
		JSONObject rootJson = new JSONObject();
		rootJson.put("establishments", establishmentsArray);

		expect(rest.getEndpointResponse(Messages.getString("FsaDao.fsa.api.establishments").replace("{authorityId}", "2"))).andReturn(rootJson).once();
		replay(rest);

		Map<String, Double> actual = instance.fetchRatingPercentagesForAuthority(2L);

		Map<String, Double> expected = new LinkedHashMap<>();
		expected.put("Pass", 62.5d);
		expected.put("Improvement Required", 0d);
		expected.put("Exempt", 37.5d);

		assertEquals(actual, expected);
	}

	@Test
	public void testFetchPercentagesMapsJsonCorrectlyIgnoringUnwantedRatingsForNotScotland() throws IOException {
		JSONArray establishmentsArray = new JSONArray();
		establishmentsArray.put(createJsonEstablishment("5", "FHRS"));
		establishmentsArray.put(createJsonEstablishment("5", "FHRS"));
		establishmentsArray.put(createJsonEstablishment("5", "FHRS"));
		establishmentsArray.put(createJsonEstablishment("4", "FHRS"));
		establishmentsArray.put(createJsonEstablishment("2", "FHRS"));
		establishmentsArray.put(createJsonEstablishment("0", "FHRS"));
		establishmentsArray.put(createJsonEstablishment("Exempt", "FHRS"));
		establishmentsArray.put(createJsonEstablishment("Exempt", "FHRS"));
		establishmentsArray.put(createJsonEstablishment("Awaiting Inspection", "FHRS"));
		establishmentsArray.put(createJsonEstablishment("Awaiting Inspection", "FHRS"));
		establishmentsArray.put(createJsonEstablishment("Awaiting Inspection", "FHRS"));
		JSONObject rootJson = new JSONObject();
		rootJson.put("establishments", establishmentsArray);

		expect(rest.getEndpointResponse(Messages.getString("FsaDao.fsa.api.establishments").replace("{authorityId}", "2"))).andReturn(rootJson).once();
		replay(rest);

		Map<String, Double> actual = instance.fetchRatingPercentagesForAuthority(2L);

		Map<String, Double> expected = new LinkedHashMap<>();
		expected.put("5", 37.5d);
		expected.put("4", 12.5d);
		expected.put("3", 0d);
		expected.put("2", 12.5d);
		expected.put("1", 0d);
		expected.put("0", 12.5d);
		expected.put("Exempt", 25d);

		assertEquals(actual, expected);
	}

	@Test
	public void testFetchPercentagesMapsJsonObjectCorrectlyWhenRatingsCountIsZero() throws IOException {
		JSONArray establishmentsArray = new JSONArray();
		establishmentsArray.put(createJsonEstablishment("Awaiting Inspection", "FHRS"));
		establishmentsArray.put(createJsonEstablishment("Awaiting Inspection", "FHRS"));
		establishmentsArray.put(createJsonEstablishment("Awaiting Inspection", "FHRS"));
		JSONObject rootJson = new JSONObject();
		rootJson.put("establishments", establishmentsArray);

		expect(rest.getEndpointResponse(Messages.getString("FsaDao.fsa.api.establishments").replace("{authorityId}", "2"))).andReturn(rootJson).once();
		replay(rest);

		Map<String, Double> actual = instance.fetchRatingPercentagesForAuthority(2L);

		Map<String, Double> expected = new LinkedHashMap<>();

		assertEquals(actual, expected);
	}

	@Test
	public void testFetchPercentagesMapsJsonObjectCorrectlyWhenRatingsCountIsZeroExcludingUnwantedRatings() throws IOException {
		JSONArray establishmentsArray = new JSONArray();
		JSONObject rootJson = new JSONObject();
		rootJson.put("establishments", establishmentsArray);

		expect(rest.getEndpointResponse(Messages.getString("FsaDao.fsa.api.establishments").replace("{authorityId}", "2"))).andReturn(rootJson).once();
		replay(rest);

		Map<String, Double> actual = instance.fetchRatingPercentagesForAuthority(2L);

		Map<String, Double> expected = new LinkedHashMap<>();

		assertEquals(actual, expected);
	}

	@Test
	public void testFetchPercentagesThrowsIOExceptionStraightUp() throws IOException {
		expect(rest.getEndpointResponse(Messages.getString("FsaDao.fsa.api.establishments").replace("{authorityId}", "2"))).andThrow(new IOException("mocked exception")).once();
		replay(rest);

		try {
			Map<String, Double> actual = instance.fetchRatingPercentagesForAuthority(2L);
			fail("IOException expected");
		} catch (IOException e) {
			// do nothing;
		}
	}

	private JSONObject createJsonAuthority(String name, Long id) {
		JSONObject auth = new JSONObject();
		auth.put("Name", name);
		auth.put("LocalAuthorityId", id);
		return auth;
	}

	private JSONObject createJsonEstablishment(String ratingValue, String schemeType) {
		JSONObject estb = new JSONObject();
		estb.put("RatingValue", ratingValue);
		estb.put("SchemeType", schemeType);
		return estb;
	}
}
