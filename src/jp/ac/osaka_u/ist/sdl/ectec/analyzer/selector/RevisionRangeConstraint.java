package jp.ac.osaka_u.ist.sdl.ectec.analyzer.selector;

import java.sql.SQLException;
import java.util.SortedMap;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.RevisionRetriever;

/**
 * A constraint for revisions of genelaogies
 * 
 * @author k-hotta
 * 
 */
public class RevisionRangeConstraint implements IConstraint {

	/**
	 * the retriever for revisions
	 */
	private final RevisionRetriever revisionRetriever;

	/**
	 * the id of the start revision
	 */
	private long startRevisionId;

	/**
	 * the id of the end revision
	 */
	private long endRevisionId;

	/**
	 * true if this constraint allows only genealogies that were born and died
	 * in the given range
	 */
	private boolean mustBeComprised;

	public RevisionRangeConstraint(final DBConnectionManager dbManager) {
		this.revisionRetriever = dbManager.getRevisionRetriever();
		this.startRevisionId = -1;
		this.endRevisionId = Long.MAX_VALUE;
		this.mustBeComprised = false;
	}

	/**
	 * set the start revision
	 * 
	 * @param identifier
	 */
	public void setStartRevision(final String identifier) {
		final DBRevisionInfo dbRevision = retrieveCorrespondingRevision(identifier);
		startRevisionId = dbRevision.getId();
	}

	/**
	 * remove the start revision from this constraint
	 */
	public void removeStartRevision() {
		startRevisionId = -1;
	}

	/**
	 * set the end revision
	 * 
	 * @param identifier
	 */
	public void setEndRevision(final String identifier) {
		final DBRevisionInfo dbRevision = retrieveCorrespondingRevision(identifier);
		endRevisionId = dbRevision.getId();
	}

	/**
	 * remove the end revision from this constraint
	 */
	public void removeEndRevision() {
		endRevisionId = Long.MAX_VALUE;
	}

	/**
	 * set the value of mustBeComprised
	 * 
	 * @param mustBeComprised
	 *            true if this constraint allows only genealogies that were born
	 *            and died in the given range, false otherwise
	 */
	public void setMustBeComprised(final boolean mustBeComprised) {
		this.mustBeComprised = mustBeComprised;
	}

	private DBRevisionInfo retrieveCorrespondingRevision(final String identifier) {
		try {
			final SortedMap<Long, DBRevisionInfo> retrieved = revisionRetriever
					.retrieveWithIdentifier(identifier);

			if (retrieved.isEmpty()) {
				return null;
			}

			return retrieved.get(retrieved.firstKey());
		} catch (SQLException e) {
			return null;
		}
	}

	@Override
	public boolean satisfy(DBCloneGenealogyInfo genealogy) {
		final long targetStartRevisionId = genealogy.getStartRevisionId();
		final long targetEndRevisionId = genealogy.getEndRevisionId();

		if (mustBeComprised) {
			return (startRevisionId <= targetStartRevisionId && endRevisionId >= targetEndRevisionId);
		} else {
			return (startRevisionId <= targetStartRevisionId || endRevisionId >= targetEndRevisionId);
		}
	}
}
