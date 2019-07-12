package thanhto.katalon.katalon_notes.dialog;

import java.util.List;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
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

import com.katalon.platform.api.service.ApplicationManager;
import com.katalon.platform.api.ui.UISynchronizeService;

import thanhto.katalon.katalon_notes.constant.CustomQueryConstants;
import thanhto.katalon.katalon_notes.model.INote;
import thanhto.katalon.katalon_notes.model.KatalonNote;
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
	private Button btnSave;
	private boolean dirty;
	private Label lblInformation;
	private MenuItem deleteThisNoteMenuItem;
	private MenuItem refreshMenuItem;
	private MenuItem addNoteMenuItem;

	private UISynchronizeService uiSynchronizedService;
	private DatabaseService<INote> databaseService;
	private MenuManager menuMgr;
	private Menu menu;

	public KatalonNotesDialog(Shell parentShell, DatabaseService<INote> service) {
		super(parentShell);
		this.databaseService = service;
		uiSynchronizedService = ApplicationManager.getInstance().getUIServiceManager()
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
		loadNotesFromDatabase();
		registerNotesListener();

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

	private void initializeMenuItemsForContextMenu(Menu menu, INote note) {
		addNoteMenuItem = new MenuItem(menu, SWT.NONE);
		addNoteMenuItem.setText("Add a child note");

		deleteThisNoteMenuItem = new MenuItem(menu, SWT.NONE);
		deleteThisNoteMenuItem.setText("Delete this note");

		refreshMenuItem = new MenuItem(menu, SWT.NONE);
		refreshMenuItem.setText("Refresh");

	}

	private void addContextMenuToTree() {
		menuMgr = new MenuManager();
		menu = menuMgr.createContextMenu(tvKatalonNotes.getControl());
		menuMgr.setRemoveAllWhenShown(true);
		tvKatalonNotes.getControl().setMenu(menu);
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
		browserPreview.setFont(txtContent.getFont());
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// Change parent layout data to fill the whole bar
		parent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		parent.setLayout(new GridLayout(2, false));
		btnSave = createButton(parent, IDialogConstants.NO_ID, "Update", true);
		btnSave.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		lblInformation = new Label(parent, SWT.NONE);
		lblInformation.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
		lblInformation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		lblInformation.setText("Manage your notes !");
	}

	private void setInput(List<INote> notes) {
		tvKatalonNotes.setInput(notes);
	}

	private void loadNotesFromDatabase() {
		List<INote> notes = databaseService.getByCustomQuery(CustomQueryConstants.NOTES_WITHOUT_PARENT);
		if (notes.size() == 0) {
			KatalonNote note = new KatalonNote("Default", "Default");
			notes.add(note);
		}
		setSelectedNote(notes.get(0));
		setInput(notes);
	}

	private void setSelectedNote(INote note) {
		this.selectedNote = note;
	}

	private INote getSelectedNote() {
		return this.selectedNote;
	}

	private void registerNotesListener() {

		INote note = getSelectedNote();

		btnSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				uiSynchronizedService.asyncExec(() -> {
					databaseService.update(getSelectedNote());
					lblInformation.setText(selectedNote.getTitle() + " has been updated !");
					tvKatalonNotes.refresh();
				});
			}
		});

		tvKatalonNotes.getTree().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (tvKatalonNotes.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection) tvKatalonNotes.getSelection();
					INote newlySelectedNote = (INote) selection.getFirstElement();

					if (newlySelectedNote == null || !(newlySelectedNote instanceof INote)) {
						return;
					}
					setSelectedNote(newlySelectedNote);
					if (selectedNote.equals(newlySelectedNote) || !dirty) {
						lblInformation.setText("No changes in your workspace ! Start working");
					} else {
						if (note.getId() != null) {
							uiSynchronizedService.asyncExec(() -> {
								databaseService.update(note);
								lblInformation.setText(note.getTitle() + " has been updated !");
							});
						} else {
							uiSynchronizedService.asyncExec(() -> {
								databaseService.create(note);
								lblInformation.setText(note.getTitle() + " has been created !");
							});
						}
					}
					uiSynchronizedService.asyncExec(() -> {
						txtTitle.setText(newlySelectedNote.getTitle());
						txtContent.setText(newlySelectedNote.getContent());
						dirty = false;
					});
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

				final ITreeContentProvider provider = (ITreeContentProvider) tvKatalonNotes.getContentProvider();

				if (!provider.hasChildren(sel))
					return;

				if (tvKatalonNotes.getExpandedState(sel))
					tvKatalonNotes.collapseToLevel(sel, AbstractTreeViewer.ALL_LEVELS);
				else
					tvKatalonNotes.expandToLevel(sel, 1);
			}
		});

		txtContent.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				Parser parser = Parser.builder().build();
				Node document = parser.parse(((Text) e.widget).getText());
				HtmlRenderer renderer = HtmlRenderer.builder().build();
				browserPreview.setText(renderer.render(document));
			}
		});

		txtTitle.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				getSelectedNote().setTitle(txtTitle.getText());
				dirty = true;
			}
		});

		txtContent.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				getSelectedNote().setContent(txtContent.getText());
				dirty = true;
			}
		});

		addNoteMenuItem.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				uiSynchronizedService.asyncExec(() -> {
					INote currentNote = null;
					if (note == null) {
						KatalonNote newNote = new KatalonNote("Default title", "Default content");
						currentNote = databaseService.create(newNote);
						setSelectedNote(currentNote);
						((List<INote>) tvKatalonNotes.getInput()).add(currentNote);
						tvKatalonNotes.refresh();
						return;
					} else if (note.getId() == null) {
						currentNote = databaseService.create(note);
					}

					KatalonNote newNote = new KatalonNote("Default title", "Default content");
					newNote.setParent(currentNote);
					currentNote.getChildNotes().add(newNote);
					databaseService.create(newNote);
					databaseService.update(currentNote);
					setSelectedNote(newNote);
					tvKatalonNotes.refresh();
				});
			}
		});

		deleteThisNoteMenuItem.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				uiSynchronizedService.asyncExec(() -> {
					INote parentNote = note.getParent();
					databaseService.delete(note);
					if (parentNote != null) {
						parentNote.getChildNotes().remove(note);
						databaseService.update(parentNote);
					}
					((List<INote>) tvKatalonNotes.getInput()).remove(note);
					tvKatalonNotes.refresh();
				});
			}
		});

		refreshMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				uiSynchronizedService.asyncExec(() -> {
					tvKatalonNotes.refresh();
				});
			}
		});

		menu.addMenuListener(new MenuAdapter() {
			public void menuShown(MenuEvent e) {
				MenuItem[] items = menu.getItems();
				for (int i = 0; i < items.length; i++) {
					items[i].dispose();
				}
				initializeMenuItemsForContextMenu(menu, selectedNote);
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
					setSelectedNote((INote) selection.getFirstElement());
				}
			}
		});
	}

}
