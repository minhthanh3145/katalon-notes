package thanhto.katalon.katalon_notes.api;

import java.util.List;

/**
 * A Database controller interface that offers CRUD operations.
 * 
 * Controller can only be functional if the corresponding
 * {@link IDatabaseActionProvider} has been called to open the database.
 * 
 * Calling the constructor alone is <b>NOT</b> sufficient to make it functional
 * 
 * @see thanhto.katalon.katalon_notes.provider.ServiceProvider#getDatabaseService(String,
 *      String, String...)
 * 
 * @param actionProvider
 *
 */
public interface IDatabaseController<T> {

	List<T> getByCustomQuery(String query);

	T create(T note);

	T update(T note);

	T delete(T id);

	T getById(Long id);

	List<T> getByName(String title);
}
