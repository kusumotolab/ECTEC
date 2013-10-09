package jp.ac.osaka_u.ist.sdl.ectec.analyzer.data;

import java.util.Collections;
import java.util.List;

/**
 * A class that represents clone sets
 * 
 * @author k-hotta
 * 
 */
public class CloneSetInfo extends AbstractElement implements
		Comparable<CloneSetInfo> {

	/**
	 * the owner revision
	 */
	private final RevisionInfo revision;

	/**
	 * the list of code fragments
	 */
	private final List<CodeFragmentInfo> elements;

	public CloneSetInfo(final long id, final RevisionInfo revision,
			final List<CodeFragmentInfo> elements) {
		super(id);
		this.revision = revision;
		this.elements = elements;
	}

	/**
	 * get the revision
	 * 
	 * @return
	 */
	public final RevisionInfo getRevision() {
		return revision;
	}

	/**
	 * get the list of fragments
	 * 
	 * @return
	 */
	public final List<CodeFragmentInfo> getElements() {
		return Collections.unmodifiableList(elements);
	}

	@Override
	public int compareTo(CloneSetInfo another) {
		final int compareWithRevision = revision.compareTo(another
				.getRevision());
		if (compareWithRevision != 0) {
			return compareWithRevision;
		}

		return ((Long) id).compareTo(another.getId());
	}

}
