package thanhto.katalon.katalon_notes.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dizitart.no2.objects.Cursor;
import org.dizitart.no2.objects.ObjectRepository;
import org.dizitart.no2.objects.filters.ObjectFilters;

import thanhto.katalon.katalon_notes.constant.CustomQueryConstants;
import thanhto.katalon.katalon_notes.model.KatalonNote;

public class NitriteDatabaseController extends AbstractDatabaseController<KatalonNote> {

	private ObjectRepository<KatalonNote> collection;

	@SuppressWarnings("unchecked")
	public NitriteDatabaseController() {
		super();
		collection = (ObjectRepository<KatalonNote>) getDatabaseArtifact("objectRepository");
	}

	@Override
	public List<KatalonNote> getByName(String title) {
		List<KatalonNote> notes = new ArrayList<>();
		Iterator<KatalonNote> note = collection.find(ObjectFilters.eq("title", title)).iterator();
		while (note.hasNext()) {
			KatalonNote next = note.next();
			notes.add(next);
		}
		return notes;
	}

	@Override
	public KatalonNote create(KatalonNote note) {
		if (note == null) {
			return null;
		}

		KatalonNote parent = note.getParent();
		if (parent != null) {
			parent.addChildNote(note);
			update(parent);
		}

		collection.insert(note);

		recursiveCreateChildFor(note);

		return note;
	}

	private void recursiveCreateChildFor(KatalonNote note) {
		note.getChildNotes().forEach(child -> {
			collection.insert(child);
			recursiveCreateChildFor(child);
		});
	}

	@Override
	public KatalonNote update(KatalonNote note) {
		if (note == null) {
			return note;
		}

		KatalonNote upward = note;
		while (upward != null) {
			collection.update(upward);
			upward = upward.getParent();
		}

		recursiveUpdateChildFor(note);

		return note;
	}

	private void recursiveUpdateChildFor(KatalonNote note) {
		note.getChildNotes().forEach(child -> {
			collection.update(child);
			recursiveUpdateChildFor(child);
		});
	}

	@Override
	public KatalonNote delete(KatalonNote note) {
		if (note == null) {
			return null;
		}

		collection.remove(note);
		recursiveRemoveChildFor(note);

		KatalonNote parent = note.getParent();
		if (parent != null) {
			parent.getChildNotes().remove(note);
			update(parent);
		}

		return note;
	}

	private void recursiveRemoveChildFor(KatalonNote note) {
		note.getChildNotes().forEach(child -> {
			collection.remove(child);
			recursiveRemoveChildFor(child);
		});
	}

	@Override
	public KatalonNote getById(Long id) {
		KatalonNote note = collection.find(ObjectFilters.eq("id", id)).firstOrDefault();
		return note;
	}

	public List<KatalonNote> getByCustomQuery(String query) {
		List<KatalonNote> notes = new ArrayList<>();
		if (query.contains(CustomQueryConstants.NOTES_WITHOUT_PARENT)) {
			Cursor<KatalonNote> results = collection.find(ObjectFilters.eq("parent", null));
			Iterator<KatalonNote> it = results.iterator();
			while (it.hasNext()) {
				KatalonNote current = it.next();
				notes.add(current);
			}
		} else if (query.contains(CustomQueryConstants.ALL)) {
			Cursor<KatalonNote> results = collection.find();
			Iterator<KatalonNote> it = results.iterator();
			while (it.hasNext()) {
				KatalonNote current = it.next();
				notes.add(current);
			}
		}
		return notes;
	}
}
