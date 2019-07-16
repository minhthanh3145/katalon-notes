package thanhto.katalon.katalon_notes.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dizitart.no2.Cursor;
import org.dizitart.no2.Document;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteCollection;
import org.dizitart.no2.NitriteId;
import org.dizitart.no2.WriteResult;
import org.dizitart.no2.filters.Filters;

import com.katalon.platform.api.service.ApplicationManager;

import thanhto.katalon.katalon_notes.constant.CustomQueryConstants;
import thanhto.katalon.katalon_notes.model.INote;
import thanhto.katalon.katalon_notes.model.KatalonNote;
import thanhto.katalon.katalon_notes.util.NoteUtils;

public class NitriteDatabaseController implements IDatabaseController<INote> {

	private static final String NOTES_COLLECTION = "notes";
	private Nitrite db;
	private NitriteCollection collection;
	private String databaseFilePath = "";

	@Override
	public List<INote> getByName(String title) {
		List<INote> notes = new ArrayList<>();
		Cursor results = collection.find(Filters.eq("title", title));
		Iterator<Document> it = results.iterator();
		while (it.hasNext()) {
			Document doc = it.next();
			INote note = NoteUtils.katalonNoteFrom(doc);
			notes.add(note);
		}
		return notes;
	}

	@Override
	public INote create(INote note) {
		if (note.getId() != null) {
			return null;
		}
		Document doc = NoteUtils.from(note);
		WriteResult result = collection.insert(doc);
		note.setId(result.iterator().next().getIdValue());
		for (INote childNote : note.getChildNotes()) {
			create(childNote);
		}
		return note;
	}

	@Override
	public INote update(INote note) {
		recursiveAscentUpdate(note);
		return note;
	}

	@Override
	public INote delete(INote note) {
		recursiveDescentDelete(note);
		recursiveAscentUpdate(note);
		return note;
	}

	private void recursiveDescentDelete(INote note) {
		Document doc = collection.getById(NitriteId.createId(note.getId()));
		collection.remove(doc);
		List<INote> childNotes = note.getChildNotes();
		if (childNotes != null && childNotes.size() != 0) {
			for (INote childNote : childNotes) {
				recursiveDescentDelete(childNote);
			}
		}
		// Empty-out child notes
		note.setChildNotes(new ArrayList<>());
	}

	public void recursiveAscentUpdate(INote note) {

		Document doc = collection.getById(NitriteId.createId(note.getId()));
		doc = NoteUtils.copy(note, doc);
		collection.update(doc);

		INote parent = note.getParent();
		while (parent != null) {
			recursiveAscentUpdate(parent);
			parent = parent.getParent();
		}
	}

	@Override
	public INote getById(Long id) {
		Document doc = collection.getById(NitriteId.createId(id));
		if (doc == null) {
			System.out.println("Document for ID: " + id + " doesn't exist !");
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
				KatalonNote note = NoteUtils.katalonNoteFrom(current);
				notes.add(note);
			}
		}
		return notes;
	}

	@Override
	public void openConnection() {
		openConnection("");
	}

	@Override
	public void openConnection(String... credentials) {
		db = getDatabase(credentials);
		collection = db.getCollection(NOTES_COLLECTION);
	}

	public Nitrite getDatabase(String... credentials) {
		Nitrite database = null;
		String currentProjectPath = getCurrentProjectPath();
		String pathToDatabase = (databaseFilePath.equals("") ? (currentProjectPath) : databaseFilePath)
				+ "/katalon_notes.db";
		if (credentials != null && credentials.length == 2) {
			String username = credentials[0];
			String password = credentials[1];
			database = Nitrite.builder().compressed().filePath(pathToDatabase).openOrCreate(username, password);
		} else {
			database = Nitrite.builder().compressed().filePath(pathToDatabase).openOrCreate();
		}
		return database;
	}

	public String getCurrentProjectPath() {
		return ApplicationManager.getInstance().getProjectManager().getCurrentProject().getFolderLocation();
	}

	@Override
	public void closeConnection() {
		db.close();
	}

	@Override
	public void setLocalDatabaseLocation(String location) {
		this.databaseFilePath = location;
	}

	@Override
	public String getLocalDatabaseLocation() {
		return this.databaseFilePath;
	}
}
