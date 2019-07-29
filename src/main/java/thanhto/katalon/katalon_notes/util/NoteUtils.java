package thanhto.katalon.katalon_notes.util;

import thanhto.katalon.katalon_notes.model.KatalonNote;

public class NoteUtils {

	public static KatalonNote from(String title, String content) {
		return new KatalonNote(title, content);
	}

	/**
	 * Compare two notes by name and title recursively.
	 * 
	 * @param root1
	 * @param root2
	 * @return True if two notes represent the same root for the same tree
	 */
	public static boolean compare(KatalonNote root1, KatalonNote root2) {
		if (!root1.getTitle().equals(root2.getTitle()) || !root1.getContent().equals(root2.getContent())) {
			return false;
		}

		if (root1.getChildNotes().size() != root2.getChildNotes().size()) {
			return false;
		}

		int i = 0;
		while (i < Math.max(root1.getChildNotes().size(), root2.getChildNotes().size())) {
			KatalonNote note1 = root1.getChildNotes().get(i);
			KatalonNote note2 = root2.getChildNotes().get(i);
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
