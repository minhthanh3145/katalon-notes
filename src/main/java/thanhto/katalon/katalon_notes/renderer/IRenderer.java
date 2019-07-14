package thanhto.katalon.katalon_notes.renderer;

import java.util.List;
import java.util.Map;

/**
 * Interface represents a renderer that can be used to convert markdown string
 * to html string. All renderers must implement this interface in order for
 * {@link HtmlRendererFactory} to properly create their instances
 * 
 * @author thanhto
 *
 */
public interface IRenderer {

	/**
	 * Convert mark-down string to HTML string.
	 * 
	 * This method will be invoked after
	 * {@link IRenderer#onRegisteredKeysHaveValues(Map)} so that implementing
	 * renderer can be aware of user-settings and set corresponding extra
	 * capabilities
	 * 
	 * @param markdownString
	 * @return A HTML-string representation of the input
	 */
	public String render(String markdownString);

	/**
	 * Implementing renderer must use this method to return a list of registered
	 * keys. See {@link IRenderer#onRegisteredKeysHaveValues(Map)}
	 * 
	 * @return
	 */
	public List<String> getRegisteredKeys();

	/**
	 * The implementing renderer must implement this method in order to add
	 * extra capabilities to the renderer based on values (available at
	 * run-time) of registered keys. Only keys registered with
	 * {@link IRenderer#getRegisteredKeys()} will be available to this method
	 * when invoked
	 * 
	 * @param registeredKeyValueMap
	 *            The map of registered keys and user-input values
	 */
	public void onRegisteredKeysHaveValues(Map<String, String> registeredKeyValueMap);

}
