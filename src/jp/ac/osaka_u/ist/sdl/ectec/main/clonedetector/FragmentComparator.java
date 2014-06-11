package jp.ac.osaka_u.ist.sdl.ectec.main.clonedetector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;

/**
 * A class to detect code clones
 * 
 * @author k-hotta
 * 
 */
public class FragmentComparator {

	/**
	 * the id of the target combined revision
	 */
	private final long combinedRevisionId;

	/**
	 * the threshold of the size of clones
	 */
	private final int sizeThreshold;

	public FragmentComparator(final long combinedRevisionId,
			final int sizeThreshold) {
		this.combinedRevisionId = combinedRevisionId;
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
			if (codeFragment.getSize() < sizeThreshold) {
				continue;
			}

			final long hash = codeFragment.getHashForClone();
			if (codeFragmentsSortedByHash.containsKey(hash)) {
				codeFragmentsSortedByHash.get(hash).add(codeFragment);
			} else {
				final Set<DBCodeFragmentInfo> newSet = new HashSet<DBCodeFragmentInfo>();
				newSet.add(codeFragment);
				codeFragmentsSortedByHash.put(hash, newSet);
			}
		}

		final Set<Long> processedHash = new TreeSet<Long>();
		final Set<Long> toBeRemovedHash = new TreeSet<Long>();

		for (final Map.Entry<Long, Set<DBCodeFragmentInfo>> entry1 : codeFragmentsSortedByHash
				.entrySet()) {
			if (processedHash.contains(entry1.getKey())) {
				continue;
			}
			processedHash.add(entry1.getKey());

			final Set<DBCodeFragmentInfo> fragments1 = entry1.getValue();
			if (fragments1.size() < 2) {
				toBeRemovedHash.add(entry1.getKey());
				continue;
			}

			for (final Map.Entry<Long, Set<DBCodeFragmentInfo>> entry2 : codeFragmentsSortedByHash
					.entrySet()) {
				if (processedHash.contains(entry2.getKey())) {
					continue;
				}

				final Set<DBCodeFragmentInfo> fragments2 = entry2.getValue();
				if (fragments1.size() != fragments2.size()) {
					continue;
				}

				if (subsume(fragments1, fragments2)) {
					toBeRemovedHash.add(entry2.getKey());
					processedHash.add(entry2.getKey());
				} else if (subsume(fragments2, fragments1)) {
					toBeRemovedHash.add(entry1.getKey());
				}
			}
		}

		for (final long toBeRemoved : toBeRemovedHash) {
			codeFragmentsSortedByHash.remove(toBeRemoved);
		}

		// create instances of clone sets
		final Map<Long, DBCloneSetInfo> result = new TreeMap<Long, DBCloneSetInfo>();

		for (final Map.Entry<Long, Set<DBCodeFragmentInfo>> entry : codeFragmentsSortedByHash
				.entrySet()) {
			final Set<DBCodeFragmentInfo> fragmentsSet = entry.getValue();
			final List<Long> elements = new ArrayList<Long>();
			for (final DBCodeFragmentInfo element : fragmentsSet) {
				elements.add(element.getId());
			}

			if (elements.size() > 1) {
				final DBCloneSetInfo cloneSet = new DBCloneSetInfo(
						combinedRevisionId, elements);
				result.put(cloneSet.getId(), cloneSet);
			}
		}

		return Collections.unmodifiableMap(result);
	}

	private boolean subsume(final Set<DBCodeFragmentInfo> fragments1,
			final Set<DBCodeFragmentInfo> fragments2) {
		assert fragments1.size() == fragments2.size();

		final SortedSet<DBCodeFragmentInfo> fragments1Sorted = new TreeSet<DBCodeFragmentInfo>(
				new FragmentSorter());
		fragments1Sorted.addAll(fragments1);
		final SortedSet<DBCodeFragmentInfo> fragments2Sorted = new TreeSet<DBCodeFragmentInfo>(
				new FragmentSorter());
		fragments2Sorted.addAll(fragments2);

		final DBCodeFragmentInfo[] fragments1Array = fragments1Sorted
				.toArray(new DBCodeFragmentInfo[0]);
		final DBCodeFragmentInfo[] fragments2Array = fragments2Sorted
				.toArray(new DBCodeFragmentInfo[0]);

		for (int i = 0; i < fragments1Array.length; i++) {
			final DBCodeFragmentInfo fragment1 = fragments1Array[i];
			final DBCodeFragmentInfo fragment2 = fragments2Array[i];

			if (!subsume(fragment1, fragment2)) {
				return false;
			}
		}

		return true;
	}

	private boolean subsume(final DBCodeFragmentInfo fragment1,
			final DBCodeFragmentInfo fragment2) {
		if (fragment1.getOwnerRepositoryId() != fragment2
				.getOwnerRepositoryId()) {
			return false;
		}

		if (fragment1.getOwnerFileId() != fragment2.getOwnerFileId()) {
			return false;
		}

		final int start1 = fragment1.getStartLine();
		final int start2 = fragment2.getStartLine();
		final int end1 = fragment1.getEndLine();
		final int end2 = fragment2.getEndLine();

		return (start1 <= start2 && end1 >= end2);
	}

	private class FragmentSorter implements Comparator<DBCodeFragmentInfo> {

		@Override
		public int compare(DBCodeFragmentInfo o1, DBCodeFragmentInfo o2) {
			if (o1.getOwnerRepositoryId() < o2.getOwnerRepositoryId()) {
				return -1;
			} else if (o1.getOwnerRepositoryId() > o2.getOwnerRepositoryId()) {
				return 1;
			}

			if (o1.getOwnerFileId() < o2.getOwnerFileId()) {
				return -1;
			} else if (o1.getOwnerFileId() > o2.getOwnerFileId()) {
				return 1;
			}

			if (o1.getStartLine() < o2.getStartLine()) {
				return -1;
			} else if (o1.getStartLine() > o2.getStartLine()) {
				return 1;
			}

			if (o1.getEndLine() < o2.getEndLine()) {
				return -1;
			} else if (o1.getEndLine() > o2.getEndLine()) {
				return 1;
			}

			return 0;
		}

	}

}
