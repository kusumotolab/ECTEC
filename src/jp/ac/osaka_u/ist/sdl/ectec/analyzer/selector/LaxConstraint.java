package jp.ac.osaka_u.ist.sdl.ectec.analyzer.selector;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneGenealogyInfo;

/**
 * A constraint that always returns true
 * 
 * @author k-hotta
 * 
 */
public class LaxConstraint implements IConstraint {

	@Override
	public boolean satisfy(DBCloneGenealogyInfo genealogy) {
		return true;
	}

}
