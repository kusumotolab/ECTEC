package jp.ac.osaka_u.ist.sdl.ectec.analyzer.data;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.BlockType;

import org.eclipse.jdt.core.dom.EnhancedForStatement;

/**
 * A class that represents for-each statements
 * 
 * @author k-hotta
 * 
 */
public class EnhancedForStatementInfo extends BlockInfo<EnhancedForStatement> {

	public EnhancedForStatementInfo(long id, FileInfo ownerFile, CRD crd,
			RevisionInfo startRevision, RevisionInfo endRevision,
			int startLine, int endLine, int size, EnhancedForStatement node) {
		super(id, ownerFile, crd, startRevision, endRevision, startLine,
				endLine, size, BlockType.ENHANCED_FOR, node);
	}

}
