package jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.AbstractDBElement;

/**
 * An abstract class to retrieve elements from db
 * 
 * @author k-hotta
 * 
 * @param <T>
 */
public abstract class AbstractUniqueElementRetriever<T extends AbstractDBElement> {

	/**
	 * the manager of the connection between db
	 */
	protected final DBConnectionManager dbManager;

	public AbstractUniqueElementRetriever(final DBConnectionManager dbManager) {
		this.dbManager = dbManager;
	}

	/**
	 * retrieve elements with the given query
	 * 
	 * @param query
	 * @return
	 * @throws SQLException
	 */
	public synchronized SortedMap<Long, T> retrieve(final String query)
			throws SQLException {
		final SortedMap<Long, T> result = new TreeMap<Long, T>();

		final Statement stmt = dbManager.createStatement();
		final ResultSet rs = stmt.executeQuery(query);

		while (rs.next()) {
			final T element = createElement(rs);
			result.put(element.getId(), element);
		}

		stmt.close();
		rs.close();

		return Collections.unmodifiableSortedMap(result);
	}

	/**
	 * retrieve all elements stored into the db
	 * 
	 * @return
	 * @throws SQLException
	 */
	public synchronized SortedMap<Long, T> retrieveAll() throws SQLException {
		final String query = "select * from " + getTableName();
		return retrieve(query);
	}

	/**
	 * retrieve elements having one of the given id
	 * 
	 * @param ids
	 * @return
	 * @throws SQLException
	 */
	public synchronized SortedMap<Long, T> retrieveWithIds(
			final Collection<Long> ids) throws SQLException {
		if (ids.isEmpty()) {
			return new TreeMap<Long, T>();
		}
		final StringBuilder builder = new StringBuilder();
		builder.append("select * from " + getTableName() + " where "
				+ getIdColumnName() + " in (");

		for (final long id : ids) {
			builder.append(id + ",");
		}

		builder.deleteCharAt(builder.length() - 1);
		builder.append(")");

		return retrieve(builder.toString());
	}

	/**
	 * retrieve elements having one of the given id
	 * 
	 * @param ids
	 * @return
	 * @throws SQLException
	 */
	public synchronized SortedMap<Long, T> retrieveWithIds(long... ids)
			throws SQLException {
		final Set<Long> idSet = new HashSet<Long>();
		for (final long id : ids) {
			idSet.add(id);
		}

		return retrieveWithIds(idSet);
	}

	/**
	 * retrieve elements NOT having one of the given id
	 * 
	 * @param ids
	 * @return
	 * @throws SQLException
	 */
	public synchronized SortedMap<Long, T> retrieveWithoutIds(
			final Collection<Long> ids) throws SQLException {
		if (ids.isEmpty()) {
			return new TreeMap<Long, T>();
		}
		final StringBuilder builder = new StringBuilder();
		builder.append("select * from " + getTableName() + " where "
				+ getIdColumnName() + " not in (");

		for (final long id : ids) {
			builder.append(id + ",");
		}

		builder.deleteCharAt(builder.length() - 1);
		builder.append(")");

		return retrieve(builder.toString());
	}

	/**
	 * retrieve elements NOT having one of the given id
	 * 
	 * @param ids
	 * @return
	 * @throws SQLException
	 */
	public synchronized SortedMap<Long, T> retrieveWithoutIds(long... ids)
			throws SQLException {
		final Set<Long> idSet = new HashSet<Long>();
		for (final long id : ids) {
			idSet.add(id);
		}

		return retrieveWithoutIds(idSet);
	}

	/**
	 * build an instance of element from the given record
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	protected abstract T createElement(ResultSet rs) throws SQLException;

	/**
	 * get the name of the table
	 * 
	 * @return
	 */
	protected abstract String getTableName();

	/**
	 * get the name of the column having ids
	 * 
	 * @return
	 */
	protected abstract String getIdColumnName();

}
