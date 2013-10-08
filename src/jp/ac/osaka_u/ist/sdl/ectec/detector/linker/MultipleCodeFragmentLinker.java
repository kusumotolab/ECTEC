package jp.ac.osaka_u.ist.sdl.ectec.detector.linker;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.data.CRD;
import jp.ac.osaka_u.ist.sdl.ectec.data.CodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.CodeFragmentLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.detector.sourceanalyzer.similarity.ICRDSimilarityCalculator;

public class MultipleCodeFragmentLinker implements ICodeFragmentLinker {

	@Override
	public Map<Long, CodeFragmentLinkInfo> detectFragmentPairs(
			Collection<CodeFragmentInfo> beforeFragments,
			Collection<CodeFragmentInfo> afterFragments,
			ICRDSimilarityCalculator similarityCalculator,
			double similarityThreshold, Map<Long, CRD> crds,
			long beforeRevisionId, long afterRevisionId) {
		final FragmentLinkConditionUmpire umpire = new FragmentLinkConditionUmpire(
				similarityThreshold);
		final Map<CodeFragmentInfo, CodeFragmentInfo> pairs = detectPairs(
				beforeFragments, afterFragments, similarityCalculator, umpire,
				crds);

		return makeLinkInstances(pairs, beforeRevisionId, afterRevisionId);
	}

	private Map<CodeFragmentInfo, CodeFragmentInfo> detectPairs(
			Collection<CodeFragmentInfo> beforeFragments,
			Collection<CodeFragmentInfo> afterFragments,
			ICRDSimilarityCalculator similarityCalculator,
			FragmentLinkConditionUmpire umpire, Map<Long, CRD> crds) {
		final Map<CodeFragmentInfo, CodeFragmentInfo> result = new TreeMap<CodeFragmentInfo, CodeFragmentInfo>();

		// evacuate the original collections
		final Set<CodeFragmentInfo> beforeFragmentsSet = new HashSet<CodeFragmentInfo>();
		beforeFragmentsSet.addAll(beforeFragments);
		final Set<CodeFragmentInfo> afterFragmentsSet = new HashSet<CodeFragmentInfo>();
		afterFragmentsSet.addAll(afterFragments);

		for (final CodeFragmentInfo beforeFragment : beforeFragmentsSet) {
			final CRD beforeCrd = crds.get(beforeFragment.getCrdId());

			for (final CodeFragmentInfo afterFragment : afterFragmentsSet) {
				final CRD afterCrd = crds.get(afterFragment.getCrdId());

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
	private final Map<Long, CodeFragmentLinkInfo> makeLinkInstances(
			final Map<CodeFragmentInfo, CodeFragmentInfo> pairs,
			final long beforeRevisionId, final long afterRevisionId) {
		final Map<Long, CodeFragmentLinkInfo> result = new TreeMap<Long, CodeFragmentLinkInfo>();
		for (final Map.Entry<CodeFragmentInfo, CodeFragmentInfo> entry : pairs
				.entrySet()) {
			final CodeFragmentInfo beforeFragment = entry.getKey();
			final CodeFragmentInfo afterFragment = entry.getValue();

			final boolean changed = beforeFragment.getHash() != afterFragment
					.getHash();

			final CodeFragmentLinkInfo link = new CodeFragmentLinkInfo(
					beforeFragment.getId(), afterFragment.getId(),
					beforeRevisionId, afterRevisionId, changed);
			result.put(link.getId(), link);
		}
		return Collections.unmodifiableMap(result);
	}
}
