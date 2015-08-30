package jl.fsaratings;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.Map;

import jl.fsaratings.dao.FsaDao;
import jl.fsaratings.dao.FsaDaoImpl;
import jl.fsaratings.rest.RestHandlerImpl;
import jl.fsaratings.ui.FsaRatingsUI;
import jl.fsaratings.ui.FsaRatingsUIImpl;

/**
 * Main controller class for app
 *
 * @author jonny.lyon
 *
 */
public class FsaRatings implements ItemListener {

	/**
	 * This map holds the authority ID for each authority.  It is indexed by
	 * the authority name, under the assumption that these are unique.
	 */
	private Map<String, Long> authorities;

	private final FsaRatingsUI ui;

	private final FsaDao fsaDao;

	/**
	 * Constructor for FsaRatings class, which acts effectively as an app controller.
	 *
	 * Retrieves the list of authorities and provides it to the UI.  Also sets up
	 * a listener relationship with the UI.
	 *
	 * @param fsaDao the Data Access Object used to get the relevant API data
	 * @param ui the UI component
	 */
	public FsaRatings(FsaDao fsaDao, FsaRatingsUI ui) {
		this.fsaDao = fsaDao;
		this.ui = ui;

		try {
			authorities = fsaDao.fetchAuthorityNamesWithIds();
			ui.populateAuthoritiesList(authorities.keySet());
		} catch (IOException e) {
			ui.notifyErrorLoadingAuthorities();
		}

		ui.addItemListener(this);
	}

	/**
	 * Main method for app.  Instantiates the components and their relationships.
	 *
	 * @param args (no command line args are available)
	 */
	public static void main(String[] args) throws IOException {
		FsaDao dao = new FsaDaoImpl(new RestHandlerImpl());
		FsaRatings app = new FsaRatings(dao, new FsaRatingsUIImpl());
	}

	/**
	 * Retrieves the rating percentage information for the specified authority
	 * and provides it to the UI.  Also handles various edge case and exception
	 * scenarios and informs the UI as appropriate
	 *
	 * @param authorityName the specified authority
	 */
	private void selectResults(String authorityName) {
		Long authorityId = authorities.get(authorityName);
		if (authorityId == null) {
			// Example scenario is if the Please Select option is selected on the UI
			ui.clearResultMessage();
		} else {
			Map<String, Double> results;
			try {
				results = fsaDao.fetchRatingPercentagesForAuthority(authorityId);
				if (results.size() == 0) {
					ui.displayNoResultsMessage(authorityName);
				} else {
					ui.displayResults(results);
				}
			} catch (IOException e) {
				ui.displayResultError(authorityName);
			} catch (NullPointerException e) {
				ui.displayResultError(authorityName);
			}
		}
	}

	/**
	 * This method handles selection changes on the UI.
	 *
	 * @param e the UI event
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {
		selectResults(ui.getSelectedAuthorityName());
	}

}
