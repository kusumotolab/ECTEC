package jp.ac.osaka_u.ist.sdl.ectec.analyzer.data;

import org.eclipse.jdt.core.dom.CompilationUnit;

/**
 * A class that represents file
 * 
 * @author k-hotta
 * 
 */
public class FileInfo extends AbstractElement implements Comparable<FileInfo> {

	/**
	 * the path of this file
	 */
	private final String path;

	/**
	 * the start combined revision
	 */
	private final CombinedRevisionInfo startCombinedRevision;

	/**
	 * the end combined revision
	 */
	private final CombinedRevisionInfo endCombinedRevision;

	/**
	 * the root node of AST for this file
	 */
	private final CompilationUnit node;

	public FileInfo(final long id, final String path,
			final CombinedRevisionInfo startCombinedRevision,
			final CombinedRevisionInfo endCombinedRevision,
			final CompilationUnit node) {
		super(id);
		this.path = path;
		this.startCombinedRevision = startCombinedRevision;
		this.endCombinedRevision = endCombinedRevision;
		this.node = node;
	}

	/**
	 * get the path of this file
	 * 
	 * @return
	 */
	public final String getPath() {
		return path;
	}

	/**
	 * get the start combined revision of this file
	 * 
	 * @return
	 */
	public final CombinedRevisionInfo getStartCombinedRevision() {
		return startCombinedRevision;
	}

	/**
	 * get the end combined revision of this file
	 * 
	 * @return
	 */
	public final CombinedRevisionInfo getEndCombinedRevision() {
		return endCombinedRevision;
	}

	/**
	 * get the root node of AST corresponds to this file
	 * 
	 * @return
	 */
	public final CompilationUnit getNode() {
		return node;
	}

	@Override
	public int compareTo(FileInfo another) {
		final int compareWithPath = this.path.compareTo(another.getPath());
		if (compareWithPath != 0) {
			return compareWithPath;
		}

		final int compareWithStartRev = this.startCombinedRevision
				.compareTo(another.getStartCombinedRevision());
		if (compareWithStartRev != 0) {
			return compareWithStartRev;
		}

		final int compareWithEndRev = this.endCombinedRevision
				.compareTo(another.getEndCombinedRevision());
		if (compareWithEndRev != 0) {
			return compareWithEndRev;
		}

		return ((Long) this.id).compareTo(another.getId());
	}

}
