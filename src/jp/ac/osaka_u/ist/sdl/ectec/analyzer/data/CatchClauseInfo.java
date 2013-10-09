package jp.ac.osaka_u.ist.sdl.ectec.analyzer.data;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.BlockType;

import org.eclipse.jdt.core.dom.CatchClause;

/**
 * A class that represents catch clauses
 * 
 * @author k-hotta
 * 
 */
public class CatchClauseInfo extends BlockInfo<CatchClause> {

	public CatchClauseInfo(long id, FileInfo ownerFile, CRD crd,
			RevisionInfo startRevision, RevisionInfo endRevision,
			int startLine, int endLine, int size, CatchClause node) {
		super(id, ownerFile, crd, startRevision, endRevision, startLine,
				endLine, size, BlockType.CATCH, node);
	}

}
