package jp.ac.osaka_u.ist.sdl.ectec.cdt;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.ectec.settings.MessagePrinter;

public class CloneSetMakingThread implements Runnable {

	private final AtomicInteger index;

	private final Long[] keys;

	private final ConcurrentMap<Long, Set<InstantCodeFragmentInfo>> fragmentsCategorizedByHash;

	private final ConcurrentMap<Long, CloneSet> detectedCloneSets;

	private final ConcurrentMap<String, Set<CloneSet>> cloneSetsCategorizedByPath;

	public CloneSetMakingThread(
			final AtomicInteger index,
			final Long[] keys,
			final ConcurrentMap<Long, Set<InstantCodeFragmentInfo>> fragmentsCategorizedByHash,
			final ConcurrentMap<Long, CloneSet> detectedCloneSets,
			final ConcurrentMap<String, Set<CloneSet>> cloneSetsCategorizedByPath) {
		this.index = index;
		this.keys = keys;
		this.fragmentsCategorizedByHash = fragmentsCategorizedByHash;
		this.detectedCloneSets = detectedCloneSets;
		this.cloneSetsCategorizedByPath = cloneSetsCategorizedByPath;
	}

	public final Map<Long, CloneSet> getDetectedCloneSets() {
		return detectedCloneSets;
	}

	public final Map<String, Set<CloneSet>> getCloneSetsCategorizedByHash() {
		return cloneSetsCategorizedByPath;
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
				final CloneSet cloneSet = new CloneSet(fragments);
				detectedCloneSets.put(cloneSet.getId(), cloneSet);

				final InstantCodeFragmentInfo firstFragment = cloneSet
						.getFirstElement();
				final String path = firstFragment.getFilePath();

				if (!cloneSetsCategorizedByPath.containsKey(path)) {
					synchronized (cloneSetsCategorizedByPath) {
						final Set<CloneSet> newSet = new HashSet<CloneSet>();
						cloneSetsCategorizedByPath.put(path, newSet);
					}
				}

				cloneSetsCategorizedByPath.get(path).add(cloneSet);
			}

			fragmentsCategorizedByHash.remove(key);
			MessagePrinter.println("\t[" + currentIndex + "/" + keys.length
					+ "] hash value " + key + " was processed");
		}
	}

}
