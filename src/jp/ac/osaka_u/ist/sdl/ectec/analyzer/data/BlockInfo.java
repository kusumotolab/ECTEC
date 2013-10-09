package jp.ac.osaka_u.ist.sdl.ectec.analyzer.data;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.BlockType;

import org.eclipse.jdt.core.dom.ASTNode;

/**
 * A class that represents block
 * 
 * @author k-hotta
 * 
 */
public abstract class BlockInfo extends CodeFragmentInfo {

	/**
	 * the type of this block
	 */
	protected final BlockType blockType;

	public BlockInfo(long id, FileInfo ownerFile, final CRD crd,
			RevisionInfo startRevision, RevisionInfo endRevision,
			int startLine, int endLine, int size, final BlockType blockType) {
		super(id, ownerFile, crd, startRevision, endRevision, startLine,
				endLine, size);
		this.blockType = blockType;
	}

	/**
	 * get the type of this block
	 * 
	 * @return
	 */
	public final BlockType getBlockType() {
		return blockType;
	}

	/**
	 * get the ast node
	 * 
	 * @return
	 */
	public abstract ASTNode getNode();

}
