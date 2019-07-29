package thanhto.katalon.katalon_notes.controller;

import thanhto.katalon.katalon_notes.factory.DatabaseArtifactFactory;

public abstract class AbstractDatabaseController<T> implements IDatabaseController<T> {

	protected Object getDatabaseArtifact(String key) {
		return DatabaseArtifactFactory.getInstance().getArtifact(this.getClass().getName(), key);
	}
}
