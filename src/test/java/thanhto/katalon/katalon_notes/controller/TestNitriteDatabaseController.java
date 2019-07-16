package thanhto.katalon.katalon_notes.controller;

import java.io.File;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;

import thanhto.katalon.katalon_notes.builder.NoteBuilder;
import thanhto.katalon.katalon_notes.constant.CustomQueryConstants;
import thanhto.katalon.katalon_notes.model.INote;
import thanhto.katalon.katalon_notes.util.NoteUtils;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestNitriteDatabaseController {

	static NitriteDatabaseController controller;

	@BeforeClass
	public static void prepare() {
		controller = Mockito.spy(new NitriteDatabaseController());
		Mockito.doReturn(new File("src/test/resources").getAbsolutePath()).when(controller).getCurrentProjectPath();
		controller.openConnection();
	}
	
	@Test public void testSuite() {
		testCreate();
		testGetByName();
		testUpdateWithoutMovingSubTree();
		testUpdateWithMovingSubtree();
		testDeleteWithoutSubtree();
		testDeleteWithSubtree();
		testGetNotesWithoutParent();
	}

	public void testCreate() {
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

//	@Test
	public void testGetByName() {
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

//	@Test
	public void testUpdateWithoutMovingSubTree() {
		INote expected = new NoteBuilder("root", "root")
				.addChildNote(new NoteBuilder("a", "a")
						.addChildNote(NoteUtils.from("a1", "a1"))
						.addChildNote(NoteUtils.from("a2", "a2"))
						.addChildNote(new NoteBuilder("a3", "a3")
								.addChildNote(NoteUtils.from("a31", "a31"))
								.addChildNote(NoteUtils.from("a32", "a32"))
								.build())
						.addChildNote(NoteUtils.from("After Update", "a4")).build())
				.addChildNote(NoteUtils.from("b", "b"))
				.addChildNote(NoteUtils.from("c", "c"))
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

//	@Test
	public void testUpdateWithMovingSubtree() {
		INote expected = new NoteBuilder("root", "root")
				.addChildNote(new NoteBuilder("a", "a")
						.addChildNote(NoteUtils.from("a1", "a1"))
						.addChildNote(NoteUtils.from("a2", "a2"))
						.addChildNote(NoteUtils.from("After Update", "a4"))
						.build())
				.addChildNote(NoteUtils.from("b", "b"))
				.addChildNote(NoteUtils.from("c", "c"))
				.addChildNote(new NoteBuilder("d", "d")
						.addChildNote(new NoteBuilder("a3", "a3")
								.addChildNote(NoteUtils.from("a31", "a31"))
								.addChildNote(NoteUtils.from("a32", "a32")).build())
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
	public void testDeleteWithoutSubtree() {
		INote expected = new NoteBuilder("root", "root")
				.addChildNote(new NoteBuilder("a", "a")
						.addChildNote(NoteUtils.from("a1", "a1"))
						.addChildNote(NoteUtils.from("a2", "a2"))
						.build())
				.addChildNote(NoteUtils.from("b", "b"))
				.addChildNote(NoteUtils.from("c", "c"))
				.addChildNote(new NoteBuilder("d", "d")
						.addChildNote(new NoteBuilder("a3", "a3")
								.addChildNote(NoteUtils.from("a31", "a31"))
								.addChildNote(NoteUtils.from("a32", "a32")).build())
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

	public void testDeleteWithSubtree() {
		INote expected = new NoteBuilder("root", "root")
				.addChildNote(new NoteBuilder("a", "a")
						.addChildNote(NoteUtils.from("a1", "a1"))
						.addChildNote(NoteUtils.from("a2", "a2"))
						.build())
				.addChildNote(NoteUtils.from("b", "b"))
				.addChildNote(NoteUtils.from("c", "c"))
				.addChildNote(NoteUtils.from("d", "d"))
				.build();

		List<INote> notesToUpdate = controller.getByName("a3");
		Assert.assertEquals(1, notesToUpdate.size());

		INote noteToUpdate = notesToUpdate.get(0);
		controller.delete(noteToUpdate);

		List<INote> rootNotes = controller.getByName("root");
		Assert.assertEquals(1, rootNotes.size());

		INote actual = rootNotes.get(0);
		Assert.assertTrue(NoteUtils.compare(actual, expected));
	}
	
	public void testGetNotesWithoutParent() {
		INote expected1 = new NoteBuilder("root", "root")
				.addChildNote(new NoteBuilder("a", "a")
						.addChildNote(NoteUtils.from("a1", "a1"))
						.addChildNote(NoteUtils.from("a2", "a2"))
						.build())
				.addChildNote(NoteUtils.from("b", "b"))
				.addChildNote(NoteUtils.from("c", "c"))
				.addChildNote(NoteUtils.from("d", "d"))
				.build();
		
		
		INote expected2 =  new NoteBuilder("root", "root")
				.addChildNote(new NoteBuilder("a", "a")
						.addChildNote(NoteUtils.from("a1", "a1"))
						.addChildNote(NoteUtils.from("a2", "a2"))
						.build())
				.addChildNote(NoteUtils.from("b", "b"))
				.addChildNote(NoteUtils.from("c", "c"))
				.addChildNote(NoteUtils.from("d", "d"))
				.build();
		
		controller.create(expected2);
		
		List<INote> rootNotes = controller.getByCustomQuery(CustomQueryConstants.NOTES_WITHOUT_PARENT);
		Assert.assertEquals(2, rootNotes.size());
		
		INote actual1 = rootNotes.get(0);
		Assert.assertTrue(NoteUtils.compare(actual1, expected1));		
		
		INote actual2 = rootNotes.get(0);
		Assert.assertTrue(NoteUtils.compare(actual2, expected1));		
	}
	
	@AfterClass
	public static void tearDown() {
		controller.closeConnection();
		new File("src/test/resources/katalon_notes.db").delete();
	}

}
