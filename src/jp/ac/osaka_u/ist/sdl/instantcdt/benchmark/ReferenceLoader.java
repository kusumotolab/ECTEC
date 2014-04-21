package jp.ac.osaka_u.ist.sdl.instantcdt.benchmark;


public class ReferenceLoader extends CloneDataLoader<CloneReference> {

	@Override
	protected CloneReference createInstance(int id, BenchmarkCloneFragment fragment1,
			BenchmarkCloneFragment fragment2) {
		return new CloneReference(id, fragment1, fragment2);
	}

}
