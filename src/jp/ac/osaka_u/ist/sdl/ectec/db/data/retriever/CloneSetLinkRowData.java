package jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever;

/**
 * A class that represents rows in the table of clone set links
 * 
 * @author k-hotta
 * 
 */
class CloneSetLinkRowData extends AbstractRowData {

	private final long beforeElementId;

	private final long afterElementId;

	private final long beforeCombinedRevisionId;

	private final long afterCombinedRevisionId;

	private final long codeFragmentLinkId;

	CloneSetLinkRowData(final long id, final long beforeElementId,
			final long afterElementId, final long beforeCombinedRevisionId,
			final long afterCombinedRevisionId, final long codeFragmentLinkId) {
		super(id);
		this.beforeElementId = beforeElementId;
		this.afterElementId = afterElementId;
		this.beforeCombinedRevisionId = beforeCombinedRevisionId;
		this.afterCombinedRevisionId = afterCombinedRevisionId;
		this.codeFragmentLinkId = codeFragmentLinkId;
	}

	final long getBeforeElementId() {
		return beforeElementId;
	}

	final long getAfterElementId() {
		return afterElementId;
	}

	final long getBeforeCombinedRevisionId() {
		return beforeCombinedRevisionId;
	}

	final long getAfterCombinedRevisionId() {
		return afterCombinedRevisionId;
	}

	final long getCodeFragmentLinkId() {
		return codeFragmentLinkId;
	}

}
