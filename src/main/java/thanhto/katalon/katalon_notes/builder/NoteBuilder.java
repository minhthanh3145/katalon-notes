package thanhto.katalon.katalon_notes.builder;

import thanhto.katalon.katalon_notes.model.INote;
import thanhto.katalon.katalon_notes.model.KatalonNote;

public class NoteBuilder {
	private INote note;

	public NoteBuilder(String title, String content) {
		this.note = new KatalonNote(title, content);
	}

	public NoteBuilder addChildNote(INote childNote) {
		childNote.setParent(note);
		note.getChildNotes().add(childNote);
		return this;
	}

	public INote build() {
		return this.note;
	}
}
