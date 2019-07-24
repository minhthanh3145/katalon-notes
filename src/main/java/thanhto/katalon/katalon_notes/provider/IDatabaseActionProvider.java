package thanhto.katalon.katalon_notes.provider;

/**
 * Provide a common interface for operations on database that are out of
 * CRUD-scope (including open/close database connection operations)
 * 
 * @author thanhto
 *
 */
public interface IDatabaseActionProvider {

	/**
	 * Implementing classes can use this method to return the Collection
	 * instance or Database instance, and every custom objects that clients may
	 * need
	 * 
	 * @param clazz
	 *            Class whose instance is to be retrieved
	 * @return An instance of the requested class if the implementing class
	 *         provides one. Null otherwise
	 */
	default <E extends V, V> E get(Class<V> clazz) {
		return null;
	}

	void openConnection();

	void openConnection(String... credentials);

	void closeConnection();

	void switchDatabase(String databaseLocation);

	default String getLocalDatabaseLocation() {
		return "";
	}

	void setLocalDatabaseLocation(String location);

	void commit();

}
