package jp.ac.osaka_u.ist.sdl.ectec.analyzer.data;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.BlockType;

import org.eclipse.jdt.core.dom.Statement;

/**
 * A class that represents else statements
 * 
 * @author k-hotta
 * 
 */
public class ElseStatementInfo extends BlockInfo<Statement> {

	public ElseStatementInfo(long id, FileInfo ownerFile, CRD crd,
			RevisionInfo startRevision, RevisionInfo endRevision,
			int startLine, int endLine, int size, Statement node) {
		super(id, ownerFile, crd, startRevision, endRevision, startLine,
				endLine, size, BlockType.ELSE, node);
	}

}
