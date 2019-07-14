package thanhto.katalon.katalon_notes.renderer;

import java.util.Set;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.reflections.Reflections;

import thanhto.katalon.katalon_notes.exception.PluginPreferenceIsNotAvailable;
import thanhto.katalon.katalon_notes.exception.RendererNotRegisteredException;

public class TestHtmlRendererFactory {

	@Test
	public void testGetInstance() {
		Reflections reflections = new Reflections("thanhto.katalon.katalon_notes");
		Set<Class<? extends IRenderer>> classesImplementingIRenderer = reflections.getSubTypesOf(IRenderer.class);

		long tempCount = classesImplementingIRenderer.stream().count();
		Assert.assertNotEquals(0, tempCount);

		HtmlRendererFactory renderer = Mockito.spy(HtmlRendererFactory.getInstance());

		IRenderer iRenderer = Mockito.mock(CommonMarkRenderer.class);

		try {
			Mockito.doNothing().when(renderer).makeRenderersAwareOfUserSettings(iRenderer);
		} catch (PluginPreferenceIsNotAvailable e1) {
			e1.printStackTrace();
		}

		Mockito.when(renderer.getRenderer(CommonMarkRenderer.class)).thenReturn(iRenderer);

		classesImplementingIRenderer.stream().forEach(b -> {
			try {
				MatcherAssert.assertThat(renderer.getUserSettingAwareRenderer(b),
						CoreMatchers.instanceOf(IRenderer.class));
			} catch (RendererNotRegisteredException | PluginPreferenceIsNotAvailable e) {
				e.printStackTrace();
			}
		});
	}
}
