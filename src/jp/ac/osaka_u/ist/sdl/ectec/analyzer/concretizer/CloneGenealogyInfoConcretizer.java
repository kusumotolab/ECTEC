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
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.RevisionInfo;
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
			final Map<Long, RevisionInfo> revisions,
			final Map<Long, CloneSetInfo> clones,
			final Map<Long, CloneSetLinkInfo> cloneLinks) {
		final long id = dbGenealogy.getId();
		final RevisionInfo startRevision = revisions.get(dbGenealogy
				.getStartRevisionId());
		final RevisionInfo endRevision = revisions.get(dbGenealogy
				.getEndRevisionId());

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

		final int numberOfChanges = dbGenealogy.getNumberOfChanges();
		final int numberOfAdditions = dbGenealogy.getNumberOfAdditions();
		final int numberOfDeletions = dbGenealogy.getNumberOfDeletions();
		final boolean dead = dbGenealogy.isDead();

		return new CloneGenealogyInfo(id, startRevision, endRevision,
				clonesList, cloneLinksList, numberOfChanges, numberOfAdditions,
				numberOfDeletions, dead);
	}

	public Map<Long, CloneGenealogyInfo> concretizeAll(
			final Collection<DBCloneGenealogyInfo> dbGenealogies,
			final Map<Long, RevisionInfo> revisions,
			final Map<Long, CloneSetInfo> clones,
			final Map<Long, CloneSetLinkInfo> cloneLinks) {
		final Map<Long, CloneGenealogyInfo> result = new TreeMap<Long, CloneGenealogyInfo>();

		for (final DBCloneGenealogyInfo dbGenealogy : dbGenealogies) {
			final CloneGenealogyInfo genealogy = concretize(dbGenealogy,
					revisions, clones, cloneLinks);
			result.put(genealogy.getId(), genealogy);
		}

		return Collections.unmodifiableMap(result);
	}

	public Map<Long, CloneGenealogyInfo> concretizeAll(
			final Map<Long, DBCloneGenealogyInfo> dbGenealogies,
			final Map<Long, RevisionInfo> revisions,
			final Map<Long, CloneSetInfo> clones,
			final Map<Long, CloneSetLinkInfo> cloneLinks) {
		return concretizeAll(dbGenealogies.values(), revisions, clones,
				cloneLinks);
	}

}
