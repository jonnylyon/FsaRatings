package jl.fsaratings.ui;

import java.awt.Choice;
import java.awt.Dimension;
import java.awt.event.ItemListener;
import java.util.Map;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

/**
 * This class extends JFrame and provides the front end for the project
 * No automated testing is provided for this class, but functionality
 * is kept very specific to the front end.
 * @author jonny.lyon
 *
 */
public class FsaRatingsUIImpl extends JFrame implements FsaRatingsUI {

	private Choice authoritiesChoice;
	private JLabel resultsLabel;
	private JPanel panel;
	private JScrollPane scrollPane;

	/**
	 * Constructor for UI.  Sets up all of the UI components and their initial states.
	 */
	public FsaRatingsUIImpl() {
		panel = new JPanel();

		resultsLabel = new JLabel("");
		resultsLabel.setPreferredSize(new Dimension(0,400));
		resultsLabel.setVerticalAlignment(SwingConstants.TOP);

		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		authoritiesChoice = new Choice();
		authoritiesChoice.setPreferredSize(new Dimension(400, 0));
		panel.add(authoritiesChoice);
		scrollPane = new JScrollPane(resultsLabel);
		panel.add(scrollPane);
		this.add(panel);
		this.pack();
		this.setVisible(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void populateAuthoritiesList(Set<String> authorities) {
		authoritiesChoice.removeAll();
		authoritiesChoice.add("Please select an authority");
		authorities.forEach(a -> authoritiesChoice.add(a));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addItemListener(ItemListener listener) {
		authoritiesChoice.addItemListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getSelectedAuthorityName() {
		return authoritiesChoice.getSelectedItem();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void displayResults(Map<String, Double> results) {
		String resultsHtml = "<html><table><tr><th>Rating</th><th>Percentage</th></tr>";

		for (String key : results.keySet()) {
			resultsHtml += "<tr><td>"+formatRatingName(key)+"</td><td>"+String.format("%.2f%%", results.get(key))+"</td></tr>";
		}

		resultsHtml += "</table></html>";

		resultsLabel.setText(resultsHtml);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void displayResultError(String authority) {
		resultsLabel.setText("<html>An error occurred while loading results for "+authority+"</html>");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void displayNoResultsMessage(String authority) {
		resultsLabel.setText("<html>No results were found for "+authority+"</html>");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearResultMessage() {
		resultsLabel.setText("");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void notifyErrorLoadingAuthorities() {
		JOptionPane.showMessageDialog(this, "An error occurred while fetching the list of authorities");
	}

	/**
	 * Formats the rating names as appropriate.  This only consists of transforming
	 * ratings such as '4' and '5' into '4-star' and '5-star'.  It assumes that any
	 * rating string consisting of a single character qualifies for the '-star' suffix,
	 * which is true for current API data...
	 *
	 * @param original the original name of the rating
	 * @return the new name, with '-star' appended if appropriate
	 */
	private String formatRatingName(String original) {
		if (original.length() == 1) {
			return original + "-star";
		}
		return original;
	}
}
