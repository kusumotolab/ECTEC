package jp.ac.osaka_u.ist.sdl.ectec.analyzer.data;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.BlockType;

import org.eclipse.jdt.core.dom.IfStatement;

/**
 * A class that represents if statements
 * 
 * @author k-hotta
 * 
 */
public class IfStatementInfo extends BlockInfo<IfStatement> {

	public IfStatementInfo(long id, FileInfo ownerFile, CRD crd,
			RevisionInfo startRevision, RevisionInfo endRevision,
			int startLine, int endLine, int size, IfStatement node) {
		super(id, ownerFile, crd, startRevision, endRevision, startLine,
				endLine, size, BlockType.IF, node);
	}

}
