package jp.ac.osaka_u.ist.sdl.ectec.main.linker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCrdInfo;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.similarity.ICRDSimilarityCalculator;
import jp.ac.osaka_u.ist.sdl.ectec.util.Table;

/**
 * A class to detect 1 by 1 links of code fragments
 * 
 * @author k-hotta
 * 
 */
public class SingleCodeFragmentLinker implements ICodeFragmentLinker {

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

	/**
	 * detect pairs of fragments as a map
	 * 
	 * @param beforeFragments
	 * @param afterFragments
	 * @param similarityCalculator
	 * @param similarityThreshold
	 * @param crds
	 * @return
	 */
	private Map<DBCodeFragmentInfo, DBCodeFragmentInfo> detectPairs(
			Collection<DBCodeFragmentInfo> beforeFragments,
			Collection<DBCodeFragmentInfo> afterFragments,
			ICRDSimilarityCalculator similarityCalculator,
			FragmentLinkConditionUmpire umpire, Map<Long, DBCrdInfo> crds) {
		// the result (detected pairs of fragments) with the shuffled
		// the key is an AFTER fragment and the value is a BEFORE fragment
		final Map<DBCodeFragmentInfo, DBCodeFragmentInfo> reversedResult = new TreeMap<DBCodeFragmentInfo, DBCodeFragmentInfo>();

		// evacuate the original collections
		final Set<DBCodeFragmentInfo> beforeFragmentsSet = new HashSet<DBCodeFragmentInfo>();
		beforeFragmentsSet.addAll(beforeFragments);
		final Set<DBCodeFragmentInfo> afterFragmentsSet = new HashSet<DBCodeFragmentInfo>();
		afterFragmentsSet.addAll(afterFragments);

		// detect pairs of fragments whose crds are equal to each other
		// and remove them
		removeSameCrdFragmentPairs(beforeFragmentsSet, afterFragmentsSet, crds);

		/*
		 * initialize the similarity table and with lists for each of remaining
		 * before fragments
		 */
		final Table<Long, Long, Double> similarityTable = new Table<Long, Long, Double>();
		final Map<DBCodeFragmentInfo, Queue<DBCodeFragmentInfo>> wishLists = new TreeMap<DBCodeFragmentInfo, Queue<DBCodeFragmentInfo>>();

		fillSimilarityTableAndWishList(beforeFragmentsSet, afterFragmentsSet,
				similarityTable, wishLists, similarityCalculator, umpire, crds);

		final List<DBCodeFragmentInfo> unmarriedBeforeFragments = new ArrayList<DBCodeFragmentInfo>();
		unmarriedBeforeFragments.addAll(beforeFragmentsSet);

		while (true) {

			// make every of unmarried before fragments attack the most favorite
			// after fragment
			// the contents in reversedResult and unmarriedBeforeMethods will
			// change by this method call
			if (processAllProposes(reversedResult, unmarriedBeforeFragments,
					similarityTable, wishLists)) {
				break;
			}

		}

		final Map<DBCodeFragmentInfo, DBCodeFragmentInfo> result = tailorReversedMap(reversedResult);
		return result;
	}

	/**
	 * initialize the given similarity table and the given wish lists with the
	 * specified values
	 * 
	 * @param beforeFragments
	 * @param afterFragments
	 * @param similarityTable
	 * @param wishLists
	 * @param similarityCalculator
	 * @param similarityThreshold
	 * @param crds
	 */
	private void fillSimilarityTableAndWishList(
			final Collection<DBCodeFragmentInfo> beforeFragments,
			final Collection<DBCodeFragmentInfo> afterFragments,
			final Table<Long, Long, Double> similarityTable,
			final Map<DBCodeFragmentInfo, Queue<DBCodeFragmentInfo>> wishLists,
			final ICRDSimilarityCalculator similarityCalculator,
			final FragmentLinkConditionUmpire umpire, final Map<Long, DBCrdInfo> crds) {
		for (final DBCodeFragmentInfo beforeFragment : beforeFragments) {
			// fill a row of similarity table
			final Map<DBCodeFragmentInfo, Double> similarities = new TreeMap<DBCodeFragmentInfo, Double>();
			final DBCrdInfo beforeCrd = crds.get(beforeFragment.getCrdId());

			for (final DBCodeFragmentInfo afterFragment : afterFragments) {
				final DBCrdInfo afterCrd = crds.get(afterFragment.getCrdId());
				final double similarity = similarityCalculator.calcSimilarity(
						beforeCrd, afterCrd);

				// register the similarity into the table
				// if the similarity is equal to or over than the threshold
				if (umpire
						.satisfyAllConditions(beforeCrd, afterCrd, similarity)) {
					similarityTable.changeValueAt(beforeFragment.getId(),
							afterFragment.getId(), similarity);
					similarities.put(afterFragment, similarity);
				}
			}

			// fill a wish list for the fragment under processing
			final Queue<DBCodeFragmentInfo> wishList = sortWithValues(similarities);
			wishLists.put(beforeFragment, wishList);
		}
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

	private void removeSameCrdFragmentPairs(
			final Collection<DBCodeFragmentInfo> beforeFragments,
			final Collection<DBCodeFragmentInfo> afterFragments,
			final Map<Long, DBCrdInfo> crds) {
		final Set<DBCodeFragmentInfo> processedBeforeFragments = new HashSet<DBCodeFragmentInfo>();
		final Set<DBCodeFragmentInfo> processedAfterFragments = new HashSet<DBCodeFragmentInfo>();

		for (final DBCodeFragmentInfo beforeFragment : beforeFragments) {
			for (final DBCodeFragmentInfo afterFragment : afterFragments) {
				if (processedAfterFragments.contains(afterFragment)) {
					continue;
				}
				if (crds.get(beforeFragment.getCrdId()).equals(
						crds.get(afterFragment.getCrdId()))) {
					processedBeforeFragments.add(beforeFragment);
					processedAfterFragments.add(afterFragment);
				}
			}
		}

		beforeFragments.removeAll(processedBeforeFragments);
		afterFragments.removeAll(processedAfterFragments);
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
		// the set of code fragments which detect their partners in this loop
		final Set<DBCodeFragmentInfo> marriedBeforeFragments = new HashSet<DBCodeFragmentInfo>();

		// the set of code fragments which are said good-bye by their partners
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

			// propose to the most favorite after fragment
			final DBCodeFragmentInfo proposedMethod = wishLists.get(
					proposingFragment).poll();

			// there are no candidate fragments that this fragment can propose
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
				// fragment is more similar to the proposed fragment than the
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

}
