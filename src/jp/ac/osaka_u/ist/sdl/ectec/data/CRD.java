package jp.ac.osaka_u.ist.sdl.ectec.data;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A class that represents a CRD for a block
 * 
 * @author k-hotta
 * 
 */
public class CRD extends AbstractElement {

	/**
	 * a counter to keep the number of created elements
	 */
	private static final AtomicLong count = new AtomicLong(0);

	/**
	 * the type of this block
	 */
	private final BlockType type;

	/**
	 * the string that is appended at the head of this crd
	 */
	private final String head;

	/**
	 * the anchor of this crd
	 */
	private final String anchor;

	/**
	 * the value of metrics
	 */
	private final int cm;

	/**
	 * the ids of crds that are the ancestors of this block
	 */
	private final List<Long> ancestors;

	/**
	 * A full textual representation of this crd
	 */
	private final String fullText;

	/**
	 * the constructor for elements that are retrieved from the db
	 * 
	 * @param id
	 * @param type
	 * @param head
	 * @param anchor
	 * @param cm
	 * @param ancestors
	 * @param fullText
	 */
	public CRD(final long id, final BlockType type, final String head,
			final String anchor, final int cm, final List<Long> ancestors,
			final String fullText) {
		super(id);
		this.type = type;
		this.head = head;
		this.anchor = anchor;
		this.cm = cm;
		this.ancestors = ancestors;
		this.fullText = fullText;
	}

	/**
	 * the constructor for newly created elements
	 * 
	 * @param type
	 * @param head
	 * @param anchor
	 * @param cm
	 * @param ancestors
	 * @param fullText
	 */
	public CRD(final BlockType type, final String head, final String anchor,
			final int cm, final List<Long> ancestors, final String fullText) {
		this(count.getAndIncrement(), type, head, anchor, cm, ancestors,
				fullText);
	}

	/**
	 * get the type of this block
	 * 
	 * @return
	 */
	public final BlockType getType() {
		return this.type;
	}

	/**
	 * the string that is appended at the head of this crd
	 * 
	 * @return
	 */
	public final String getHead() {
		return this.head;
	}

	/**
	 * get the anchor of this crd
	 * 
	 * @return
	 */
	public final String getAnchor() {
		return this.anchor;
	}

	/**
	 * get the value of metrics
	 * 
	 * @return
	 */
	public final int getCm() {
		return this.cm;
	}

	/**
	 * get the list of ids of the ancestors
	 * 
	 * @return
	 */
	public final List<Long> getAncestors() {
		return Collections.unmodifiableList(this.ancestors);
	}

	/**
	 * get the full text of this crd
	 * 
	 * @return
	 */
	public final String getFullText() {
		return this.fullText;
	}

	@Override
	public boolean equals(Object o) {
		if (o.getClass() != this.getClass()) {
			return false;
		}

		return ((CRD) o).getFullText().equals(this.getFullText());
	}

}
