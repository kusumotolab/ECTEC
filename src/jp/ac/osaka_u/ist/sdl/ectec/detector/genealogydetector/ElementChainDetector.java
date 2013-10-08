package jp.ac.osaka_u.ist.sdl.ectec.detector.genealogydetector;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBElementLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.LinkElementRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.settings.MessagePrinter;

/**
 * A class to detect element chains
 * 
 * @author k-hotta
 * 
 * @param <G>
 * @param <L>
 */
public class ElementChainDetector<L extends DBElementLinkInfo> {

	/**
	 * target revisions
	 */
	private final Map<Long, DBRevisionInfo> targetRevisions;

	/**
	 * the retriever to get elements of the given type L from db
	 */
	private final LinkElementRetriever<L> retriever;

	/**
	 * the number of threads
	 */
	private final int threadsCount;

	public ElementChainDetector(final Map<Long, DBRevisionInfo> targetRevisions,
			final LinkElementRetriever<L> retriever, final int threadsCount) {
		this.targetRevisions = targetRevisions;
		this.retriever = retriever;
		this.threadsCount = threadsCount;
	}

	/**
	 * detect element chains
	 * 
	 * @return
	 * @throws Exception
	 */
	public Collection<ElementChain<L>> detect() throws Exception {
		final ConcurrentMap<Long, ElementChain<L>> detectedChains = new ConcurrentHashMap<Long, ElementChain<L>>();
		int count = 0;

		// analyze a single revision with multiple threads
		for (final Map.Entry<Long, DBRevisionInfo> entry : targetRevisions
				.entrySet()) {
			final long revisionId = entry.getKey();
			final DBRevisionInfo revision = entry.getValue();
			MessagePrinter.println("\t[" + (++count) + "/"
					+ targetRevisions.size() + "] processing revision "
					+ revision.getIdentifier());

			final ConcurrentMap<Long, L> beforeLinks = new ConcurrentHashMap<Long, L>();
			beforeLinks.putAll(retriever
					.retrieveElementsWithAfterRevision(revisionId));

			if (beforeLinks.isEmpty()) {
				continue;
			}

			final Long[] keyArray = beforeLinks.keySet().toArray(new Long[0]);
			final Set<ElementChain<L>> alreadyProcessedChains = new HashSet<ElementChain<L>>();
			alreadyProcessedChains.addAll(detectedChains.values());
			final AtomicInteger index = new AtomicInteger(0);

			final Thread[] threads = new Thread[threadsCount];
			for (int i = 0; i < threads.length; i++) {
				threads[i] = new Thread(new GenealogyDetectingThread(keyArray,
						index, detectedChains, beforeLinks,
						alreadyProcessedChains));
				threads[i].start();
			}

			for (final Thread thread : threads) {
				try {
					thread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		return Collections.unmodifiableCollection(detectedChains.values());
	}

	/**
	 * An inner class to detect chains of elements
	 * 
	 * @author k-hotta
	 * 
	 */
	private class GenealogyDetectingThread implements Runnable {

		private final Long[] keyArray;

		private final AtomicInteger index;

		private final ConcurrentMap<Long, ElementChain<L>> detectedChains;

		private final ConcurrentMap<Long, L> links;

		private final Set<ElementChain<L>> alreadyProcessedChains;

		private GenealogyDetectingThread(final Long[] keyArray,
				final AtomicInteger index,
				final ConcurrentMap<Long, ElementChain<L>> detectedChains,
				final ConcurrentMap<Long, L> links,
				final Set<ElementChain<L>> alreadyProcessedChains) {
			this.keyArray = keyArray;
			this.index = index;
			this.detectedChains = detectedChains;
			this.links = links;
			this.alreadyProcessedChains = new HashSet<ElementChain<L>>();
			this.alreadyProcessedChains.addAll(alreadyProcessedChains);
		}

		@Override
		public void run() {
			while (true) {
				final int currentIndex = index.getAndIncrement();

				if (currentIndex >= keyArray.length) {
					break;
				}

				final long key = keyArray[currentIndex];
				final L link = links.get(key);

				// true if
				// this link has its correspondents in the detected chains
				boolean invited = false;
				for (final ElementChain<L> chain : alreadyProcessedChains) {
					if (chain.isFriend(link)) {
						chain.invite(link);
						invited = true;
						break;
					}
				}

				// if this link has no correspondents
				// then create new chain
				if (!invited) {
					final ElementChain<L> newChain = new ElementChain<L>(link);
					detectedChains.put(newChain.getId(), newChain);
				}
			}
		}

	}

}
