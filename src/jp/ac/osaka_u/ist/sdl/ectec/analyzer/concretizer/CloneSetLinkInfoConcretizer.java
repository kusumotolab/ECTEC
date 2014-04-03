package jp.ac.osaka_u.ist.sdl.ectec.analyzer.concretizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CloneSetInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CloneSetLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CodeFragmentLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.RevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetLinkInfo;

/**
 * A class for concretizing clone set links
 * 
 * @author k-hotta
 * 
 */
public class CloneSetLinkInfoConcretizer {

	public CloneSetLinkInfo concretize(final DBCloneSetLinkInfo dbCloneLink,
			final Map<Long, RevisionInfo> revisions,
			final Map<Long, CloneSetInfo> clones,
			final Map<Long, CodeFragmentLinkInfo> fragmentLinks) {
		final long id = dbCloneLink.getId();
		final RevisionInfo beforeRevision = revisions.get(dbCloneLink
				.getBeforeCombinedRevisionId());
		final RevisionInfo afterRevision = revisions.get(dbCloneLink
				.getAfterCombinedRevisionId());
		final CloneSetInfo beforeClone = clones.get(dbCloneLink
				.getBeforeElementId());
		final CloneSetInfo afterClone = clones.get(dbCloneLink
				.getAfterElementId());
		final int numberOfAddedElements = dbCloneLink
				.getNumberOfAddedElements();
		final int numberOfDeletedElements = dbCloneLink
				.getNumberOfDeletedElements();
		final int numberOfChangedElements = dbCloneLink
				.getNumberOfChangedElements();
		final int numberOfCoChangedElements = dbCloneLink
				.getNumberOfCoChangedElements();

		final List<CodeFragmentLinkInfo> fragmentLinksList = new ArrayList<CodeFragmentLinkInfo>();
		final List<Long> fragmentLinkIds = dbCloneLink.getCodeFragmentLinks();
		for (final long fragmentLinkId : fragmentLinkIds) {
			fragmentLinksList.add(fragmentLinks.get(fragmentLinkId));
		}

		return new CloneSetLinkInfo(id, beforeRevision, afterRevision,
				beforeClone, afterClone, numberOfAddedElements,
				numberOfDeletedElements, numberOfChangedElements,
				numberOfCoChangedElements, fragmentLinksList);
	}

	public Map<Long, CloneSetLinkInfo> concretizeAll(
			final Collection<DBCloneSetLinkInfo> dbCloneLinks,
			final Map<Long, RevisionInfo> revisions,
			final Map<Long, CloneSetInfo> clones,
			final Map<Long, CodeFragmentLinkInfo> fragmentLinks) {
		final Map<Long, CloneSetLinkInfo> result = new TreeMap<Long, CloneSetLinkInfo>();

		for (final DBCloneSetLinkInfo dbCloneLink : dbCloneLinks) {
			final CloneSetLinkInfo cloneLink = concretize(dbCloneLink,
					revisions, clones, fragmentLinks);
			result.put(cloneLink.getId(), cloneLink);
		}

		return Collections.unmodifiableMap(result);
	}

	public Map<Long, CloneSetLinkInfo> concretizeAll(
			final Map<Long, DBCloneSetLinkInfo> dbCloneLinks,
			final Map<Long, RevisionInfo> revisions,
			final Map<Long, CloneSetInfo> clones,
			final Map<Long, CodeFragmentLinkInfo> fragmentLinks) {
		return concretizeAll(dbCloneLinks.values(), revisions, clones,
				fragmentLinks);
	}

}
