package jp.ac.osaka_u.ist.sdl.instantcdt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ClonePairDetector {

	private final int threadsCount;

	public ClonePairDetector(final int threadsCount) {
		this.threadsCount = threadsCount;
	}

	public Collection<ClonePair> detectClonePairs(
			final Map<String, List<InstantCodeFragmentInfo>> fragments) {
		final Set<InstantCodeFragmentInfo> fragmentsAsSet = new HashSet<InstantCodeFragmentInfo>();
		for (Map.Entry<String, List<InstantCodeFragmentInfo>> entry : fragments
				.entrySet()) {
			fragmentsAsSet.addAll(entry.getValue());
		}

		return detectClonePairs(fragmentsAsSet);
	}

	public Collection<ClonePair> detectClonePairs(
			final Collection<InstantCodeFragmentInfo> fragments) {
		final ConcurrentMap<Long, Set<InstantCodeFragmentInfo>> fragmentsCategorizedByHash = categorizeFragments(fragments);
		fragments.clear();

		final Long[] keys = fragmentsCategorizedByHash.keySet().toArray(
				new Long[] {});
		final AtomicInteger index = new AtomicInteger(0);

		final ClonePairMakingThread[] makingThreads = new ClonePairMakingThread[threadsCount];
		final Thread[] threads = new Thread[threadsCount];

		for (int i = 0; i < threadsCount; i++) {
			final ClonePairMakingThread makingThread = new ClonePairMakingThread(
					index, keys, fragmentsCategorizedByHash);
			makingThreads[i] = makingThread;
			threads[i] = new Thread(makingThread);
			threads[i].start();
		}

		for (final Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		fragmentsCategorizedByHash.clear();

		System.out.println();
		System.out.println("merging the results of all the threads ... ");

		final ConcurrentMap<Long, ClonePair> detectedPairs = new ConcurrentHashMap<Long, ClonePair>();
		final ConcurrentMap<String, Set<ClonePair>> clonePairsCategorizedByPath = new ConcurrentHashMap<String, Set<ClonePair>>();
		long pairsCount = 0;

		for (int i = 0; i < makingThreads.length; i++) {
			final List<ClonePair> pairs = makingThreads[i]
					.getCreatedClonePairs();
			final Map<String, Set<ClonePair>> categorizedByPath = makingThreads[i]
					.getClonePairsCategoriedByHash();

			for (final ClonePair pair : pairs) {
				detectedPairs.put(pairsCount++, pair);
			}
			for (final Map.Entry<String, Set<ClonePair>> entry : categorizedByPath
					.entrySet()) {
				if (clonePairsCategorizedByPath.containsKey(entry.getKey())) {
					clonePairsCategorizedByPath.get(entry.getKey()).addAll(
							entry.getValue());
				} else {
					clonePairsCategorizedByPath.put(entry.getKey(),
							entry.getValue());
				}
			}

			makingThreads[i] = null;
		}

		System.out.println("\t" + detectedPairs.size()
				+ " clone pairs are detected");
		System.out.println();

		return refinePairs(detectedPairs, clonePairsCategorizedByPath);
	}

	private Collection<ClonePair> refinePairs(
			final ConcurrentMap<Long, ClonePair> detectedPairs,
			final ConcurrentMap<String, Set<ClonePair>> clonePairsCategorizedByPath) {
		System.out.println("removing subsumed clone pairs ...");

		final AtomicLong index = new AtomicLong();

		final ClonePairRefiningThread[] refiningThreads = new ClonePairRefiningThread[threadsCount];
		final Thread[] threads = new Thread[threadsCount];

		for (int i = 0; i < threadsCount; i++) {
			final ClonePairRefiningThread refiningThread = new ClonePairRefiningThread(
					index, detectedPairs, clonePairsCategorizedByPath);
			refiningThreads[i] = refiningThread;
			threads[i] = new Thread(refiningThread);
			threads[i].start();
		}

		for (final Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		for (final ClonePairRefiningThread thread : refiningThreads) {
			for (final long subsumedClonePair : thread.getSubsumedClonePairs()) {
				detectedPairs.remove(subsumedClonePair);
			}
		}

		return Collections.unmodifiableCollection(detectedPairs.values());
	}

	public List<ClonePair> detectClonePairsOld(
			final Collection<InstantCodeFragmentInfo> fragments) {
		final Map<Long, Set<InstantCodeFragmentInfo>> fragmentsCategorizedByHash = categorizeFragments(fragments);

		final List<ClonePair> result = new ArrayList<ClonePair>();
		final Map<String, Set<ClonePair>> clonePairsCategorizedByPath = new HashMap<String, Set<ClonePair>>();

		final long length = fragmentsCategorizedByHash.size();
		long count = 0;

		for (final Map.Entry<Long, Set<InstantCodeFragmentInfo>> entry : fragmentsCategorizedByHash
				.entrySet()) {
			if (++count % 10000 == 0) {
				System.out.println("\t[" + count + "/" + length
						+ "] 2nd step processed " + count + " elements");
			}
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
					if (processed.contains(fragment2)) {
						continue;
					}

					if (count == 27412) {
						System.out.println(cloneSet.size());
					}
					System.out.println(count);
					final ClonePair clonePair = new ClonePair(fragment1,
							fragment2);
					final Set<ClonePair> subsumedPairs = new HashSet<ClonePair>();
					boolean subsumed = false;

					if (clonePairsCategorizedByPath.containsKey(fragment1
							.getFilePath())) {
						final Set<ClonePair> tmpPairs = clonePairsCategorizedByPath
								.get(fragment1.getFilePath());

						for (final ClonePair tmpPair : tmpPairs) {
							if (tmpPair.subsume(clonePair)) {
								subsumed = true;
								break;
							}
							if (clonePair.subsume(tmpPair)) {
								subsumedPairs.add(tmpPair);
							}
						}
					}

					if (!subsumed) {
						result.add(clonePair);

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

					for (final ClonePair subsumedPair : subsumedPairs) {
						result.remove(subsumedPair);
						clonePairsCategorizedByPath.get(
								subsumedPair.getFragment1().getFilePath())
								.remove(subsumedPair);
						clonePairsCategorizedByPath.get(
								subsumedPair.getFragment2().getFilePath())
								.remove(subsumedPair);
					}

				}
				processed.add(fragment1);
			}
		}

		System.out.println("\t[" + count + "/" + length
				+ "] 2nd step processed all elements");

		return result;
	}

	private ConcurrentMap<Long, Set<InstantCodeFragmentInfo>> categorizeFragments(
			Collection<InstantCodeFragmentInfo> fragments) {
		final ConcurrentMap<Long, Set<InstantCodeFragmentInfo>> result = new ConcurrentHashMap<Long, Set<InstantCodeFragmentInfo>>();

		final long length = fragments.size();
		long count = 0;

		for (final InstantCodeFragmentInfo fragment : fragments) {
			if (result.containsKey(fragment.getHash())) {
				result.get(fragment.getHash()).add(fragment);
			} else {
				final Set<InstantCodeFragmentInfo> newSet = new HashSet<InstantCodeFragmentInfo>();
				newSet.add(fragment);
				result.put(fragment.getHash(), newSet);
			}
			if (++count % 10000 == 0) {
				System.out.println("\t[" + count + "/" + length
						+ "] 1st step processed " + count + " elements");
			}
		}

		System.out.println("\t[" + count + "/" + length
				+ "] 1st step processed all elements");

		return result;
	}

}
