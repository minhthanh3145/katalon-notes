package thanhto.katalon.katalon_notes.service;

import java.util.List;

import thanhto.katalon.katalon_notes.controller.IDatabaseController;
import thanhto.katalon.katalon_notes.model.INote;

public class DatabaseService<T> {

	IDatabaseController<T> controller;

	public void setController(IDatabaseController<T> controller) {
		this.controller = controller;
	}

	public IDatabaseController<T> getController() {
		return this.controller;
	}

	public T getNoteById(Long id) {
		return controller.getNoteById(id);
	}

	public List<T> getNoteByTitle(String title) {
		return controller.getNoteByTitle(title);
	}

	public T createNote(INote note) {
		return controller.createNote(note);
	}

	public T updateNote(INote note) {
		return controller.updateNote(note);
	}

	public T deleteNote(INote note) {
		return controller.deleteNote(note);
	}

	public List<T> getNotesBasedOnCustomQuery(String query) {
		return controller.getNotesBasedOnCustomQuery(query);
	}
}
