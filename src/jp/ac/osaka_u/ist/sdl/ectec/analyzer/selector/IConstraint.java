package jp.ac.osaka_u.ist.sdl.ectec.analyzer.selector;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneGenealogyInfo;

/**
 * An interface represents a constraint for choosing clone genealogies
 * 
 * @author k-hotta
 * 
 */
public interface IConstraint {

	/**
	 * judge whether the given genealogy satifies this constraint
	 * 
	 * @param genealogy
	 * @return
	 */
	public boolean satisfy(final DBCloneGenealogyInfo genealogy);

}
