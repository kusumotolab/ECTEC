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

	public final boolean subsume(final ClonePair another) {
		return (this.fragment1.subsume(another.getFragment1()) && this.fragment2
				.subsume(another.getFragment2()))
				|| (this.fragment1.subsume(another.getFragment2()) && this.fragment2
						.subsume(another.getFragment1()));
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof ClonePair)) {
			return false;
		}

		final ClonePair another = (ClonePair) obj;

		if (this.fragment1.equals(another.fragment1)
				&& this.fragment2.equals(another.fragment2)) {
			return true;
		}

		if (this.fragment2.equals(another.getFragment1())
				&& this.fragment1.equals(another.getFragment2())) {
			return true;
		}

		return false;
	}

}
