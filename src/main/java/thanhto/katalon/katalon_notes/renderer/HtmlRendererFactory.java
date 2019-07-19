package thanhto.katalon.katalon_notes.renderer;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import com.katalon.platform.api.exception.ResourceException;
import com.katalon.platform.api.model.ProjectEntity;
import com.katalon.platform.api.preference.PluginPreference;
import com.katalon.platform.api.service.ApplicationManager;

import thanhto.katalon.katalon_notes.exception.PluginPreferenceIsNotAvailable;
import thanhto.katalon.katalon_notes.exception.RendererNotRegisteredException;

/**
 * Factory to create instances of html renderers. All renderers are given
 * run-time information about user-settings so that extra capabilities can be
 * added appropriately
 * 
 * @author thanhto
 *
 */
public class HtmlRendererFactory {
	private static HtmlRendererFactory _instance;
	private Map<String, IRenderer> rendererMap;

	public static HtmlRendererFactory getInstance() {
		if (_instance == null) {
			_instance = new HtmlRendererFactory();
		}
		return _instance;
	}

	/**
	 * Use reflection to load all classes implementing
	 * {@link thanhto.katalon.katalon_notes.renderer.IRenderer}
	 */
	public HtmlRendererFactory() {
		rendererMap = new HashMap<>();
		Reflections reflections = new Reflections("thanhto.katalon.katalon_notes");
		Set<Class<? extends IRenderer>> classesImplementingIRenderer = reflections.getSubTypesOf(IRenderer.class);

		classesImplementingIRenderer.forEach(a -> {
			try {
				rendererMap.put(a.getName(), (IRenderer) a.getConstructors()[0].newInstance(new Object[0]));
				System.out.println(a.getName() + " has been registered as a renderer");
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | SecurityException e) {
				e.printStackTrace();
			}
		});
	}

	public void makeRenderersAwareOfUserSettings(IRenderer renderer) throws PluginPreferenceIsNotAvailable {
		ProjectEntity projectEntity = ApplicationManager.getInstance().getProjectManager().getCurrentProject();
		if (projectEntity == null) {
			throw new PluginPreferenceIsNotAvailable();
		}
		try {
			PluginPreference preferences = ApplicationManager.getInstance().getPreferenceManager()
					.getPluginPreference(projectEntity.getId(), "thanhto.katalon.katalon-notes");
			Map<String, String> registeredKeyMap = new HashMap<>();
			for (String registeredKey : renderer.getRegisteredKeys()) {
				registeredKeyMap.put(registeredKey, preferences.getString(registeredKey, ""));
			}
			renderer.onRegisteredKeysHaveValues(registeredKeyMap);
		} catch (ResourceException e) {
			e.printStackTrace();
		}
	}

	public IRenderer getRenderer(Class<?> clazz) {
		return rendererMap.get(clazz.getName());
	}

	/**
	 * Construct a renderer instance of the renderer class if it was registered.
	 * Retrieve user-setting and pass the key-value map containing only entries
	 * whose keys are registered by {@link IRenderer#getRegisteredKeys()} into
	 * the method {@link IRenderer#onRegisteredKeysHaveValues(Map)} of the
	 * renderer
	 * 
	 * @param clazz
	 *            The renderer class
	 * @return An {@link IRenderer} instance
	 * @throws RendererNotRegisteredException
	 *             If the renderer class does not implement interface
	 *             {@link IRenderer}
	 * @throws PluginPreferenceIsNotAvailable
	 *             If no project is not open
	 */
	public IRenderer getUserSettingAwareRenderer(Class<?> clazz)
			throws RendererNotRegisteredException, PluginPreferenceIsNotAvailable {
		IRenderer renderer = getRenderer(clazz);
		if (renderer == null) {
			throw new RendererNotRegisteredException(clazz);
		}
		makeRenderersAwareOfUserSettings(renderer);
		return renderer;
	}
}
