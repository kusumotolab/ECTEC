package jp.ac.osaka_u.ist.sdl.ectec.main.linker;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.BlockType;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCrdInfo;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.similarity.ICRDSimilarityCalculator;

public class MultipleCodeFragmentLinker extends
		AbstractLocationLimitedCodeFragmentLinker {

	@Override
	protected AbstractLocationLimitedCodeFragmentLinkMaker createMaker(
			FragmentLinkConditionUmpire umpire,
			ICRDSimilarityCalculator similarityCalculator,
			double similarityThreshold, Map<Long, DBCrdInfo> crds,
			long beforeRevisionId, long afterRevisionId) {
		return new MultipleCodeFragmentLinkMaker(umpire, similarityCalculator,
				similarityThreshold, crds, beforeRevisionId, afterRevisionId);
	}

	@Override
	protected void detectLinks(
			AbstractLocationLimitedCodeFragmentLinkMaker maker,
			Map<BlockType, Map<Integer, List<DBCodeFragmentInfo>>> fragmentsStayed,
			Map<BlockType, Map<Integer, List<DBCodeFragmentInfo>>> fragmentsDeleted,
			Map<BlockType, Map<Integer, List<DBCodeFragmentInfo>>> fragmentsAdded,
			final Map<BlockType, Map<Integer, List<DBCodeFragmentInfo>>> afterFragmentsSorted,
			Map<Long, DBCrdInfo> crds) {
		processFragmentsBasedOnBeforeRevision(maker, fragmentsDeleted,
				afterFragmentsSorted, crds);
		processFragmentsBasedOnAfterRevision(maker, fragmentsStayed,
				fragmentsAdded, crds);
	}

	private void processFragmentsBasedOnBeforeRevision(
			final AbstractLocationLimitedCodeFragmentLinkMaker maker,
			final Map<BlockType, Map<Integer, List<DBCodeFragmentInfo>>> fragmentsDeleted,
			Map<BlockType, Map<Integer, List<DBCodeFragmentInfo>>> afterFragmentsSorted,
			final Map<Long, DBCrdInfo> crds) {
		for (final Map.Entry<BlockType, Map<Integer, List<DBCodeFragmentInfo>>> fragmentDeletedEntry : fragmentsDeleted
				.entrySet()) {
			final BlockType bType = fragmentDeletedEntry.getKey();
			if (!afterFragmentsSorted.containsKey(bType)) {
				continue;
			}

			final Map<Integer, List<DBCodeFragmentInfo>> correspondingAfterElements = afterFragmentsSorted
					.get(bType);
			if (correspondingAfterElements.isEmpty()) {
				continue;
			}

			if (bType == BlockType.CLASS) {
				processClassesBasedOnBeforeRevision(maker, fragmentDeletedEntry
						.getValue().get(DEFAULT_IDENTFYING_NUMBER),
						correspondingAfterElements
								.get(DEFAULT_IDENTFYING_NUMBER), crds);
			} else if (bType == BlockType.METHOD) {
				processMethods(
						maker,
						fragmentDeletedEntry.getValue().get(
								DEFAULT_IDENTFYING_NUMBER),
						correspondingAfterElements
								.get(DEFAULT_IDENTFYING_NUMBER), crds);
			} else {
				processOtherBlocks(maker, fragmentDeletedEntry.getValue(),
						correspondingAfterElements, crds);
			}
		}
	}

	private void processFragmentsBasedOnAfterRevision(
			final AbstractLocationLimitedCodeFragmentLinkMaker maker,
			final Map<BlockType, Map<Integer, List<DBCodeFragmentInfo>>> fragmentsStayed,
			Map<BlockType, Map<Integer, List<DBCodeFragmentInfo>>> fragmentsAdded,
			final Map<Long, DBCrdInfo> crds) {
		for (final Map.Entry<BlockType, Map<Integer, List<DBCodeFragmentInfo>>> fragmentAddedEntry : fragmentsAdded
				.entrySet()) {
			final BlockType bType = fragmentAddedEntry.getKey();
			if (!fragmentsStayed.containsKey(bType)) {
				continue;
			}

			final Map<Integer, List<DBCodeFragmentInfo>> correspondingBeforeElements = fragmentsStayed
					.get(bType);
			if (correspondingBeforeElements.isEmpty()) {
				continue;
			}

			if (bType == BlockType.CLASS) {
				processClassesBasedOnAfterRevision(
						maker,
						correspondingBeforeElements
								.get(DEFAULT_IDENTFYING_NUMBER),
						fragmentAddedEntry.getValue().get(
								DEFAULT_IDENTFYING_NUMBER), crds);
			} else if (bType == BlockType.METHOD) {
				processMethods(
						maker,
						correspondingBeforeElements
								.get(DEFAULT_IDENTFYING_NUMBER),
						fragmentAddedEntry.getValue().get(
								DEFAULT_IDENTFYING_NUMBER), crds);
			} else {
				processOtherBlocks(maker, correspondingBeforeElements,
						fragmentAddedEntry.getValue(), crds);
			}
		}
	}

	private class MultipleCodeFragmentLinkMaker extends
			AbstractLocationLimitedCodeFragmentLinkMaker {

		public MultipleCodeFragmentLinkMaker(
				FragmentLinkConditionUmpire umpire,
				ICRDSimilarityCalculator similarityCalculator,
				double similarityThreshold, Map<Long, DBCrdInfo> crds,
				long beforeRevisionId, long afterRevisionId) {
			super(umpire, similarityCalculator, similarityThreshold, crds,
					beforeRevisionId, afterRevisionId);
		}

		@Override
		public void processFragment(DBCodeFragmentInfo fragment,
				Collection<DBCodeFragmentInfo> pairCandidateFragments,
				boolean reversed) {
			final DBCrdInfo targetCrd = crds.get(fragment.getCrdId());
			for (final DBCodeFragmentInfo pairCandidate : pairCandidateFragments) {
				final DBCrdInfo candidateCrd = crds.get(pairCandidate
						.getCrdId());

				if (match(targetCrd, candidateCrd)) {
					final DBCodeFragmentLinkInfo link = (reversed) ? makeLinkInstance(
							pairCandidate, fragment) : makeLinkInstance(
							fragment, pairCandidate);
					links.put(link.getId(), link);
				}
			}
		}

	}

}
