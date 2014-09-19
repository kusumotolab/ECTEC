package jp.ac.osaka_u.ist.sdl.ectec.analyzer.concretizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CloneGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CloneSetInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CloneSetLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CombinedRevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneGenealogyInfo;

/**
 * A class for concretizing clone genealogies
 * 
 * @author k-hotta
 * 
 */
public class CloneGenealogyInfoConcretizer {

	public CloneGenealogyInfo concretize(
			final DBCloneGenealogyInfo dbGenealogy,
			final Map<Long, CombinedRevisionInfo> combinedRevisions,
			final Map<Long, CloneSetInfo> clones,
			final Map<Long, CloneSetLinkInfo> cloneLinks) {
		final long id = dbGenealogy.getId();
		final CombinedRevisionInfo startCombinedRevision = combinedRevisions
				.get(dbGenealogy.getStartCombinedRevisionId());
		final CombinedRevisionInfo endCombinedRevision = combinedRevisions
				.get(dbGenealogy.getEndCombinedRevisionId());

		final List<CloneSetInfo> clonesList = new ArrayList<CloneSetInfo>();
		final List<Long> cloneIds = dbGenealogy.getElements();
		for (final long cloneId : cloneIds) {
			clonesList.add(clones.get(cloneId));
		}

		final List<CloneSetLinkInfo> cloneLinksList = new ArrayList<CloneSetLinkInfo>();
		final List<Long> cloneLinkIds = dbGenealogy.getLinks();
		for (final long cloneLinkId : cloneLinkIds) {
			cloneLinksList.add(cloneLinks.get(cloneLinkId));
		}

		int numberOfChanges = 0;
		int numberOfAdditions = 0;
		int numberOfDeletions = 0;

		for (final CloneSetLinkInfo cloneLink : cloneLinksList) {
			if (cloneLink.getNumberOfChangedElements() != 0) {
				numberOfChanges++;
			}
			if (cloneLink.getNumberOfAddedElements() != 0) {
				numberOfAdditions++;
			}
			if (cloneLink.getNumberOfDeletedElements() != 0) {
				numberOfDeletions++;
			}
		}

		return new CloneGenealogyInfo(id, startCombinedRevision,
				endCombinedRevision, clonesList, cloneLinksList,
				numberOfChanges, numberOfAdditions, numberOfDeletions);
	}

	public Map<Long, CloneGenealogyInfo> concretizeAll(
			final Collection<DBCloneGenealogyInfo> dbGenealogies,
			final Map<Long, CombinedRevisionInfo> combinedRevisions,
			final Map<Long, CloneSetInfo> clones,
			final Map<Long, CloneSetLinkInfo> cloneLinks) {
		final Map<Long, CloneGenealogyInfo> result = new TreeMap<Long, CloneGenealogyInfo>();

		for (final DBCloneGenealogyInfo dbGenealogy : dbGenealogies) {
			final CloneGenealogyInfo genealogy = concretize(dbGenealogy,
					combinedRevisions, clones, cloneLinks);
			result.put(genealogy.getId(), genealogy);
		}

		return Collections.unmodifiableMap(result);
	}

	public Map<Long, CloneGenealogyInfo> concretizeAll(
			final Map<Long, DBCloneGenealogyInfo> dbGenealogies,
			final Map<Long, CombinedRevisionInfo> combinedRevisions,
			final Map<Long, CloneSetInfo> clones,
			final Map<Long, CloneSetLinkInfo> cloneLinks) {
		return concretizeAll(dbGenealogies.values(), combinedRevisions, clones,
				cloneLinks);
	}

}
