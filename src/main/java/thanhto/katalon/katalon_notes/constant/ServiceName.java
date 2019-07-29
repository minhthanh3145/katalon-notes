package thanhto.katalon.katalon_notes.constant;

import thanhto.katalon.katalon_notes.controller.NitriteDatabaseController;

public enum ServiceName {

	Nitrite;

	private String controllerName;

	static {
		Nitrite.controllerName = NitriteDatabaseController.class.getName();
	}

	public String getControllerName() {
		return controllerName;
	}

	public static ServiceName serviceNameFrom(String controllerName) {
		if (controllerName == null) {
			return null;
		}
		if (controllerName.equals(Nitrite.controllerName)) {
			return Nitrite;
		}
		return null;
	}
}
