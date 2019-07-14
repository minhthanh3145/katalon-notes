package thanhto.katalon.katalon_notes.service;

import java.util.List;

import thanhto.katalon.katalon_notes.controller.IDatabaseController;

/**
 * Controller-agnostic service that delegate method calls to whatever
 * controllers were given. This service should be obtained through
 * ServiceProvider to ensure that appropriate controllers are given
 * 
 * @author thanhto
 *
 * @param <T>
 */
public class DatabaseService<T> {

	IDatabaseController<T> controller;

	public void setController(IDatabaseController<T> controller) {
		this.controller = controller;
	}

	public IDatabaseController<T> getController() {
		return this.controller;
	}

	public T getById(Long id) {
		return controller.getById(id);
	}

	public List<T> getByName(String title) {
		return controller.getByName(title);
	}

	public T create(T note) {
		return controller.create(note);
	}

	public T update(T note) {
		return controller.update(note);
	}

	public T delete(T note) {
		return controller.delete(note);
	}

	public List<T> getByCustomQuery(String query) {
		return controller.getByCustomQuery(query);
	}
}
