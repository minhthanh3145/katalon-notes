package thanhto.katalon.katalon_notes.exception;

public class DatabaseControllerUnselectedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String serviceName = "";

	public DatabaseControllerUnselectedException(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getErrorMessage() {
		return "Service with " + this.serviceName + " has not been given a Database Controller instance yet !";
	}
}
