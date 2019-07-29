package thanhto.katalon.katalon_notes.factory;

import java.util.HashMap;
import java.util.Map;

import thanhto.katalon.katalon_notes.constant.ServiceName;
import thanhto.katalon.katalon_notes.controller.NitriteDatabaseController;
import thanhto.katalon.katalon_notes.provider.IDatabaseActionProvider;
import thanhto.katalon.katalon_notes.provider.ServiceProvider;

/**
 * <p>
 * Client DatabaseControllers uses this factory to retrieve database artifacts.
 * </p>
 * 
 * <p>
 * {@link ServiceProvider} uses this factory to create database artifacts when
 * the database is created and opened
 * </p>
 * 
 * @author thanhto
 *
 */
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
	 * Should be used by the client
	 * {@link thanhto.katalon.katalon_notes.controller.IDatabaseController} to
	 * retrieve needed database artifacts
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

	/**
	 * <p>
	 * This method is called by
	 * {@link thanhto.katalon.katalon_notes.provider.ServiceProvider} to create
	 * appropriate database artifacts for its services.
	 * </p>
	 * 
	 * <p>
	 * Client
	 * {@link thanhto.katalon.katalon_notes.controller.IDatabaseController}
	 * should not use this method because this method assumes prerequisites that
	 * can only be satisfied by
	 * {@link thanhto.katalon.katalon_notes.provider.ServiceProvider}
	 * </p>
	 * 
	 * @param serviceName
	 */
	public void createArtifactsFor(ServiceName serviceName) {
		IDatabaseActionProvider actionProvider = DatabaseActionProviderFactory.getInstance().get(serviceName);
		if (serviceName.getControllerName().equals(NitriteDatabaseController.class.getName())) {
			setArtifact(serviceName.getControllerName(), "objectRepository", actionProvider.get("objectRepository"));
		}
	}
}
