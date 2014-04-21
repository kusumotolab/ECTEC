package jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCombinedRevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCrdInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBFileInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer.CRDRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer.CodeFragmentRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.hash.IHashCalculator;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.normalizer.NormalizerCreator;
import jp.ac.osaka_u.ist.sdl.ectec.settings.AnalyzeGranularity;
import jp.ac.osaka_u.ist.sdl.ectec.vcs.IRepositoryManager;

import org.apache.log4j.Logger;

/**
 * A class for managing threads that detects code fragments
 * 
 * @author k-hotta
 * 
 */
public class CodeFragmentIdentifier {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggingManager
			.getLogger(CodeFragmentIdentifier.class.getName());

	/**
	 * target files
	 */
	private final Collection<DBFileInfo> targetFiles;

	/**
	 * target revisions
	 */
	private final ConcurrentMap<Long, DBRevisionInfo> originalRevisions;

	/**
	 * target combined revisions
	 */
	private final ConcurrentMap<Long, DBCombinedRevisionInfo> combinedRevisions;

	/**
	 * the number of threads
	 */
	private final int threadsCount;

	/**
	 * the registerer for crds
	 */
	private final CRDRegisterer crdRegisterer;

	/**
	 * the registerer for fragments
	 */
	private final CodeFragmentRegisterer fragmentRegisterer;

	/**
	 * the threshold for storing elements
	 */
	private final int maxElementsCount;

	/**
	 * the repository managers
	 */
	private final ConcurrentMap<Long, IRepositoryManager> repositoryManagers;

	/**
	 * the granularity of the analysis
	 */
	private final AnalyzeGranularity granularity;

	/**
	 * a factory for block analyzers
	 */
	private final NormalizerCreator blockAnalyzerCreator;

	/**
	 * the hash calculator
	 */
	private final IHashCalculator hashCalculator;

	public CodeFragmentIdentifier(
			final Collection<DBFileInfo> targetFiles,
			final ConcurrentMap<Long, DBRevisionInfo> originalRevisions,
			final ConcurrentMap<Long, DBCombinedRevisionInfo> combinedRevisions,
			final int threadsCount, final CRDRegisterer crdRegisterer,
			final CodeFragmentRegisterer fragmentRegisterer,
			final int maxElementsCount,
			final ConcurrentMap<Long, IRepositoryManager> repositoryManagers,
			final AnalyzeGranularity granularity,
			final NormalizerCreator blockAnalyzerCreator,
			final IHashCalculator hashCalculator) {
		this.targetFiles = targetFiles;
		this.originalRevisions = originalRevisions;
		this.combinedRevisions = combinedRevisions;
		this.threadsCount = threadsCount;
		this.crdRegisterer = crdRegisterer;
		this.fragmentRegisterer = fragmentRegisterer;
		this.maxElementsCount = maxElementsCount;
		this.repositoryManagers = repositoryManagers;
		this.granularity = granularity;
		this.blockAnalyzerCreator = blockAnalyzerCreator;
		this.hashCalculator = hashCalculator;
	}

	public void run() throws Exception {
		final DBFileInfo[] filesArray = targetFiles.toArray(new DBFileInfo[0]);

		// the minimum number of thread is 2
		final int tailoredThreadsCount = Math.min(targetFiles.size(),
				Math.max(threadsCount, 2));

		final Thread[] threads = new Thread[tailoredThreadsCount - 1];

		final ConcurrentMap<Long, DBCrdInfo> detectedCrds = new ConcurrentHashMap<Long, DBCrdInfo>();
		final ConcurrentMap<Long, DBCodeFragmentInfo> detectedFragments = new ConcurrentHashMap<Long, DBCodeFragmentInfo>();

		final AtomicInteger index = new AtomicInteger(0);

		for (int i = 0; i < tailoredThreadsCount - 1; i++) {
			threads[i] = new Thread(new CodeFragmentDetectingThread(
					detectedCrds, detectedFragments, filesArray, index,
					repositoryManagers, originalRevisions, combinedRevisions,
					granularity, blockAnalyzerCreator, hashCalculator));
			threads[i].start();
			logger.info("thread " + threads[i].getName() + " started");
		}

		final CodeFragmentDetectingThreadMonitor monitor = new CodeFragmentDetectingThreadMonitor(
				detectedCrds, detectedFragments, maxElementsCount,
				crdRegisterer, fragmentRegisterer, threads);
		logger.info("monitoring thread started");
		monitor.monitor();
	}

}
