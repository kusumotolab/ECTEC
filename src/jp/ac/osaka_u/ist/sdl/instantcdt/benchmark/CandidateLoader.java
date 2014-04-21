package jp.ac.osaka_u.ist.sdl.instantcdt.benchmark;


public class CandidateLoader extends CloneDataLoader<CloneCandidate> {

	@Override
	protected CloneCandidate createInstance(int id, BenchmarkCloneFragment fragment1,
			BenchmarkCloneFragment fragment2) {
		return new CloneCandidate(id, fragment1, fragment2);
	}

}
