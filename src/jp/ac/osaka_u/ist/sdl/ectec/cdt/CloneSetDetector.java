package jp.ac.osaka_u.ist.sdl.ectec.cdt;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import jp.ac.osaka_u.ist.sdl.ectec.settings.MessagePrinter;

public class CloneSetDetector {

	private final int threadsCount;

	public CloneSetDetector(final int threadsCount) {
		this.threadsCount = threadsCount;
	}

	public Collection<CloneSet> detectCloneSets(
			final Map<String, List<InstantCodeFragmentInfo>> fragments) {
		final Set<InstantCodeFragmentInfo> fragmentsAsSet = new HashSet<InstantCodeFragmentInfo>();
		for (Map.Entry<String, List<InstantCodeFragmentInfo>> entry : fragments
				.entrySet()) {
			fragmentsAsSet.addAll(entry.getValue());
		}

		return detectCloneSets(fragmentsAsSet);
	}

	public Collection<CloneSet> detectCloneSets(
			final Collection<InstantCodeFragmentInfo> fragments) {
		final ConcurrentMap<Long, Set<InstantCodeFragmentInfo>> fragmentsCategorizedByHash = categorizeFragments(fragments);
		final Long[] keys = fragmentsCategorizedByHash.keySet().toArray(
				new Long[] {});
		final AtomicInteger index = new AtomicInteger(0);
		final CloneSetMakingThread[] makingThreads = new CloneSetMakingThread[threadsCount];
		final Thread[] threads = new Thread[threadsCount];
		final ConcurrentMap<Long, CloneSet> detectedSets = new ConcurrentHashMap<Long, CloneSet>();
		final ConcurrentMap<String, Set<CloneSet>> cloneSetsCategorizedByPath = new ConcurrentHashMap<String, Set<CloneSet>>();

		for (int i = 0; i < threadsCount; i++) {
			final CloneSetMakingThread makingThread = new CloneSetMakingThread(
					index, keys, fragmentsCategorizedByHash, detectedSets,
					cloneSetsCategorizedByPath);
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

		MessagePrinter.println("\t" + detectedSets.size()
				+ " clone sets are detected");
		MessagePrinter.println();

		return refineSets(detectedSets, cloneSetsCategorizedByPath);
	}

	private Collection<CloneSet> refineSets(
			final ConcurrentMap<Long, CloneSet> cloneSets,
			final ConcurrentMap<String, Set<CloneSet>> cloneSetsCategorizedByPath) {
		MessagePrinter.println("removing subsumed clone sets ...");

		final AtomicLong index = new AtomicLong();

		final CloneSetRefiningThread[] refiningThreads = new CloneSetRefiningThread[threadsCount];
		final Thread[] threads = new Thread[threadsCount];

		for (int i = 0; i < threadsCount; i++) {
			final CloneSetRefiningThread refiningThread = new CloneSetRefiningThread(
					index, cloneSets, cloneSetsCategorizedByPath);
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

		for (final CloneSetRefiningThread thread : refiningThreads) {
			for (final long subsumedCloneSet : thread.getSubsumedCloneSets()) {
				cloneSets.remove(subsumedCloneSet);
			}
		}

		return Collections.unmodifiableCollection(cloneSets.values());
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
