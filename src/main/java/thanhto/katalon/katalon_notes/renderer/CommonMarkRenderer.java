package thanhto.katalon.katalon_notes.renderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.commonmark.Extension;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import thanhto.katalon.katalon_notes.api.IRenderer;

/**
 * Adapter from <a href='https://github.com/atlassian/commonmark-java'>
 * common-mark </a> library to a IRenderer
 * 
 * @author thanhto
 *
 */
public class CommonMarkRenderer implements IRenderer {
	List<Extension> extensions = new ArrayList<>();
	private Parser parser;
	private HtmlRenderer renderer;

	public void addExtensions(Extension commonMarkExtensions) {
		this.extensions.add(commonMarkExtensions);
	}

	@Override
	public String render(String markdownString) {
		Node document = parser.parse(markdownString);
		return renderer.render(document);
	}

	@Override
	public List<String> getRegisteredKeys() {
		return Arrays.asList("heading-anchor", "autolink");
	}

	@Override
	public void onRegisteredKeysHaveValues(Map<String, String> registeredKeyValueMap) {
		if (registeredKeyValueMap.getOrDefault("heading-anchor", "").equals("true")) {
			System.out.println("heading-anchor extension for common-mark is enabled");
			addExtensions(HeadingAnchorExtension.create());
		}

		if (registeredKeyValueMap.getOrDefault("autolink", "").equals("true")) {
			System.out.println("autolink anchor extension for common-mark is enabled");
			addExtensions(HeadingAnchorExtension.create());
		}

		parser = Parser.builder().extensions(extensions).build();
		renderer = HtmlRenderer.builder().extensions(extensions).build();
	}

}
