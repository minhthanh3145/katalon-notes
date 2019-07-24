package thanhto.katalon.katalon_notes.provider;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.dizitart.no2.NitriteCollection;

import thanhto.katalon.katalon_notes.controller.IDatabaseController;
import thanhto.katalon.katalon_notes.controller.NitriteDatabaseController;
import thanhto.katalon.katalon_notes.exception.DatabaseControllerUnselectedException;
import thanhto.katalon.katalon_notes.factory.DatabaseActionProviderFactory;
import thanhto.katalon.katalon_notes.factory.DatabaseArtifactFactory;
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
	private DatabaseActionProviderFactory actionProviderFactory = DatabaseActionProviderFactory.getInstance();

	public static ServiceProvider getInstance() {
		if (_instance == null) {
			_instance = new ServiceProvider();
		}
		return _instance;
	}

	private ServiceProvider() {
		serviceMap = new HashMap<>();
	}

	/**
	 * 
	 * Open database connection corresponding to the given service's name. Only
	 * after calling this method does the corresponding
	 * {@link IDatabaseController} instance is functional
	 * 
	 * @param serviceName
	 * @param databaseLocation
	 * @param credentials
	 * @return A {@link DatabaseService} instance
	 */
	@SuppressWarnings("unchecked")
	public DatabaseService<INote> getDatabaseService(String serviceName, String databaseLocation, String... credentials)
			throws DatabaseControllerUnselectedException {
		DatabaseService<INote> service = serviceMap.get(serviceName);

		IDatabaseActionProvider actionProvider = actionProviderFactory.get(serviceName);
		actionProvider.setLocalDatabaseLocation(databaseLocation);
		actionProvider.openConnection(credentials);

		registerDatabaseArtifacts(serviceName, actionProvider);

		try {
			service.setController(
					(IDatabaseController<INote>) Class.forName(serviceName).getConstructors()[0].newInstance());
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| SecurityException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return service;
	}

	/**
	 * Register important artifacts for each respective type of databases such
	 * as database instance, collection instance, etc.
	 * 
	 * The given instance of {@link IDatabaseActionProvider} is assumed to have
	 * already opened a database connection with appropriate location and
	 * credentials before invocation of this method
	 * 
	 * @param serviceName
	 *            Name of the {@link IDatabaseController} class
	 * @param actionProvider
	 *            Instance of {@link IDatabaseActionProvider} associating with
	 *            the given controller class name
	 */
	private void registerDatabaseArtifacts(String serviceName, IDatabaseActionProvider actionProvider) {
		if (serviceName.equals(NitriteDatabaseController.class.getName())) {
			DatabaseArtifactFactory.getInstance().setArtifact(serviceName, NitriteCollection.class.getName(),
					actionProvider.get(NitriteCollection.class));
		}
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
			actionProviderFactory.get(service.getKey()).closeConnection();
		}
	}

	/**
	 * Register all services. Connections to databases are not open here
	 */
	public void registerAllServices() {
		DatabaseService<INote> service = new DatabaseService<INote>();
		serviceMap.put(NitriteDatabaseController.class.getName(), service);
	}
}
