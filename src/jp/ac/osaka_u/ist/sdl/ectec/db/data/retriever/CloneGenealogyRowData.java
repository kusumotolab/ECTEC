package jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever;

/**
 * A class that represents a row in the table of clone genealogies
 * 
 * @author k-hotta
 * 
 */
public class CloneGenealogyRowData extends AbstractRowData {

	private final long startCombinedRevisionId;

	private final long endCombinedRevisionId;

//	private final long cloneSetId;

	private final long cloneSetLinkId;

	CloneGenealogyRowData(final long id,
			final long startCombinedRevisionId,
			final long endCombinedRevisionId, // final long cloneSetId,
			final long cloneSetLinkId) {
		super(id);
		this.startCombinedRevisionId = startCombinedRevisionId;
		this.endCombinedRevisionId = endCombinedRevisionId;
//		this.cloneSetId = cloneSetId;
		this.cloneSetLinkId = cloneSetLinkId;
	}

	final long getStartCombinedRevisionId() {
		return startCombinedRevisionId;
	}

	final long getEndCombinedRevisionId() {
		return endCombinedRevisionId;
	}

//	final long getCloneSetId() {
//		return cloneSetId;
//	}

	final long getCloneSetLinkId() {
		return cloneSetLinkId;
	}

}
