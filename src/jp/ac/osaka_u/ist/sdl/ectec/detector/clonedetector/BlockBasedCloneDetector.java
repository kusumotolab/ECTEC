package jp.ac.osaka_u.ist.sdl.ectec.detector.clonedetector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;

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
	public Map<Long, DBCloneSetInfo> detectClones(
			final Map<Long, DBCodeFragmentInfo> codeFragments) {
		// sort fragments with their hash values
		final Map<Long, Set<DBCodeFragmentInfo>> codeFragmentsSortedByHash = new HashMap<Long, Set<DBCodeFragmentInfo>>();
		for (final Map.Entry<Long, DBCodeFragmentInfo> entry : codeFragments
				.entrySet()) {
			final DBCodeFragmentInfo codeFragment = entry.getValue();
			final long hash = codeFragment.getHashForClone();
			if (codeFragmentsSortedByHash.containsKey(hash)) {
				codeFragmentsSortedByHash.get(hash).add(codeFragment);
			} else {
				final Set<DBCodeFragmentInfo> newSet = new HashSet<DBCodeFragmentInfo>();
				newSet.add(codeFragment);
				codeFragmentsSortedByHash.put(hash, newSet);
			}
		}

		// create instances of clone sets
		final Map<Long, DBCloneSetInfo> result = new TreeMap<Long, DBCloneSetInfo>();

		for (final Map.Entry<Long, Set<DBCodeFragmentInfo>> entry : codeFragmentsSortedByHash
				.entrySet()) {
			final Set<DBCodeFragmentInfo> fragmentsSet = entry.getValue();
			if (fragmentsSet.size() > 1) {
				final List<Long> elements = new ArrayList<Long>();
				for (final DBCodeFragmentInfo element : fragmentsSet) {
					if (element.getSize() >= sizeThreshold) {
						elements.add(element.getId());
					}
				}

				if (elements.size() > 1) {
					final DBCloneSetInfo cloneSet = new DBCloneSetInfo(revisionId,
							elements);
					result.put(cloneSet.getId(), cloneSet);
				}
			}
		}

		return Collections.unmodifiableMap(result);
	}

}
