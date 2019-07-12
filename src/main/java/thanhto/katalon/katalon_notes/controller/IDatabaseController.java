package thanhto.katalon.katalon_notes.controller;

import java.util.List;

import thanhto.katalon.katalon_notes.model.INote;

public interface IDatabaseController<T> {

	void openConnection();

	void closeConnection();

	List<T> getNotesBasedOnCustomQuery(String query);

	T createNote(INote note);

	T updateNote(INote note);

	T deleteNote(INote id);

	T getNoteById(Long id);

	List<T> getNoteByTitle(String title);

}
