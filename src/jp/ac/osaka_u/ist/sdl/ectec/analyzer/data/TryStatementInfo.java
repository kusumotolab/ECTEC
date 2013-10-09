package jp.ac.osaka_u.ist.sdl.ectec.analyzer.data;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.BlockType;

import org.eclipse.jdt.core.dom.TryStatement;

/**
 * A class that represents try statements
 * 
 * @author k-hotta
 * 
 */
public class TryStatementInfo extends BlockInfo<TryStatement> {

	public TryStatementInfo(long id, FileInfo ownerFile, CRD crd,
			RevisionInfo startRevision, RevisionInfo endRevision,
			int startLine, int endLine, int size, TryStatement node) {
		super(id, ownerFile, crd, startRevision, endRevision, startLine,
				endLine, size, BlockType.TRY, node);
	}

}
