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

	@Test
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

	@Test
	public void testGetByName() {
		INote rootNoteExpected = new NoteBuilder("root", "root")
				.addChildNote(new NoteBuilder("a", "a").addChildNote(NoteUtils.from("a1", "a1"))
						.addChildNote(NoteUtils.from("a2", "a2"))
						.addChildNote(new NoteBuilder("a3", "a3").addChildNote(NoteUtils.from("a31", "a31"))
								.addChildNote(NoteUtils.from("a32", "a32")).build())
						.addChildNote(NoteUtils.from("a4", "a4")).build())
				.addChildNote(NoteUtils.from("b", "b")).addChildNote(NoteUtils.from("c", "c"))
				.addChildNote(NoteUtils.from("d", "d")).build();

		List<INote> notes = controller.getByName("root");
		Assert.assertEquals(1, notes.size());
		INote rootNoteFromName = notes.get(0);
		Assert.assertTrue(NoteUtils.compare(rootNoteFromName, rootNoteExpected));
	}

	@Test
	public void testUpdate() {
		INote afterUpdateExpected = new NoteBuilder("root", "root")
				.addChildNote(new NoteBuilder("a", "a").addChildNote(NoteUtils.from("a1", "a1"))
						.addChildNote(NoteUtils.from("after update", "a2"))
						.addChildNote(new NoteBuilder("a3", "a3").addChildNote(NoteUtils.from("a31", "a31"))
								.addChildNote(NoteUtils.from("a32", "a32")).build())
						.addChildNote(NoteUtils.from("a4", "a4")).build())
				.addChildNote(NoteUtils.from("b", "b")).addChildNote(NoteUtils.from("c", "c"))
				.addChildNote(NoteUtils.from("d", "d")).build();

		List<INote> notesToUpdate = controller.getByName("a2");
		Assert.assertEquals(1, notesToUpdate.size());

		INote noteToUpdate = notesToUpdate.get(0);
		noteToUpdate.setTitle("after update");
		controller.update(noteToUpdate);

		List<INote> rootNotes = controller.getByName("root");
		Assert.assertEquals(1, rootNotes.size());

		INote afterUpdateActual = rootNotes.get(0);
		Assert.assertTrue(NoteUtils.compare(afterUpdateActual, afterUpdateExpected));

	}

	@AfterClass
	public static void tearDown() {
		controller.closeConnection();
		new File("src/test/resources/katalon_notes.db").delete();
	}

}
