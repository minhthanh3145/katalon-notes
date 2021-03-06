package thanhto.katalon.katalon_notes.ui.toolitem;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.katalon.platform.api.extension.ToolItemWithMenuDescription;

import thanhto.katalon.katalon_notes.ui.dialog.KatalonNotesDialog;

public class KatalonNotesToolItemWithMenuDescription implements ToolItemWithMenuDescription {
	private Menu optionMenu;

	@Override
	public Menu getMenu(Control arg0) {
		optionMenu = new Menu(arg0);
		evaluateAndAddMenuItem();
		return null;
	}

	@Override
	public void defaultEventHandler() {
		if (optionMenu != null) {
			evaluateAndAddMenuItem();
			// Display menu at the mouse position (guaranteed to be within the
			// ToolItem icon)
			optionMenu.setVisible(true);
		}
	}

	private void evaluateAndAddMenuItem() {
		// Dispose all items
		for (MenuItem item : optionMenu.getItems()) {
			if (!item.isDisposed()) {
				item.dispose();
			}
		}

		MenuItem openKatalonNotesMenuItem = new MenuItem(optionMenu, SWT.PUSH);
		openKatalonNotesMenuItem.setText("Open Katalon Notes");
		openKatalonNotesMenuItem.setToolTipText("Manage your notes through a single interface");

		openKatalonNotesMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				KatalonNotesDialog addNewNoteDialog = new KatalonNotesDialog(Display.getCurrent().getActiveShell());
				if (addNewNoteDialog.open() == Window.OK) {

				}
			}
		});
	}

	@Override
	public String iconUrl() {
		return "platform:/plugin/thanhto.katalon.katalon-notes/icons/katalon_notes_32x24.png";
	}

	@Override
	public String name() {
		return "Katalon Notes";
	}

	@Override
	public String toolItemId() {
		return "thanhto.katalon.katalon_notes.ui.katalonNotesToolItemDescription";
	}

}
