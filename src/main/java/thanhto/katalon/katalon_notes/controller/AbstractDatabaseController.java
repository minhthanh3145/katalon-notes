package thanhto.katalon.katalon_notes.controller;

import thanhto.katalon.katalon_notes.factory.DatabaseArtifactFactory;

public abstract class AbstractDatabaseController<T> implements IDatabaseController<T> {

	@SuppressWarnings("unchecked")
	protected <V> V getDatabaseArtifact(Class<V> clazz) {
		System.out.println(this.getClass().getName());
		System.out.println(clazz.getName());
		return (V) DatabaseArtifactFactory.getInstance().getArtifact(this.getClass().getName(), clazz.getName());
	}
}
