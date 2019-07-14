package thanhto.katalon.katalon_notes.listener;

import com.katalon.platform.api.Plugin;
import com.katalon.platform.api.exception.ResourceException;
import com.katalon.platform.api.extension.PluginActivationListener;
import com.katalon.platform.api.model.ProjectEntity;
import com.katalon.platform.api.preference.PluginPreference;
import com.katalon.platform.api.service.ApplicationManager;

import thanhto.katalon.katalon_notes.provider.ServiceProvider;

public class KatalonNotesPluginActivationListener implements PluginActivationListener {
	@Override
	public void afterActivation(Plugin plugin) {
		System.out.println("Plugin ID: " + plugin.getPluginId() + " is installed !");
		ServiceProvider.getInstance().deregisterAllServices();
		ServiceProvider.getInstance().registerAllServices();

		ProjectEntity projectEntity = ApplicationManager.getInstance().getProjectManager().getCurrentProject();
		if (projectEntity != null) {
			try {
				PluginPreference preferences = ApplicationManager.getInstance().getPreferenceManager()
						.getPluginPreference(projectEntity.getId(), "thanhto.katalon.katalon-notes");
				preferences.setString("heading-anchor", "true");
				preferences.setString("autolink", "true");
			} catch (ResourceException e) {
				e.printStackTrace();
			}
		}
	}

	public void beforeDeactivation(Plugin plugin) {
		System.out.println("Plugin ID: " + plugin.getPluginId() + "is uninstalled !");
		ServiceProvider.getInstance().deregisterAllServices();
	}
}
