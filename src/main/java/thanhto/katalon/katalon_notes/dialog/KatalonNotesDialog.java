package thanhto.katalon.katalon_notes.dialog;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.katalon.platform.api.service.ApplicationManager;
import com.katalon.platform.api.ui.UISynchronizeService;

import thanhto.katalon.katalon_notes.constant.CustomQueryConstants;
import thanhto.katalon.katalon_notes.exception.PluginPreferenceIsNotAvailable;
import thanhto.katalon.katalon_notes.exception.RendererNotRegisteredException;
import thanhto.katalon.katalon_notes.model.INote;
import thanhto.katalon.katalon_notes.model.KatalonNote;
import thanhto.katalon.katalon_notes.renderer.CommonMarkRenderer;
import thanhto.katalon.katalon_notes.renderer.HtmlRendererFactory;
import thanhto.katalon.katalon_notes.renderer.IRenderer;
import thanhto.katalon.katalon_notes.service.DatabaseService;
import thanhto.katalon.katalon_notes.tree.KatalonNotesTreeContentProvider;
import thanhto.katalon.katalon_notes.tree.KatalonNotesTreeLabelProvider;

public class KatalonNotesDialog extends Dialog {

	private TreeViewer tvKatalonNotes;
	private Composite mainComposite;
	private Composite treeViewerAndDetailComposite;
	private Composite noteDetailComposite;
	private Composite treeViewerComposite;
	private Text txtContent;
	private Text txtTitle;
	private Browser browserPreview;
	private Composite descriptionPreviewComposite;
	private INote selectedNote;
	private Label lblInformation;
	private MenuItem add;
	private MenuItem delete;
	private MenuItem refreshHtmlRenderer;

	private IRenderer htmlRenderer;
	private UISynchronizeService uiSynchronizeService;
	private DatabaseService<INote> databaseService;

	SelectionAdapter addSelectionAdapter = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			INote parentNote = selectedNote;
			INote savedChildNote = new KatalonNote("Default Title", "Default Content");
			INote savedParentNote = null;
			if (parentNote != null) {
				if (parentNote.getId() == null) {
					savedParentNote = databaseService.create(parentNote);
				} else {
					savedParentNote = databaseService.update(parentNote);
				}
			}
			savedChildNote.setParent(savedParentNote);
			INote updatedChildNote = databaseService.create(savedChildNote);
			setSelectedNote(updatedChildNote);
			refresh();
		}
	};

	SelectionAdapter deleteSelectionAdapter = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			databaseService.delete(selectedNote);
			setSelectedNote(null);
			refresh();
		}
	};

	SelectionAdapter refreshHtmlRendererSelectionAdapter = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			reloadHTMLRenderer();
		}
	};

	private List<SelectionAdapter> selectionAdapters = Arrays.asList(addSelectionAdapter, deleteSelectionAdapter,
			refreshHtmlRendererSelectionAdapter);

	public KatalonNotesDialog(Shell parentShell, DatabaseService<INote> service) {
		super(parentShell);
		this.databaseService = service;
		System.out.println("Database at: "
				+ ApplicationManager.getInstance().getProjectManager().getCurrentProject().getFolderLocation());
		this.uiSynchronizeService = ApplicationManager.getInstance().getUIServiceManager()
				.getService(UISynchronizeService.class);
	}

	@Override
	public void create() {
		setShellStyle(SWT.SHELL_TRIM);
		super.create();
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Katalon Notes");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		mainComposite = new Composite(parent, SWT.NONE);
		GridData gdMainComposite = new GridData(SWT.FILL, SWT.FILL, true, true);
		gdMainComposite.widthHint = 1200;
		gdMainComposite.heightHint = 800;
		mainComposite.setLayoutData(gdMainComposite);
		GridLayout gLMainComposite = new GridLayout(1, false);
		gLMainComposite.marginWidth = 5;
		gLMainComposite.marginHeight = 5;
		gLMainComposite.verticalSpacing = 10;
		gLMainComposite.horizontalSpacing = 5;
		mainComposite.setLayout(gLMainComposite);

		treeViewerAndDetailComposite = new Composite(mainComposite, SWT.NONE);
		GridData gdTreeViewerAndDetail = new GridData(SWT.FILL, SWT.FILL, true, true);
		treeViewerAndDetailComposite.setLayoutData(gdTreeViewerAndDetail);
		GridLayout glTreeViwerAndDetail = new GridLayout(2, false);
		treeViewerAndDetailComposite.setLayout(glTreeViwerAndDetail);

		createTreeViewerComposite();
		createNoteDetailsComposite();
		registerNotesListener();
		reloadHTMLRenderer();
		refresh();
		return mainComposite;
	}

	public void createTreeViewerComposite() {
		treeViewerComposite = new Composite(treeViewerAndDetailComposite, SWT.NONE);
		GridData gdTreeViewer = new GridData(SWT.FILL, SWT.FILL, false, true);
		gdTreeViewer.widthHint = 300;
		treeViewerComposite.setLayoutData(gdTreeViewer);
		treeViewerComposite.setLayout(new GridLayout(1, false));

		tvKatalonNotes = new TreeViewer(treeViewerComposite);
		tvKatalonNotes.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		tvKatalonNotes.setContentProvider(new KatalonNotesTreeContentProvider());
		tvKatalonNotes.setLabelProvider(new KatalonNotesTreeLabelProvider());

		addContextMenuToTree();
	}

	private void addContextMenuToTree() {
		MenuManager menuMgr = new MenuManager();
		Menu menu = menuMgr.createContextMenu(tvKatalonNotes.getControl());
		addMenuItemsForContextMenu(menu);
		menuMgr.setRemoveAllWhenShown(true);
		registerContextMenuListener(menuMgr, menu);
		tvKatalonNotes.getControl().setMenu(menu);
	}

	private void registerContextMenuListener(MenuManager menuMgr, Menu menu) {
		menu.addMenuListener(new MenuAdapter() {
			public void menuShown(MenuEvent e) {
				MenuItem[] items = menu.getItems();
				for (int i = 0; i < items.length; i++) {
					items[i].dispose();
				}
				addMenuItemsForContextMenu(menu);
			}
		});

		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				if (tvKatalonNotes.getSelection().isEmpty()) {
					setSelectedNote(null);
					return;
				}

				if (tvKatalonNotes.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection) tvKatalonNotes.getSelection();
					INote note = (INote) selection.getFirstElement();
					setSelectedNote(note);
				}
			}
		});
	}

	private void addMenuItemsForContextMenu(Menu menu) {
		for (MenuItem item : menu.getItems()) {
			for (SelectionAdapter selectionAdapter : selectionAdapters) {
				item.removeSelectionListener(selectionAdapter);
			}
			item.dispose();
		}

		add = new MenuItem(menu, SWT.NONE);
		add.setText("Add a child note");
		add.addSelectionListener(addSelectionAdapter);

		delete = new MenuItem(menu, SWT.NONE);
		delete.setText("Delete this note");
		delete.addSelectionListener(deleteSelectionAdapter);

		refreshHtmlRenderer = new MenuItem(menu, SWT.NONE);
		refreshHtmlRenderer.setText("Refresh HTML Renderer");
		refreshHtmlRenderer.setToolTipText("Refresh HTML Renderer to reflect changes in user-setting");
		refreshHtmlRenderer.addSelectionListener(refreshHtmlRendererSelectionAdapter);

	}

	public void createNoteDetailsComposite() {

		noteDetailComposite = new Composite(treeViewerAndDetailComposite, SWT.NONE);
		GridData gdNoteDetail = new GridData(SWT.FILL, SWT.FILL, true, true);
		gdNoteDetail.widthHint = 200;
		noteDetailComposite.setLayoutData(gdNoteDetail);
		noteDetailComposite.setLayout(new GridLayout(1, false));

		txtTitle = new Text(noteDetailComposite, SWT.BORDER);
		txtTitle.setMessage("Note's title");
		txtTitle.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		descriptionPreviewComposite = new Composite(noteDetailComposite, SWT.NONE);
		GridData gdDescPrev = new GridData(SWT.FILL, SWT.FILL, true, true);
		descriptionPreviewComposite.setLayoutData(gdDescPrev);
		GridLayout glDescPrev = new GridLayout(2, true);

		descriptionPreviewComposite.setLayout(glDescPrev);
		Label lblDescription = new Label(descriptionPreviewComposite, SWT.NONE);
		lblDescription.setText("Description");
		lblDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		Label lblPreview = new Label(descriptionPreviewComposite, SWT.NONE);
		lblPreview.setText("Preview");
		lblPreview.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		txtContent = new Text(descriptionPreviewComposite, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		txtContent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		txtContent.setMessage("Note's content");

		browserPreview = new Browser(descriptionPreviewComposite, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		browserPreview.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		parent.setLayout(new GridLayout(5, false));

		Button btnSave = createButton(parent, IDialogConstants.NO_ID, "Update", true);
		btnSave.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		btnSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				INote unsavedNote = selectedNote;
				INote savedNote = null;
				if (unsavedNote.getId() == null) {
					savedNote = databaseService.create(unsavedNote);
				} else {
					savedNote = databaseService.update(unsavedNote);
				}
				setSelectedNote(savedNote);

				if (selectedNote != null) {
					String title = selectedNote.getTitle();
					uiSynchronizeService.syncExec(() -> {
						if (!lblInformation.isDisposed()) {
							lblInformation.setText(title + " is updated !");
						}
					});
				}
				refresh();
			}
		});

		Text txtDatabase = new Text(parent, SWT.BORDER);
		txtDatabase.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtDatabase.setMessage("Choose another database location");

		Button btnBrowse = new Button(parent, SWT.NONE);
		btnBrowse.setText("...");
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog directoryDialog = new DirectoryDialog(getParentShell());
				String directory = directoryDialog.open();
				uiSynchronizeService.syncExec(() -> {
					if (!txtDatabase.isDisposed()) {
						txtDatabase.setText(directory);
					}
				});
			}
		});

		Button btnChangeDatabase = createButton(parent, IDialogConstants.NO_ID, "Change database", false);
		btnSave.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		btnChangeDatabase.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (txtDatabase.equals("")) {
					String currentProject = ApplicationManager.getInstance().getProjectManager().getCurrentProject()
							.getFolderLocation();
					databaseService.getController().setLocalDatabaseLocation(currentProject);
				} else {
					File databaseFolder = new File(txtDatabase.getText());
					if (databaseFolder.isDirectory()) {
						String newDirectory = databaseFolder.getAbsolutePath();
						try {
							databaseService.getController().switchDatabase(newDirectory);
							lblInformation.setText("Switched to new database !");
							refresh();
						} catch (Exception exception) {
							ErrorDialog.openError(getParentShell(), "Error when switching database",
									ExceptionUtils.getStackTrace(exception),
									new Status(Status.ERROR, "KatalonNotesDialog", exception.getMessage()),
									IStatus.ERROR);

							uiSynchronizeService.syncExec(() -> {
								if (!lblInformation.isDisposed()) {
									lblInformation.setText("Error when switching database !");
								}
							});
						}
					} else {
						uiSynchronizeService.syncExec(() -> {
							if (!lblInformation.isDisposed()) {
								lblInformation.setText("Invalid folder !");
							}
						});
					}
				}
			}
		});

		lblInformation = new Label(parent, SWT.NONE);
		lblInformation.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
		lblInformation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		lblInformation.setText("Manage your notes !");
	}

	private void setSelectedNote(INote note) {
		this.selectedNote = note;
	}

	private void reloadHTMLRenderer() {
		try {
			htmlRenderer = HtmlRendererFactory.getInstance().getUserSettingAwareRenderer(CommonMarkRenderer.class);
		} catch (RendererNotRegisteredException | PluginPreferenceIsNotAvailable e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reload displaying notes and refresh tree viewers
	 */
	private void refresh() {
		List<INote> notes = databaseService.getByCustomQuery(CustomQueryConstants.NOTES_WITHOUT_PARENT);
		uiSynchronizeService.syncExec(() -> {
			if (!tvKatalonNotes.getControl().isDisposed()) {
				tvKatalonNotes.setInput(notes);
				tvKatalonNotes.refresh();
				tvKatalonNotes.expandAll();
			}
		});
	}

	private void registerNotesListener() {
		tvKatalonNotes.getTree().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!tvKatalonNotes.getControl().isDisposed()
						&& tvKatalonNotes.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection) tvKatalonNotes.getSelection();

					if (selection == null || selection.isEmpty()) {
						setSelectedNote(null);
						return;
					}

					if (((INote) selection.getFirstElement()).equals(selectedNote)) {
						return;
					}

					INote savedSelectedNote = null;
					if (selectedNote != null) {
						if (selectedNote.getId() == null) {
							savedSelectedNote = databaseService.create(selectedNote);
						} else {
							savedSelectedNote = databaseService.update(selectedNote);
						}
					}
					setSelectedNote((INote) selection.getFirstElement());
					if (selectedNote != null) {
						uiSynchronizeService.syncExec(() -> {
							if (txtTitle.isDisposed() || txtContent.isDisposed()) {
								return;
							}
							txtTitle.setText(selectedNote.getTitle());
							txtContent.setText(selectedNote.getContent());
						});
					}
					if (savedSelectedNote != null) {
						String title = savedSelectedNote.getTitle();
						uiSynchronizeService.syncExec(() -> {
							if (!lblInformation.isDisposed()) {
								lblInformation.setText(title + " is updated !");
							}
						});
					}
					refresh();
				}
			}
		});

		tvKatalonNotes.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(final DoubleClickEvent event) {
				final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (selection == null || selection.isEmpty())
					return;

				final Object sel = selection.getFirstElement();
				uiSynchronizeService.syncExec(() -> {
					if (tvKatalonNotes.getControl().isDisposed()) {
						return;
					}
					final ITreeContentProvider provider = (ITreeContentProvider) tvKatalonNotes.getContentProvider();

					if (!provider.hasChildren(sel))
						return;

					if (tvKatalonNotes.getExpandedState(sel))
						tvKatalonNotes.collapseToLevel(sel, AbstractTreeViewer.ALL_LEVELS);
					else
						tvKatalonNotes.expandToLevel(sel, 1);
				});
			}
		});

		txtContent.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if (htmlRenderer == null) {
					uiSynchronizeService.syncExec(() -> {
						if (!browserPreview.isDisposed()) {
							browserPreview.setText("PLEASE OPEN A PROJECT TO START TAKING NOTES !");
						}
					});
					return;
				}
				uiSynchronizeService.syncExec(() -> {
					if (!browserPreview.isDisposed()) {
						browserPreview.setText(htmlRenderer.render(txtContent.getText()));
					}
				});

				if (selectedNote != null) {
					selectedNote.setContent(txtContent.getText());
				}
			}
		});

		txtTitle.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if (selectedNote != null) {
					selectedNote.setTitle(txtTitle.getText());
				}
			}
		});
	}
}
