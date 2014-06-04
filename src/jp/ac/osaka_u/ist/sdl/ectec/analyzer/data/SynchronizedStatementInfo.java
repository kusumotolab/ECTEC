package jp.ac.osaka_u.ist.sdl.ectec.analyzer.data;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.BlockType;

import org.eclipse.jdt.core.dom.SynchronizedStatement;

/**
 * A class that represents synchronized statements
 * 
 * @author k-hotta
 * 
 */
public class SynchronizedStatementInfo extends BlockInfo<SynchronizedStatement> {

	public SynchronizedStatementInfo(long id, FileInfo ownerFile, CRD crd,
			CombinedRevisionInfo startCombinedRevision,
			CombinedRevisionInfo endCombinedRevision, int startLine,
			int endLine, int size, SynchronizedStatement node) {
		super(id, ownerFile, crd, startCombinedRevision, endCombinedRevision,
				startLine, endLine, size, BlockType.SYNCHRONIZED, node);
	}

}
