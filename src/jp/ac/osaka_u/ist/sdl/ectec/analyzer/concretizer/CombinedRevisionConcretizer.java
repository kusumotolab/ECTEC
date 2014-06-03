package jp.ac.osaka_u.ist.sdl.ectec.analyzer.concretizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CombinedRevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.RevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCombinedRevisionInfo;

/**
 * A class for concretizing combined revisions
 * 
 * @author k-hotta
 * 
 */
public class CombinedRevisionConcretizer {

	public CombinedRevisionInfo concretize(
			final DBCombinedRevisionInfo dbCombinedRevision,
			final Map<Long, RevisionInfo> revisions) {
		final long id = dbCombinedRevision.getId();
		final List<RevisionInfo> originalRevisions = new ArrayList<RevisionInfo>();
		final List<Long> originalRevisionIds = dbCombinedRevision
				.getOriginalRevisions();
		for (final long originalRevisionId : originalRevisionIds) {
			originalRevisions.add(revisions.get(originalRevisionId));
		}

		return new CombinedRevisionInfo(id, originalRevisions);
	}

	public Map<Long, CombinedRevisionInfo> concretizeAll(
			final Collection<DBCombinedRevisionInfo> dbCombinedRevisions,
			final Map<Long, RevisionInfo> revisions) {
		final Map<Long, CombinedRevisionInfo> result = new TreeMap<Long, CombinedRevisionInfo>();

		for (final DBCombinedRevisionInfo dbCombinedRevision : dbCombinedRevisions) {
			final CombinedRevisionInfo combinedRevision = concretize(
					dbCombinedRevision, revisions);
			result.put(combinedRevision.getId(), combinedRevision);
		}

		return Collections.unmodifiableMap(result);
	}

	public Map<Long, CombinedRevisionInfo> concretizeAll(
			final Map<Long, DBCombinedRevisionInfo> dbCombinedRevisions,
			final Map<Long, RevisionInfo> revisions) {
		return concretizeAll(dbCombinedRevisions.values(), revisions);
	}

}
