package jp.ac.osaka_u.ist.sdl.ectec.analyzer.data;

/**
 * A class that represents a code fragment
 * 
 * @author k-hotta
 * 
 */
public class CodeFragmentInfo extends AbstractElement implements
		Comparable<CodeFragmentInfo> {

	/**
	 * the owner file of this fragment
	 */
	protected final FileInfo ownerFile;

	/**
	 * the crd of the owner block of this fragment
	 */
	protected final CRD crd;

	/**
	 * the start revision
	 */
	protected final RevisionInfo startRevision;

	/**
	 * the end revision
	 */
	protected final RevisionInfo endRevision;

	/**
	 * the start line
	 */
	protected final int startLine;

	/**
	 * the end line
	 */
	protected final int endLine;

	/**
	 * the size
	 */
	protected final int size;

	public CodeFragmentInfo(final long id, final FileInfo ownerFile,
			final CRD crd, final RevisionInfo startRevision,
			final RevisionInfo endRevision, final int startLine,
			final int endLine, final int size) {
		super(id);
		this.ownerFile = ownerFile;
		this.crd = crd;
		this.startRevision = startRevision;
		this.endRevision = endRevision;
		this.startLine = startLine;
		this.endLine = endLine;
		this.size = size;
	}

	/**
	 * get the owner file
	 * 
	 * @return
	 */
	public final FileInfo getOwnerFile() {
		return ownerFile;
	}

	/**
	 * get the crd
	 * 
	 * @return
	 */
	public final CRD getCrd() {
		return crd;
	}

	/**
	 * get the start revision
	 * 
	 * @return
	 */
	public final RevisionInfo getStartRevision() {
		return startRevision;
	}

	/**
	 * get the end revision
	 * 
	 * @return
	 */
	public final RevisionInfo getEndRevision() {
		return endRevision;
	}

	/**
	 * get the start line
	 * 
	 * @return
	 */
	public final int getStartLine() {
		return startLine;
	}

	/**
	 * get the end line
	 * 
	 * @return
	 */
	public final int getEndLine() {
		return endLine;
	}

	/**
	 * get the size
	 * 
	 * @return
	 */
	public final int getSize() {
		return size;
	}

	@Override
	public int compareTo(CodeFragmentInfo another) {
		final int compareWithFile = this.ownerFile.compareTo(another
				.getOwnerFile());
		if (compareWithFile != 0) {
			return compareWithFile;
		}

		final int compareWithLine = ((Integer) this.startLine)
				.compareTo(another.getStartLine());
		if (compareWithLine != 0) {
			return compareWithLine;
		}

		return ((Long) this.id).compareTo(another.getId());
	}

}
