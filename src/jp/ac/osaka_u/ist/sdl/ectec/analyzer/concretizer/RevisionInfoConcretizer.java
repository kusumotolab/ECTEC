package jp.ac.osaka_u.ist.sdl.ectec.analyzer.concretizer;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.RevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRevisionInfo;

/**
 * A class for concretizing revisions
 * 
 * @author k-hotta
 * 
 */
public final class RevisionInfoConcretizer {

	/**
	 * concretize a revision
	 * 
	 * @param dbRevision
	 * @return
	 */
	public RevisionInfo concretize(final DBRevisionInfo dbRevision) {
		final long id = dbRevision.getId();
		final String identifier = dbRevision.getIdentifier();

		return new RevisionInfo(id, identifier);
	}

	/**
	 * concretize revisions
	 * 
	 * @param dbRevisions
	 * @return
	 */
	public Map<Long, RevisionInfo> concretizeAll(
			final Collection<DBRevisionInfo> dbRevisions) {
		final Map<Long, RevisionInfo> result = new TreeMap<Long, RevisionInfo>();

		for (final DBRevisionInfo dbRevision : dbRevisions) {
			final RevisionInfo concretizedRevision = concretize(dbRevision);
			result.put(concretizedRevision.getId(), concretizedRevision);
		}

		return Collections.unmodifiableMap(result);
	}

	/**
	 * concretize revisions
	 * 
	 * @param dbRevisions
	 * @return
	 */
	public Map<Long, RevisionInfo> concretizeAll(
			final Map<Long, DBRevisionInfo> dbRevisions) {
		return concretizeAll(dbRevisions.values());
	}

}
