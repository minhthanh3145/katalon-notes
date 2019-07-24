package thanhto.katalon.katalon_notes.provider;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteCollection;

import com.katalon.platform.api.service.ApplicationManager;

public class NitriteDatabaseActionProvider implements IDatabaseActionProvider {

	private static final String NOTES_COLLECTION = "notes";
	private Nitrite db;
	private NitriteCollection collection;
	private String databaseFilePath = "";

	@Override
	public void openConnection() {
		openConnection("");
	}

	@Override
	public void openConnection(String... credentials) {
		db = getDatabase(credentials);
		collection = db.getCollection(NOTES_COLLECTION);
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
			db.close();
		}
	}

	@Override
	public void setLocalDatabaseLocation(String location) {
		this.databaseFilePath = location;
	}

	@Override
	public String getLocalDatabaseLocation() {
		return this.databaseFilePath;
	}

	@Override
	public void switchDatabase(String databaseLocation) {
		setLocalDatabaseLocation(databaseLocation);
		openConnection();
	}

	@Override
	public void commit() {
		if (db != null && !db.isClosed()) {
			db.commit();
		}
	}

	@SuppressWarnings("unchecked")
	public <E extends V, V> E get(Class<V> clazz) {
		if (clazz.getName().equals(NitriteCollection.class.getName())) {
			return (E) collection;
		}
		return null;
	}
}
