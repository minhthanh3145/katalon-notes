package thanhto.katalon.katalon_notes.factory;

import java.util.HashMap;
import java.util.Map;

import thanhto.katalon.katalon_notes.controller.IDatabaseController;
import thanhto.katalon.katalon_notes.controller.NitriteDatabaseController;
import thanhto.katalon.katalon_notes.provider.IDatabaseActionProvider;
import thanhto.katalon.katalon_notes.provider.NitriteDatabaseActionProvider;

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
		actionProviderMap.put(NitriteDatabaseController.class.getName(), new NitriteDatabaseActionProvider());
	}

	/**
	 * Get the corresponding {@link IDatabaseActionProvider}
	 * 
	 * @param className
	 *            Name of the corresponding {@link IDatabaseController}
	 * @return {@link IDatabaseActionProvider}
	 */
	public IDatabaseActionProvider get(String className) {
		return actionProviderMap.get(className);
	}
}
