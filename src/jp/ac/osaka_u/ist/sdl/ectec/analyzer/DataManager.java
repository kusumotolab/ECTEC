package jp.ac.osaka_u.ist.sdl.ectec.analyzer;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.AbstractElement;

/**
 * A class that manages data concretized from the db
 * 
 * @author k-hotta
 * 
 */
public class DataManager<T extends AbstractElement> {

	/**
	 * the elements
	 */
	private final ConcurrentMap<Long, T> elements;

	/**
	 * the sorted elements
	 */
	private final SortedSet<T> sortedElements;

	public DataManager() {
		this.elements = new ConcurrentHashMap<Long, T>();
		this.sortedElements = new TreeSet<T>();
	}

	/**
	 * return all the elements concretized before the call
	 * 
	 * @return
	 */
	public final Map<Long, T> getElements() {
		return Collections.unmodifiableMap(elements);
	}

	/**
	 * return all the elements as a concurrent map
	 * 
	 * @return
	 */
	public final ConcurrentMap<Long, T> getConcurrentElements() {
		return elements;
	}

	/**
	 * return all the elements as a sorted set
	 * 
	 * @return
	 */
	public final SortedSet<T> getSortedElements() {
		return Collections.unmodifiableSortedSet(sortedElements);
	}

	/**
	 * return true if the element that has the specified id is concretized and
	 * contained
	 * 
	 * @param id
	 * @return
	 */
	public final boolean contains(final long id) {
		return elements.containsKey(id);
	}

	/**
	 * return true if the elements is concretized and contained <br>
	 * this method equals to call {@link DataManager#contains(long)} with
	 * {@link AbstractElement#getId()}
	 * 
	 * @param element
	 * @return
	 */
	public final boolean contains(final T element) {
		return contains(element.getId());
	}

	/**
	 * return true if all elements that have one of the specified IDs are
	 * concretized and contained
	 * 
	 * @param ids
	 * @return
	 */
	public final boolean containsAll(final Collection<Long> ids) {
		for (final long id : ids) {
			if (!contains(id)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * get the element that has the specified id
	 * 
	 * @param id
	 * @return
	 */
	public final T getElement(final long id) {
		return this.elements.get(id);
	}

	/**
	 * add the specified element
	 * 
	 * @param element
	 */
	public final void add(final T element) {
		this.elements.put(element.getId(), element);
		this.sortedElements.add(element);
	}

	/**
	 * add all the specified elements
	 * 
	 * @param elements
	 */
	public final void addAll(final Collection<T> elements) {
		for (final T element : elements) {
			add(element);
		}
	}

	/**
	 * remove the element that has the specified id
	 * 
	 * @param id
	 */
	public final void remove(final long id) {
		final T removedElement = this.elements.get(id);
		if (removedElement == null) {
			return;
		}

		this.elements.remove(id);
		this.sortedElements.remove(removedElement);
	}

	/**
	 * remove the specified element
	 * 
	 * @param element
	 */
	public final void remove(final T element) {
		this.elements.remove(element);
		this.sortedElements.remove(element);
	}

	/**
	 * remove all the specified elements
	 * 
	 * @param elements
	 */
	public final void removeAll(final Collection<T> elements) {
		for (final T element : elements) {
			remove(element);
		}
	}

	/**
	 * clear the manager (all elements that have been contained by this manager
	 * will be removed from this manager)
	 */
	public final void clear() {
		this.elements.clear();
		this.sortedElements.clear();
	}

}
