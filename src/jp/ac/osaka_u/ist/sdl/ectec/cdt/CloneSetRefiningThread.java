package jp.ac.osaka_u.ist.sdl.ectec.cdt;

import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import jp.ac.osaka_u.ist.sdl.ectec.settings.MessagePrinter;

public class CloneSetRefiningThread implements Runnable {

	private final AtomicLong index;

	private final ConcurrentMap<Long, CloneSet> cloneSets;

	private final ConcurrentMap<String, Set<CloneSet>> cloneSetsCategorizedByPath;

	private final Set<Long> subsumedCloneSets;

	private final int length;

	public CloneSetRefiningThread(
			final AtomicLong index,
			final ConcurrentMap<Long, CloneSet> cloneSets,
			final ConcurrentMap<String, Set<CloneSet>> cloneSetsCategorizedByPath) {
		this.index = index;
		this.cloneSets = cloneSets;
		this.cloneSetsCategorizedByPath = cloneSetsCategorizedByPath;
		this.subsumedCloneSets = new TreeSet<Long>();
		this.length = cloneSets.size();
	}

	public Set<Long> getSubsumedCloneSets() {
		return subsumedCloneSets;
	}

	@Override
	public void run() {
		while (true) {
			final long currentIndex = index.getAndIncrement();
			if (currentIndex >= length) {
				break;
			}

			MessagePrinter.println("\t[" + currentIndex + "/" + length
					+ "] judging " + currentIndex + " elements");

			final CloneSet currentCloneSet = cloneSets.get(currentIndex);
			final InstantCodeFragmentInfo firstElement = currentCloneSet
					.getFirstElement();

			if (cloneSetsCategorizedByPath.containsKey(firstElement
					.getFilePath())) {
				final Set<CloneSet> tmpSets = cloneSetsCategorizedByPath
						.get(firstElement.getFilePath());

				for (final CloneSet tmpSet : tmpSets) {
					if (tmpSet.getId() == currentCloneSet.getId()) {
						continue;
					}

					if (tmpSet.subsume(currentCloneSet)) {
						subsumedCloneSets.add(currentCloneSet.getId());
						break;
					}
				}
			}
		}
	}
}
