package jp.ac.osaka_u.ist.sdl.ectec.cdt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ClonePairDetector {

	public List<ClonePair> detectClonePairs(
			final Map<String, List<InstantCodeFragmentInfo>> fragments) {
		final Set<InstantCodeFragmentInfo> fragmentsAsSet = new HashSet<InstantCodeFragmentInfo>();
		for (Map.Entry<String, List<InstantCodeFragmentInfo>> entry : fragments
				.entrySet()) {
			fragmentsAsSet.addAll(entry.getValue());
		}

		return detectClonePairs(fragmentsAsSet);
	}

	public List<ClonePair> detectClonePairs(
			final Collection<InstantCodeFragmentInfo> fragments) {
		final Map<Long, Set<InstantCodeFragmentInfo>> fragmentsCategorizedByHash = categorizeFragments(fragments);

		final List<ClonePair> result = new ArrayList<ClonePair>();

		for (final Map.Entry<Long, Set<InstantCodeFragmentInfo>> entry : fragmentsCategorizedByHash
				.entrySet()) {
			final Set<InstantCodeFragmentInfo> cloneSet = entry.getValue();
			if (cloneSet.size() < 2) {
				continue;
			}

			final Set<InstantCodeFragmentInfo> processed = new HashSet<InstantCodeFragmentInfo>();

			for (final InstantCodeFragmentInfo fragment1 : cloneSet) {
				for (final InstantCodeFragmentInfo fragment2 : cloneSet) {
					if (fragment1 == fragment2) {
						continue;
					}
					if (processed.contains(fragment1)) {
						continue;
					}

					final ClonePair clonePair = new ClonePair(fragment1,
							fragment2);
					result.add(clonePair);
				}
				processed.add(fragment1);
			}
		}

		return result;
	}

	private Map<Long, Set<InstantCodeFragmentInfo>> categorizeFragments(
			Collection<InstantCodeFragmentInfo> fragments) {
		final Map<Long, Set<InstantCodeFragmentInfo>> result = new TreeMap<Long, Set<InstantCodeFragmentInfo>>();

		for (final InstantCodeFragmentInfo fragment : fragments) {
			if (result.containsKey(fragment.getHash())) {
				result.get(fragment.getHash()).add(fragment);
			} else {
				final Set<InstantCodeFragmentInfo> newSet = new HashSet<InstantCodeFragmentInfo>();
				newSet.add(fragment);
				result.put(fragment.getHash(), newSet);
			}
		}

		return result;
	}

}
