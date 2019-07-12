package thanhto.katalon.katalon_notes.provider;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import thanhto.katalon.katalon_notes.controller.IDatabaseController;
import thanhto.katalon.katalon_notes.controller.NitriteDatabaseController;
import thanhto.katalon.katalon_notes.exception.DatabaseControllerUnselectedException;
import thanhto.katalon.katalon_notes.model.INote;
import thanhto.katalon.katalon_notes.service.DatabaseService;

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
	 * Return the service with and opened connection
	 * 
	 * @param serviceName
	 *            Name of the service
	 * @return An {@link DatabaseService} instance
	 * @throws DatabaseControllerUnselectedException
	 *             If the selected service has not been given a
	 *             {@link IDatabaseController} instance yet
	 */
	public DatabaseService<INote> getAndOpenService(String serviceName) throws DatabaseControllerUnselectedException {
		DatabaseService<INote> service = serviceMap.get(serviceName);
		if (service.getController() == null) {
			throw new DatabaseControllerUnselectedException(serviceName);
		}
		service.getController().openConnection();
		return service;
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
	 * Note that this does not ensure that service connections are opened. See
	 * {@link ServiceProvider#getAndOpenService}
	 */
	public void registerAllServices() {
		DatabaseService<INote> service = new DatabaseService<INote>();
		service.setController(getController("nitrite"));
		serviceMap.put("nitrite", service);
		System.out.println("Nitrite database service is registed !");
	}

	public IDatabaseController<INote> getController(String key) {
		return noteDatabaseControllerMap.get(key);
	}
}
