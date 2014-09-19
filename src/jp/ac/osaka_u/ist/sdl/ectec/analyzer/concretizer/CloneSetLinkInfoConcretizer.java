package jp.ac.osaka_u.ist.sdl.ectec.analyzer.concretizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CloneSetInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CloneSetLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CodeFragmentLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CombinedRevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetLinkInfo;

/**
 * A class for concretizing clone set links
 * 
 * @author k-hotta
 * 
 */
public class CloneSetLinkInfoConcretizer {

	public CloneSetLinkInfo concretize(final DBCloneSetLinkInfo dbCloneLink,
			final Map<Long, CombinedRevisionInfo> combinedRevisions,
			final Map<Long, CloneSetInfo> clones,
			final Map<Long, CodeFragmentLinkInfo> fragmentLinks) {
		final long id = dbCloneLink.getId();
		final CombinedRevisionInfo beforeCombinedRevision = combinedRevisions
				.get(dbCloneLink.getBeforeCombinedRevisionId());
		final CombinedRevisionInfo afterCombinedRevision = combinedRevisions
				.get(dbCloneLink.getAfterCombinedRevisionId());
		final CloneSetInfo beforeClone = clones.get(dbCloneLink
				.getBeforeElementId());
		final CloneSetInfo afterClone = clones.get(dbCloneLink
				.getAfterElementId());

		final List<CodeFragmentLinkInfo> fragmentLinksList = new ArrayList<CodeFragmentLinkInfo>();
		final List<Long> fragmentLinkIds = dbCloneLink.getCodeFragmentLinks();
		for (final long fragmentLinkId : fragmentLinkIds) {
			fragmentLinksList.add(fragmentLinks.get(fragmentLinkId));
		}

		int numberOfChangedElements = 0;
		int numberOfCoChangedElements = 0;

		final Set<Long> beforeElements = new TreeSet<Long>();
		for (final CodeFragmentInfo beforeClonedFragment : beforeClone
				.getElements()) {
			beforeElements.add(beforeClonedFragment.getId());
		}
		final Set<Long> afterElements = new TreeSet<Long>();
		for (final CodeFragmentInfo afterClonedFragment : afterClone
				.getElements()) {
			afterElements.add(afterClonedFragment.getId());
		}

		final Set<Long> unchangedElements = new TreeSet<Long>(beforeElements);
		unchangedElements.retainAll(afterElements);

		for (final CodeFragmentLinkInfo fragmentLink : fragmentLinksList) {
			if (fragmentLink.isChanged()) {
				numberOfChangedElements++;
				numberOfCoChangedElements++;
			}
		}

		int numberOfAddedElements = afterElements.size()
				- unchangedElements.size() - fragmentLinksList.size();
		int numberOfDeletedElements = beforeElements.size()
				- unchangedElements.size() - fragmentLinksList.size();

		return new CloneSetLinkInfo(id, beforeCombinedRevision,
				afterCombinedRevision, beforeClone, afterClone,
				numberOfAddedElements, numberOfDeletedElements,
				numberOfChangedElements, numberOfCoChangedElements,
				fragmentLinksList);
	}

	public Map<Long, CloneSetLinkInfo> concretizeAll(
			final Collection<DBCloneSetLinkInfo> dbCloneLinks,
			final Map<Long, CombinedRevisionInfo> combinedRevisions,
			final Map<Long, CloneSetInfo> clones,
			final Map<Long, CodeFragmentLinkInfo> fragmentLinks) {
		final Map<Long, CloneSetLinkInfo> result = new TreeMap<Long, CloneSetLinkInfo>();

		for (final DBCloneSetLinkInfo dbCloneLink : dbCloneLinks) {
			final CloneSetLinkInfo cloneLink = concretize(dbCloneLink,
					combinedRevisions, clones, fragmentLinks);
			result.put(cloneLink.getId(), cloneLink);
		}

		return Collections.unmodifiableMap(result);
	}

	public Map<Long, CloneSetLinkInfo> concretizeAll(
			final Map<Long, DBCloneSetLinkInfo> dbCloneLinks,
			final Map<Long, CombinedRevisionInfo> combinedRevisions,
			final Map<Long, CloneSetInfo> clones,
			final Map<Long, CodeFragmentLinkInfo> fragmentLinks) {
		return concretizeAll(dbCloneLinks.values(), combinedRevisions, clones,
				fragmentLinks);
	}

}
