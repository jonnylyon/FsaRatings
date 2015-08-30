package jl.fsaratings.dao;

import java.io.IOException;
import java.util.Map;

/**
 * DAO interface, provided to allow easy mocking for test purposes.
 * @author jonny.lyon
 *
 */
public interface FsaDao {

	/**
	 * Retrieves a map indexed by the names of all of the authorities (assuming they are unique),
	 * mapped to their local authority IDs.
	 *
	 * @return the authorities map
	 * @throws IOException if an error occurs retrieving data from the external API
	 */
	Map<String, Long> fetchAuthorityNamesWithIds() throws IOException;

	/**
	 * For the authority ID specified, calculates the distribution of ratings between all of the
	 * 'wanted' ratings.  An assumption is made that all establishments in a single authority
	 * use the same rating scheme, and that only those rating types specified in the tech test
	 * document are included in the distribution.
	 *
	 * @param authorityId the authority to find rating distributions for
	 * @return a map indexed by rating name, mapping to the percentage for that rating
	 * @throws IOException if an error occurs retrieving data from the external API
	 */
	Map<String, Double> fetchRatingPercentagesForAuthority(Long authorityId) throws IOException;
}
