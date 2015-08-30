package jl.fsaratings;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;

import jl.fsaratings.dao.FsaDao;
import jl.fsaratings.ui.FsaRatingsUI;

/**
 * Test class for FsaRatings class, mocking out the UI and DAO implementations.
 *
 * @author jonny.lyon
 *
 */
public class FsaRatingsTest {

	/**
	 * Mock DAO
	 */
	private FsaDao fsaDao;

	/**
	 * Mock UI
	 */
	private FsaRatingsUI ui;

	/**
	 * set up mocks
	 */
	@Before
	public void setUp() {
		fsaDao = createStrictMock(FsaDao.class);
		ui = createStrictMock(FsaRatingsUI.class);
	}

	@Test
	public void testFsaRatingsInitializesUICorrectlyWithValidAuthoritiesList() throws IOException {
		Map<String,Long> mockedAuthorities = buildFakeAuthorities();

		expect(fsaDao.fetchAuthorityNamesWithIds()).andReturn(mockedAuthorities).once();
		replay(fsaDao);

		ui.populateAuthoritiesList(mockedAuthorities.keySet());
		expectLastCall();
		ui.addItemListener(anyObject(FsaRatings.class));
		expectLastCall();
		replay(ui);

		new FsaRatings(fsaDao, ui);

		verify(ui);
	}

	@Test
	public void testFsaRatingsNotifiesUIOfErrorLoadingAuthorities() throws IOException {
		expect(fsaDao.fetchAuthorityNamesWithIds()).andThrow(new IOException("mockedException")).once();
		replay(fsaDao);

		ui.notifyErrorLoadingAuthorities();
		expectLastCall();
		ui.addItemListener(anyObject(FsaRatings.class));
		expectLastCall();
		replay(ui);

		new FsaRatings(fsaDao, ui);

		verify(ui);
	}

	@Test
	public void testFsaRatingsSelectsAndDisplaysCorrectResults() throws IOException {
		Map<String,Long> mockedAuthorities = buildFakeAuthorities();
		Map<String,Double> mockedPercentages = buildFakePercentages();

		expect(fsaDao.fetchAuthorityNamesWithIds()).andReturn(mockedAuthorities).once();
		expect(fsaDao.fetchRatingPercentagesForAuthority(2L)).andReturn(mockedPercentages).once();
		replay(fsaDao);

		ui.populateAuthoritiesList(mockedAuthorities.keySet());
		ui.addItemListener(anyObject());
		expect(ui.getSelectedAuthorityName()).andReturn("York").once();
		ui.displayResults(mockedPercentages);
		expectLastCall();
		replay(ui);

		FsaRatings instance = new FsaRatings(fsaDao, ui);
		instance.itemStateChanged(null);

		verify(fsaDao);
		verify(ui);
	}

	@Test
	public void testFsaRatingsClearsResultsIfPleaseSelectOptionIsSelected() throws IOException {
		Map<String,Long> mockedAuthorities = buildFakeAuthorities();

		expect(fsaDao.fetchAuthorityNamesWithIds()).andReturn(mockedAuthorities).once();
		replay(fsaDao);

		ui.populateAuthoritiesList(mockedAuthorities.keySet());
		ui.addItemListener(anyObject());
		expect(ui.getSelectedAuthorityName()).andReturn("Please Select").once();
		ui.clearResultMessage();;
		replay(ui);

		FsaRatings instance = new FsaRatings(fsaDao, ui);
		instance.itemStateChanged(null);

		verify();
	}

	@Test
	public void testFsaRatingsNotifiesUIOfErrorFetchingPercentages() throws IOException {
		Map<String,Long> mockedAuthorities = buildFakeAuthorities();

		expect(fsaDao.fetchAuthorityNamesWithIds()).andReturn(mockedAuthorities).once();
		expect(fsaDao.fetchRatingPercentagesForAuthority(2L)).andThrow(new IOException("mocked exception")).once();
		replay(fsaDao);

		ui.populateAuthoritiesList(mockedAuthorities.keySet());
		ui.addItemListener(anyObject());
		expect(ui.getSelectedAuthorityName()).andReturn("York").once();
		ui.displayResultError("York");
		replay(ui);

		FsaRatings instance = new FsaRatings(fsaDao, ui);
		instance.itemStateChanged(null);

		verify(fsaDao);
		verify(ui);
	}

	@Test
	public void testFsaRatingsNotifiesUIIfNoResultsArePresent() throws IOException {
		Map<String,Long> mockedAuthorities = buildFakeAuthorities();

		expect(fsaDao.fetchAuthorityNamesWithIds()).andReturn(mockedAuthorities).once();
		expect(fsaDao.fetchRatingPercentagesForAuthority(2L)).andReturn(new LinkedHashMap<String,Double>()).once();
		replay(fsaDao);

		ui.populateAuthoritiesList(mockedAuthorities.keySet());
		ui.addItemListener(anyObject());
		expect(ui.getSelectedAuthorityName()).andReturn("York").once();
		ui.displayNoResultsMessage("York");
		replay(ui);

		FsaRatings instance = new FsaRatings(fsaDao, ui);
		instance.itemStateChanged(null);

		verify();
	}

	private Map<String, Long> buildFakeAuthorities() {
		Map<String, Long> fakeAuthorities = new TreeMap<String, Long>();
		fakeAuthorities.put("Leeds", 1L);
		fakeAuthorities.put("York", 2L);
		fakeAuthorities.put("Sheffield", 3L);
		return fakeAuthorities;
	}

	private Map<String, Double> buildFakePercentages() {
		Map<String, Double> fakePercentages = new LinkedHashMap<String, Double>();
		fakePercentages.put("5", 20d);
		fakePercentages.put("4", 0d);
		fakePercentages.put("3", 0d);
		fakePercentages.put("2", 0d);
		fakePercentages.put("1", 0d);
		fakePercentages.put("0", 0d);
		fakePercentages.put("Exempt", 80d);
		return fakePercentages;
	}

}
