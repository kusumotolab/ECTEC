package jp.ac.osaka_u.ist.sdl.ectec.detector.sourceanalyzer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.ectec.ast.ASTCreator;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCrdInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBFileInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer.CRDRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer.CodeFragmentRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.detector.sourceanalyzer.hash.IHashCalculator;
import jp.ac.osaka_u.ist.sdl.ectec.detector.sourceanalyzer.normalizer.NormalizerCreator;
import jp.ac.osaka_u.ist.sdl.ectec.main.vcs.IRepositoryManager;
import jp.ac.osaka_u.ist.sdl.ectec.settings.AnalyzeGranularity;
import jp.ac.osaka_u.ist.sdl.ectec.settings.MessagePrinter;

import org.eclipse.jdt.core.dom.CompilationUnit;

/**
 * A class for managing threads that detects code fragments
 * 
 * @author k-hotta
 * 
 */
public class CodeFragmentIdentifier {

	/**
	 * target files
	 */
	private final Collection<DBFileInfo> targetFiles;

	/**
	 * target revisions
	 */
	private final Collection<DBRevisionInfo> revisions;

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
	 * the repository manager
	 */
	private final IRepositoryManager repositoryManager;

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

	public CodeFragmentIdentifier(final Collection<DBFileInfo> targetFiles,
			final Collection<DBRevisionInfo> revisions, final int threadsCount,
			final CRDRegisterer crdRegisterer,
			final CodeFragmentRegisterer fragmentRegisterer,
			final int maxElementsCount,
			final IRepositoryManager repositoryManager,
			final AnalyzeGranularity granularity,
			final NormalizerCreator blockAnalyzerCreator,
			final IHashCalculator hashCalculator) {
		this.targetFiles = targetFiles;
		this.revisions = revisions;
		this.threadsCount = threadsCount;
		this.crdRegisterer = crdRegisterer;
		this.fragmentRegisterer = fragmentRegisterer;
		this.maxElementsCount = maxElementsCount;
		this.repositoryManager = repositoryManager;
		this.granularity = granularity;
		this.blockAnalyzerCreator = blockAnalyzerCreator;
		this.hashCalculator = hashCalculator;
	}

	public void run() throws Exception {
		// creating a map between revision id and revision identifier
		final ConcurrentMap<Long, String> revisionIdentifiers = new ConcurrentHashMap<Long, String>();
		for (final DBRevisionInfo revision : revisions) {
			revisionIdentifiers.put(revision.getId(), revision.getIdentifier());
		}

		final DBFileInfo[] filesArray = targetFiles.toArray(new DBFileInfo[0]);

		if (threadsCount == 1) {
			runWithSingleThread(revisionIdentifiers, filesArray);
		} else {
			runWithMultipleThreads(revisionIdentifiers, filesArray);
		}
	}

	private final void runWithMultipleThreads(
			final ConcurrentMap<Long, String> revisionIdentifiers,
			final DBFileInfo[] filesArray) throws Exception {
		assert threadsCount > 1;

		final Thread[] threads = new Thread[threadsCount - 1];

		final ConcurrentMap<Long, DBCrdInfo> detectedCrds = new ConcurrentHashMap<Long, DBCrdInfo>();
		final ConcurrentMap<Long, DBCodeFragmentInfo> detectedFragments = new ConcurrentHashMap<Long, DBCodeFragmentInfo>();

		final AtomicInteger index = new AtomicInteger(0);

		for (int i = 0; i < threadsCount - 1; i++) {
			threads[i] = new Thread(new CodeFragmentDetectingThread(
					detectedCrds, detectedFragments, filesArray, index,
					repositoryManager, revisionIdentifiers, granularity,
					blockAnalyzerCreator, hashCalculator));
			threads[i].start();
		}

		final CodeFragmentDetectingThreadMonitor monitor = new CodeFragmentDetectingThreadMonitor(
				detectedCrds, detectedFragments, maxElementsCount,
				crdRegisterer, fragmentRegisterer);
		monitor.monitor();
	}

	private final void runWithSingleThread(
			final ConcurrentMap<Long, String> revisionIdentifiers,
			final DBFileInfo[] filesArray) throws Exception {
		final Map<Long, DBCrdInfo> detectedCrds = new TreeMap<Long, DBCrdInfo>();
		final Map<Long, DBCodeFragmentInfo> detectedFragments = new TreeMap<Long, DBCodeFragmentInfo>();

		long numberOfCrds = 0;
		long numberOfFragments = 0;

		for (int i = 0; i < filesArray.length; i++) {
			final DBFileInfo file = filesArray[i];
			MessagePrinter.println("\t[" + (i + 1) + "/" + filesArray.length
					+ "] processing " + file.getPath());

			final String startRevision = revisionIdentifiers.get(file
					.getStartCombinedRevisionId());

			final String src = repositoryManager.getFileContents(startRevision,
					file.getPath());
			final CompilationUnit root = ASTCreator.createAST(src);

			final CodeFragmentDetector detector = new CodeFragmentDetector(
					file.getId(), file.getStartCombinedRevisionId(),
					file.getCombinedEndRevisionId(), hashCalculator, root, granularity,
					blockAnalyzerCreator);

			root.accept(detector);

			detectedCrds.putAll(detector.getDetectedCrds());
			detectedFragments.putAll(detector.getDetectedFragments());

			if (detectedCrds.size() >= maxElementsCount) {
				final Set<DBCrdInfo> currentElements = new TreeSet<DBCrdInfo>();
				currentElements.addAll(detectedCrds.values());
				crdRegisterer.register(currentElements);
				MessagePrinter.println("\t" + currentElements.size()
						+ " CRDs have been registered into db");
				numberOfCrds += currentElements.size();

				for (final DBCrdInfo crd : currentElements) {
					detectedCrds.remove(crd.getId());
				}
			}

			if (detectedFragments.size() >= maxElementsCount) {
				final Collection<DBCodeFragmentInfo> currentElements = new HashSet<DBCodeFragmentInfo>();
				currentElements.addAll(detectedFragments.values());
				fragmentRegisterer.register(currentElements);
				MessagePrinter.println("\t" + currentElements.size()
						+ " fragments have been registered into db");
				numberOfFragments += currentElements.size();

				for (final DBCodeFragmentInfo fragment : currentElements) {
					detectedFragments.remove(fragment.getId());
				}
			}
		}

		MessagePrinter.println();

		MessagePrinter
				.println("\tregistering all the remaining elements into db ");
		crdRegisterer.register(detectedCrds.values());
		fragmentRegisterer.register(detectedFragments.values());

		numberOfCrds += detectedCrds.size();
		numberOfFragments += detectedFragments.size();

		MessagePrinter.println("\t\tOK");

		MessagePrinter.println();

		MessagePrinter.println("the numbers of detected elements are ... ");
		MessagePrinter.println("\tCRD: " + numberOfCrds);
		MessagePrinter.println("\tFragment: " + numberOfFragments);
	}

}
