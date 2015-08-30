package jl.fsaratings.ui;

import java.awt.event.ItemListener;
import java.util.Map;
import java.util.Set;

/**
 * UI interface, provided to allow easy mocking for test purposes.
 * @author jonny.lyon
 *
 */
public interface FsaRatingsUI {

	/**
	 * Populates the dropdown list with the authority options
	 *
	 * @param authorities A set of authority names
	 */
	void populateAuthoritiesList(Set<String> authorities);

	/**
	 * Adds an item listener to listen for changes to the dropdown
	 *
	 * @param listener A suitable listener, implementing the ItemListener interface
	 */
	void addItemListener(ItemListener listener);

	/**
	 * Gets the name of the selected authority from the dropdown
	 *
	 * @return the name of the selected authority
	 */
	String getSelectedAuthorityName();

	/**
	 * Takes the results, formats them and displays them in the results
	 * section of the UI
	 *
	 * @param results A map from rating type to percentage
	 */
	void displayResults(Map<String, Double> results);

	/**
	 * Replaces the current contents of the results section with a messsage
	 * informing the user of an error retrieving the results
	 *
	 * @param authority the authority that was being retrieved when the error occurred
	 */
	void displayResultError(String authority);

	/**
	 * Replaces the current contents of the results section with a message
	 * informing the user that no 'wanted' results are available for the selected
	 * authority
	 *
	 * @param authority the authority for which no results are available
	 */
	void displayNoResultsMessage(String authority);

	/**
	 * Removes the current contents of the results section
	 */
	void clearResultMessage();

	/**
	 * Displays a dialog informing the user of an error loading the list of available
	 * authorities
	 */
	void notifyErrorLoadingAuthorities();
}
