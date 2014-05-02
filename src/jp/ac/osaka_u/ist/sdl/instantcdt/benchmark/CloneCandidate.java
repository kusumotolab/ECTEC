package jp.ac.osaka_u.ist.sdl.instantcdt.benchmark;

public class CloneCandidate extends BenchmarkClonePair implements
		Comparable<CloneCandidate> {

	public CloneCandidate(int id, BenchmarkCloneFragment fragment,
			BenchmarkCloneFragment anotherFragment) {
		super(id, fragment, anotherFragment);
	}

	@Override
	public int compareTo(CloneCandidate o) {
		return ((Integer) this.getId()).compareTo(o.getId());
	}

}
