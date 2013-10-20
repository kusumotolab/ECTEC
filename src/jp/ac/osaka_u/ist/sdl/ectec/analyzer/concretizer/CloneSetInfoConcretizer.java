package jp.ac.osaka_u.ist.sdl.ectec.analyzer.concretizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CloneSetInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.RevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetInfo;

/**
 * A class for concretizing clone sets
 * 
 * @author k-hotta
 * 
 */
public class CloneSetInfoConcretizer {

	public CloneSetInfo concretize(final DBCloneSetInfo dbClone,
			final Map<Long, RevisionInfo> revisions,
			final Map<Long, CodeFragmentInfo> fragments) {
		final long id = dbClone.getId();
		final RevisionInfo revision = revisions.get(dbClone.getRevisionId());

		final List<CodeFragmentInfo> fragmentsList = new ArrayList<CodeFragmentInfo>();
		final List<Long> fragmentIds = dbClone.getElements();
		for (final long fragmentId : fragmentIds) {
			fragmentsList.add(fragments.get(fragmentId));
		}

		return new CloneSetInfo(id, revision, fragmentsList);
	}

	public Map<Long, CloneSetInfo> concretizeAll(
			final Collection<DBCloneSetInfo> dbClones,
			final Map<Long, RevisionInfo> revisions,
			final Map<Long, CodeFragmentInfo> fragments) {
		final Map<Long, CloneSetInfo> result = new TreeMap<Long, CloneSetInfo>();

		for (final DBCloneSetInfo dbClone : dbClones) {
			final CloneSetInfo clone = concretize(dbClone, revisions, fragments);
			result.put(clone.getId(), clone);
		}

		return Collections.unmodifiableMap(result);
	}

	public Map<Long, CloneSetInfo> concretizeAll(
			final Map<Long, DBCloneSetInfo> dbClones,
			final Map<Long, RevisionInfo> revisions,
			final Map<Long, CodeFragmentInfo> fragments) {
		return concretizeAll(dbClones.values(), revisions, fragments);
	}

}
