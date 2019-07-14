package thanhto.katalon.katalon_notes.exception;

public class DatabaseControllerUnselectedException extends Exception {

	private static final long serialVersionUID = 1L;

	public DatabaseControllerUnselectedException(String serviceName) {
		super("Service with " + serviceName + " has not been given a Database Controller instance yet !");
	}
}
