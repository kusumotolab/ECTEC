package jp.ac.osaka_u.ist.sdl.ectec.data;

import java.util.concurrent.atomic.AtomicLong;

/**
 * A class that represents a code fragment <br>
 * (which is a block in the default clone detection)
 * 
 * @author k-hotta
 * 
 */
public class CodeFragmentInfo extends AbstractElement implements
		Comparable<CodeFragmentInfo> {

	/**
	 * a counter to keep the number of created elements
	 */
	private static final AtomicLong count = new AtomicLong(0);

	/**
	 * the id of the owner file of this fragment
	 */
	private final long ownerFileId;

	/**
	 * the id of the crd for this fragment
	 */
	private final long crdId;

	/**
	 * the id of the start revision
	 */
	private final long startRevisionId;

	/**
	 * the id of the end revision
	 */
	private final long endRevisionId;

	/**
	 * the hash value created from this fragment
	 */
	private final long hash;

	/**
	 * the start line of this fragment
	 */
	private final int startLine;

	/**
	 * the end line of this fragment
	 */
	private final int endLine;

	/**
	 * the constructor for elements that are retrieved from the db
	 * 
	 * @param id
	 * @param ownerFileId
	 * @param crdId
	 * @param startRevisionId
	 * @param endRevisionId
	 * @param hash
	 * @param startLine
	 * @param endLine
	 */
	public CodeFragmentInfo(final long id, final long ownerFileId,
			final long crdId, final long startRevisionId,
			final long endRevisionId, final long hash, final int startLine,
			final int endLine) {
		super(id);
		this.ownerFileId = ownerFileId;
		this.crdId = crdId;
		this.startRevisionId = startRevisionId;
		this.endRevisionId = endRevisionId;
		this.hash = hash;
		this.startLine = startLine;
		this.endLine = endLine;
	}

	/**
	 * the constructor for newly created elements
	 * 
	 * @param ownerFileId
	 * @param crdId
	 * @param startRevisionId
	 * @param endRevisionId
	 * @param hash
	 * @param startLine
	 * @param endLine
	 */
	public CodeFragmentInfo(final long ownerFileId, final long crdId,
			final long startRevisionId, final long endRevisionId,
			final long hash, final int startLine, final int endLine) {
		this(count.getAndIncrement(), ownerFileId, crdId, startRevisionId,
				endRevisionId, hash, startLine, endLine);
	}

	/**
	 * get the id of the owner file of this fragment
	 * 
	 * @return
	 */
	public final long getOwnerFileId() {
		return this.ownerFileId;
	}

	/**
	 * get the id of the crd for this fragment
	 * 
	 * @return
	 */
	public final long getCrdId() {
		return this.crdId;
	}

	/**
	 * get the id of the start revision
	 * 
	 * @return
	 */
	public final long getStartRevisionId() {
		return this.startRevisionId;
	}

	/**
	 * get the id of the end revision
	 * 
	 * @return
	 */
	public final long getEndRevisionId() {
		return this.endRevisionId;
	}

	/**
	 * get the hash value
	 * 
	 * @return
	 */
	public final long getHash() {
		return this.hash;
	}

	/**
	 * get the start line
	 * 
	 * @return
	 */
	public final int getStartLine() {
		return this.startLine;
	}

	/**
	 * get the end line
	 * 
	 * @return
	 */
	public final int getEndLine() {
		return this.endLine;
	}

	@Override
	public int compareTo(CodeFragmentInfo another) {
		return ((Long) this.getId()).compareTo(another.getId());
	}

}
