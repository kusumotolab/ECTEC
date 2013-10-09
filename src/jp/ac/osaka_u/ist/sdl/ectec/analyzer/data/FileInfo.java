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
	 * the start revision
	 */
	private final RevisionInfo startRevision;

	/**
	 * the end revision
	 */
	private final RevisionInfo endRevision;

	/**
	 * the root node of AST for this file
	 */
	private final CompilationUnit node;

	public FileInfo(final long id, final String path,
			final RevisionInfo startRevision, final RevisionInfo endRevision,
			final CompilationUnit node) {
		super(id);
		this.path = path;
		this.startRevision = startRevision;
		this.endRevision = endRevision;
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
	 * get the start revision of this file
	 * 
	 * @return
	 */
	public final RevisionInfo getStartRevision() {
		return startRevision;
	}

	/**
	 * get the end revision of this file
	 * 
	 * @return
	 */
	public final RevisionInfo getEndRevision() {
		return endRevision;
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

		final int compareWithStartRev = this.startRevision.compareTo(another
				.getStartRevision());
		if (compareWithStartRev != 0) {
			return compareWithStartRev;
		}

		final int compareWithEndRev = this.endRevision.compareTo(another
				.getEndRevision());
		if (compareWithEndRev != 0) {
			return compareWithEndRev;
		}

		return ((Long) this.id).compareTo(another.getId());
	}

}
