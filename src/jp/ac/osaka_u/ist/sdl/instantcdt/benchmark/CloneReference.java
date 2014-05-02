package jp.ac.osaka_u.ist.sdl.instantcdt.benchmark;

public class CloneReference extends BenchmarkClonePair implements
		Comparable<CloneReference> {

	public CloneReference(int id, BenchmarkCloneFragment fragment,
			BenchmarkCloneFragment anotherFragment) {
		super(id, fragment, anotherFragment);
	}

	@Override
	public int compareTo(CloneReference o) {
		return ((Integer) this.getId()).compareTo(o.getId());
	}

}
