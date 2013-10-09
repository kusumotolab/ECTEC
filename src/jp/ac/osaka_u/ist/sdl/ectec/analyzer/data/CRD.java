package jp.ac.osaka_u.ist.sdl.ectec.analyzer.data;

import java.util.Collections;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.BlockType;

/**
 * A class that represents CRD
 * 
 * @author k-hotta
 * 
 */
public class CRD extends AbstractElement implements Comparable<CRD> {

	/**
	 * the type of block
	 */
	private final BlockType blockType;

	/**
	 * the header
	 */
	private final String head;

	/**
	 * the anchor
	 */
	private final String anchor;

	/**
	 * the normalized anchor
	 */
	private final String normalizedAnchor;

	/**
	 * the value of metrics
	 */
	private final int cm;

	/**
	 * the list of ancestor crds of this crd
	 */
	private final List<CRD> ancestors;

	/**
	 * the full representation of this crd
	 */
	private final String fullText;

	public CRD(final long id, final BlockType blockType, final String head,
			final String anchor, final String normalizedAnchor, final int cm,
			final List<CRD> ancestors, final String fullText) {
		super(id);
		this.blockType = blockType;
		this.head = head;
		this.anchor = anchor;
		this.normalizedAnchor = normalizedAnchor;
		this.cm = cm;
		this.ancestors = ancestors;
		this.fullText = fullText;
	}

	/**
	 * get the type of the block
	 * 
	 * @return
	 */
	public final BlockType getBlockType() {
		return blockType;
	}

	/**
	 * get the header string
	 * 
	 * @return
	 */
	public final String getHead() {
		return head;
	}

	/**
	 * get the anchor string
	 * 
	 * @return
	 */
	public final String getAnchor() {
		return anchor;
	}

	/**
	 * get the normalized anchor
	 * 
	 * @return
	 */
	public final String getNormalizedAnchor() {
		return normalizedAnchor;
	}

	/**
	 * get the value of metrics
	 * 
	 * @return
	 */
	public final int getCm() {
		return cm;
	}

	/**
	 * get the list of ancestors
	 * 
	 * @return
	 */
	public final List<CRD> getAncestors() {
		return Collections.unmodifiableList(ancestors);
	}

	/**
	 * get the full representation
	 * 
	 * @return
	 */
	public final String getFullText() {
		return fullText;
	}

	@Override
	public int compareTo(CRD another) {
		return ((Long) this.id).compareTo(another.getId());
	}

}
