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

	public BenchmarkClonePair(final int id,
			final BenchmarkCloneFragment fragment,
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

	public final boolean subsume(final BenchmarkClonePair anotherPair) {
		final boolean subsume1 = this.fragment1.subsume(anotherPair
				.getFragment1());
		final boolean subsume2 = this.fragment2.subsume(anotherPair
				.getFragment2());
		
		return subsume1 && subsume2;
	}
	
	@Override
	public String toString() {
		return this.fragment1.toString() + "\n" + this.fragment2.toString();
	}

}
