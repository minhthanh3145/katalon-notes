package thanhto.katalon.katalon_notes.service;

import java.util.List;

import thanhto.katalon.katalon_notes.controller.IDatabaseController;
import thanhto.katalon.katalon_notes.model.INote;

public class DatabaseService {

	IDatabaseController controller;

	public void setController(IDatabaseController controller) {
		this.controller = controller;
	}

	public IDatabaseController getController() {
		return this.controller;
	}

	public INote getNoteById(Long id) {
		return controller.getNoteById(id);
	}

	public List<INote> getNoteByTitle(String title) {
		return controller.getNoteByTitle(title);
	}

	public INote createNote(INote note) {
		return controller.createNote(note);
	}

	public INote updateNote(INote note) {
		return controller.updateNote(note);
	}

	public INote deleteNote(INote note) {
		return controller.deleteNote(note);
	}

	public List<INote> getNotesBasedOnCustomQuery(String query) {
		return controller.getNotesBasedOnCustomQuery(query);
	}
}
