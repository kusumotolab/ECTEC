package jp.ac.osaka_u.ist.sdl.ectec.cdt;

import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class CloneSet {

	private static final AtomicLong count = new AtomicLong(0);

	private final long id;

	private final Set<InstantCodeFragmentInfo> elements;

	public CloneSet(final Set<InstantCodeFragmentInfo> elements) {
		this.id = count.getAndIncrement();
		this.elements = elements;
	}

	public static final AtomicLong getCount() {
		return count;
	}

	public final long getId() {
		return id;
	}

	public final Set<InstantCodeFragmentInfo> getElements() {
		return elements;
	}

}
