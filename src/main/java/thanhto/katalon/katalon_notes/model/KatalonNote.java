package thanhto.katalon.katalon_notes.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KatalonNote implements INote, Serializable {

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

	public KatalonNote() {
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KatalonNote other = (KatalonNote) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
