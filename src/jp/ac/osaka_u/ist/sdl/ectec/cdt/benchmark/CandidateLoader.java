package jp.ac.osaka_u.ist.sdl.ectec.cdt.benchmark;

public class CandidateLoader extends CloneDataLoader<CloneCandidate> {

	@Override
	protected CloneCandidate createInstance(int id, CloneFragment fragment1,
			CloneFragment fragment2) {
		return new CloneCandidate(id, fragment1, fragment2);
	}

}
