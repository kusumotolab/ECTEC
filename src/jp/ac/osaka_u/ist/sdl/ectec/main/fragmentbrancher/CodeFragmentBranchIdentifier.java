package jp.ac.osaka_u.ist.sdl.ectec.main.fragmentbrancher;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCombinedCommitInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer.CodeFragmentLinkRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CloneSetRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CodeFragmentLinkRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CodeFragmentRetriever;

public class CodeFragmentBranchIdentifier {

	private final Map<Long, DBCombinedCommitInfo> combinedCommits;

	private final int threadsCount;

	private final CodeFragmentLinkRegisterer linkRegisterer;

	private final CodeFragmentLinkRetriever linkRetriever;

	private final CloneSetRetriever cloneRetriever;

	private final CodeFragmentRetriever fragmentRetriever;

	private final int maxElementsCount;

	public CodeFragmentBranchIdentifier(
			final Map<Long, DBCombinedCommitInfo> combinedCommits,
			final int threadsCount,
			final CodeFragmentLinkRegisterer linkRegisterer,
			final CodeFragmentLinkRetriever linkRetriever,
			final CloneSetRetriever cloneRetriever,
			final CodeFragmentRetriever fragmentRetriever,
			final int maxElementsCount) {
		this.combinedCommits = combinedCommits;
		this.threadsCount = threadsCount;
		this.linkRegisterer = linkRegisterer;
		this.linkRetriever = linkRetriever;
		this.cloneRetriever = cloneRetriever;
		this.fragmentRetriever = fragmentRetriever;
		this.maxElementsCount = maxElementsCount;
	}

	public void run() throws Exception {
		final DBCombinedCommitInfo[] combinedCommitsArray = combinedCommits
				.values().toArray(new DBCombinedCommitInfo[0]);

		final ConcurrentMap<Long, DBCodeFragmentLinkInfo> detectedLinks = new ConcurrentHashMap<Long, DBCodeFragmentLinkInfo>();
		final AtomicInteger index = new AtomicInteger(0);
		final ConcurrentMap<Long, DBCombinedCommitInfo> processedCombinedCommits = new ConcurrentHashMap<Long, DBCombinedCommitInfo>();

		final Thread[] threads = new Thread[threadsCount];
		for (int i = 0; i < threadsCount; i++) {
			threads[i] = new Thread(new CodeFragmentBranchingThread(
					detectedLinks, combinedCommitsArray, fragmentRetriever,
					cloneRetriever, linkRetriever, linkRegisterer,
					processedCombinedCommits, index, maxElementsCount));
			threads[i].start();
		}
		
		for (final Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if (!detectedLinks.isEmpty()) {
			linkRegisterer.register(detectedLinks.values());
		}
	}

}
