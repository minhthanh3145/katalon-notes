package thanhto.katalon.katalon_notes.provider;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import thanhto.katalon.katalon_notes.controller.IDatabaseController;
import thanhto.katalon.katalon_notes.controller.NitriteDatabaseController;
import thanhto.katalon.katalon_notes.exception.DatabaseControllerUnselectedException;
import thanhto.katalon.katalon_notes.model.INote;
import thanhto.katalon.katalon_notes.service.DatabaseService;

/**
 * A central place to provide services across Katalon Notes
 * 
 * @author thanhto
 *
 */
public class ServiceProvider {
	private static ServiceProvider _instance;
	private Map<String, DatabaseService<INote>> serviceMap;
	private Map<String, IDatabaseController<INote>> noteDatabaseControllerMap;

	public static ServiceProvider getInstance() {
		if (_instance == null) {
			_instance = new ServiceProvider();
		}
		return _instance;
	}

	private ServiceProvider() {
		serviceMap = new HashMap<>();
		noteDatabaseControllerMap = new HashMap<>();
		noteDatabaseControllerMap.put("nitrite", new NitriteDatabaseController());
	}

	/**
	 * 
	 * Open the connection of the database at the given location that is
	 * associated with the needed service by the given credentials and return
	 * that service
	 * 
	 * @param serviceName
	 * @param databaseLocation
	 * @param credentials
	 * @return
	 * @throws DatabaseControllerUnselectedException
	 */
	public DatabaseService<INote> getDatabaseService(String serviceName, String databaseLocation, String... credentials)
			throws DatabaseControllerUnselectedException {
		DatabaseService<INote> service = serviceMap.get(serviceName);
		if (service.getController() == null) {
			throw new DatabaseControllerUnselectedException(serviceName);
		}
		service.getController().setLocalDatabaseLocation(databaseLocation);
		service.getController().openConnection(credentials);
		return service;
	}

	/**
	 * Open the connection of the database at the given location that is
	 * associated with the needed service and return that service
	 * 
	 * This is equivalent to call
	 * {@link ServiceProvider#getDatabaseService(serviceName, databaseLocation,
	 * "")}
	 * 
	 * @param serviceName
	 * @param databaseLocation
	 * @return
	 * @throws DatabaseControllerUnselectedException
	 */
	public DatabaseService<INote> getDatabaseService(String serviceName, String databaseLocation)
			throws DatabaseControllerUnselectedException {
		return getDatabaseService(serviceName, databaseLocation, "");
	}

	/**
	 * Open the connection of the database associated with the needed service
	 * and return that service.
	 * 
	 * This is equivalent to call
	 * {@link ServiceProvider#getDatabaseService(serviceName, "", "")}
	 * 
	 * @param serviceName
	 *            Name of the service
	 * @return An {@link DatabaseService} instance
	 * @throws DatabaseControllerUnselectedException
	 *             If the selected service has not been given a
	 *             {@link IDatabaseController} instance yet
	 */
	public DatabaseService<INote> getDatabaseService(String serviceName) throws DatabaseControllerUnselectedException {
		return getDatabaseService(serviceName, "", "");
	}

	/**
	 * Close all existing connections from all services
	 */
	public void deregisterAllServices() {
		for (Entry<String, DatabaseService<INote>> service : serviceMap.entrySet()) {
			service.getValue().getController().closeConnection();
		}
	}

	/**
	 * Register services and give them the appropriate database controllers.
	 * Note database connections are lazily opened (delayed until the first time
	 * they're needed) {@link ServiceProvider#getAndOpenService}
	 */
	public void registerAllServices() {
		DatabaseService<INote> service = new DatabaseService<INote>();
		service.setController(getController("nitrite"));
		serviceMap.put("nitrite", service);
	}

	public IDatabaseController<INote> getController(String key) {
		return noteDatabaseControllerMap.get(key);
	}
}
