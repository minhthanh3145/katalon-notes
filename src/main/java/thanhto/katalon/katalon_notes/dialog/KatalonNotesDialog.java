package thanhto.katalon.katalon_notes.dialog;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

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
	private Display display;
	private DatabaseService<INote> databaseService;

	SelectionAdapter addSelectionAdapter = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			INote unsavedSelectedNote = selectedNote;
			INote unsavedChildNote = new KatalonNote("Default Title", "Default Content");
			INote savedParent = databaseService.update(unsavedSelectedNote);
			INote savedChildNote = databaseService.create(unsavedChildNote);
			savedChildNote.setParent(savedParent);
			savedChildNote = databaseService.update(savedChildNote);
			refresh();
		}
	};

	SelectionAdapter deleteSelectionAdapter = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			databaseService.delete(selectedNote);
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
		this.display = Display.getDefault();
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
		parent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		parent.setLayout(new GridLayout(2, false));

		Button btnSave = createButton(parent, IDialogConstants.NO_ID, "Update", true);
		btnSave.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		btnSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				INote unsavedNote = selectedNote;
				INote savedNote = databaseService.update(unsavedNote);
				setSelectedNote(savedNote);
				display.syncExec(() -> {
					lblInformation.setText(selectedNote.getTitle() + " is updated !");
				});
				refresh();
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
		display.syncExec(() -> {
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
				if (tvKatalonNotes.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection) tvKatalonNotes.getSelection();

					if (selection == null || selection.isEmpty()) {
						setSelectedNote(null);
						return;
					}

					if (((INote) selection.getFirstElement()).equals(selectedNote)) {
						return;
					}

					INote savedSelectedNote = databaseService.update(selectedNote);
					setSelectedNote((INote) selection.getFirstElement());
					if (savedSelectedNote != null) {
						display.syncExec(() -> {
							if (selectedNote != null) {
								txtTitle.setText(selectedNote.getTitle());
								txtContent.setText(selectedNote.getContent());
							}
							lblInformation.setText(savedSelectedNote.getTitle() + " is updated !");
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
				display.syncExec(() -> {
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
				display.syncExec(() -> {
					if (htmlRenderer == null) {
						browserPreview.setText("PLEASE OPEN A PROJECT TO START TAKING NOTES !");
						return;
					}
					browserPreview.setText(htmlRenderer.render(txtContent.getText()));

				});
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

		txtContent.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if (selectedNote != null) {
					selectedNote.setContent(txtContent.getText());
				}
			}
		});
	}
}
