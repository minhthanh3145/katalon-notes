package thanhto.katalon.katalon_notes.controller;

import java.io.File;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;

import thanhto.katalon.katalon_notes.builder.NoteBuilder;
import thanhto.katalon.katalon_notes.constant.CustomQueryConstants;
import thanhto.katalon.katalon_notes.model.INote;
import thanhto.katalon.katalon_notes.model.KatalonNote;
import thanhto.katalon.katalon_notes.util.NoteUtils;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestNitriteDatabaseController {

	static NitriteDatabaseController controller;

	@Before
	public void prepare() {
		controller = Mockito.spy(new NitriteDatabaseController());
		Mockito.doReturn(new File("src/test/resources").getAbsolutePath()).when(controller).getCurrentProjectPath();
		controller.openConnection();
	}

	@Test
	public void basicOperationsSuite() {
		basicOperationsSuite_testCreateWithSubtree();
		basicOperationsSuite_testGetByName();
		basicOperationsSuite_testUpdateWithoutMovingSubTree();
		basicOperationsSuite_testUpdateWithMovingSubtree();
		basicOperationsSuite_testDeleteWithoutSubtree();
		basicOperationsSuite_testDeleteWithSubtree();
		basicOperationsSuite_testGetNotesWithoutParent();
	}

	@Test
	public void updateRootNoteSuite() {
		basicOperationsSuite_testCreateWithSubtree();
		updateRootNote_updateRootNote();
	}

	@Test
	public void updateRootThenChildNoteSuite() {
		basicOperationsSuite_testCreateWithSubtree();
		updateRootThenChildNote_updateRootNote();
		updateRootThenChildNote_updateChildNote();
	}

	@Test
	public void updateNoteThenItsChildNoteSuite() {
		basicOperationsSuite_testCreateWithSubtree();
		updateRootThenChildNote_updateNote();
		updateRootThenChildNote_updateItsChildNote();
	}

	@Test
	public void createNoteThenItsChildNoteSuite() {
		INote expected = new NoteBuilder("root", "root").addChildNote(new NoteBuilder("child", "child").build())
				.build();

		INote rootNote = new KatalonNote("root", "root");
		INote savedRootNote = controller.create(rootNote);

		INote childNote = new KatalonNote("child", "child");
		childNote.setParent(savedRootNote);
		controller.create(childNote);

		List<INote> rootNotes = controller.getByCustomQuery(CustomQueryConstants.NOTES_WITHOUT_PARENT);
		Assert.assertNotEquals(0, rootNotes.size());

		INote actual1 = rootNotes.get(0);
		Assert.assertTrue(NoteUtils.compare(actual1, expected));
	}

	private void updateRootThenChildNote_updateItsChildNote() {
		INote expected = new NoteBuilder("root", "root")
				.addChildNote(new NoteBuilder("changed a", "a").addChildNote(NoteUtils.from("a1", "a1"))
						.addChildNote(NoteUtils.from("a2", "a2"))
						.addChildNote(new NoteBuilder("changed a3", "a3").addChildNote(NoteUtils.from("a31", "a31"))
								.addChildNote(NoteUtils.from("a32", "a32")).build())
						.addChildNote(NoteUtils.from("a4", "a4")).build())
				.addChildNote(NoteUtils.from("b", "b")).addChildNote(NoteUtils.from("c", "c"))
				.addChildNote(NoteUtils.from("d", "d")).build();

		List<INote> notes = controller.getByName("a3");
		Assert.assertEquals(1, notes.size());
		INote rootBeforeUpdate = notes.get(0);
		rootBeforeUpdate.setTitle("changed a3");
		controller.update(rootBeforeUpdate);

		notes = controller.getByName("root");
		Assert.assertEquals(1, notes.size());
		INote actual = notes.get(0);

		Assert.assertTrue(NoteUtils.compare(actual, expected));
	}

	private void updateRootThenChildNote_updateNote() {
		INote expected = new NoteBuilder("root", "root")
				.addChildNote(new NoteBuilder("changed a", "a").addChildNote(NoteUtils.from("a1", "a1"))
						.addChildNote(NoteUtils.from("a2", "a2"))
						.addChildNote(new NoteBuilder("a3", "a3").addChildNote(NoteUtils.from("a31", "a31"))
								.addChildNote(NoteUtils.from("a32", "a32")).build())
						.addChildNote(NoteUtils.from("a4", "a4")).build())
				.addChildNote(NoteUtils.from("b", "b")).addChildNote(NoteUtils.from("c", "c"))
				.addChildNote(NoteUtils.from("d", "d")).build();

		List<INote> notes = controller.getByName("a");
		Assert.assertEquals(1, notes.size());
		INote rootBeforeUpdate = notes.get(0);
		rootBeforeUpdate.setTitle("changed a");
		controller.update(rootBeforeUpdate);

		notes = controller.getByName("root");
		Assert.assertEquals(1, notes.size());
		INote actual = notes.get(0);

		Assert.assertTrue(NoteUtils.compare(actual, expected));

	}

	public void updateRootThenChildNote_updateChildNote() {
		INote expected = new NoteBuilder("changed root", "root")
				.addChildNote(new NoteBuilder("a", "a").addChildNote(NoteUtils.from("a1", "a1"))
						.addChildNote(NoteUtils.from("a2", "a2"))
						.addChildNote(new NoteBuilder("a3", "a3").addChildNote(NoteUtils.from("a31", "a31"))
								.addChildNote(NoteUtils.from("Child Note Title", "a32")).build())
						.addChildNote(NoteUtils.from("a4", "a4")).build())
				.addChildNote(NoteUtils.from("b", "b")).addChildNote(NoteUtils.from("c", "c"))
				.addChildNote(NoteUtils.from("d", "d")).build();

		List<INote> notes = controller.getByName("a32");
		Assert.assertEquals(1, notes.size());
		INote rootBeforeUpdate = notes.get(0);
		rootBeforeUpdate.setTitle("Child Note Title");
		controller.update(rootBeforeUpdate);

		notes = controller.getByName("changed root");
		Assert.assertEquals(1, notes.size());
		INote actual = notes.get(0);

		Assert.assertTrue(NoteUtils.compare(actual, expected));
	}

	public void updateRootThenChildNote_updateRootNote() {
		INote expected = new NoteBuilder("changed root", "root")
				.addChildNote(new NoteBuilder("a", "a").addChildNote(NoteUtils.from("a1", "a1"))
						.addChildNote(NoteUtils.from("a2", "a2"))
						.addChildNote(new NoteBuilder("a3", "a3").addChildNote(NoteUtils.from("a31", "a31"))
								.addChildNote(NoteUtils.from("a32", "a32")).build())
						.addChildNote(NoteUtils.from("a4", "a4")).build())
				.addChildNote(NoteUtils.from("b", "b")).addChildNote(NoteUtils.from("c", "c"))
				.addChildNote(NoteUtils.from("d", "d")).build();

		List<INote> notes = controller.getByName("root");
		Assert.assertEquals(1, notes.size());
		INote rootBeforeUpdate = notes.get(0);
		rootBeforeUpdate.setTitle("changed root");
		controller.update(rootBeforeUpdate);

		notes = controller.getByName("changed root");
		Assert.assertEquals(1, notes.size());
		INote actual = notes.get(0);

		Assert.assertTrue(NoteUtils.compare(actual, expected));
	}

	public void basicOperationsSuite_testCreateWithSubtree() {
		INote rootNoteExpected = new NoteBuilder("root", "root")
				.addChildNote(new NoteBuilder("a", "a").addChildNote(NoteUtils.from("a1", "a1"))
						.addChildNote(NoteUtils.from("a2", "a2"))
						.addChildNote(new NoteBuilder("a3", "a3").addChildNote(NoteUtils.from("a31", "a31"))
								.addChildNote(NoteUtils.from("a32", "a32")).build())
						.addChildNote(NoteUtils.from("a4", "a4")).build())
				.addChildNote(NoteUtils.from("b", "b")).addChildNote(NoteUtils.from("c", "c"))
				.addChildNote(NoteUtils.from("d", "d")).build();

		INote actual = controller.create(rootNoteExpected);
		Assert.assertTrue(NoteUtils.compare(actual, rootNoteExpected));

		INote rootNoteFromId = controller.getById(actual.getId());
		Assert.assertTrue(NoteUtils.compare(rootNoteFromId, actual));
	}

	public void updateRootNote_updateRootNote() {
		INote expected = new NoteBuilder("New Title", "root")
				.addChildNote(new NoteBuilder("a", "a").addChildNote(NoteUtils.from("a1", "a1"))
						.addChildNote(NoteUtils.from("a2", "a2"))
						.addChildNote(new NoteBuilder("a3", "a3").addChildNote(NoteUtils.from("a31", "a31"))
								.addChildNote(NoteUtils.from("a32", "a32")).build())
						.addChildNote(NoteUtils.from("a4", "a4")).build())
				.addChildNote(NoteUtils.from("b", "b")).addChildNote(NoteUtils.from("c", "c"))
				.addChildNote(NoteUtils.from("d", "d")).build();

		List<INote> notes = controller.getByName("root");
		Assert.assertEquals(1, notes.size());
		INote rootBeforeUpdate = notes.get(0);
		rootBeforeUpdate.setTitle("New Title");
		controller.update(rootBeforeUpdate);

		notes = controller.getByName("New Title");
		Assert.assertEquals(1, notes.size());
		INote actual = notes.get(0);

		Assert.assertTrue(NoteUtils.compare(actual, expected));
	}

	// @Test
	public void basicOperationsSuite_testGetByName() {
		INote expected = new NoteBuilder("root", "root")
				.addChildNote(new NoteBuilder("a", "a").addChildNote(NoteUtils.from("a1", "a1"))
						.addChildNote(NoteUtils.from("a2", "a2"))
						.addChildNote(new NoteBuilder("a3", "a3").addChildNote(NoteUtils.from("a31", "a31"))
								.addChildNote(NoteUtils.from("a32", "a32")).build())
						.addChildNote(NoteUtils.from("a4", "a4")).build())
				.addChildNote(NoteUtils.from("b", "b")).addChildNote(NoteUtils.from("c", "c"))
				.addChildNote(NoteUtils.from("d", "d")).build();

		List<INote> notes = controller.getByName("root");
		Assert.assertEquals(1, notes.size());
		INote actual = notes.get(0);
		Assert.assertTrue(NoteUtils.compare(actual, expected));
	}

	// @Test
	public void basicOperationsSuite_testUpdateWithoutMovingSubTree() {
		INote expected = new NoteBuilder("root", "root")
				.addChildNote(new NoteBuilder("a", "a").addChildNote(NoteUtils.from("a1", "a1"))
						.addChildNote(NoteUtils.from("a2", "a2"))
						.addChildNote(new NoteBuilder("a3", "a3").addChildNote(NoteUtils.from("a31", "a31"))
								.addChildNote(NoteUtils.from("a32", "a32")).build())
						.addChildNote(NoteUtils.from("After Update", "a4")).build())
				.addChildNote(NoteUtils.from("b", "b")).addChildNote(NoteUtils.from("c", "c"))
				.addChildNote(NoteUtils.from("d", "d")).build();

		List<INote> notesToUpdate = controller.getByName("a4");
		Assert.assertEquals(1, notesToUpdate.size());

		INote noteToUpdate = notesToUpdate.get(0);
		noteToUpdate.setTitle("After Update");
		controller.update(noteToUpdate);

		List<INote> rootNotes = controller.getByName("root");
		Assert.assertEquals(1, rootNotes.size());

		INote actual = rootNotes.get(0);
		Assert.assertTrue(NoteUtils.compare(actual, expected));
	}

	// @Test
	public void basicOperationsSuite_testUpdateWithMovingSubtree() {
		INote expected = new NoteBuilder("root", "root")
				.addChildNote(new NoteBuilder("a", "a").addChildNote(NoteUtils.from("a1", "a1"))
						.addChildNote(NoteUtils.from("a2", "a2")).addChildNote(NoteUtils.from("After Update", "a4"))
						.build())
				.addChildNote(NoteUtils.from("b", "b")).addChildNote(NoteUtils.from("c", "c"))
				.addChildNote(new NoteBuilder("d", "d").addChildNote(new NoteBuilder("a3", "a3")
						.addChildNote(NoteUtils.from("a31", "a31")).addChildNote(NoteUtils.from("a32", "a32")).build())
						.build())
				.build();

		List<INote> notesToUpdate = controller.getByName("a3");
		Assert.assertEquals(1, notesToUpdate.size());

		List<INote> notesToMoveSubtreeTo = controller.getByName("d");
		Assert.assertEquals(1, notesToMoveSubtreeTo.size());

		INote noteToMoveSubtreeTo = notesToMoveSubtreeTo.get(0);
		INote noteToUpdate = notesToUpdate.get(0);
		noteToUpdate.setParent(noteToMoveSubtreeTo);
		controller.update(noteToUpdate);

		List<INote> rootNotes = controller.getByName("root");
		Assert.assertEquals(1, rootNotes.size());

		INote actual = rootNotes.get(0);
		Assert.assertTrue(NoteUtils.compare(actual, expected));
	}

	// @Test
	public void basicOperationsSuite_testDeleteWithoutSubtree() {
		INote expected = new NoteBuilder("root", "root")
				.addChildNote(new NoteBuilder("a", "a").addChildNote(NoteUtils.from("a1", "a1"))
						.addChildNote(NoteUtils.from("a2", "a2")).build())
				.addChildNote(NoteUtils.from("b", "b")).addChildNote(NoteUtils.from("c", "c"))
				.addChildNote(new NoteBuilder("d", "d").addChildNote(new NoteBuilder("a3", "a3")
						.addChildNote(NoteUtils.from("a31", "a31")).addChildNote(NoteUtils.from("a32", "a32")).build())
						.build())
				.build();

		List<INote> notesToDelete = controller.getByName("After Update");
		Assert.assertEquals(1, notesToDelete.size());

		INote noteToDelete = notesToDelete.get(0);
		controller.delete(noteToDelete);

		List<INote> rootNotes = controller.getByName("root");
		Assert.assertEquals(1, rootNotes.size());

		INote actual = rootNotes.get(0);
		Assert.assertTrue(NoteUtils.compare(actual, expected));
	}

	public void basicOperationsSuite_testDeleteWithSubtree() {
		INote expected = new NoteBuilder("root", "root")
				.addChildNote(new NoteBuilder("a", "a").addChildNote(NoteUtils.from("a1", "a1"))
						.addChildNote(NoteUtils.from("a2", "a2")).build())
				.addChildNote(NoteUtils.from("b", "b")).addChildNote(NoteUtils.from("c", "c"))
				.addChildNote(NoteUtils.from("d", "d")).build();

		List<INote> notesToUpdate = controller.getByName("a3");
		Assert.assertEquals(1, notesToUpdate.size());

		INote noteToUpdate = notesToUpdate.get(0);
		controller.delete(noteToUpdate);

		List<INote> a31s = controller.getByName("a31");
		Assert.assertEquals(0, a31s.size());

		List<INote> rootNotes = controller.getByName("root");
		Assert.assertEquals(1, rootNotes.size());

		INote actual = rootNotes.get(0);
		Assert.assertTrue(NoteUtils.compare(actual, expected));
	}

	public void basicOperationsSuite_testGetNotesWithoutParent() {
		INote expected1 = new NoteBuilder("root", "root")
				.addChildNote(new NoteBuilder("a", "a").addChildNote(NoteUtils.from("a1", "a1"))
						.addChildNote(NoteUtils.from("a2", "a2")).build())
				.addChildNote(NoteUtils.from("b", "b")).addChildNote(NoteUtils.from("c", "c"))
				.addChildNote(NoteUtils.from("d", "d")).build();

		INote expected2 = new NoteBuilder("root", "root")
				.addChildNote(new NoteBuilder("a", "a").addChildNote(NoteUtils.from("a1", "a1"))
						.addChildNote(NoteUtils.from("a2", "a2")).build())
				.addChildNote(NoteUtils.from("b", "b")).addChildNote(NoteUtils.from("c", "c"))
				.addChildNote(NoteUtils.from("d", "d")).build();

		controller.create(expected2);

		List<INote> rootNotes = controller.getByCustomQuery(CustomQueryConstants.NOTES_WITHOUT_PARENT);
		Assert.assertEquals(2, rootNotes.size());

		INote actual1 = rootNotes.get(0);
		Assert.assertTrue(NoteUtils.compare(actual1, expected1));

		INote actual2 = rootNotes.get(0);
		Assert.assertTrue(NoteUtils.compare(actual2, expected1));
	}

	@Test
	public void customQuery() {
		INote expected1 = new NoteBuilder("root", "root")
				.addChildNote(new NoteBuilder("a", "a").addChildNote(NoteUtils.from("a1", "a1"))
						.addChildNote(NoteUtils.from("a2", "a2")).build())
				.addChildNote(NoteUtils.from("b", "b")).addChildNote(NoteUtils.from("c", "c"))
				.addChildNote(NoteUtils.from("d", "d")).build();

		controller.create(expected1);

		List<INote> rootNotes = controller.getByCustomQuery(CustomQueryConstants.NOTES_WITHOUT_PARENT);
		Assert.assertNotEquals(0, rootNotes.size());

		INote actual1 = rootNotes.get(0);
		Assert.assertTrue(NoteUtils.compare(actual1, expected1));
	}

	@After
	public void tearDown() {
		controller.closeConnection();
		new File("src/test/resources/katalon_notes.db").delete();
	}

}
