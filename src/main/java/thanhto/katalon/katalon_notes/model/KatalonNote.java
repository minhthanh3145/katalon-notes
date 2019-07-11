package thanhto.katalon.katalon_notes.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KatalonNote implements INote, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -224448022871740541L;
	private String title;
	private String content;
	private List<INote> childNotes;
	private INote parent;
	private Long id;

	public KatalonNote(String title, String content) {
		this.title = title;
		this.content = content;
	}

	public KatalonNote(INote parentNote, String title, String content) {
		this(title, content);
		this.parent = parentNote;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getContent() {
		return content;
	}

	@Override
	public Map<String, Object> getProperties() {
		return null;
	}

	@Override
	public List<INote> getChildNotes() {
		if (childNotes == null) {
			childNotes = new ArrayList<>();
		}
		return childNotes;
	}

	public void addChildNote(INote note) {
		getChildNotes().add(note);
	}

	@Override
	public INote getParent() {
		return parent;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public void setContent(String content) {
		this.content = content;
	}

	public void setChildNotes(List<INote> childNotes) {
		this.childNotes = childNotes;
	}

	public void setParent(INote parent) {
		this.parent = parent;
	}
}
