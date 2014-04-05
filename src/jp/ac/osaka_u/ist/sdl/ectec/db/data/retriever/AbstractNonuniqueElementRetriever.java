package jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.AbstractDBElement;

/**
 * An abstract class to retrieve non-unique elements from db
 * 
 * @author k-hotta
 * 
 * @param <T>
 * @param <U>
 */
public abstract class AbstractNonuniqueElementRetriever<T extends AbstractDBElement, U extends AbstractRowData>
		extends AbstractElementRetriever<T> {

	public AbstractNonuniqueElementRetriever(DBConnectionManager dbManager) {
		super(dbManager);
	}

	@Override
	public SortedMap<Long, T> instantiate(ResultSet rs) throws SQLException {
		final SortedMap<Long, T> result = new TreeMap<Long, T>();
		final SortedMap<Long, List<U>> rows = new TreeMap<Long, List<U>>();
		
		while (rs.next()) {
			final U rowInstance = makeRowInstance(rs);
			final long id = rowInstance.getId();
			
			if (rows.containsKey(id)) {
				rows.get(id).add(rowInstance);
			} else {
				final List<U> newList = new ArrayList<U>();
				newList.add(rowInstance);
				rows.put(id, newList);
			}
		}
		
		for (final Map.Entry<Long, List<U>> entry : rows.entrySet()) {
			final T element = createElement(entry.getValue());
			result.put(element.getId(), element);
		}
		
		return Collections.unmodifiableSortedMap(result);
	}

	protected abstract U makeRowInstance(ResultSet rs) throws SQLException;
	
	protected abstract T createElement(Collection<U> rows);

}
