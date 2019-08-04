package thanhto.katalon.katalon_notes.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dizitart.no2.NitriteId;
import org.dizitart.no2.objects.Id;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;


@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = KatalonNote.class)
public class KatalonNote implements Serializable {

	private static final long serialVersionUID = -224448022871740541L;
	private String title;
	private String content;

	private List<KatalonNote> childNotes;

	private KatalonNote parent;

	@Id
	private Long id;

	public KatalonNote(String title, String content) {
		this.title = title;
		this.content = content;
		id = NitriteId.newId().getIdValue();
	}

	public KatalonNote() {
		id = NitriteId.newId().getIdValue();
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}

	public Map<String, Object> getProperties() {
		return null;
	}

	public List<KatalonNote> getChildNotes() {
		if (childNotes == null) {
			childNotes = new ArrayList<>();
		}
		return childNotes;
	}

	public void addChildNote(KatalonNote id) {
		getChildNotes().add(id);
	}

	public KatalonNote getParent() {
		return parent;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setChildNotes(List<KatalonNote> childNotes) {
		this.childNotes = childNotes;
	}

	public void setParent(KatalonNote parent) {
		this.parent = parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof KatalonNote)) {
			return false;
		}
		KatalonNote other = (KatalonNote) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}
}
