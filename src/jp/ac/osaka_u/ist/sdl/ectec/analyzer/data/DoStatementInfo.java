package jp.ac.osaka_u.ist.sdl.ectec.analyzer.data;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.BlockType;

import org.eclipse.jdt.core.dom.DoStatement;

/**
 * A class that represents do-while statements
 * 
 * @author k-hotta
 * 
 */
public class DoStatementInfo extends BlockInfo<DoStatement> {

	public DoStatementInfo(long id, FileInfo ownerFile, CRD crd,
			RevisionInfo startRevision, RevisionInfo endRevision,
			int startLine, int endLine, int size, DoStatement node) {
		super(id, ownerFile, crd, startRevision, endRevision, startLine,
				endLine, size, BlockType.DO, node);
	}

}
