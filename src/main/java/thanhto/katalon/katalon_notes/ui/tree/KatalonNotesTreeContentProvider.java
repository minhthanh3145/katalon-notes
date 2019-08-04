package thanhto.katalon.katalon_notes.ui.tree;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;

import thanhto.katalon.katalon_notes.model.KatalonNote;

public class KatalonNotesTreeContentProvider implements ITreeContentProvider {

	@Override
	public Object[] getChildren(Object arg0) {
		if (arg0 instanceof KatalonNote) {
			return ((KatalonNote) arg0).getChildNotes().toArray();
		}
		return new Object[0];
	}

	@Override
	public Object[] getElements(Object arg0) {
		if (arg0 == null) {
			return new Object[0];
		}
		if (arg0.getClass().isArray()) {
			return (Object[]) arg0;
		}
		if (arg0 instanceof List<?>) {
			return ((List<?>) arg0).toArray();
		}
		return new Object[0];
	}

	@Override
	public Object getParent(Object arg0) {
		if (arg0 instanceof KatalonNote) {
			return ((KatalonNote) arg0).getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object arg0) {
		return getChildren(arg0).length != 0;
	}

}
