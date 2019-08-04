package thanhto.katalon.katalon_notes.provider;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import thanhto.katalon.katalon_notes.api.IDatabaseActionProvider;
import thanhto.katalon.katalon_notes.api.IDatabaseController;
import thanhto.katalon.katalon_notes.constant.ServiceName;
import thanhto.katalon.katalon_notes.factory.DatabaseActionProviderFactory;
import thanhto.katalon.katalon_notes.factory.DatabaseArtifactFactory;
import thanhto.katalon.katalon_notes.model.KatalonNote;
import thanhto.katalon.katalon_notes.service.DatabaseService;

/**
 * A central place to retrieve services across Katalon Notes
 * 
 * @author thanhto
 *
 */
public class ServiceProvider {
	private static ServiceProvider _instance;
	private Map<ServiceName, DatabaseService<KatalonNote>> serviceMap;
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
	 * The service is active by performing the following steps:
	 * <ul>
	 * <li>Retrieve {@link IDatabaseActionProvider} for the service</li>
	 * <li>Use the action provider to open the database connection at the given
	 * location with the given credentials</li>
	 * <li>Create database artifacts (collection, secondary storage, etc) using
	 * {@link DatabaseArtifactFactory}</li>
	 * <li>Create a new instance of {@link IDatabaseController} and give it to
	 * the service</li>
	 * </ul>
	 * 
	 * @param serviceName
	 *            Name of the {@link IDatabaseController} class corresponding to
	 *            this service
	 * @param databaseLocation
	 * @param credentials
	 * @return A {@link DatabaseService} instance after all the above steps have
	 *         been performed
	 */
	@SuppressWarnings("unchecked")
	public DatabaseService<KatalonNote> getDatabaseService(ServiceName serviceName, String databaseLocation,
			String... credentials) {
		DatabaseService<KatalonNote> service = serviceMap.get(serviceName);

		IDatabaseActionProvider actionProvider = actionProviderFactory.get(serviceName);
		actionProvider.setDatabaseLocation(databaseLocation);
		actionProvider.openConnection(credentials);

		DatabaseArtifactFactory.getInstance().createArtifactsFor(serviceName);

		try {
			service.setController((IDatabaseController<KatalonNote>) Class.forName(serviceName.getControllerName())
					.getConstructors()[0].newInstance());
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| SecurityException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return service;
	}

	/**
	 * Close all existing connections from all services
	 */
	public void deregisterAllServices() {
		for (Entry<ServiceName, DatabaseService<KatalonNote>> service : serviceMap.entrySet()) {
			actionProviderFactory.get(service.getKey()).closeConnection();
		}
	}

	/**
	 * Register all services. Connections to databases are not open here
	 */
	public void registerAllServices() {
		DatabaseService<KatalonNote> service = new DatabaseService<KatalonNote>();
		serviceMap.put(ServiceName.Nitrite, service);
	}
}
