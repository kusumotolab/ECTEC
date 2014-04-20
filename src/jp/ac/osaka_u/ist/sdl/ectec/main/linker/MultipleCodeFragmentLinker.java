package jp.ac.osaka_u.ist.sdl.ectec.main.linker;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCrdInfo;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.similarity.ICRDSimilarityCalculator;

public class MultipleCodeFragmentLinker implements ICodeFragmentLinker {

	@Override
	public Map<Long, DBCodeFragmentLinkInfo> detectFragmentPairs(
			Map<Long, DBCodeFragmentInfo> beforeFragments,
			Map<Long, DBCodeFragmentInfo> afterFragments,
			ICRDSimilarityCalculator similarityCalculator,
			double similarityThreshold, Map<Long, DBCrdInfo> crds,
			long beforeRevisionId, long afterRevisionId,
			boolean onlyFragmentInClonesInBeforeRevision,
			Map<Long, DBCloneSetInfo> clonesInBeforeRevision) {
		final FragmentLinkConditionUmpire umpire = new FragmentLinkConditionUmpire(
				similarityThreshold);
		final Map<DBCodeFragmentInfo, DBCodeFragmentInfo> pairs = detectPairs(
				beforeFragments, afterFragments, similarityCalculator, umpire,
				crds, onlyFragmentInClonesInBeforeRevision,
				clonesInBeforeRevision);

		return makeLinkInstances(pairs, beforeRevisionId, afterRevisionId);
	}

	private Map<DBCodeFragmentInfo, DBCodeFragmentInfo> detectPairs(
			Map<Long, DBCodeFragmentInfo> beforeFragments,
			Map<Long, DBCodeFragmentInfo> afterFragments,
			ICRDSimilarityCalculator similarityCalculator,
			FragmentLinkConditionUmpire umpire, Map<Long, DBCrdInfo> crds,
			boolean onlyFragmentInClonesInBeforeRevision,
			Map<Long, DBCloneSetInfo> clonesInBeforeRevision) {
		final Map<DBCodeFragmentInfo, DBCodeFragmentInfo> result = new TreeMap<DBCodeFragmentInfo, DBCodeFragmentInfo>();

		// evacuate the original collections
		final Set<DBCodeFragmentInfo> beforeFragmentsSet = new HashSet<DBCodeFragmentInfo>();
		beforeFragmentsSet.addAll(getTargetFragmentsInBeforeRevision(
				beforeFragments, clonesInBeforeRevision,
				onlyFragmentInClonesInBeforeRevision).values());
		final Set<DBCodeFragmentInfo> afterFragmentsSet = new HashSet<DBCodeFragmentInfo>();
		afterFragmentsSet.addAll(afterFragments.values());

		for (final DBCodeFragmentInfo beforeFragment : beforeFragmentsSet) {
			final DBCrdInfo beforeCrd = crds.get(beforeFragment.getCrdId());

			for (final DBCodeFragmentInfo afterFragment : afterFragmentsSet) {
				final DBCrdInfo afterCrd = crds.get(afterFragment.getCrdId());

				if (beforeCrd.equals(afterCrd)) {
					continue;
				}

				if (!umpire.satisfyCrdConditions(beforeCrd, afterCrd)) {
					continue;
				}

				final double similarity = similarityCalculator.calcSimilarity(
						beforeCrd, afterCrd);

				if (umpire
						.satisfyAllConditions(beforeCrd, afterCrd, similarity)) {
					result.put(beforeFragment, afterFragment);
				}
			}
		}

		return result;
	}

	/**
	 * make fragment link instances with the given pairs
	 * 
	 * @param pairs
	 * @param beforeRevisionId
	 * @param afterRevisionId
	 * @return
	 */
	private final Map<Long, DBCodeFragmentLinkInfo> makeLinkInstances(
			final Map<DBCodeFragmentInfo, DBCodeFragmentInfo> pairs,
			final long beforeRevisionId, final long afterRevisionId) {
		final Map<Long, DBCodeFragmentLinkInfo> result = new TreeMap<Long, DBCodeFragmentLinkInfo>();
		for (final Map.Entry<DBCodeFragmentInfo, DBCodeFragmentInfo> entry : pairs
				.entrySet()) {
			final DBCodeFragmentInfo beforeFragment = entry.getKey();
			final DBCodeFragmentInfo afterFragment = entry.getValue();

			final boolean changed = beforeFragment.getHash() != afterFragment
					.getHash();

			final DBCodeFragmentLinkInfo link = new DBCodeFragmentLinkInfo(
					beforeFragment.getId(), afterFragment.getId(),
					beforeRevisionId, afterRevisionId, changed);
			result.put(link.getId(), link);
		}
		return Collections.unmodifiableMap(result);
	}

	public Map<Long, DBCodeFragmentInfo> getTargetFragmentsInBeforeRevision(
			final Map<Long, DBCodeFragmentInfo> codeFragmentsInBeforeRevision,
			final Map<Long, DBCloneSetInfo> clonesInBeforeRevision,
			final boolean onlyFragmentInClonesInBeforeRevision) {
		final Map<Long, DBCodeFragmentInfo> result = new TreeMap<Long, DBCodeFragmentInfo>();

		if (onlyFragmentInClonesInBeforeRevision) {
			final Set<Long> fragmentIdsInClones = new TreeSet<Long>();
			for (final Map.Entry<Long, DBCloneSetInfo> cloneEntry : clonesInBeforeRevision
					.entrySet()) {
				fragmentIdsInClones.addAll(cloneEntry.getValue().getElements());
			}

			for (final Map.Entry<Long, DBCodeFragmentInfo> fragmentEntry : codeFragmentsInBeforeRevision
					.entrySet()) {
				final long fragmentId = fragmentEntry.getKey();
				if (fragmentIdsInClones.contains(fragmentId)) {
					result.put(fragmentEntry.getKey(), fragmentEntry.getValue());
				}
			}

		} else {
			result.putAll(codeFragmentsInBeforeRevision);
		}

		return Collections.unmodifiableMap(result);
	}

}
