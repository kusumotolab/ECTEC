package jp.ac.osaka_u.ist.sdl.ectec.main.linker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.BlockType;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCrdInfo;
import jp.ac.osaka_u.ist.sdl.ectec.main.linker.similarity.ICRDSimilarityCalculator;
import jp.ac.osaka_u.ist.sdl.ectec.util.Table;

/**
 * A class to detect 1 by 1 links of code fragments
 * 
 * @author k-hotta
 * 
 */
public class SingleCodeFragmentLinker extends
		AbstractLocationLimitedCodeFragmentLinker {

	private final Table<Long, Long, Double> similarityTable;

	private final Map<DBCodeFragmentInfo, Queue<DBCodeFragmentInfo>> wishLists;

	public SingleCodeFragmentLinker() {
		this.similarityTable = new Table<Long, Long, Double>();
		this.wishLists = new TreeMap<DBCodeFragmentInfo, Queue<DBCodeFragmentInfo>>();
	}

	@Override
	protected AbstractLocationLimitedCodeFragmentLinkMaker createMaker(
			FragmentLinkConditionUmpire umpire,
			ICRDSimilarityCalculator similarityCalculator,
			double similarityThreshold, Map<Long, DBCrdInfo> crds,
			long beforeRevisionId, long afterRevisionId) {
		return new SingleCodeFragmentLinkMaker(umpire, similarityCalculator,
				similarityThreshold, crds, beforeRevisionId, afterRevisionId);
	}

	@Override
	protected void detectLinks(
			AbstractLocationLimitedCodeFragmentLinkMaker maker,
			Map<BlockType, Map<Integer, List<DBCodeFragmentInfo>>> fragmentsStayed,
			Map<BlockType, Map<Integer, List<DBCodeFragmentInfo>>> fragmentsDeleted,
			Map<BlockType, Map<Integer, List<DBCodeFragmentInfo>>> fragmentsAdded,
			Map<BlockType, Map<Integer, List<DBCodeFragmentInfo>>> afterFragmentsSorted,
			Map<Long, DBCrdInfo> crds) {
		makeSimilarityTableAndWishList(maker, fragmentsDeleted, fragmentsAdded,
				crds);

		// the result (detected pairs of fragments) with the shuffled
		// the key is an AFTER fragment and the value is a BEFORE
		// fragment
		final Map<DBCodeFragmentInfo, DBCodeFragmentInfo> reversedResult = new TreeMap<DBCodeFragmentInfo, DBCodeFragmentInfo>();

		final List<DBCodeFragmentInfo> unmarriedBeforeFragments = new ArrayList<DBCodeFragmentInfo>();
		for (final Map.Entry<BlockType, Map<Integer, List<DBCodeFragmentInfo>>> rootEntry : fragmentsDeleted
				.entrySet()) {
			for (final Map.Entry<Integer, List<DBCodeFragmentInfo>> childEntry : rootEntry
					.getValue().entrySet()) {
				unmarriedBeforeFragments.addAll(childEntry.getValue());
			}
		}

		while (true) {

			// make every of unmarried before fragments attack the most
			// favorite
			// after fragment
			// the contents in reversedResult and unmarriedBeforeMethods
			// will
			// change by this method call
			if (processAllProposes(reversedResult, unmarriedBeforeFragments,
					similarityTable, wishLists)) {
				break;
			}

		}

		final Map<DBCodeFragmentInfo, DBCodeFragmentInfo> result = tailorReversedMap(reversedResult);

		maker.makeLinkInstances(result);
	}

	/**
	 * swap the keys and the values
	 * 
	 * @param target
	 * @return
	 */
	private Map<DBCodeFragmentInfo, DBCodeFragmentInfo> tailorReversedMap(
			final Map<DBCodeFragmentInfo, DBCodeFragmentInfo> target) {
		final Map<DBCodeFragmentInfo, DBCodeFragmentInfo> result = new TreeMap<DBCodeFragmentInfo, DBCodeFragmentInfo>();

		for (final Map.Entry<DBCodeFragmentInfo, DBCodeFragmentInfo> entry : target
				.entrySet()) {
			result.put(entry.getValue(), entry.getKey());
		}

		return result;
	}

	/**
	 * make unmarried before fragments propose to the favorite fragments
	 * 
	 * @param reversedResult
	 * @param unmarriedBeforeFragments
	 * @param similarityTable
	 * @param wishLists
	 * @return
	 */
	private boolean processAllProposes(
			final Map<DBCodeFragmentInfo, DBCodeFragmentInfo> reversedResult,
			final List<DBCodeFragmentInfo> unmarriedBeforeFragments,
			final Table<Long, Long, Double> similarityTable,
			final Map<DBCodeFragmentInfo, Queue<DBCodeFragmentInfo>> wishLists) {
		// the set of code fragments which detect their partners in this
		// loop
		final Set<DBCodeFragmentInfo> marriedBeforeFragments = new HashSet<DBCodeFragmentInfo>();

		// the set of code fragments which are said good-bye by their
		// partners
		// in this loop
		final Set<DBCodeFragmentInfo> dumpedBeforeFragments = new HashSet<DBCodeFragmentInfo>();

		// whether any of before fragments make a propose
		// if this value is false in the tail of this method,
		// all the remaining before fragments no longer have candidates
		boolean anyoneAttacked = false;

		/*
		 * make propose
		 */
		for (final DBCodeFragmentInfo proposingFragment : unmarriedBeforeFragments) {

			if (!wishLists.containsKey(proposingFragment)) {
				continue;
			}

			// propose to the most favorite after fragment
			final DBCodeFragmentInfo proposedMethod = wishLists.get(
					proposingFragment).poll();

			// there are no candidate fragments that this fragment can
			// propose
			if (proposedMethod == null) {
				continue;
			}

			anyoneAttacked = true;

			/*
			 * switch by whether the proposed fragment already has had its
			 * fiance
			 */

			/*
			 * exist
			 */
			if (reversedResult.containsKey(proposedMethod)) {
				// this propose becomes to be successful if the proposing
				// fragment is more similar to the proposed fragment than
				// the
				// fiance
				// otherwise, the propose will fail

				// the fiance, the rival
				final DBCodeFragmentInfo rivalMethod = reversedResult
						.get(proposedMethod);

				// similarities
				final double similarityBetweenProposingMethod = similarityTable
						.getValueAt(proposingFragment.getId(),
								proposedMethod.getId());
				final double similarityBetweenCurrentPartner = similarityTable
						.getValueAt(rivalMethod.getId(), proposedMethod.getId());

				// have a match
				if (similarityBetweenProposingMethod > similarityBetweenCurrentPartner) {
					// the proposing fragment won!!
					reversedResult.remove(proposedMethod);
					reversedResult.put(proposedMethod, proposingFragment);
					marriedBeforeFragments.add(proposingFragment);
					dumpedBeforeFragments.add(rivalMethod);
				} else {
					// the proposing fragment lost
					// there is nothing for the loser to be able to do
				}

			}

			/*
			 * not exist
			 */
			else {
				// fortunately the propose is success
				reversedResult.put(proposedMethod, proposingFragment);
				marriedBeforeFragments.add(proposingFragment);
			}
		}

		// remove fragments that got their partner from the unmarried list
		unmarriedBeforeFragments.removeAll(marriedBeforeFragments);

		// add fragments that were said good-bye from their partner into the
		// unmarried list
		unmarriedBeforeFragments.addAll(dumpedBeforeFragments);

		return !anyoneAttacked;
	}

	private void makeSimilarityTableAndWishList(
			AbstractLocationLimitedCodeFragmentLinkMaker maker,
			Map<BlockType, Map<Integer, List<DBCodeFragmentInfo>>> fragmentsDeleted,
			Map<BlockType, Map<Integer, List<DBCodeFragmentInfo>>> fragmentsAdded,
			Map<Long, DBCrdInfo> crds) {
		for (final Map.Entry<BlockType, Map<Integer, List<DBCodeFragmentInfo>>> rootEntry : fragmentsDeleted
				.entrySet()) {
			final BlockType bType = rootEntry.getKey();
			if (!fragmentsAdded.containsKey(bType)) {
				continue;
			}

			final Map<Integer, List<DBCodeFragmentInfo>> correspondingAfterFragments = fragmentsAdded
					.get(bType);

			if (bType == BlockType.CLASS) {
				processClassesBasedOnBeforeRevision(maker, rootEntry.getValue()
						.get(DEFAULT_IDENTFYING_NUMBER),
						correspondingAfterFragments
								.get(DEFAULT_IDENTFYING_NUMBER), crds);
			} else if (bType == BlockType.METHOD) {
				processMethods(maker,
						rootEntry.getValue().get(DEFAULT_IDENTFYING_NUMBER),
						correspondingAfterFragments
								.get(DEFAULT_IDENTFYING_NUMBER), crds);
			} else {
				processOtherBlocks(maker, rootEntry.getValue(),
						correspondingAfterFragments, crds);
			}
		}
	}

	private class SingleCodeFragmentLinkMaker extends
			AbstractLocationLimitedCodeFragmentLinkMaker {

		public SingleCodeFragmentLinkMaker(FragmentLinkConditionUmpire umpire,
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
			fillRow(fragment, pairCandidateFragments);
		}

		private void fillRow(final DBCodeFragmentInfo beforeCandidate,
				final Collection<DBCodeFragmentInfo> afterCandidates) {
			// fill a row of similarity table
			final Map<DBCodeFragmentInfo, Double> similarities = new TreeMap<DBCodeFragmentInfo, Double>();
			final DBCrdInfo beforeCrd = crds.get(beforeCandidate.getCrdId());

			for (final DBCodeFragmentInfo afterFragment : afterCandidates) {
				final DBCrdInfo afterCrd = crds.get(afterFragment.getCrdId());
				final double similarity = similarityCalculator.calcSimilarity(
						beforeCrd, beforeCandidate, afterCrd, afterFragment);

				// register the similarity into the table
				// if the similarity is equal to or over than the threshold
				if (umpire
						.satisfyAllConditions(beforeCrd, afterCrd, similarity)) {
					similarityTable.changeValueAt(beforeCandidate.getId(),
							afterFragment.getId(), similarity);
					similarities.put(afterFragment, similarity);
				}
			}

			// fill a wish list for the fragment under processing
			final Queue<DBCodeFragmentInfo> wishList = sortWithValues(similarities);
			wishLists.put(beforeCandidate, wishList);
		}

		/**
		 * sort the given map with the descending order of values and return the
		 * result as a queue of keys
		 * 
		 * @param target
		 * @return
		 */
		private Queue<DBCodeFragmentInfo> sortWithValues(
				final Map<DBCodeFragmentInfo, Double> target) {
			final Queue<DBCodeFragmentInfo> result = new LinkedList<DBCodeFragmentInfo>();
			final int targetSize = target.size();

			while (result.size() < targetSize) {
				double maxValue = -1.0;
				DBCodeFragmentInfo keyHasMaxValue = null;

				for (final Map.Entry<DBCodeFragmentInfo, Double> entry : target
						.entrySet()) {
					final DBCodeFragmentInfo key = entry.getKey();
					final double value = entry.getValue();

					if (result.contains(key)) {
						continue;
					}

					if (value > maxValue) {
						maxValue = value;
						keyHasMaxValue = key;
					}
				}

				result.add(keyHasMaxValue);
			}

			return result;
		}

	}

}
