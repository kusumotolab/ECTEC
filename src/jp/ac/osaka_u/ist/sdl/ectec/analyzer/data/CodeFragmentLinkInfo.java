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
	 * the before combined revision
	 */
	private final CombinedRevisionInfo beforeCombinedRevision;

	/**
	 * the after combined revision
	 */
	private final CombinedRevisionInfo afterCombinedRevision;

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
			final CombinedRevisionInfo beforeCombinedRevision,
			final CombinedRevisionInfo afterCombinedRevision,
			final CodeFragmentInfo beforeFragment,
			final CodeFragmentInfo afterFragment, final boolean changed) {
		super(id);
		this.beforeCombinedRevision = beforeCombinedRevision;
		this.afterCombinedRevision = afterCombinedRevision;
		this.beforeFragment = beforeFragment;
		this.afterFragment = afterFragment;
		this.changed = changed;
	}

	/**
	 * get the before combined revision
	 * 
	 * @return
	 */
	public final CombinedRevisionInfo getBeforeCombinedRevision() {
		return beforeCombinedRevision;
	}

	/**
	 * get the after combined revision
	 * 
	 * @return
	 */
	public final CombinedRevisionInfo getAfterCombinedRevision() {
		return afterCombinedRevision;
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
		final int compareWithBeforeRev = beforeCombinedRevision
				.compareTo(another.getBeforeCombinedRevision());
		if (compareWithBeforeRev != 0) {
			return compareWithBeforeRev;
		}

		final int compareWithAfterRev = afterCombinedRevision.compareTo(another
				.getAfterCombinedRevision());
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
