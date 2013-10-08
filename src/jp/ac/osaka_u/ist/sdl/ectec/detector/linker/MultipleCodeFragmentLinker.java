package jp.ac.osaka_u.ist.sdl.ectec.detector.linker;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCrdInfo;
import jp.ac.osaka_u.ist.sdl.ectec.detector.sourceanalyzer.similarity.ICRDSimilarityCalculator;

public class MultipleCodeFragmentLinker implements ICodeFragmentLinker {

	@Override
	public Map<Long, DBCodeFragmentLinkInfo> detectFragmentPairs(
			Collection<DBCodeFragmentInfo> beforeFragments,
			Collection<DBCodeFragmentInfo> afterFragments,
			ICRDSimilarityCalculator similarityCalculator,
			double similarityThreshold, Map<Long, DBCrdInfo> crds,
			long beforeRevisionId, long afterRevisionId) {
		final FragmentLinkConditionUmpire umpire = new FragmentLinkConditionUmpire(
				similarityThreshold);
		final Map<DBCodeFragmentInfo, DBCodeFragmentInfo> pairs = detectPairs(
				beforeFragments, afterFragments, similarityCalculator, umpire,
				crds);

		return makeLinkInstances(pairs, beforeRevisionId, afterRevisionId);
	}

	private Map<DBCodeFragmentInfo, DBCodeFragmentInfo> detectPairs(
			Collection<DBCodeFragmentInfo> beforeFragments,
			Collection<DBCodeFragmentInfo> afterFragments,
			ICRDSimilarityCalculator similarityCalculator,
			FragmentLinkConditionUmpire umpire, Map<Long, DBCrdInfo> crds) {
		final Map<DBCodeFragmentInfo, DBCodeFragmentInfo> result = new TreeMap<DBCodeFragmentInfo, DBCodeFragmentInfo>();

		// evacuate the original collections
		final Set<DBCodeFragmentInfo> beforeFragmentsSet = new HashSet<DBCodeFragmentInfo>();
		beforeFragmentsSet.addAll(beforeFragments);
		final Set<DBCodeFragmentInfo> afterFragmentsSet = new HashSet<DBCodeFragmentInfo>();
		afterFragmentsSet.addAll(afterFragments);

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
}
