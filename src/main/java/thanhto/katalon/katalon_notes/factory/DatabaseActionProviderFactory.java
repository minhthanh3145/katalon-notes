package thanhto.katalon.katalon_notes.factory;

import java.util.HashMap;
import java.util.Map;

import thanhto.katalon.katalon_notes.constant.ServiceName;
import thanhto.katalon.katalon_notes.provider.IDatabaseActionProvider;
import thanhto.katalon.katalon_notes.provider.NitriteDatabaseActionProvider;

/**
 * Provide actions for database service including open/close/switch connections
 * 
 * @author thanhto
 *
 */
public class DatabaseActionProviderFactory {

	private static DatabaseActionProviderFactory _instance;
	private Map<String, IDatabaseActionProvider> actionProviderMap = new HashMap<>();

	public static DatabaseActionProviderFactory getInstance() {
		if (_instance == null) {
			_instance = new DatabaseActionProviderFactory();
		}
		return _instance;
	}

	private DatabaseActionProviderFactory() {
		actionProviderMap.put(ServiceName.Nitrite.toString(), new NitriteDatabaseActionProvider());
	}

	/**
	 * Get the corresponding {@link IDatabaseActionProvider} for the service
	 * 
	 * @param serviceName
	 *            name of the requested service
	 * @return {@link IDatabaseActionProvider}
	 */
	public IDatabaseActionProvider get(ServiceName serviceName) {
		return actionProviderMap.get(serviceName.toString());
	}
}
