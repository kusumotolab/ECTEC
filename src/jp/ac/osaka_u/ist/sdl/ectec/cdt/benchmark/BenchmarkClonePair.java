package jp.ac.osaka_u.ist.sdl.ectec.cdt.benchmark;

public class BenchmarkClonePair {

	/**
	 * the id
	 */
	private final int id;

	/**
	 * A fragment
	 */
	private final BenchmarkCloneFragment fragment1;

	/**
	 * The other fragment (fragment1 < fragment2 must be satisfied)
	 */
	private final BenchmarkCloneFragment fragment2;

	public BenchmarkClonePair(final int id, final BenchmarkCloneFragment fragment,
			final BenchmarkCloneFragment anotherFragment) {
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

	public final BenchmarkCloneFragment getFragment1() {
		return fragment1;
	}

	public final BenchmarkCloneFragment getFragment2() {
		return fragment2;
	}

}
