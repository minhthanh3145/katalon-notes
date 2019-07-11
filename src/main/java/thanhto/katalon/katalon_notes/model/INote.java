package thanhto.katalon.katalon_notes.model;

import java.util.List;
import java.util.Map;

/**
 * Interface representing a note used by Katalon Notes plug-in. Implementing
 * classes must also implement Serializable in order to be persisted into
 * database
 * 
 * @author thanhto
 *
 */
public interface INote {

	String getTitle();

	void setTitle(String title);

	String getContent();

	void setContent(String content);

	List<INote> getChildNotes();

	void setChildNotes(List<INote> childNotes);

	INote getParent();

	void setParent(INote parent);

	Map<String, Object> getProperties();

	Long getId();

	void setId(Long id);
}
