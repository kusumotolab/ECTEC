package jp.ac.osaka_u.ist.sdl.ectec.cdt;

import java.util.concurrent.atomic.AtomicLong;

public class ClonePair {

	private static final AtomicLong count = new AtomicLong(0);

	private final long id;

	private final InstantCodeFragmentInfo fragment1;

	private final InstantCodeFragmentInfo fragment2;

	public ClonePair(final InstantCodeFragmentInfo fragment1,
			final InstantCodeFragmentInfo fragment2) {
		this.id = count.getAndIncrement();
		this.fragment1 = fragment1;
		this.fragment2 = fragment2;
	}

	public final long getId() {
		return id;
	}

	public final InstantCodeFragmentInfo getFragment1() {
		return fragment1;
	}

	public final InstantCodeFragmentInfo getFragment2() {
		return fragment2;
	}

}
