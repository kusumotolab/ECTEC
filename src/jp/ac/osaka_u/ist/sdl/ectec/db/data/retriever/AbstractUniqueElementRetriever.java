package jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.AbstractDBElement;

/**
 * An abstract class to retrieve unique elements from db
 * 
 * @author k-hotta
 * 
 * @param <T>
 */
public abstract class AbstractUniqueElementRetriever<T extends AbstractDBElement>
		extends AbstractElementRetriever<T> {

	public AbstractUniqueElementRetriever(final DBConnectionManager dbManager) {
		super(dbManager);
	}

	@Override
	public SortedMap<Long, T> instantiate(final ResultSet rs)
			throws SQLException {
		final SortedMap<Long, T> result = new TreeMap<Long, T>();

		while (rs.next()) {
			final T element = createElement(rs);
			result.put(element.getId(), element);
		}

		return Collections.unmodifiableSortedMap(result);
	}

	/**
	 * build an instance of element from the given record
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	protected abstract T createElement(ResultSet rs) throws SQLException;

}
