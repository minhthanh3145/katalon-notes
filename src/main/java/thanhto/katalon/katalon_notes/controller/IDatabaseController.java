package thanhto.katalon.katalon_notes.controller;

import java.util.List;

import thanhto.katalon.katalon_notes.model.INote;

public interface IDatabaseController {
	
	void openConnection();
	
	void closeConnection();

	List<INote> getNotesBasedOnCustomQuery(String query);

	INote createNote(INote note);

	INote updateNote(INote note);

	INote deleteNote(INote id);

	INote getNoteById(Long id);

	List<INote> getNoteByTitle(String title);
	
}
