package jp.ac.osaka_u.ist.sdl.instantcdt;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.hash.IHashCalculator;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.normalizer.NormalizerCreator;
import jp.ac.osaka_u.ist.sdl.ectec.settings.AnalyzeGranularity;

/**
 * A class to detect fragments with multi threads
 * 
 * @author k-hotta
 * 
 */
public class InstantCodeFragmentDetector {

	private final IHashCalculator hashCalculator;

	private final NormalizerCreator normalizerCreator;

	private final int tokenThreshold;

	private final AnalyzeGranularity granularity;

	private final int threadsCount;

	private final int lineThreshold;

	private final ConcurrentMap<String, List<InstantCodeFragmentInfo>> fragments;

	private final ConcurrentMap<Long, InstantFileInfo> files;

	public InstantCodeFragmentDetector(final IHashCalculator hashCalculator,
			final NormalizerCreator normalizerCreator,
			final int tokenThreshold, final AnalyzeGranularity granularity,
			final int threadsCount, final int lineThreshold) {
		this.hashCalculator = hashCalculator;
		this.normalizerCreator = normalizerCreator;
		this.tokenThreshold = tokenThreshold;
		this.granularity = granularity;
		this.threadsCount = threadsCount;
		this.lineThreshold = lineThreshold;
		this.fragments = new ConcurrentHashMap<String, List<InstantCodeFragmentInfo>>();
		this.files = new ConcurrentHashMap<Long, InstantFileInfo>();
	}

	public final Map<String, List<InstantCodeFragmentInfo>> getDetectedFragments() {
		return Collections.unmodifiableMap(fragments);
	}

	public final Map<Long, InstantFileInfo> getDetectedFiles() {
		return Collections.unmodifiableMap(files);
	}

	public void analyzeFiles(final Collection<String> filePaths) {
		final AtomicInteger index = new AtomicInteger(0);

		final String[] filePathsArray = filePaths.toArray(new String[0]);

		final Thread[] threads = new Thread[threadsCount];
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Thread(new InstantCodeFragmentDetectingThread(
					filePathsArray, fragments, files, index, hashCalculator,
					normalizerCreator, tokenThreshold, granularity,
					lineThreshold));
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

}
