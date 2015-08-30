package jl.fsaratings;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;

import jl.fsaratings.dao.FsaDao;
import jl.fsaratings.ui.FsaRatingsUI;

public class FsaRatings implements ItemListener {

	private JFrame frame = new JFrame("Test");
	private JFrame appFrame;
	private JLabel resultsLabel;
	private Map<String, Long> authorities;
	private JTable resultsTable;
	private String[] columnNames = {"Rating", "Percentage"};

	private FsaRatingsUI ui;

	public FsaRatings() throws IOException {
		authorities = FsaDao.fetchAuthorityNamesWithIds();
		//buildUI2(authorities.keySet());
		ui = new FsaRatingsUI(authorities.keySet());
		ui.addItemListener(this);
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws IOException {
		FsaRatings app = new FsaRatings();
	}

	private void selectResults(String authorityName) {
		ui.showLoadingText();
		Long authorityId = authorities.get(authorityName);
		Map<String, Double> results = FsaDao.fetchRatingPercentagesForAuthority(authorityId);
		ui.displayResults(results);
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		selectResults(ui.getSelectedAuthorityName());
	}

}
