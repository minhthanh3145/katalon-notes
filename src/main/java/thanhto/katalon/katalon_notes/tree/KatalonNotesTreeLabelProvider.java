package thanhto.katalon.katalon_notes.tree;

import java.io.IOException;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

import thanhto.katalon.katalon_notes.constant.ImageConstants;
import thanhto.katalon.katalon_notes.model.KatalonNote;
import thanhto.katalon.katalon_notes.util.ImageUtil;

public class KatalonNotesTreeLabelProvider extends CellLabelProvider {

	@Override
	public void update(ViewerCell cell) {
		String name = "";
		if (cell.getElement() != null && cell.getElement() instanceof KatalonNote) {
			name = ((KatalonNote) cell.getElement()).getTitle();
		}
		cell.setText(name);
		try {
			cell.setImage(getImage(ImageConstants.NOTE_SMALL_ICON));
		} catch (IOException e) {

		}
	}

	public static Image getImage(String location) throws IOException {
		ImageData source = new ImageData(ImageUtil.class.getResourceAsStream(location));
		return new Image(null, source);
	}

}
