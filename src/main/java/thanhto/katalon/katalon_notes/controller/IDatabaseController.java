package thanhto.katalon.katalon_notes.controller;

import java.util.List;

/**
 * A Database controller interface that offers querying along with connection
 * open/close mechanisms
 * 
 * @author thanhto
 *
 * @param <T>
 */

public interface IDatabaseController<T> {

	/**
	 * Open the connection if one hasn't existed, otherwise do nothing
	 */
	void openConnection();

	/**
	 * Close the connection if previously opened, otherwise do nothing
	 */
	void closeConnection();

	List<T> getByCustomQuery(String query);

	T create(T note);

	T update(T note);

	T delete(T id);

	T getById(Long id);

	List<T> getByName(String title);

}
