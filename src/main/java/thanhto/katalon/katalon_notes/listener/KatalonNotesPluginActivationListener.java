package thanhto.katalon.katalon_notes.listener;

import com.katalon.platform.api.Plugin;
import com.katalon.platform.api.extension.PluginActivationListener;

import thanhto.katalon.katalon_notes.provider.ServiceProvider;

public class KatalonNotesPluginActivationListener implements PluginActivationListener {
    @Override
    public void afterActivation(Plugin plugin) {
        System.out.println("Plugin ID: " + plugin.getPluginId() + "is installed !");
        ServiceProvider.getInstance().deregisterAllServices();
        ServiceProvider.getInstance().registerAllServices();
    }
    
    public void beforeDeactivation(Plugin plugin) {
        System.out.println("Plugin ID: " + plugin.getPluginId() + "is uninstalled !");
        ServiceProvider.getInstance().deregisterAllServices();
    }
}
