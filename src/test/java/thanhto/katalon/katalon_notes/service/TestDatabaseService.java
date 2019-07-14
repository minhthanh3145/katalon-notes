package thanhto.katalon.katalon_notes.service;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import thanhto.katalon.katalon_notes.controller.NitriteDatabaseController;
import thanhto.katalon.katalon_notes.model.INote;
import thanhto.katalon.katalon_notes.model.KatalonNote;

public class TestDatabaseService {

	private static Long mockId = new Long(5);

	private static INote mockNote = new KatalonNote("mock", "mock");

	private static List<INote> mockNotes = Arrays.asList(mockNote);

	private static DatabaseService<INote> noteDatabaseService;

	private static NitriteDatabaseController controller;

	@Before
	public void initializeMockObjects() {
		controller = Mockito.mock(NitriteDatabaseController.class);
		Mockito.when(controller.getById(mockId)).thenReturn(mockNote);
		Mockito.when(controller.getByName("")).thenReturn(mockNotes);
		Mockito.when(controller.getByCustomQuery("")).thenReturn(mockNotes);
		noteDatabaseService = new DatabaseService<INote>();
		noteDatabaseService.setController(controller);
	}

	@Test
	public void testGetByID() {
		Assert.assertEquals(mockNote, noteDatabaseService.getById(mockId));
	}

	@Test
	public void testGetByName() {
		Assert.assertEquals(mockNotes, noteDatabaseService.getByName(""));
	}

	@Test
	public void testGetByCustomQuery() {
		Assert.assertEquals(mockNotes, noteDatabaseService.getByCustomQuery(""));
	}

	@Test
	public void testCreate() {
		INote mockNoteWithId = mockNote;
		mockNoteWithId.setId(mockId);
		Mockito.when(controller.create(mockNote)).thenReturn(mockNoteWithId);
		Assert.assertEquals(mockNoteWithId, noteDatabaseService.create(mockNote));
	}

	@Test
	public void testUpdate() {
		INote mockNoteWithId = mockNote;
		mockNoteWithId.setId(mockId);
		mockNoteWithId.setTitle("Updated");
		Mockito.when(controller.update(mockNote)).thenReturn(mockNoteWithId);
		Assert.assertEquals(mockNoteWithId, noteDatabaseService.update(mockNote));
	}

	@Test
	public void testDelete() {
		INote mockNoteWithId = mockNote;
		mockNoteWithId.setId(mockId);
		Mockito.when(controller.delete(mockNote)).thenReturn(mockNoteWithId);
		Assert.assertEquals(mockNoteWithId, noteDatabaseService.delete(mockNote));
	}
}
