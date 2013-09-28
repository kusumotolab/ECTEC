package jp.ac.osaka_u.ist.sdl.ectec.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A class to represent a table.
 * 
 * @author k-hotta
 * 
 * @param <K1>
 *            the type of a key
 * @param <K2>
 *            the type of the other key
 * @param <V>
 *            the type of contents contained by this table
 */
public class Table<K1, K2, V> {

	/**
	 * contents of this table
	 */
	private final Map<K1, Map<K2, V>> contents;

	/**
	 * A constructor
	 */
	public Table() {
		this.contents = new ConcurrentHashMap<K1, Map<K2, V>>();
	}

	/**
	 * Judge whether the specified sell is filled with any value
	 * 
	 * @param k1
	 * @param k2
	 * @return
	 */
	public boolean containsValueAt(final K1 k1, final K2 k2) {
		if (!contents.containsKey(k1)) {
			return false;
		}

		return contents.get(k1).containsKey(k2);
	}

	/**
	 * Get the value of the specified cell
	 * 
	 * @param k1
	 * @param k2
	 * @return the value of the specified cell. return null if the specified
	 *         cell does not contain any value.
	 */
	public V getValueAt(final K1 k1, final K2 k2) {
		if (k1 == null || k2 == null) {
			throw new IllegalArgumentException("k1 and k2 must not be null");
		}

		if (!contents.containsKey(k1)) {
			return null;
		}
		return contents.get(k1).get(k2);
	}

	/**
	 * Get all the values of the specified row.
	 * 
	 * @param k1
	 * @return
	 */
	public Map<K2, V> getValuesAt(final K1 k1) {
		if (k1 == null) {
			throw new IllegalArgumentException("k1 must not be null");
		}

		if (!contents.containsKey(k1)) {
			return null;
		}
		return contents.get(k1);
	}

	/**
	 * Change the value of the specified cell
	 * 
	 * @param k1
	 * @param k2
	 * @param value
	 */
	public void changeValueAt(final K1 k1, final K2 k2, final V value) {
		if (k1 == null || k2 == null) {
			throw new IllegalArgumentException("k1 and k2 must not be null");
		}

		if (!contents.containsKey(k1)) {
			contents.put(k1, new ConcurrentHashMap<K2, V>());
		}

		final Map<K2, V> row = contents.get(k1);
		if (row.containsKey(k2)) {
			row.remove(k2);
		}

		row.put(k2, value);
	}

}
