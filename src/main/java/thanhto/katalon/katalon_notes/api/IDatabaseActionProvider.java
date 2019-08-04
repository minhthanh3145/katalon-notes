package thanhto.katalon.katalon_notes.api;

/**
 * Provide a common interface for operations on database that are out of
 * CRUD-scope (open/close database connection operations, etc)
 * 
 * @author thanhto
 *
 */
public interface IDatabaseActionProvider {

	/**
	 * Implementing classes use this method to return the Collection instance or
	 * Database instance, and every custom objects that clients may need
	 * 
	 * @param key
	 * @return Object that the implementing class offers for this key. By
	 *         default this function returns null.
	 */
	default Object get(String key) {
		return null;
	}

	void openConnection(String... credentials);

	void closeConnection();

	void switchDatabase(String databaseLocation, String... credentials);

	default String getDatabaseLocation() {
		return "";
	}

	void setDatabaseLocation(String location);

}
