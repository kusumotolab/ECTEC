package jp.ac.osaka_u.ist.sdl.ectec.analyzer.data;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.BlockType;

import org.eclipse.jdt.core.dom.ASTNode;

/**
 * A class that represents block
 * 
 * @author k-hotta
 * 
 */
public abstract class BlockInfo<T extends ASTNode> extends CodeFragmentInfo {

	/**
	 * the type of this block
	 */
	protected final BlockType blockType;

	/**
	 * the root node of ast for this block
	 */
	protected final T node;

	public BlockInfo(long id, FileInfo ownerFile, final CRD crd,
			CombinedRevisionInfo startCombinedRevision,
			CombinedRevisionInfo endCombinedRevision, int startLine,
			int endLine, int size, final BlockType blockType, final T node) {
		super(id, ownerFile, crd, startCombinedRevision, endCombinedRevision,
				startLine, endLine, size);
		this.blockType = blockType;
		this.node = node;
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
	public T getNode() {
		return node;
	}

}
