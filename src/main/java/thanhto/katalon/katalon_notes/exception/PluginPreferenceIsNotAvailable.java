package thanhto.katalon.katalon_notes.exception;

public class PluginPreferenceIsNotAvailable extends Exception {

	private static final long serialVersionUID = 1L;

	public PluginPreferenceIsNotAvailable() {
		super("Plugin preference is not available, this most often means that a project is not opened !");
	}

}
