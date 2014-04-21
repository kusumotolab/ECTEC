package jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.AbstractDBSubTableElementInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.AbstractDBSubTableElementInfoComparator;

public abstract class AbstractDBSubTableElementRetriever<T extends AbstractDBSubTableElementInfo>
		extends AbstractElementRetriever<T> {

	public AbstractDBSubTableElementRetriever(DBConnectionManager dbManager) {
		super(dbManager);
	}

	@Override
	public SortedMap<Long, T> instantiate(ResultSet rs) throws SQLException {
		final SortedSet<T> elements = new TreeSet<T>(
				new AbstractDBSubTableElementInfoComparator<T>());

		while (rs.next()) {
			elements.add(makeInstance(rs));
		}

		final SortedMap<Long, T> result = new TreeMap<Long, T>();
		long count = 0;
		for (T element : elements) {
			result.put(count++, element);
		}

		return Collections.unmodifiableSortedMap(result);
	}

	protected abstract T makeInstance(ResultSet rs) throws SQLException;

}
