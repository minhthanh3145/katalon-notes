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
	 * Open the connection with credentials
	 * 
	 * @param credentials
	 */
	void openConnection(String... credentials);

	/**
	 * Close the connection if previously opened, otherwise do nothing
	 */
	void closeConnection();

	/**
	 * Close connection if previously opened and open connection for the
	 * specified database
	 * 
	 * @param databaseLocation
	 */
	void switchDatabase(String databaseLocation);

	List<T> getByCustomQuery(String query);

	T create(T note);

	T update(T note);

	T delete(T id);

	T getById(Long id);

	List<T> getByName(String title);

	default String getLocalDatabaseLocation() {
		return "";
	}

	/**
	 * Set location for the database. Implementing classs will decide if this
	 * location is to be used or it will use a default one
	 * 
	 * @param location
	 */
	void setLocalDatabaseLocation(String location);

}
