package jp.ac.osaka_u.ist.sdl.ectec.analyzer.data;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.BlockType;

import org.eclipse.jdt.core.dom.Block;

/**
 * A class that represents finally blocks
 * 
 * @author k-hotta
 * 
 */
public class FinallyBlockInfo extends BlockInfo<Block> {

	public FinallyBlockInfo(long id, FileInfo ownerFile, CRD crd,
			RevisionInfo startRevision, RevisionInfo endRevision,
			int startLine, int endLine, int size, Block node) {
		super(id, ownerFile, crd, startRevision, endRevision, startLine,
				endLine, size, BlockType.FINALLY, node);
	}

}
