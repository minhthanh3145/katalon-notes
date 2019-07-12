package thanhto.katalon.katalon_notes.toolitem;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.katalon.platform.api.extension.ToolItemWithMenuDescription;

import thanhto.katalon.katalon_notes.dialog.KatalonNotesDialog;
import thanhto.katalon.katalon_notes.exception.DatabaseControllerUnselectedException;
import thanhto.katalon.katalon_notes.model.INote;
import thanhto.katalon.katalon_notes.provider.ServiceProvider;
import thanhto.katalon.katalon_notes.service.DatabaseService;

public class KatalonNotesToolItemWithMenuDescription implements ToolItemWithMenuDescription {
	private Menu optionMenu;
	private DatabaseService<INote> service;

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

		MenuItem addNewNoteMenuItem = new MenuItem(optionMenu, SWT.PUSH);
		addNewNoteMenuItem.setText("Open Katalon Notes");
		addNewNoteMenuItem.setToolTipText("Manage your notes through a single interface");

		addNewNoteMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					service = ServiceProvider.getInstance().getAndOpenService("nitrite");
				} catch (DatabaseControllerUnselectedException exception) {
					System.out.println(ExceptionUtils.getStackTrace(exception));
				}
				KatalonNotesDialog addNewNoteDialog = new KatalonNotesDialog(Display.getCurrent().getActiveShell(),
						service);
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
		return "thanhto.katalon.katalon_notes.katalonNotesToolItemDescription";
	}

}
