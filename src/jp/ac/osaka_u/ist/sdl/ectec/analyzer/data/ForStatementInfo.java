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
			RevisionInfo startRevision, RevisionInfo endRevision,
			int startLine, int endLine, int size, ForStatement node) {
		super(id, ownerFile, crd, startRevision, endRevision, startLine,
				endLine, size, BlockType.FOR, node);
	}

}
