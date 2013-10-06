package jp.ac.osaka_u.ist.sdl.ectec.analyzer.clonedetector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.data.CloneSetInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.CodeFragmentInfo;

/**
 * A class to detect code clones
 * 
 * @author k-hotta
 * 
 */
public class BlockBasedCloneDetector {

	/**
	 * the id of the target revision
	 */
	private final long revisionId;

	/**
	 * the threshold of the size of clones
	 */
	private final int sizeThreshold;

	public BlockBasedCloneDetector(final long revisionId,
			final int sizeThreshold) {
		this.revisionId = revisionId;
		this.sizeThreshold = sizeThreshold;
	}

	/**
	 * detect clones
	 * 
	 * @param codeFragments
	 * @return
	 */
	public Map<Long, CloneSetInfo> detectClones(
			final Map<Long, CodeFragmentInfo> codeFragments) {
		// sort fragments with their hash values
		final Map<Long, Set<CodeFragmentInfo>> codeFragmentsSortedByHash = new HashMap<Long, Set<CodeFragmentInfo>>();
		for (final Map.Entry<Long, CodeFragmentInfo> entry : codeFragments
				.entrySet()) {
			final CodeFragmentInfo codeFragment = entry.getValue();
			final long hash = codeFragment.getHashForClone();
			if (codeFragmentsSortedByHash.containsKey(hash)) {
				codeFragmentsSortedByHash.get(hash).add(codeFragment);
			} else {
				final Set<CodeFragmentInfo> newSet = new HashSet<CodeFragmentInfo>();
				newSet.add(codeFragment);
				codeFragmentsSortedByHash.put(hash, newSet);
			}
		}

		// create instances of clone sets
		final Map<Long, CloneSetInfo> result = new TreeMap<Long, CloneSetInfo>();

		for (final Map.Entry<Long, Set<CodeFragmentInfo>> entry : codeFragmentsSortedByHash
				.entrySet()) {
			final Set<CodeFragmentInfo> fragmentsSet = entry.getValue();
			if (fragmentsSet.size() > 1) {
				final List<Long> elements = new ArrayList<Long>();
				for (final CodeFragmentInfo element : fragmentsSet) {
					if (element.getSize() >= sizeThreshold) {
						elements.add(element.getId());
					}
				}

				if (elements.size() > 1) {
					final CloneSetInfo cloneSet = new CloneSetInfo(revisionId,
							elements);
					result.put(cloneSet.getId(), cloneSet);
				}
			}
		}

		return Collections.unmodifiableMap(result);
	}

}
