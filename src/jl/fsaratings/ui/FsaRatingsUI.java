package jl.fsaratings.ui;

import java.awt.Choice;
import java.awt.Dimension;
import java.awt.event.ItemListener;
import java.util.Map;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

public class FsaRatingsUI extends JFrame {

	private Choice authoritiesChoice;
	private JLabel resultsLabel;

	public FsaRatingsUI(Set<String> authorities) {
		JPanel panel = new JPanel();

		resultsLabel = new JLabel("Please select an authority to begin");

		resultsLabel.setPreferredSize(new Dimension(0,400));
		resultsLabel.setVerticalAlignment(SwingConstants.TOP);

		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		authoritiesChoice = new Choice();
		authoritiesChoice.add("Please select an authority");
		authorities.forEach(a -> authoritiesChoice.add(a));
		panel.add(authoritiesChoice);

		panel.add(new JScrollPane(resultsLabel));
		this.add(panel);
		this.pack();
		this.setVisible(true);
	}

	public void addItemListener(ItemListener listener) {
		authoritiesChoice.addItemListener(listener);
	}

	public String getSelectedAuthorityName() {
		return authoritiesChoice.getSelectedItem();
	}

	public void showLoadingText() {
		resultsLabel.setText("Currently loading data...");
	}

	public void displayResults(Map<String, Double> results) {
		String resultsHtml = "<html><table><tr><th>Rating</th><th>Percentage</th></tr>";

		for (String key : results.keySet()) {
			resultsHtml += "<tr><td>"+key+"</td><td>"+String.format("%.2f%%", results.get(key))+"</td></tr>";
		}

		resultsHtml += "</table></html>";

		resultsLabel.setText(resultsHtml);
	}
}
