package thanhto.katalon.katalon_notes.provider;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.objects.ObjectRepository;

import com.katalon.platform.api.service.ApplicationManager;

import thanhto.katalon.katalon_notes.api.IDatabaseActionProvider;
import thanhto.katalon.katalon_notes.model.KatalonNote;

public class NitriteDatabaseActionProvider implements IDatabaseActionProvider {

	private Nitrite db;
	private ObjectRepository<KatalonNote> collection;
	private String databaseFilePath = "";

	@Override
	public void openConnection(String... credentials) {
		db = getDatabase(credentials);
		collection = db.getRepository(KatalonNote.class);
	}

	private Nitrite getDatabase(String... credentials) {
		closeConnection();
		Nitrite database = null;
		String pathToDatabase = "";

		if (databaseFilePath.equals("")) {
			pathToDatabase = getCurrentProjectPath() + "/katalon_notes.db";
		} else {
			pathToDatabase = databaseFilePath + "/katalon_notes.db";
		}

		if (credentials != null && credentials.length == 2) {
			String username = credentials[0];
			String password = credentials[1];
			database = Nitrite.builder().compressed().filePath(pathToDatabase).openOrCreate(username, password);
		} else {
			database = Nitrite.builder().compressed().filePath(pathToDatabase).openOrCreate("katalon-notes",
					"katalon_notes");
		}
		return database;
	}

	public String getCurrentProjectPath() {
		return ApplicationManager.getInstance().getProjectManager().getCurrentProject().getFolderLocation();
	}

	@Override
	public void closeConnection() {
		if (db != null) {
			db.commit();
			db.close();
		}
	}

	@Override
	public void setDatabaseLocation(String location) {
		this.databaseFilePath = location;
	}

	@Override
	public String getDatabaseLocation() {
		return this.databaseFilePath;
	}

	@Override
	public void switchDatabase(String databaseLocation, String... credentials) {
		setDatabaseLocation(databaseLocation);
		openConnection(credentials);
	}

	@Override
	public Object get(String key) {
		if (key != null && key.equals("objectRepository")) {
			return collection;
		}
		return null;
	}
}
