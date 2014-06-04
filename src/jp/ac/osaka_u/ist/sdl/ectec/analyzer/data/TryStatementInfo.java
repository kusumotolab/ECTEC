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
			CombinedRevisionInfo startCombinedRevision,
			CombinedRevisionInfo endCombinedRevision, int startLine,
			int endLine, int size, TryStatement node) {
		super(id, ownerFile, crd, startCombinedRevision, endCombinedRevision,
				startLine, endLine, size, BlockType.TRY, node);
	}

}
