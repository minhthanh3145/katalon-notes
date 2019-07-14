package thanhto.katalon.katalon_notes.exception;

/**
 * Throw if trying to get a renderer instance from a class that does not
 * implement {@link thanhto.katalon.katalon_notes.renderer.IRenderer}
 * 
 * @author thanhto
 *
 */
public class RendererNotRegisteredException extends Exception {
	private static final long serialVersionUID = 1L;
	Class<?> unregisteredClass;

	public RendererNotRegisteredException(Class<?> clazz) {
		this.unregisteredClass = clazz;
	}

	public String getMessageInfo() {
		return this.unregisteredClass == null ? "This exception is not initialized with a faulty renderer"
				: this.unregisteredClass.getName()
						+ " is not a valid renderer. A vaid renderer must extend interface IRenderer";
	}
}
