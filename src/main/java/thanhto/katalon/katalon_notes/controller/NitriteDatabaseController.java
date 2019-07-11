package thanhto.katalon.katalon_notes.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dizitart.no2.Cursor;
import org.dizitart.no2.Document;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteCollection;
import org.dizitart.no2.NitriteId;
import org.dizitart.no2.WriteResult;
import org.dizitart.no2.filters.Filters;

import thanhto.katalon.katalon_notes.constant.CustomQueryConstants;
import thanhto.katalon.katalon_notes.model.INote;
import thanhto.katalon.katalon_notes.model.KatalonNote;

public class NitriteDatabaseController implements IDatabaseController {

	private static final String NOTES_COLLECTION = "notes";
	private Nitrite db;

	public void initializeDb(String absolutePath) {
		String pathToDatabase = absolutePath + "/katalon_notes.db";
		if (db == null || db.isClosed()) {
			System.out.println("Database is at: " + pathToDatabase);
			db = Nitrite.builder().compressed().filePath(pathToDatabase).openOrCreate("katalon-notes", "katalon-notes");
		}
	}

	public Nitrite getDb() {
		return db;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<INote> getNoteByTitle(String title) {
		List<INote> notes = new ArrayList<>();
		NitriteCollection collection = db.getCollection(NOTES_COLLECTION);
		Cursor results = collection.find(Filters.eq("title", title));
		Iterator<Document> it = results.iterator();
		while (it.hasNext()) {
			Document doc = it.next();
			KatalonNote note = new KatalonNote(doc.get("title").toString(), doc.get("content").toString());
			note.setId(doc.getId().getIdValue());
			note.setParent((INote) doc.get("parent"));
			note.setChildNotes((List<INote>) doc.get("childNotes"));
			notes.add(note);
		}

		return notes;
	}

	@Override
	public INote createNote(INote note) {
		NitriteCollection collection = db.getCollection(NOTES_COLLECTION);
		Map<String, Object> noteMap = new HashMap<>();
		noteMap.put("title", note.getTitle());
		noteMap.put("content", note.getContent());
		noteMap.put("parent", note.getParent());
		noteMap.put("childNotes", note.getChildNotes());
		Document doc = new Document(noteMap);
		WriteResult result = collection.insert(doc);
		note.setId(result.iterator().next().getIdValue());
		return note;
	}

	@Override
	public INote updateNote(INote note) {
		Document doc = db.getCollection(NOTES_COLLECTION).getById(NitriteId.createId(note.getId()));
		doc.put("title", note.getTitle());
		doc.put("content", note.getContent());
		doc.put("parent", note.getParent());
		doc.put("childNotes", note.getChildNotes());
		db.getCollection(NOTES_COLLECTION).update(doc);
		return note;
	}

	@Override
	public INote deleteNote(INote note) {
		Document doc = db.getCollection(NOTES_COLLECTION).getById(NitriteId.createId(note.getId()));
		db.getCollection(NOTES_COLLECTION).remove(doc);
		return note;
	}

	@SuppressWarnings("unchecked")
	@Override
	public INote getNoteById(Long id) {
		Document doc = db.getCollection(NOTES_COLLECTION).getById(NitriteId.createId(id));
		if (doc == null) {
			System.out.println("Document for ID: " + id + " doesn't exist !");
			return null;
		}
		System.out.println("Document for ID: " + id + " is note with title: " + doc.get("title").toString());
		KatalonNote note = new KatalonNote(doc.get("title").toString(), doc.get("content").toString());
		note.setId(doc.getId().getIdValue());
		note.setParent((INote) doc.get("parent"));
		note.setChildNotes((List<INote>) doc.get("childNotes"));
		return note;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<INote> getNotesBasedOnCustomQuery(String query) {
		List<INote> notes = new ArrayList<>();
		if (query.contains(CustomQueryConstants.NOTES_WITHOUT_PARENT)) {
			Cursor results = db.getCollection(NOTES_COLLECTION).find(Filters.eq("parent", null));
			Iterator<Document> it = results.iterator();
			while (it.hasNext()) {
				Document current = it.next();
				KatalonNote note = new KatalonNote(current.get("title").toString(), current.get("content").toString());
				note.setId(current.getId().getIdValue());
				note.setParent((INote) current.get("parent"));
				note.setChildNotes((List<INote>) current.get("childNotes"));
				notes.add(note);
			}
		}
		return notes;
	}
}