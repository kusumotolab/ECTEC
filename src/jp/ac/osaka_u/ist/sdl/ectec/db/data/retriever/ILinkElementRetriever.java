package jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever;

import java.sql.SQLException;
import java.util.Collection;
import java.util.SortedMap;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.AbstractDBElementLinkInfo;

public interface ILinkElementRetriever<L extends AbstractDBElementLinkInfo> {

	public SortedMap<Long, L> retrieveElementsWithBeforeCombinedRevision(
			final long beforeRevisionId) throws SQLException;

	public SortedMap<Long, L> retrieveElementsWithAfterCombinedRevision(
			final long afterRevisionId) throws SQLException;

	public SortedMap<Long, L> retrieveWithIds(final Collection<Long> ids)
			throws SQLException;

}
