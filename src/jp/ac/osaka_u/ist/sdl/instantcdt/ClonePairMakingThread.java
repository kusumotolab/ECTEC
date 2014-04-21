package jp.ac.osaka_u.ist.sdl.instantcdt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ClonePairMakingThread implements Runnable {

	private final AtomicInteger index;

	private final Long[] keys;

	private final ConcurrentMap<Long, Set<InstantCodeFragmentInfo>> fragmentsCategorizedByHash;

	private final List<ClonePair> createdClonePairs;

	private final Map<String, Set<ClonePair>> clonePairsCategorizedByPath;

	public ClonePairMakingThread(
			final AtomicInteger index,
			final Long[] keys,
			final ConcurrentMap<Long, Set<InstantCodeFragmentInfo>> fragmentsCategorizedByHash) {
		this.index = index;
		this.keys = keys;
		this.fragmentsCategorizedByHash = fragmentsCategorizedByHash;
		this.createdClonePairs = new ArrayList<ClonePair>();
		this.clonePairsCategorizedByPath = new HashMap<String, Set<ClonePair>>();
	}

	public final List<ClonePair> getCreatedClonePairs() {
		return Collections.unmodifiableList(createdClonePairs);
	}

	public final Map<String, Set<ClonePair>> getClonePairsCategoriedByHash() {
		return Collections.unmodifiableMap(clonePairsCategorizedByPath);
	}

	@Override
	public void run() {
		while (true) {
			final int currentIndex = index.getAndIncrement();
			if (currentIndex >= keys.length) {
				break;
			}

			final long key = keys[currentIndex];
			final Set<InstantCodeFragmentInfo> fragments = fragmentsCategorizedByHash
					.get(key);

			if (fragments.size() >= 2) {
				final Set<InstantCodeFragmentInfo> processed = new HashSet<InstantCodeFragmentInfo>();

				for (final InstantCodeFragmentInfo fragment1 : fragments) {
					for (final InstantCodeFragmentInfo fragment2 : fragments) {
						if (fragment1 == fragment2) {
							continue;
						}
						if (processed.contains(fragment2)) {
							continue;
						}

						final ClonePair clonePair = new ClonePair(fragment1,
								fragment2);
						createdClonePairs.add(clonePair);
						
						if (clonePairsCategorizedByPath.containsKey(fragment1
								.getFilePath())) {
							clonePairsCategorizedByPath.get(
									fragment1.getFilePath()).add(clonePair);
						} else {
							final Set<ClonePair> newSet = new HashSet<ClonePair>();
							newSet.add(clonePair);
							clonePairsCategorizedByPath.put(
									fragment1.getFilePath(), newSet);
						}

						if (clonePairsCategorizedByPath.containsKey(fragment2
								.getFilePath())) {
							clonePairsCategorizedByPath.get(
									fragment2.getFilePath()).add(clonePair);
						} else {
							final Set<ClonePair> newSet = new HashSet<ClonePair>();
							newSet.add(clonePair);
							clonePairsCategorizedByPath.put(
									fragment2.getFilePath(), newSet);
						}
					}
					processed.add(fragment1);
				}
			}

			fragmentsCategorizedByHash.remove(key);
			System.out.println("\t[" + currentIndex + "/" + keys.length
					+ "] hash value " + key + " was processed");
		}
	}

}
