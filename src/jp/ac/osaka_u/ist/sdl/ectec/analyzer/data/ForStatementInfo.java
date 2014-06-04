package jp.ac.osaka_u.ist.sdl.ectec.analyzer.data;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.BlockType;

import org.eclipse.jdt.core.dom.ForStatement;

/**
 * A class that represents for statements
 * 
 * @author k-hotta
 * 
 */
public class ForStatementInfo extends BlockInfo<ForStatement> {

	public ForStatementInfo(long id, FileInfo ownerFile, CRD crd,
			CombinedRevisionInfo startCombinedRevision,
			CombinedRevisionInfo endCombinedRevision, int startLine,
			int endLine, int size, ForStatement node) {
		super(id, ownerFile, crd, startCombinedRevision, endCombinedRevision,
				startLine, endLine, size, BlockType.FOR, node);
	}

}
