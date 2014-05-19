package jp.ac.osaka_u.ist.sdl.ectec.main.linker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.BlockType;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCrdInfo;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.similarity.ICRDSimilarityCalculator;

public abstract class AbstractLocationLimitedCodeFragmentLinker implements
		ICodeFragmentLinker {

	protected static final int DEFAULT_IDENTFYING_NUMBER = 1;

	@Override
	public Map<Long, DBCodeFragmentLinkInfo> detectFragmentPairs(
			Map<Long, DBCodeFragmentInfo> beforeFragments,
			Map<Long, DBCodeFragmentInfo> afterFragments,
			ICRDSimilarityCalculator similarityCalculator,
			double similarityThreshold, Map<Long, DBCrdInfo> crds,
			long beforeRevisionId, long afterRevisionId,
			boolean onlyFragmentInClonesInBeforeRevision,
			Map<Long, DBCloneSetInfo> clonesInBeforeRevision) {
		final Map<Long, DBCodeFragmentInfo> targetBeforeFragments = getTargetFragmentsInBeforeRevision(
				beforeFragments, clonesInBeforeRevision,
				onlyFragmentInClonesInBeforeRevision);

		final Map<BlockType, Map<Integer, List<DBCodeFragmentInfo>>> beforeFragmentsSorted = sortFragments(
				targetBeforeFragments, crds);
		final Map<BlockType, Map<Integer, List<DBCodeFragmentInfo>>> afterFragmentsSorted = sortFragments(
				afterFragments, crds);

		return detectFragmentPairsWithSortedElements(beforeFragments,
				afterFragments, beforeFragmentsSorted, afterFragmentsSorted,
				similarityCalculator, similarityThreshold, crds,
				beforeRevisionId, afterRevisionId,
				onlyFragmentInClonesInBeforeRevision, clonesInBeforeRevision);
	}

	public abstract Map<Long, DBCodeFragmentLinkInfo> detectFragmentPairsWithSortedElements(
			final Map<Long, DBCodeFragmentInfo> beforeFragments,
			final Map<Long, DBCodeFragmentInfo> afterFragments,
			final Map<BlockType, Map<Integer, List<DBCodeFragmentInfo>>> beforeFragmetnsSorted,
			final Map<BlockType, Map<Integer, List<DBCodeFragmentInfo>>> afterFragmentsSorted,
			ICRDSimilarityCalculator similarityCalculator,
			double similarityThreshold, Map<Long, DBCrdInfo> crds,
			long beforeRevisionId, long afterRevisionId,
			boolean onlyFragmentInClonesInBeforeRevision,
			Map<Long, DBCloneSetInfo> clonesInBeforeRevision);

	private Map<BlockType, Map<Integer, List<DBCodeFragmentInfo>>> sortFragments(
			final Map<Long, DBCodeFragmentInfo> fragments,
			final Map<Long, DBCrdInfo> crds) {
		final Map<BlockType, Map<Integer, List<DBCodeFragmentInfo>>> result = new HashMap<BlockType, Map<Integer, List<DBCodeFragmentInfo>>>();

		for (final Map.Entry<Long, DBCodeFragmentInfo> entry : fragments
				.entrySet()) {
			final DBCodeFragmentInfo block = entry.getValue();
			final DBCrdInfo crd = crds.get(block.getCrdId());
			final BlockType bType = crd.getType();

			if (result.containsKey(bType)) {
				final Map<Integer, List<DBCodeFragmentInfo>> registeredMap = result
						.get(bType);
				final int constraintObject = getIdentifyingNumber(crd);
				if (registeredMap.containsKey(constraintObject)) {
					registeredMap.get(constraintObject).add(block);
				} else {
					final List<DBCodeFragmentInfo> newList = new ArrayList<DBCodeFragmentInfo>();
					newList.add(block);
					registeredMap.put(constraintObject, newList);
				}

			} else {
				final List<DBCodeFragmentInfo> newList = new ArrayList<DBCodeFragmentInfo>();
				newList.add(block);
				final Map<Integer, List<DBCodeFragmentInfo>> newMap = new HashMap<Integer, List<DBCodeFragmentInfo>>();
				newMap.put(getIdentifyingNumber(crd), newList);
				result.put(bType, newMap);
			}
		}

		return result;
	}

	private int getIdentifyingNumber(final DBCrdInfo crd) {
		switch (crd.getType()) {
		case IF:
			return crd.getNormalizedAnchor().hashCode();
		case FOR:
			return crd.getNormalizedAnchor().hashCode();
		case DO:
			return crd.getNormalizedAnchor().hashCode();
		case ENHANCED_FOR:
			return crd.getNormalizedAnchor().hashCode();
		case WHILE:
			return crd.getNormalizedAnchor().hashCode();
		default:
			return DEFAULT_IDENTFYING_NUMBER;

		}
	}

	protected String getMethodName(final DBCrdInfo crd) {
		final String anchor = crd.getAnchor();
		final int leftParenIndex = anchor.indexOf("(");

		return anchor.substring(0, leftParenIndex);
	}

	protected String getParameters(final DBCrdInfo crd) {
		final String anchor = crd.getAnchor();
		final int leftParenIndex = anchor.indexOf("(");
		final int rightParenIndex = anchor.indexOf(")");

		return anchor.substring(leftParenIndex + 1, rightParenIndex);
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
	
	protected void removeCommonFragments(
			Map<BlockType, Map<Integer, List<DBCodeFragmentInfo>>> fragmentsSorted,
			final Set<Long> commonFragmentIds) {
		final List<BlockType> toBeRemovedBlockTypes = new ArrayList<BlockType>();

		for (final Map.Entry<BlockType, Map<Integer, List<DBCodeFragmentInfo>>> rootEntry : fragmentsSorted
				.entrySet()) {
			final List<Integer> toBeRemovedIdentifyingNumbers = new ArrayList<Integer>();

			for (final Map.Entry<Integer, List<DBCodeFragmentInfo>> childEntry : rootEntry
					.getValue().entrySet()) {
				final List<DBCodeFragmentInfo> originalFragments = childEntry
						.getValue();
				final List<DBCodeFragmentInfo> toBeRemovedFragments = new ArrayList<DBCodeFragmentInfo>();
				for (final DBCodeFragmentInfo fragment : childEntry.getValue()) {
					if (commonFragmentIds.contains(fragment.getId())) {
						toBeRemovedFragments.add(fragment);
					}
				}
				originalFragments.removeAll(toBeRemovedFragments);
				if (originalFragments.isEmpty()) {
					toBeRemovedIdentifyingNumbers.add(childEntry.getKey());
				}
			}

			for (final int toBeRemovedIdentyfingNumber : toBeRemovedIdentifyingNumbers) {
				rootEntry.getValue().remove(toBeRemovedIdentyfingNumber);
			}

			if (rootEntry.getValue().isEmpty()) {
				toBeRemovedBlockTypes.add(rootEntry.getKey());
			}
		}

		for (final BlockType toBeRemovedBlockType : toBeRemovedBlockTypes) {
			fragmentsSorted.remove(toBeRemovedBlockType);
		}
	}

}
