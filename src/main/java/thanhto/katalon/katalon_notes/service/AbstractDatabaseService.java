package thanhto.katalon.katalon_notes.service;

import java.util.List;

import thanhto.katalon.katalon_notes.controller.IDatabaseController;
import thanhto.katalon.katalon_notes.model.INote;

public abstract class AbstractDatabaseService {

	IDatabaseController controller;

	public void setController(IDatabaseController controller) {
		this.controller = controller;
	}

	public IDatabaseController getController() {
		return this.controller;
	}

	public abstract boolean isConnectionOpen();

	public abstract void openConnection();

	public abstract void closeConnection();

	public void ensureConnectionEstablished() {
		if (!isConnectionOpen()) {
			openConnection();
		}
	}

	public INote getNoteById(Long id) {
		ensureConnectionEstablished();
		return controller.getNoteById(id);
	}

	public List<INote> getNoteByTitle(String title) {
		ensureConnectionEstablished();
		return controller.getNoteByTitle(title);
	}

	public INote createNote(INote note) {
		ensureConnectionEstablished();
		return controller.createNote(note);
	}

	public INote updateNote(INote note) {
		ensureConnectionEstablished();
		return controller.updateNote(note);
	}

	public INote deleteNote(INote note) {
		ensureConnectionEstablished();
		return controller.deleteNote(note);
	}

	public List<INote> getNotesBasedOnCustomQuery(String query) {
		ensureConnectionEstablished();
		return controller.getNotesBasedOnCustomQuery(query);
	}
}
