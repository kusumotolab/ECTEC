package jp.ac.osaka_u.ist.sdl.instantcdt;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class ClonePairRefiningThread implements Runnable {

	private final AtomicLong index;

	private final ConcurrentMap<Long, ClonePair> clonePairs;

	private final ConcurrentMap<String, Set<ClonePair>> clonePairsCategorizedByPath;

	private final Set<Long> subsumedClonePairs;
	
	private final int length;

	public ClonePairRefiningThread(
			final AtomicLong index,
			final ConcurrentMap<Long, ClonePair> clonePairs,
			final ConcurrentMap<String, Set<ClonePair>> clonePairsCategorizedByPath) {
		this.index = index;
		this.clonePairs = clonePairs;
		this.clonePairsCategorizedByPath = clonePairsCategorizedByPath;
		this.subsumedClonePairs = new HashSet<Long>();
		this.length = clonePairs.size();
	}

	public final Set<Long> getSubsumedClonePairs() {
		return Collections.unmodifiableSet(subsumedClonePairs);
	}

	@Override
	public void run() {
		while (true) {
			final long currentIndex = index.getAndIncrement();
			if (currentIndex >= length) {
				break;
			}

			System.out.println("\t[" + currentIndex + "/"
					+ length + "] judging " + currentIndex
					+ " elements");

			final ClonePair currentClonePair = clonePairs.get(currentIndex);
			final InstantCodeFragmentInfo fragment1 = currentClonePair
					.getFragment1();

			if (clonePairsCategorizedByPath
					.containsKey(fragment1.getFilePath())) {
				final Set<ClonePair> tmpPairs = clonePairsCategorizedByPath
						.get(fragment1.getFilePath());

				for (final ClonePair tmpPair : tmpPairs) {
					if (tmpPair.equals(currentClonePair)) {
						continue;
					}
					if (tmpPair.subsume(currentClonePair)) {
						subsumedClonePairs.add(currentIndex);
						break;
					}
				}
			}
		}
	}

}
