package jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever;

/**
 * A class that represents rows in the table of code fragment genealogies
 * 
 * @author k-hotta
 * 
 */
class CodeFragmentGenealogyRowData extends AbstractRowData {

	private final long startCombinedRevisionId;

	private final long endCombinedRevisionId;

//	private final long codeFragmentId;

	private final long codeFragmentLinkId;

	CodeFragmentGenealogyRowData(final long id,
			final long startCombinedRevisionId,
			final long endCombinedRevisionId, // final long codeFragmentId,
			final long codeFragmentLinkId) {
		super(id);
		this.startCombinedRevisionId = startCombinedRevisionId;
		this.endCombinedRevisionId = endCombinedRevisionId;
//		this.codeFragmentId = codeFragmentId;
		this.codeFragmentLinkId = codeFragmentLinkId;
	}

	final long getStartCombinedRevisionId() {
		return startCombinedRevisionId;
	}

	final long getEndCombinedRevisionId() {
		return endCombinedRevisionId;
	}

//	final long getCodeFragmentId() {
//		return codeFragmentId;
//	}

	final long getCodeFragmentLinkId() {
		return codeFragmentLinkId;
	}

}
