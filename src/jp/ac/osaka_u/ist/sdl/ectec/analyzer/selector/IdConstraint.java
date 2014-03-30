package jp.ac.osaka_u.ist.sdl.ectec.analyzer.selector;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneGenealogyInfo;

/**
 * A constraint for revisions of genelaogies
 * 
 * @author k-hotta
 * 
 */
public class IdConstraint implements IConstraint {

	private long startId;

	private long endId;

	public IdConstraint() {
		this.startId = -1;
		this.endId = Long.MAX_VALUE;
	}

	public void setStartId(final long id) {
		this.startId = id;
	}

	public void setEndId(final long id) {
		endId = id;
	}

	@Override
	public boolean satisfy(DBCloneGenealogyInfo genealogy) {
		final long targetId = genealogy.getId();

		return (startId <= targetId) && (targetId <= endId);
	}
}
