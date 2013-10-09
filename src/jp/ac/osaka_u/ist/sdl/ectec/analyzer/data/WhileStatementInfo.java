package jp.ac.osaka_u.ist.sdl.ectec.analyzer.data;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.BlockType;

import org.eclipse.jdt.core.dom.WhileStatement;

/**
 * A class that represents while statements
 * 
 * @author k-hotta
 * 
 */
public class WhileStatementInfo extends BlockInfo<WhileStatement> {

	public WhileStatementInfo(long id, FileInfo ownerFile, CRD crd,
			RevisionInfo startRevision, RevisionInfo endRevision,
			int startLine, int endLine, int size, WhileStatement node) {
		super(id, ownerFile, crd, startRevision, endRevision, startLine,
				endLine, size, BlockType.WHILE, node);
	}

}
