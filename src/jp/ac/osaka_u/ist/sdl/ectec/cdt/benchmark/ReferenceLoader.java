package jp.ac.osaka_u.ist.sdl.ectec.cdt.benchmark;

import jp.ac.osaka_u.ist.sdl.ectec.cdt.CloneFragment;

public class ReferenceLoader extends CloneDataLoader<CloneReference> {

	@Override
	protected CloneReference createInstance(int id, CloneFragment fragment1,
			CloneFragment fragment2) {
		return new CloneReference(id, fragment1, fragment2);
	}

}
