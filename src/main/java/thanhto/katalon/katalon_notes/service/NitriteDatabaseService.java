package thanhto.katalon.katalon_notes.service;

import org.dizitart.no2.Nitrite;

import thanhto.katalon.katalon_notes.controller.NitriteDatabaseController;

public class NitriteDatabaseService extends AbstractDatabaseService {

	public Nitrite getDatabase() {
		NitriteDatabaseController castedController = (NitriteDatabaseController) super.getController();
		if (castedController == null) {
			castedController = new NitriteDatabaseController();
			super.setController(castedController);
		}
		return castedController.getDb();
	}

	@Override
	public boolean isConnectionOpen() {
		return getDatabase() != null;
	}

	@Override
	public void openConnection() {
		controller = new NitriteDatabaseController();
	}

	@Override
	public void closeConnection() {
		if (!getDatabase().isClosed()) {
			getDatabase().close();
		}
	}

}
