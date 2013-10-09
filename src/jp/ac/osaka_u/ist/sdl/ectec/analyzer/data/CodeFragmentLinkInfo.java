package jp.ac.osaka_u.ist.sdl.ectec.analyzer.data;

/**
 * A class that represents links of code fragments
 * 
 * @author k-hotta
 * 
 */
public class CodeFragmentLinkInfo extends AbstractElement implements
		Comparable<CodeFragmentLinkInfo> {

	/**
	 * the before revision
	 */
	private final RevisionInfo beforeRevision;

	/**
	 * the after revision
	 */
	private final RevisionInfo afterRevision;

	/**
	 * the before fragment
	 */
	private final CodeFragmentInfo beforeFragment;

	/**
	 * the after fragment
	 */
	private final CodeFragmentInfo afterFragment;

	/**
	 * true if the code fragment was changed
	 */
	private final boolean changed;

	public CodeFragmentLinkInfo(final long id,
			final RevisionInfo beforeRevision,
			final RevisionInfo afterRevision,
			final CodeFragmentInfo beforeFragment,
			final CodeFragmentInfo afterFragment, final boolean changed) {
		super(id);
		this.beforeRevision = beforeRevision;
		this.afterRevision = afterRevision;
		this.beforeFragment = beforeFragment;
		this.afterFragment = afterFragment;
		this.changed = changed;
	}

	/**
	 * get the before revision
	 * 
	 * @return
	 */
	public final RevisionInfo getBeforeRevision() {
		return beforeRevision;
	}

	/**
	 * get the after revision
	 * 
	 * @return
	 */
	public final RevisionInfo getAfterRevision() {
		return afterRevision;
	}

	/**
	 * get the before fragment
	 * 
	 * @return
	 */
	public final CodeFragmentInfo getBeforeFragment() {
		return beforeFragment;
	}

	/**
	 * get the after fragment
	 * 
	 * @return
	 */
	public final CodeFragmentInfo getAfterFragment() {
		return afterFragment;
	}

	/**
	 * is the fragment was changed?
	 * 
	 * @return
	 */
	public final boolean isChanged() {
		return changed;
	}

	@Override
	public int compareTo(CodeFragmentLinkInfo another) {
		final int compareWithBeforeRev = beforeRevision.compareTo(another
				.getBeforeRevision());
		if (compareWithBeforeRev != 0) {
			return compareWithBeforeRev;
		}

		final int compareWithAfterRev = afterRevision.compareTo(another
				.getAfterRevision());
		if (compareWithAfterRev != 0) {
			return compareWithAfterRev;
		}

		final int compareWithBeforeFragment = beforeFragment.compareTo(another
				.getBeforeFragment());
		if (compareWithBeforeFragment != 0) {
			return compareWithBeforeFragment;
		}

		final int compareWithAfterFragment = afterFragment.compareTo(another
				.getAfterFragment());
		if (compareWithAfterFragment != 0) {
			return compareWithAfterFragment;
		}

		return ((Long) id).compareTo(another.getId());
	}

}
