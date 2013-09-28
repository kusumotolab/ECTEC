package jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.linker;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.similarity.ICRDSimilarityCalculator;
import jp.ac.osaka_u.ist.sdl.ectec.data.CRD;
import jp.ac.osaka_u.ist.sdl.ectec.data.CodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.CodeFragmentLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.util.Table;

/**
 * A class to detect 1 by 1 links of code fragments
 * 
 * @author k-hotta
 * 
 */
public class SingleCodeFragmentLinker implements ICodeFragmentLinker {
	
	@Override
	public Map<Long, CodeFragmentLinkInfo> detectFragmentPairs(
			Collection<CodeFragmentInfo> beforeBlocks,
			Collection<CodeFragmentInfo> afterBlocks,
			ICRDSimilarityCalculator similarityCalculator,
			long similarityThreshold, Map<Long, CRD> crds) {
		Table<Long, Long, Double> similarityTable = null;
		Map<CodeFragmentInfo, Queue<CodeFragmentInfo>> wishLists = null;

		return null;
	}
	

	private void fillSimilarityTableAndWishList(
			final Set<CodeFragmentInfo> beforeFragments,
			final Set<CodeFragmentInfo> afterFragments,
			final Table<Long, Long, Double> similarityTable,
			final Map<CodeFragmentInfo, Queue<CodeFragmentInfo>> wishLists,
			final ICRDSimilarityCalculator similarityCalculator,
			final long similarityThreshold, final Map<Long, CRD> crds) {
		for (final CodeFragmentInfo beforeFragment : beforeFragments) {
			final Map<CodeFragmentInfo, Double> similarities = new TreeMap<CodeFragmentInfo, Double>();

			for (final CodeFragmentInfo afterFragment : afterFragments) {
				final double similarity = similarityCalculator.calcSimilarity(
						crds.get(beforeFragment.getCrdId()),
						crds.get(afterFragment.getCrdId()));
				if (similarity >= similarityThreshold) {
					similarityTable.changeValueAt(beforeFragment.getId(),
							afterFragment.getId(), similarity);
					similarities.put(afterFragment, similarity);
				}
			}

			final Queue<CodeFragmentInfo> wishList = sortWithValues(similarities);
			wishLists.put(beforeFragment, wishList);
		}
	}

	private Queue<CodeFragmentInfo> sortWithValues(
			final Map<CodeFragmentInfo, Double> target) {
		final Queue<CodeFragmentInfo> result = new LinkedList<CodeFragmentInfo>();
		final int targetSize = target.size();

		while (result.size() < targetSize) {
			double maxValue = -1.0;
			CodeFragmentInfo keyHasMaxValue = null;

			for (final Map.Entry<CodeFragmentInfo, Double> entry : target.entrySet()) {
				final CodeFragmentInfo key = entry.getKey();
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
