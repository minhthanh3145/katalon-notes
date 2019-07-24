package thanhto.katalon.katalon_notes.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.dizitart.no2.Cursor;
import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteCollection;
import org.dizitart.no2.NitriteId;
import org.dizitart.no2.WriteResult;
import org.dizitart.no2.filters.Filters;

import thanhto.katalon.katalon_notes.constant.CustomQueryConstants;
import thanhto.katalon.katalon_notes.model.INote;
import thanhto.katalon.katalon_notes.model.KatalonNote;
import thanhto.katalon.katalon_notes.util.NoteUtils;

public class NitriteDatabaseController extends AbstractDatabaseController<INote> {

	private NitriteCollection collection;

	public NitriteDatabaseController() {
		super();
		collection = getDatabaseArtifact(NitriteCollection.class);
	}

	@Override
	public List<INote> getByName(String title) {
		List<INote> notes = new ArrayList<>();
		Cursor results = collection.find(Filters.eq("title", title));
		Iterator<Document> it = results.iterator();
		while (it.hasNext()) {
			Document doc = it.next();
			if (NoteUtils.documentInvalid(doc)) {
				continue;
			}
			INote note = NoteUtils.katalonNoteFrom(doc);
			notes.add(note);
		}
		return notes;
	}

	@Override
	public INote create(INote note) {
		if (note == null || note.getId() != null) {
			return note;
		}
		Document doc = NoteUtils.from(note);

		if (NoteUtils.documentInvalid(doc)) {
			return null;
		}

		WriteResult result = collection.insert(doc);
		note.setId(result.iterator().next().getIdValue());
		for (int i = 0; i < note.getChildNotes().size(); i++) {
			INote childNote = note.getChildNotes().get(i);
			create(childNote);
		}
		update(note);
		return note;
	}

	@Override
	public INote update(INote note) {
		if (note == null || note.getId() == null) {
			return note;
		}

		Document doc = collection.getById(NitriteId.createId(note.getId()));

		if (NoteUtils.documentInvalid(doc)) {
			return null;
		}

		INote originalNote = NoteUtils.katalonNoteFrom(doc);

		if (note.getParent() != null && !note.getParent().equals(originalNote.getParent())) {
			System.out.println(note.getParent().toString() + " " + originalNote.getParent().toString());
		}

		Optional.ofNullable(originalNote.getParent()).ifPresent(parent -> {
			if (!parent.equals(note.getParent())) {
				parent.getChildNotes().removeIf(child -> child.equals(originalNote));
			}
		});

		Optional.ofNullable(note.getParent()).ifPresent(parent -> {
			INote originalNoteInParent = parent.getChildNotes().stream().filter(a -> a.getId().equals(note.getId()))
					.findFirst().orElse(null);
			if (originalNoteInParent != null) {
				NoteUtils.copy(note, originalNoteInParent);
			} else {
				parent.getChildNotes().add(note);
			}
		});
		doc = NoteUtils.copy(note, doc);
		collection.update(doc);
		return note;
	}

	@Override
	public INote delete(INote note) {
		if (note == null || note.getId() == null) {
			return null;
		}

		Document doc = collection.getById(NitriteId.createId(note.getId()));

		if (NoteUtils.documentInvalid(doc)) {
			return null;
		}

		INote originalNote = NoteUtils.katalonNoteFrom(doc);
		collection.remove(doc);

		Optional.ofNullable(originalNote.getParent()).ifPresent(parent -> {
			parent.getChildNotes().remove(originalNote);
		});

		for (int i = 0; i < originalNote.getChildNotes().size(); i++) {
			INote childNote = note.getChildNotes().get(i);
			delete(childNote);
		}
		return note;
	}

	@Override
	public INote getById(Long id) {
		Document doc = collection.getById(NitriteId.createId(id));
		if (doc == null) {
			System.out.println("Document for ID: " + id + " doesn't exist !");
			return null;
		}

		if (NoteUtils.documentInvalid(doc)) {
			return null;
		}

		KatalonNote note = NoteUtils.katalonNoteFrom(doc);
		return note;
	}

	/**
	 * Query notes based on some built-in conditions ( @see
	 * {@link CustomQueryConstants})
	 * 
	 * @param query
	 *            A query string
	 * @return A list of {@link INote} satisfying the query
	 * 
	 */
	public List<INote> getByCustomQuery(String query) {
		List<INote> notes = new ArrayList<>();
		if (query.contains(CustomQueryConstants.NOTES_WITHOUT_PARENT)) {
			Cursor results = collection.find(Filters.eq("parent", null));
			Iterator<Document> it = results.iterator();
			while (it.hasNext()) {
				Document current = it.next();
				if (NoteUtils.documentInvalid(current)) {
					continue;
				}
				KatalonNote note = NoteUtils.katalonNoteFrom(current);
				notes.add(note);
			}
		}
		return notes;
	}
}
