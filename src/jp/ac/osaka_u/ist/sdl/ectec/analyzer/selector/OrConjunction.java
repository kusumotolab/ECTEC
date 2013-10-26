package jp.ac.osaka_u.ist.sdl.ectec.analyzer.selector;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneGenealogyInfo;

/**
 * A class that represents an or conjunction of two constraints
 * 
 * @author k-hotta
 * 
 */
public class OrConjunction implements IConstraint {

	private final IConstraint constraint1;

	private final IConstraint constraint2;

	public OrConjunction(final IConstraint constraint1,
			final IConstraint constraint2) {
		this.constraint1 = constraint1;
		this.constraint2 = constraint2;
	}

	@Override
	public boolean satisfy(DBCloneGenealogyInfo genealogy) {
		return constraint1.satisfy(genealogy) || constraint2.satisfy(genealogy);
	}

}
