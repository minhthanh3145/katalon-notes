package thanhto.katalon.katalon_notes.toolitem;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.katalon.platform.api.extension.ToolItemWithMenuDescription;

import thanhto.katalon.katalon_notes.dialog.AddNewNoteDialog;
import thanhto.katalon.katalon_notes.provider.ServiceProvider;
import thanhto.katalon.katalon_notes.service.AbstractDatabaseService;

public class KatalonNotesToolItemWithMenuDescription implements ToolItemWithMenuDescription {
	private Menu optionMenu;
	private AbstractDatabaseService service;

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
		service = ServiceProvider.getInstance().getService("nitrite");
		addNewNoteMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println(service.toString());
				AddNewNoteDialog addNewNoteDialog = new AddNewNoteDialog(Display.getCurrent().getActiveShell(),
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
