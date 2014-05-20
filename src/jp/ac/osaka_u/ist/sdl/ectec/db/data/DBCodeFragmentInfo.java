package jp.ac.osaka_u.ist.sdl.ectec.db.data;

import java.util.concurrent.atomic.AtomicLong;

/**
 * A class that represents a code fragment <br>
 * (which is a block in the default clone detection)
 * 
 * @author k-hotta
 * 
 */
public class DBCodeFragmentInfo extends AbstractDBElement implements
		Comparable<DBCodeFragmentInfo> {

	/**
	 * a counter to keep the number of created elements
	 */
	private static AtomicLong count = new AtomicLong(0);

	/**
	 * the id of the owner file of this fragment
	 */
	private final long ownerFileId;

	/**
	 * the id of the owner repository
	 */
	private final long ownerRepositoryId;

	/**
	 * the id of the crd for this fragment
	 */
	private final long crdId;

	/**
	 * the id of the start combined revision
	 */
	private final long startCombinedRevisionId;

	/**
	 * the id of the end combined revision
	 */
	private final long endCombinedRevisionId;

	/**
	 * the hash value created from this fragment
	 */
	private final long hash;

	/**
	 * the hash value used to detect clones
	 */
	private final long hashForClone;

	/**
	 * the start line of this fragment
	 */
	private final int startLine;

	/**
	 * the end line of this fragment
	 */
	private final int endLine;

	/**
	 * the size of this fragment
	 */
	private final int size;

	/**
	 * whether the owner file of this fragment was added at the start
	 */
	private final boolean fileAddedAtStart;

	/**
	 * whether the owner file of this fragment was deleted at the end
	 */
	private final boolean fileDeletedAtEnd;

	/**
	 * the constructor for elements that are retrieved from the db
	 * 
	 * @param id
	 * @param ownerFileId
	 * @param ownerRepositoryId
	 * @param crdId
	 * @param startCombinedRevisionId
	 * @param endCombinedRevisionId
	 * @param hash
	 * @param hashForClone
	 * @param startLine
	 * @param endLine
	 * @param size
	 * @param fileAddedAtStart
	 * @param fileDeletedAtEnd
	 */
	public DBCodeFragmentInfo(final long id, final long ownerFileId,
			final long ownerRepositoryId, final long crdId,
			final long startCombinedRevisionId,
			final long endCombinedRevisionId, final long hash,
			final long hashForClone, final int startLine, final int endLine,
			final int size, final boolean fileAddedAtStart,
			final boolean fileDeletedAtEnd) {
		super(id);
		this.ownerFileId = ownerFileId;
		this.ownerRepositoryId = ownerRepositoryId;
		this.crdId = crdId;
		this.startCombinedRevisionId = startCombinedRevisionId;
		this.endCombinedRevisionId = endCombinedRevisionId;
		this.hash = hash;
		this.hashForClone = hashForClone;
		this.startLine = startLine;
		this.endLine = endLine;
		this.size = size;
		this.fileAddedAtStart = fileAddedAtStart;
		this.fileDeletedAtEnd = fileDeletedAtEnd;
	}

	/**
	 * the constructor for newly created elements
	 * 
	 * @param ownerFileId
	 * @param ownerRepositoryId
	 * @param crdId
	 * @param startCombinedRevisionId
	 * @param endCombinedRevisionId
	 * @param hash
	 * @param hashForClone
	 * @param startLine
	 * @param endLine
	 * @param size
	 * @param fileAddedAtStart
	 * @param fileDeletedAtEnd
	 */
	public DBCodeFragmentInfo(final long ownerFileId,
			final long ownerRepositoryId, final long crdId,
			final long startCombinedRevisionId,
			final long endCombinedRevisionId, final long hash,
			final long hashForClone, final int startLine, final int endLine,
			final int size, final boolean fileAddedAtStart,
			final boolean fileDeletedAtEnd) {
		this(count.getAndIncrement(), ownerFileId, ownerRepositoryId, crdId,
				startCombinedRevisionId, endCombinedRevisionId, hash,
				hashForClone, startLine, endLine, size, fileAddedAtStart,
				fileDeletedAtEnd);
	}

	/**
	 * reset the count with the given long value
	 * 
	 * @param l
	 */
	public static void resetCount(final long l) {
		count = new AtomicLong(l);
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
	 * get the id of the owner repository of this fragment
	 * 
	 * @return
	 */
	public final long getOwnerRepositoryId() {
		return this.ownerRepositoryId;
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
	 * get the id of the start combined revision
	 * 
	 * @return
	 */
	public final long getStartCombinedRevisionId() {
		return this.startCombinedRevisionId;
	}

	/**
	 * get the id of the end combined revision
	 * 
	 * @return
	 */
	public final long getEndCombinedRevisionId() {
		return this.endCombinedRevisionId;
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
	 * get the hash value for clone detection
	 * 
	 * @return
	 */
	public final long getHashForClone() {
		return this.hashForClone;
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

	/**
	 * get the size
	 * 
	 * @return
	 */
	public final int getSize() {
		return this.size;
	}

	/**
	 * get whether the owner file of this fragment was added at the start
	 * 
	 * @return
	 */
	public final boolean isFileAddedAtStart() {
		return this.fileAddedAtStart;
	}

	/**
	 * get whether the owner file of this fragment was deleted at the end
	 * 
	 * @return
	 */
	public final boolean isFileDeletedAtEnd() {
		return this.fileDeletedAtEnd;
	}

	@Override
	public int compareTo(DBCodeFragmentInfo another) {
		return ((Long) this.getId()).compareTo(another.getId());
	}

}
