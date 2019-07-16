package thanhto.katalon.katalon_notes.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dizitart.no2.Document;

import thanhto.katalon.katalon_notes.model.INote;
import thanhto.katalon.katalon_notes.model.KatalonNote;

public class NoteUtils {

	public static Document from(INote note) {
		Map<String, Object> noteMap = new HashMap<>();
		noteMap.put("title", note.getTitle());
		noteMap.put("content", note.getContent());
		noteMap.put("parent", note.getParent());
		noteMap.put("childNotes", note.getChildNotes());
		Document doc = new Document(noteMap);
		return doc;
	}

	public static Document copy(INote from, Document to) {
		to.put("title", from.getTitle());
		to.put("content", from.getContent());
		to.put("parent", from.getParent());
		to.put("childNotes", from.getChildNotes());
		return to;
	}

	public static void copy(INote from, INote to) {
		to.setId(from.getId());
		to.setTitle(from.getTitle());
		to.setContent(from.getContent());
		to.setParent(from.getParent());
		to.setChildNotes(from.getChildNotes());
	}

	@SuppressWarnings("unchecked")
	public static KatalonNote katalonNoteFrom(Document doc) {
		KatalonNote note = new KatalonNote();
		note.setTitle(doc.get("title").toString());
		note.setContent(doc.get("content").toString());
		note.setId(doc.getId().getIdValue());
		note.setParent((INote) doc.get("parent"));
		note.setChildNotes((List<INote>) doc.get("childNotes"));
		return note;
	}

	public static INote from(String title, String content) {
		return new KatalonNote(title, content);
	}

	/**
	 * Compare two notes by name and title recursively.
	 * 
	 * @param root1
	 * @param root2
	 * @return True if two notes represent the same root for the same tree
	 */
	public static boolean compare(INote root1, INote root2) {
		if (!root1.getTitle().equals(root2.getTitle()) || !root1.getContent().equals(root2.getContent())) {
			return false;
		}

		if (root1.getChildNotes().size() != root2.getChildNotes().size()) {
			return false;
		}

		int i = 0;
		while (i < Math.max(root1.getChildNotes().size(), root2.getChildNotes().size())) {
			INote note1 = root1.getChildNotes().get(i);
			INote note2 = root2.getChildNotes().get(i);
			if (compare(note1, note2)) {
				i++;
				continue;
			} else {
				return false;
			}
		}
		return true;
	}
}
