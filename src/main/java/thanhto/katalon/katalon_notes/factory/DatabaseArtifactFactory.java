package thanhto.katalon.katalon_notes.factory;

import java.util.HashMap;
import java.util.Map;

public class DatabaseArtifactFactory {
	private static DatabaseArtifactFactory _instance;
	private Map<String, Map<String, Object>> artifactMap = new HashMap<>();

	public static DatabaseArtifactFactory getInstance() {
		if (_instance == null) {
			_instance = new DatabaseArtifactFactory();
		}
		return _instance;
	}

	private DatabaseArtifactFactory() {

	}

	public void setArtifact(String databaseControllerName, String key, Object value) {
		Map<String, Object> map = artifactMap.get(databaseControllerName);
		if (map == null) {
			map = new HashMap<>();
		}
		map.put(key, value);
		artifactMap.put(databaseControllerName, map);
	}

	/**
	 * Get the artifact inside the artifact map of the corresponding
	 * {@link thanhto.katalon.katalon_notes.controller.IDatabaseController}
	 * 
	 * @param databaseControllerName
	 *            Name of the database controller class
	 * @param key
	 *            Key into the artifact map retrieved from the given database
	 *            controller class name
	 * @return The artifact {@link Object}. Null if none exists
	 */
	public Object getArtifact(String databaseControllerName, String key) {
		Map<String, Object> map = artifactMap.get(databaseControllerName);
		if (map == null) {
			return null;
		}
		return map.get(key);
	}
}
