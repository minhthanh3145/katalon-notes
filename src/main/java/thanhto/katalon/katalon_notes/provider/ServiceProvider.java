package thanhto.katalon.katalon_notes.provider;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.katalon.platform.api.service.ApplicationManager;

import thanhto.katalon.katalon_notes.controller.NitriteDatabaseController;
import thanhto.katalon.katalon_notes.service.AbstractDatabaseService;
import thanhto.katalon.katalon_notes.service.NitriteDatabaseService;

public class ServiceProvider {
	private static ServiceProvider _instance;
	private Map<String, AbstractDatabaseService> serviceMap;

	public static ServiceProvider getInstance() {
		if (_instance == null) {
			_instance = new ServiceProvider();
		}
		return _instance;
	}

	private ServiceProvider() {
		serviceMap = new HashMap<>();
	}

	public AbstractDatabaseService getService(String serviceName) {
		return serviceMap.get(serviceName);
	}

	public void deregisterAllServices() {
		for (Entry<String, AbstractDatabaseService> service : serviceMap.entrySet()) {
			service.getValue().closeConnection();
		}
	}

	public void registerAllServices() {
		AbstractDatabaseService service = new NitriteDatabaseService();
		NitriteDatabaseController nitriteController = new NitriteDatabaseController();
		nitriteController.initializeDb(
				ApplicationManager.getInstance().getProjectManager().getCurrentProject().getFolderLocation());
		service.setController(nitriteController);
		serviceMap.put("nitrite", service);
		System.out.println("Nitrite database service is registed !");
	}
}
