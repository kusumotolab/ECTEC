package jp.ac.osaka_u.ist.sdl.ectec.cdt.benchmark;

public abstract class ClonePair {

	/**
	 * the id
	 */
	private final int id;

	/**
	 * A fragment
	 */
	private final CloneFragment fragment1;

	/**
	 * The other fragment (fragment1 < fragment2 must be satisfied)
	 */
	private final CloneFragment fragment2;

	public ClonePair(final int id, final CloneFragment fragment,
			final CloneFragment anotherFragment) {
		this.id = id;

		if (fragment.compareTo(anotherFragment) < 0) {
			this.fragment1 = fragment;
			this.fragment2 = anotherFragment;
		} else {
			this.fragment1 = anotherFragment;
			this.fragment2 = fragment;
		}
	}

	public final int getId() {
		return id;
	}

	public final CloneFragment getFragment1() {
		return fragment1;
	}

	public final CloneFragment getFragment2() {
		return fragment2;
	}

}
