package jp.ac.osaka_u.ist.sdl.ectec.main.linker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
import jp.ac.osaka_u.ist.sdl.ectec.main.linker.similarity.ICRDSimilarityCalculator;

public abstract class AbstractLocationLimitedCodeFragmentLinker implements
		ICodeFragmentLinker {

	protected static final int DEFAULT_IDENTFYING_NUMBER = 1;

	protected abstract AbstractLocationLimitedCodeFragmentLinkMaker createMaker(
			FragmentLinkConditionUmpire umpire,
			ICRDSimilarityCalculator similarityCalculator,
			double similarityThreshold, Map<Long, DBCrdInfo> crds,
			long beforeRevisionId, long afterRevisionId);

	protected abstract void detectLinks(
			final AbstractLocationLimitedCodeFragmentLinkMaker maker,
			final Map<BlockType, Map<Integer, List<DBCodeFragmentInfo>>> fragmentsStayed,
			final Map<BlockType, Map<Integer, List<DBCodeFragmentInfo>>> fragmentsDeleted,
			final Map<BlockType, Map<Integer, List<DBCodeFragmentInfo>>> fragmentsAdded,
			final Map<BlockType, Map<Integer, List<DBCodeFragmentInfo>>> afterFragmentsSorted,
			final Map<Long, DBCrdInfo> crds);

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

		final Map<Long, DBCodeFragmentInfo> targetBeforeFragments = getTargetFragmentsInBeforeRevision(
				beforeFragments, clonesInBeforeRevision,
				onlyFragmentInClonesInBeforeRevision);

		final Map<Long, DBCodeFragmentInfo> stayedBeforeFragments = new TreeMap<Long, DBCodeFragmentInfo>();

		final Map<Long, DBCodeFragmentInfo> deletedBeforeFragments = new TreeMap<Long, DBCodeFragmentInfo>();
		deletedBeforeFragments.putAll(targetBeforeFragments);

		final Map<Long, DBCodeFragmentInfo> addedAfterFragments = new TreeMap<Long, DBCodeFragmentInfo>();
		addedAfterFragments.putAll(afterFragments);

		final Set<Long> commonFragmentIds = new HashSet<Long>();
		commonFragmentIds.addAll(targetBeforeFragments.keySet());
		commonFragmentIds.retainAll(afterFragments.keySet());

		for (final long commonFragmentId : commonFragmentIds) {
			if (targetBeforeFragments.containsKey(commonFragmentId)) {
				stayedBeforeFragments.put(commonFragmentId,
						targetBeforeFragments.get(commonFragmentId));
			}
			deletedBeforeFragments.remove(commonFragmentId);
			addedAfterFragments.remove(commonFragmentId);
		}

		final Map<BlockType, Map<Integer, List<DBCodeFragmentInfo>>> afterFragmentsSorted = sortFragments(
				afterFragments, crds);

		final Map<BlockType, Map<Integer, List<DBCodeFragmentInfo>>> fragmentsStayed = sortFragments(
				stayedBeforeFragments, crds);
		final Map<BlockType, Map<Integer, List<DBCodeFragmentInfo>>> fragmentsDeleted = sortFragments(
				deletedBeforeFragments, crds);
		final Map<BlockType, Map<Integer, List<DBCodeFragmentInfo>>> fragmentsAdded = sortFragments(
				addedAfterFragments, crds);

		final AbstractLocationLimitedCodeFragmentLinkMaker maker = createMaker(
				umpire, similarityCalculator, similarityThreshold, crds,
				beforeRevisionId, afterRevisionId);

		detectLinks(maker, fragmentsStayed, fragmentsDeleted, fragmentsAdded,
				afterFragmentsSorted, crds);

		return maker.getDetectedLinks();
	}

	protected void processClassesBasedOnBeforeRevision(
			final AbstractLocationLimitedCodeFragmentLinkMaker maker,
			final List<DBCodeFragmentInfo> beforeClasses,
			final List<DBCodeFragmentInfo> afterClasses,
			final Map<Long, DBCrdInfo> crds) {
		final Map<Integer, List<DBCodeFragmentInfo>> afterClassesByName = sortClassesByName(
				afterClasses, crds);

		for (final DBCodeFragmentInfo beforeClass : beforeClasses) {
			final DBCrdInfo beforeCrd = crds.get(beforeClass.getCrdId());

			if (beforeClass.isFileDeletedAtEnd()) {
				maker.processFragment(beforeClass, afterClasses, false);
			} else {
				final String name = beforeCrd.getAnchor();
				final int hash = name.hashCode();

				if (afterClassesByName.containsKey(hash)) {
					maker.processFragment(beforeClass,
							afterClassesByName.get(hash), false);
				}
			}
		}
	}

	protected void processClassesBasedOnAfterRevision(
			final AbstractLocationLimitedCodeFragmentLinkMaker maker,
			final List<DBCodeFragmentInfo> beforeClasses,
			final List<DBCodeFragmentInfo> afterClasses,
			final Map<Long, DBCrdInfo> crds) {
		final Map<Integer, List<DBCodeFragmentInfo>> beforeClassesByName = sortClassesByName(
				beforeClasses, crds);

		for (final DBCodeFragmentInfo afterClass : afterClasses) {
			final DBCrdInfo afterCrd = crds.get(afterClass.getCrdId());

			if (afterClass.isFileAddedAtStart()) {
				maker.processFragment(afterClass, beforeClasses, true);
			} else {
				final String name = afterCrd.getAnchor();
				final int hash = name.hashCode();

				if (beforeClassesByName.containsKey(hash)) {
					maker.processFragment(afterClass,
							beforeClassesByName.get(hash), true);
				}
			}
		}
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

	protected void processMethods(
			final AbstractLocationLimitedCodeFragmentLinkMaker maker,
			final List<DBCodeFragmentInfo> beforeMethods,
			final List<DBCodeFragmentInfo> afterMethods,
			final Map<Long, DBCrdInfo> crds) {
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

			maker.processFragment(beforeMethod, afterCandidateMethods, false);
		}
	}

	protected void processOtherBlocks(
			final AbstractLocationLimitedCodeFragmentLinkMaker maker,
			final Map<Integer, List<DBCodeFragmentInfo>> beforeFragments,
			final Map<Integer, List<DBCodeFragmentInfo>> afterFragments,
			Map<Long, DBCrdInfo> crds) {
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
				maker.processFragment(beforeCandidate, afterCandidates, false);
			}
		}
	}

	protected Map<BlockType, Map<Integer, List<DBCodeFragmentInfo>>> sortFragments(
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

	protected void retainCommonFragments(
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
					if (!commonFragmentIds.contains(fragment.getId())) {
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
