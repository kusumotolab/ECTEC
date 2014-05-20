package jp.ac.osaka_u.ist.sdl.ectec.main.linker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.BlockType;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCrdInfo;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.similarity.ICRDSimilarityCalculator;

import org.apache.log4j.Logger;

public class MultipleCodeFragmentLinker extends
		AbstractLocationLimitedCodeFragmentLinker {

	private final static Logger logger = LoggingManager
			.getLogger(MultipleCodeFragmentLinker.class.getName());

	@Override
	public Map<Long, DBCodeFragmentLinkInfo> detectFragmentPairsWithSortedElements(
			Map<Long, DBCodeFragmentInfo> beforeFragments,
			Map<Long, DBCodeFragmentInfo> afterFragments,
			Map<BlockType, Map<Integer, List<DBCodeFragmentInfo>>> beforeFragmentsSorted,
			Map<BlockType, Map<Integer, List<DBCodeFragmentInfo>>> afterFragmentsSorted,
			ICRDSimilarityCalculator similarityCalculator,
			double similarityThreshold, Map<Long, DBCrdInfo> crds,
			long beforeRevisionId, long afterRevisionId,
			boolean onlyFragmentInClonesInBeforeRevision,
			Map<Long, DBCloneSetInfo> clonesInBeforeRevision) {
		final FragmentLinkConditionUmpire umpire = new FragmentLinkConditionUmpire(
				similarityThreshold);
		final Map<DBCodeFragmentInfo, DBCodeFragmentInfo> pairs = detectPairs(
				beforeFragments, afterFragments, beforeFragmentsSorted,
				afterFragmentsSorted, similarityCalculator, umpire, crds,
				onlyFragmentInClonesInBeforeRevision, clonesInBeforeRevision);

		return makeLinkInstances(pairs, beforeRevisionId, afterRevisionId);
	}

	private Map<DBCodeFragmentInfo, DBCodeFragmentInfo> detectPairs(
			Map<Long, DBCodeFragmentInfo> beforeFragments,
			Map<Long, DBCodeFragmentInfo> afterFragments,
			Map<BlockType, Map<Integer, List<DBCodeFragmentInfo>>> beforeFragmentsSorted,
			Map<BlockType, Map<Integer, List<DBCodeFragmentInfo>>> afterFragmentsSorted,
			ICRDSimilarityCalculator similarityCalculator,
			FragmentLinkConditionUmpire umpire, Map<Long, DBCrdInfo> crds,
			boolean onlyFragmentInClonesInBeforeRevision,
			Map<Long, DBCloneSetInfo> clonesInBeforeRevision) {
		final Set<Long> commonFragmentIds = new HashSet<Long>();
		commonFragmentIds.addAll(beforeFragments.keySet());
		commonFragmentIds.retainAll(afterFragments.keySet());

		final Map<BlockType, Map<Integer, List<DBCodeFragmentInfo>>> fragmentsDeleted = new HashMap<BlockType, Map<Integer, List<DBCodeFragmentInfo>>>();
		fragmentsDeleted.putAll(beforeFragmentsSorted);

		final Map<BlockType, Map<Integer, List<DBCodeFragmentInfo>>> fragmentsAdded = new HashMap<BlockType, Map<Integer, List<DBCodeFragmentInfo>>>();
		fragmentsAdded.putAll(afterFragmentsSorted);

		// evacuate the original collections
		removeCommonFragments(fragmentsDeleted, commonFragmentIds);
		removeCommonFragments(fragmentsAdded, commonFragmentIds);

		final Map<DBCodeFragmentInfo, DBCodeFragmentInfo> result = new TreeMap<DBCodeFragmentInfo, DBCodeFragmentInfo>();

		for (final Map.Entry<BlockType, Map<Integer, List<DBCodeFragmentInfo>>> fragmentDeletedEntry : fragmentsDeleted
				.entrySet()) {
			final BlockType bType = fragmentDeletedEntry.getKey();
			if (!afterFragmentsSorted.containsKey(bType)) {
				continue;
			}

			// logger.debug("processing " + bType);

			final Map<Integer, List<DBCodeFragmentInfo>> correspondingAfterElements = afterFragmentsSorted
					.get(bType);

			if (correspondingAfterElements.isEmpty()) {
				continue;
			}

			if (bType == BlockType.CLASS) {
				processClassesBasedOnBeforeRevision(fragmentDeletedEntry
						.getValue().get(DEFAULT_IDENTFYING_NUMBER),
						correspondingAfterElements
								.get(DEFAULT_IDENTFYING_NUMBER),
						similarityCalculator, umpire, crds, result);
			} else if (bType == BlockType.METHOD) {
				processMethods(
						fragmentDeletedEntry.getValue().get(
								DEFAULT_IDENTFYING_NUMBER),
						correspondingAfterElements
								.get(DEFAULT_IDENTFYING_NUMBER),
						similarityCalculator, umpire, crds, result);
			} else {
				processOtherBlocks(fragmentDeletedEntry.getValue(),
						correspondingAfterElements, similarityCalculator,
						umpire, crds, result);
			}
		}

		for (final Map.Entry<BlockType, Map<Integer, List<DBCodeFragmentInfo>>> fragmentAddedEntry : fragmentsAdded
				.entrySet()) {
			final BlockType bType = fragmentAddedEntry.getKey();
			if (!beforeFragmentsSorted.containsKey(bType)) {
				continue;
			}

			// logger.debug("processing " + bType);

			final Map<Integer, List<DBCodeFragmentInfo>> correspondingBeforeElements = beforeFragmentsSorted
					.get(bType);

			if (correspondingBeforeElements.isEmpty()) {
				continue;
			}

			if (bType == BlockType.CLASS) {
				processClassesBasedOnAfterRevision(
						correspondingBeforeElements
								.get(DEFAULT_IDENTFYING_NUMBER),
						fragmentAddedEntry.getValue().get(
								DEFAULT_IDENTFYING_NUMBER),
						similarityCalculator, umpire, crds, result);
			} else if (bType == BlockType.METHOD) {
				processMethods(
						correspondingBeforeElements
								.get(DEFAULT_IDENTFYING_NUMBER),
						fragmentAddedEntry.getValue().get(
								DEFAULT_IDENTFYING_NUMBER),
						similarityCalculator, umpire, crds, result);
			} else {
				processOtherBlocks(correspondingBeforeElements,
						fragmentAddedEntry.getValue(), similarityCalculator,
						umpire, crds, result);
			}
		}

		return result;
	}

	private Map<Integer, List<DBCodeFragmentInfo>> sortClassesByName(
			final List<DBCodeFragmentInfo> classes,
			final Map<Long, DBCrdInfo> crds) {
		final Map<Integer, List<DBCodeFragmentInfo>> classesByName = new TreeMap<Integer, List<DBCodeFragmentInfo>>();
		for (final DBCodeFragmentInfo curentClass : classes) {
			final DBCrdInfo crd = crds.get(curentClass.getCrdId());
			final String name = crd.getAnchor();
			final int hash = name.hashCode();

			if (classesByName.containsKey(hash)) {
				classesByName.get(hash).add(curentClass);
			} else {
				final List<DBCodeFragmentInfo> newList = new ArrayList<DBCodeFragmentInfo>();
				newList.add(curentClass);
				classesByName.put(hash, newList);
			}
		}

		return classesByName;
	}

	private void processClassesBasedOnBeforeRevision(
			final List<DBCodeFragmentInfo> beforeClasses,
			final List<DBCodeFragmentInfo> afterClasses,
			final ICRDSimilarityCalculator similarityCalculator,
			final FragmentLinkConditionUmpire umpire,
			final Map<Long, DBCrdInfo> crds,
			final Map<DBCodeFragmentInfo, DBCodeFragmentInfo> result) {
		final Map<Integer, List<DBCodeFragmentInfo>> afterClassesByName = sortClassesByName(
				afterClasses, crds);

		for (final DBCodeFragmentInfo beforeClass : beforeClasses) {
			final DBCrdInfo beforeCrd = crds.get(beforeClass.getCrdId());

			if (beforeClass.isFileDeletedAtEnd()) {
				for (final DBCodeFragmentInfo afterClass : afterClasses) {
					final DBCrdInfo afterCrd = crds.get(afterClass.getCrdId());

					if (match(umpire, similarityCalculator, beforeCrd, afterCrd)) {
						result.put(beforeClass, afterClass);
					}
				}

			} else {
				final String name = beforeCrd.getAnchor();
				final int hash = name.hashCode();

				if (afterClassesByName.containsKey(hash)) {
					for (final DBCodeFragmentInfo afterClass : afterClassesByName
							.get(hash)) {
						final DBCrdInfo afterCrd = crds.get(afterClass
								.getCrdId());

						if (match(umpire, similarityCalculator, beforeCrd,
								afterCrd)) {
							result.put(beforeClass, afterClass);
						}
					}
				}
			}
		}
	}

	private void processClassesBasedOnAfterRevision(
			final List<DBCodeFragmentInfo> beforeClasses,
			final List<DBCodeFragmentInfo> afterClasses,
			final ICRDSimilarityCalculator similarityCalculator,
			final FragmentLinkConditionUmpire umpire,
			final Map<Long, DBCrdInfo> crds,
			final Map<DBCodeFragmentInfo, DBCodeFragmentInfo> result) {
		final Map<Integer, List<DBCodeFragmentInfo>> beforeClassesByName = sortClassesByName(
				beforeClasses, crds);

		for (final DBCodeFragmentInfo afterClass : afterClasses) {
			final DBCrdInfo afterCrd = crds.get(afterClass.getCrdId());

			if (afterClass.isFileAddedAtStart()) {
				for (final DBCodeFragmentInfo beforeClass : beforeClasses) {
					final DBCrdInfo beforeCrd = crds
							.get(beforeClass.getCrdId());

					if (match(umpire, similarityCalculator, beforeCrd, afterCrd)) {
						result.put(beforeClass, afterClass);
					}
				}

			} else {
				final String name = afterCrd.getAnchor();
				final int hash = name.hashCode();

				if (beforeClassesByName.containsKey(hash)) {
					for (final DBCodeFragmentInfo beforeClass : beforeClassesByName
							.get(hash)) {
						final DBCrdInfo beforeCrd = crds.get(beforeClass
								.getCrdId());

						if (match(umpire, similarityCalculator, beforeCrd,
								afterCrd)) {
							result.put(beforeClass, afterClass);
						}
					}
				}
			}
		}
	}

	private void processMethods(final List<DBCodeFragmentInfo> beforeMethods,
			final List<DBCodeFragmentInfo> afterMethods,
			final ICRDSimilarityCalculator similarityCalculator,
			final FragmentLinkConditionUmpire umpire,
			final Map<Long, DBCrdInfo> crds,
			final Map<DBCodeFragmentInfo, DBCodeFragmentInfo> result) {
		final Map<Integer, List<DBCodeFragmentInfo>> afterMethodsByName = new HashMap<Integer, List<DBCodeFragmentInfo>>();
		final Map<Integer, List<DBCodeFragmentInfo>> afterMethodsByParameters = new HashMap<Integer, List<DBCodeFragmentInfo>>();

		for (final DBCodeFragmentInfo afterMethod : afterMethods) {
			final DBCrdInfo afterCrd = crds.get(afterMethod.getCrdId());
			final int nameHash = getMethodName(afterCrd).hashCode();
			final int parametersHash = getParameters(afterCrd).hashCode();

			if (afterMethodsByName.containsKey(nameHash)) {
				afterMethodsByName.get(nameHash).add(afterMethod);
			} else {
				final List<DBCodeFragmentInfo> newList = new ArrayList<DBCodeFragmentInfo>();
				newList.add(afterMethod);
				afterMethodsByName.put(nameHash, newList);
			}

			if (afterMethodsByParameters.containsKey(parametersHash)) {
				afterMethodsByParameters.get(parametersHash).add(afterMethod);
			} else {
				final List<DBCodeFragmentInfo> newList = new ArrayList<DBCodeFragmentInfo>();
				newList.add(afterMethod);
				afterMethodsByParameters.put(parametersHash, newList);
			}
		}

		for (final DBCodeFragmentInfo beforeMethod : beforeMethods) {
			final DBCrdInfo beforeCrd = crds.get(beforeMethod.getCrdId());
			final int nameHash = getMethodName(beforeCrd).hashCode();
			final int parametersHash = getParameters(beforeCrd).hashCode();

			final Set<DBCodeFragmentInfo> afterCandidateMethods = new HashSet<DBCodeFragmentInfo>();
			if (afterMethodsByName.containsKey(nameHash)) {
				afterCandidateMethods.addAll(afterMethodsByName.get(nameHash));
			}
			if (afterMethodsByParameters.containsKey(parametersHash)) {
				afterCandidateMethods.addAll(afterMethodsByParameters
						.get(parametersHash));
			}

			for (final DBCodeFragmentInfo afterMethod : afterCandidateMethods) {
				final DBCrdInfo afterCrd = crds.get(afterMethod.getCrdId());

				if (match(umpire, similarityCalculator, beforeCrd, afterCrd)) {
					result.put(beforeMethod, afterMethod);
				}
			}

		}
	}

	private void processOtherBlocks(
			final Map<Integer, List<DBCodeFragmentInfo>> beforeFragments,
			final Map<Integer, List<DBCodeFragmentInfo>> afterFragments,
			ICRDSimilarityCalculator similarityCalculator,
			FragmentLinkConditionUmpire umpire, Map<Long, DBCrdInfo> crds,
			final Map<DBCodeFragmentInfo, DBCodeFragmentInfo> result) {
		for (final Map.Entry<Integer, List<DBCodeFragmentInfo>> childEntry : beforeFragments
				.entrySet()) {
			final int identifyingNumber = childEntry.getKey();

			if (!afterFragments.containsKey(identifyingNumber)) {
				continue;
			}

			final List<DBCodeFragmentInfo> beforeCandidates = childEntry
					.getValue();
			final List<DBCodeFragmentInfo> afterCandidates = afterFragments
					.get(identifyingNumber);

			for (final DBCodeFragmentInfo beforeCandidate : beforeCandidates) {
				final DBCrdInfo beforeCrd = crds
						.get(beforeCandidate.getCrdId());

				for (final DBCodeFragmentInfo afterCandidate : afterCandidates) {
					final DBCrdInfo afterCrd = crds.get(afterCandidate
							.getCrdId());

					if (match(umpire, similarityCalculator, beforeCrd, afterCrd)) {
						result.put(beforeCandidate, afterCandidate);
					}
				}
			}
		}
	}

	private final boolean match(final FragmentLinkConditionUmpire umpire,
			final ICRDSimilarityCalculator similarityCalculator,
			final DBCrdInfo beforeCrd, final DBCrdInfo afterCrd) {
		if (beforeCrd.equals(afterCrd)) {
			return false;
		}

		if (!umpire.satisfyCrdConditions(beforeCrd, afterCrd)) {
			return false;
		}

		final double similarity = similarityCalculator.calcSimilarity(
				beforeCrd, afterCrd);

		if (umpire.satisfyAllConditions(beforeCrd, afterCrd, similarity)) {
			return true;
		} else {
			return false;
		}
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
