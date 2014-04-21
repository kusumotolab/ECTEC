package jp.ac.osaka_u.ist.sdl.instantcdt;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;

public class CloneSet {

	private static final AtomicLong count = new AtomicLong(0);

	private final long id;

	private final SortedSet<InstantCodeFragmentInfo> elements;

	public CloneSet(final SortedSet<InstantCodeFragmentInfo> elements) {
		this.id = count.getAndIncrement();
		this.elements = elements;
	}

	public CloneSet(final Collection<InstantCodeFragmentInfo> elements) {
		this.id = count.getAndIncrement();
		this.elements = new TreeSet<InstantCodeFragmentInfo>();
		this.elements.addAll(elements);
	}

	public static final AtomicLong getCount() {
		return count;
	}

	public final long getId() {
		return id;
	}

	public final int getSize() {
		return elements.size();
	}

	public final SortedSet<InstantCodeFragmentInfo> getElements() {
		return elements;
	}

	public final InstantCodeFragmentInfo getFirstElement() {
		return elements.first();
	}

	public boolean subsume(final CloneSet another) {
		if (this.elements.size() != another.getSize()) {
			return false;
		}

		for (final InstantCodeFragmentInfo fragment1 : this.elements) {
			for (final InstantCodeFragmentInfo fragment2 : another
					.getElements()) {
				if (!fragment1.subsume(fragment2)) {
					return false;
				}
			}
		}

		return true;
	}

}
