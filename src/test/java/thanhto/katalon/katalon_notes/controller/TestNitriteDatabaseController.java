package thanhto.katalon.katalon_notes.controller;

import java.io.File;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import thanhto.katalon.katalon_notes.api.IDatabaseActionProvider;
import thanhto.katalon.katalon_notes.builder.NoteBuilder;
import thanhto.katalon.katalon_notes.constant.CustomQueryConstants;
import thanhto.katalon.katalon_notes.constant.ServiceName;
import thanhto.katalon.katalon_notes.factory.DatabaseActionProviderFactory;
import thanhto.katalon.katalon_notes.factory.DatabaseArtifactFactory;
import thanhto.katalon.katalon_notes.model.KatalonNote;
import thanhto.katalon.katalon_notes.util.NoteUtils;

public class TestNitriteDatabaseController {

	static NitriteDatabaseController controller;
	private IDatabaseActionProvider actionProvider = DatabaseActionProviderFactory.getInstance()
			.get(ServiceName.Nitrite);

	@Before
	public void prepare() {
		actionProvider.setDatabaseLocation(
				"/Users/thanhto/Documents/repository/others/katalon-notes/src/test/resources");
		actionProvider.openConnection("");

		DatabaseArtifactFactory.getInstance()
		.setArtifact(NitriteDatabaseController.class.getName(), "objectRepository",
				actionProvider.get("objectRepository"));

		controller = Mockito.spy(new NitriteDatabaseController());
	}

	@Test
	public void crudOperationSuite() {
		crud_testCreateWithSubtree();
		crud_testGetByName();
		crud_testUpdateWithoutMovingSubTree();
		crud_testDeleteWithoutSubtree();
		crud_testDeleteWithSubtree();
	}

	@Test
	public void updateRootNoteSuite() {
		crud_testCreateWithSubtree();
		updateRootNote_updateRootNote();
	}

	@Test
	public void updateRootThenChildNoteSuite() {
		crud_testCreateWithSubtree();
		updateRootThenChildNote_updateRootNote();
		updateRootThenChildNote_updateChildNote();
	}

	@Test
	public void updateNoteThenItsChildNoteSuite() {
		crud_testCreateWithSubtree();
		updateRootThenChildNote_updateNote();
		updateRootThenChildNote_updateItsChildNote();
	}

	@Test
	public void createNoteThenItsChildNoteSuite() {
		KatalonNote expected = new NoteBuilder("root", "root").addChildNote(new NoteBuilder("child", "child").build())
				.build();

		KatalonNote rootNote = new KatalonNote("root", "root");
		KatalonNote savedRootNote = controller.create(rootNote);

		KatalonNote childNote = new KatalonNote("child", "child");
		childNote.setParent(savedRootNote);
		controller.create(childNote);

		List<KatalonNote> rootNotes = controller.getByCustomQuery(CustomQueryConstants.NOTES_WITHOUT_PARENT);
		Assert.assertNotEquals(0, rootNotes.size());

		KatalonNote actual1 = rootNotes.get(0);
		Assert.assertTrue(NoteUtils.compare(actual1, expected));
	}

	@After
	public void tearDown() {
		actionProvider.closeConnection();
		new File("src/test/resources/katalon_notes.db").delete();
	}

	private void updateRootThenChildNote_updateItsChildNote() {
		KatalonNote expected = new NoteBuilder("root", "root")
				.addChildNote(new NoteBuilder("changed a", "a").addChildNote(NoteUtils.from("a1", "a1"))
						.addChildNote(NoteUtils.from("a2", "a2"))
						.addChildNote(new NoteBuilder("changed a3", "a3").addChildNote(NoteUtils.from("a31", "a31"))
								.addChildNote(NoteUtils.from("a32", "a32")).build())
						.addChildNote(NoteUtils.from("a4", "a4")).build())
				.addChildNote(NoteUtils.from("b", "b")).addChildNote(NoteUtils.from("c", "c"))
				.addChildNote(NoteUtils.from("d", "d")).build();

		List<KatalonNote> notes = controller.getByName("a3");
		Assert.assertEquals(1, notes.size());
		KatalonNote rootBeforeUpdate = notes.get(0);
		rootBeforeUpdate.setTitle("changed a3");
		controller.update(rootBeforeUpdate);

		notes = controller.getByName("root");
		Assert.assertEquals(1, notes.size());
		KatalonNote actual = notes.get(0);

		Assert.assertTrue(NoteUtils.compare(actual, expected));
	}

	private void updateRootThenChildNote_updateNote() {
		KatalonNote expected = new NoteBuilder("root", "root")
				.addChildNote(new NoteBuilder("changed a", "a").addChildNote(NoteUtils.from("a1", "a1"))
						.addChildNote(NoteUtils.from("a2", "a2"))
						.addChildNote(new NoteBuilder("a3", "a3").addChildNote(NoteUtils.from("a31", "a31"))
								.addChildNote(NoteUtils.from("a32", "a32")).build())
						.addChildNote(NoteUtils.from("a4", "a4")).build())
				.addChildNote(NoteUtils.from("b", "b")).addChildNote(NoteUtils.from("c", "c"))
				.addChildNote(NoteUtils.from("d", "d")).build();

		List<KatalonNote> notes = controller.getByName("a");
		Assert.assertEquals(1, notes.size());
		KatalonNote rootBeforeUpdate = notes.get(0);
		rootBeforeUpdate.setTitle("changed a");
		controller.update(rootBeforeUpdate);

		notes = controller.getByName("root");
		Assert.assertEquals(1, notes.size());
		KatalonNote actual = notes.get(0);

		Assert.assertTrue(NoteUtils.compare(actual, expected));

	}

	public void updateRootThenChildNote_updateChildNote() {
		KatalonNote expected = new NoteBuilder("changed root", "root")
				.addChildNote(new NoteBuilder("a", "a").addChildNote(NoteUtils.from("a1", "a1"))
						.addChildNote(NoteUtils.from("a2", "a2"))
						.addChildNote(new NoteBuilder("a3", "a3").addChildNote(NoteUtils.from("a31", "a31"))
								.addChildNote(NoteUtils.from("Child Note Title", "a32")).build())
						.addChildNote(NoteUtils.from("a4", "a4")).build())
				.addChildNote(NoteUtils.from("b", "b")).addChildNote(NoteUtils.from("c", "c"))
				.addChildNote(NoteUtils.from("d", "d")).build();

		List<KatalonNote> notes = controller.getByName("a32");
		Assert.assertEquals(1, notes.size());
		KatalonNote rootBeforeUpdate = notes.get(0);
		rootBeforeUpdate.setTitle("Child Note Title");
		controller.update(rootBeforeUpdate);

		notes = controller.getByName("changed root");
		Assert.assertEquals(1, notes.size());
		KatalonNote actual = notes.get(0);
		viewAllNotes();
		Assert.assertTrue(NoteUtils.compare(actual, expected));
	}

	public void updateRootThenChildNote_updateRootNote() {
		KatalonNote expected = new NoteBuilder("changed root", "root")
				.addChildNote(new NoteBuilder("a", "a").addChildNote(NoteUtils.from("a1", "a1"))
						.addChildNote(NoteUtils.from("a2", "a2"))
						.addChildNote(new NoteBuilder("a3", "a3").addChildNote(NoteUtils.from("a31", "a31"))
								.addChildNote(NoteUtils.from("a32", "a32")).build())
						.addChildNote(NoteUtils.from("a4", "a4")).build())
				.addChildNote(NoteUtils.from("b", "b")).addChildNote(NoteUtils.from("c", "c"))
				.addChildNote(NoteUtils.from("d", "d")).build();

		List<KatalonNote> notes = controller.getByName("root");
		Assert.assertEquals(1, notes.size());
		KatalonNote rootBeforeUpdate = notes.get(0);
		rootBeforeUpdate.setTitle("changed root");
		controller.update(rootBeforeUpdate);

		notes = controller.getByName("changed root");
		Assert.assertEquals(1, notes.size());
		KatalonNote actual = notes.get(0);

		Assert.assertTrue(NoteUtils.compare(actual, expected));

		notes = controller.getByName("root");
		Assert.assertEquals(0, notes.size());
	}

	public void crud_testCreateWithSubtree() {
		KatalonNote rootNoteExpected = new NoteBuilder("root", "root")
				.addChildNote(new NoteBuilder("a", "a").addChildNote(NoteUtils.from("a1", "a1"))
						.addChildNote(NoteUtils.from("a2", "a2"))
						.addChildNote(new NoteBuilder("a3", "a3").addChildNote(NoteUtils.from("a31", "a31"))
								.addChildNote(NoteUtils.from("a32", "a32")).build())
						.addChildNote(NoteUtils.from("a4", "a4")).build())
				.addChildNote(NoteUtils.from("b", "b")).addChildNote(NoteUtils.from("c", "c"))
				.addChildNote(NoteUtils.from("d", "d")).build();

		KatalonNote actual = controller.create(rootNoteExpected);
		Assert.assertTrue(NoteUtils.compare(actual, rootNoteExpected));

		KatalonNote rootNoteFromId = controller.getById(actual.getId());
		Assert.assertTrue(NoteUtils.compare(rootNoteFromId, actual));
	}

	public void updateRootNote_updateRootNote() {
		KatalonNote expected = new NoteBuilder("New Title", "root")
				.addChildNote(new NoteBuilder("a", "a").addChildNote(NoteUtils.from("a1", "a1"))
						.addChildNote(NoteUtils.from("a2", "a2"))
						.addChildNote(new NoteBuilder("a3", "a3").addChildNote(NoteUtils.from("a31", "a31"))
								.addChildNote(NoteUtils.from("a32", "a32")).build())
						.addChildNote(NoteUtils.from("a4", "a4")).build())
				.addChildNote(NoteUtils.from("b", "b")).addChildNote(NoteUtils.from("c", "c"))
				.addChildNote(NoteUtils.from("d", "d")).build();

		List<KatalonNote> notes = controller.getByName("root");
		Assert.assertEquals(1, notes.size());
		KatalonNote rootBeforeUpdate = notes.get(0);
		rootBeforeUpdate.setTitle("New Title");
		controller.update(rootBeforeUpdate);

		notes = controller.getByName("New Title");
		Assert.assertEquals(1, notes.size());
		KatalonNote actual = notes.get(0);

		Assert.assertTrue(NoteUtils.compare(actual, expected));
	}

	// @Test
	public void crud_testGetByName() {
		KatalonNote expected = new NoteBuilder("root", "root")
				.addChildNote(new NoteBuilder("a", "a").addChildNote(NoteUtils.from("a1", "a1"))
						.addChildNote(NoteUtils.from("a2", "a2"))
						.addChildNote(new NoteBuilder("a3", "a3").addChildNote(NoteUtils.from("a31", "a31"))
								.addChildNote(NoteUtils.from("a32", "a32")).build())
						.addChildNote(NoteUtils.from("a4", "a4")).build())
				.addChildNote(NoteUtils.from("b", "b")).addChildNote(NoteUtils.from("c", "c"))
				.addChildNote(NoteUtils.from("d", "d")).build();

		List<KatalonNote> notes = controller.getByName("root");
		Assert.assertEquals(1, notes.size());
		KatalonNote actual = notes.get(0);
		Assert.assertTrue(NoteUtils.compare(actual, expected));
	}

	// @Test
	public void crud_testUpdateWithoutMovingSubTree() {
		KatalonNote expected = new NoteBuilder("root", "root")
				.addChildNote(new NoteBuilder("a", "a").addChildNote(NoteUtils.from("a1", "a1"))
						.addChildNote(NoteUtils.from("a2", "a2"))
						.addChildNote(new NoteBuilder("a3", "a3").addChildNote(NoteUtils.from("a31", "a31"))
								.addChildNote(NoteUtils.from("a32", "a32")).build())
						.addChildNote(NoteUtils.from("After Update", "a4")).build())
				.addChildNote(NoteUtils.from("b", "b")).addChildNote(NoteUtils.from("c", "c"))
				.addChildNote(NoteUtils.from("d", "d")).build();

		List<KatalonNote> notesToUpdate = controller.getByName("a4");
		Assert.assertEquals(1, notesToUpdate.size());

		KatalonNote noteToUpdate = notesToUpdate.get(0);
		noteToUpdate.setTitle("After Update");
		controller.update(noteToUpdate);

		List<KatalonNote> rootNotes = controller.getByName("root");
		Assert.assertEquals(1, rootNotes.size());

		KatalonNote actual = rootNotes.get(0);
		Assert.assertTrue(NoteUtils.compare(actual, expected));
	}

	// @Test
	public void crud_testDeleteWithoutSubtree() {
		KatalonNote expected = new NoteBuilder("root", "root")
				.addChildNote(new NoteBuilder("a", "a").addChildNote(NoteUtils.from("a1", "a1"))
						.addChildNote(NoteUtils.from("a2", "a2"))
						.addChildNote(new NoteBuilder("a3", "a3").addChildNote(NoteUtils.from("a31", "a31"))
								.addChildNote(NoteUtils.from("a32", "a32")).build())
						.build())
				.addChildNote(NoteUtils.from("b", "b")).addChildNote(NoteUtils.from("c", "c"))
				.addChildNote(NoteUtils.from("d", "d")).build();

		List<KatalonNote> notesToDelete = controller.getByName("After Update");
		Assert.assertEquals(1, notesToDelete.size());

		KatalonNote noteToDelete = notesToDelete.get(0);
		controller.delete(noteToDelete);

		List<KatalonNote> rootNotes = controller.getByName("root");
		Assert.assertEquals(1, rootNotes.size());

		KatalonNote actual = rootNotes.get(0);
		Assert.assertTrue(NoteUtils.compare(actual, expected));
	}

	public void crud_testDeleteWithSubtree() {
		KatalonNote expected = new NoteBuilder("root", "root")
				.addChildNote(new NoteBuilder("a", "a").addChildNote(NoteUtils.from("a1", "a1"))
						.addChildNote(NoteUtils.from("a2", "a2")).build())
				.addChildNote(NoteUtils.from("b", "b")).addChildNote(NoteUtils.from("c", "c"))
				.addChildNote(NoteUtils.from("d", "d")).build();

		List<KatalonNote> notesToUpdate = controller.getByName("a3");
		Assert.assertEquals(1, notesToUpdate.size());

		KatalonNote noteToUpdate = notesToUpdate.get(0);
		controller.delete(noteToUpdate);

		List<KatalonNote> a31s = controller.getByName("a31");
		Assert.assertEquals(0, a31s.size());

		List<KatalonNote> rootNotes = controller.getByName("root");
		Assert.assertEquals(1, rootNotes.size());

		KatalonNote actual = rootNotes.get(0);
		Assert.assertTrue(NoteUtils.compare(actual, expected));
	}

	@Test
	public void customQuery() {
		KatalonNote expected1 = new NoteBuilder("root", "root")
				.addChildNote(new NoteBuilder("a", "a").addChildNote(NoteUtils.from("a1", "a1"))
						.addChildNote(NoteUtils.from("a2", "a2")).build())
				.addChildNote(NoteUtils.from("b", "b")).addChildNote(NoteUtils.from("c", "c"))
				.addChildNote(NoteUtils.from("d", "d")).build();

		controller.create(expected1);

		List<KatalonNote> rootNotes = controller.getByCustomQuery(CustomQueryConstants.NOTES_WITHOUT_PARENT);
		Assert.assertNotEquals(0, rootNotes.size());

		KatalonNote actual1 = rootNotes.get(0);
		Assert.assertTrue(NoteUtils.compare(actual1, expected1));
	}

	public void viewAllNotes() {
		List<KatalonNote> notes = controller.getByCustomQuery(CustomQueryConstants.ALL);
		notes.forEach(b -> {
			System.out.println(b.getTitle() + " " + b.getContent());
		});
	}
}
