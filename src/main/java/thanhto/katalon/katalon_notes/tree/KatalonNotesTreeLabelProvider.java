package thanhto.katalon.katalon_notes.tree;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

import thanhto.katalon.katalon_notes.constant.ImageConstants;
import thanhto.katalon.katalon_notes.model.INote;
import thanhto.katalon.katalon_notes.util.ImageManager;

public class KatalonNotesTreeLabelProvider extends CellLabelProvider {

	@Override
	public void update(ViewerCell cell) {
		 String name = "";
	        if (cell.getElement() != null && cell.getElement() instanceof INote) {
	            name = ((INote) cell.getElement()).getTitle();
	        }
	        cell.setText(name);
	        cell.setImage(getImage(cell.getElement()));
	}

	private Image getImage(Object element) {
		if(element instanceof INote) {
			return ImageManager.getImage(ImageConstants.NOTE_ICON);
		}
		return null;
	}

}
