package jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.ectec.ast.ASTCreator;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCrdInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBFileInfo;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.hash.IHashCalculator;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.normalizer.NormalizerCreator;
import jp.ac.osaka_u.ist.sdl.ectec.main.vcs.IRepositoryManager;
import jp.ac.osaka_u.ist.sdl.ectec.settings.AnalyzeGranularity;
import jp.ac.osaka_u.ist.sdl.ectec.settings.MessagePrinter;

import org.eclipse.jdt.core.dom.CompilationUnit;

/**
 * A thread class for detecting code fragments and crds
 * 
 * @author k-hotta
 * 
 */
public class CodeFragmentDetectingThread implements Runnable {

	/**
	 * a map having detected crds
	 */
	private final ConcurrentMap<Long, DBCrdInfo> detectedCrds;

	/**
	 * a map having detected fragments
	 */
	private final ConcurrentMap<Long, DBCodeFragmentInfo> detectedFragments;

	/**
	 * a array of target files
	 */
	private final DBFileInfo[] targetFiles;

	/**
	 * a counter that points the current state of the processing
	 */
	private final AtomicInteger index;

	/**
	 * the repository manager
	 */
	private final IRepositoryManager manager;

	/**
	 * a map having target revisions
	 */
	private final ConcurrentMap<Long, String> revisions;

	/**
	 * the factory for block analyzers
	 */
	private final NormalizerCreator blockAnalyzerCreator;

	/**
	 * the granularity of the analysis
	 */
	private final AnalyzeGranularity granularity;

	/**
	 * the hash calculator
	 */
	private final IHashCalculator hashCalculator;

	public CodeFragmentDetectingThread(
			final ConcurrentMap<Long, DBCrdInfo> detectedCrds,
			final ConcurrentMap<Long, DBCodeFragmentInfo> detectedFragments,
			final DBFileInfo[] targetFiles, final AtomicInteger index,
			final IRepositoryManager manager,
			final ConcurrentMap<Long, String> revisions,
			final AnalyzeGranularity granularity,
			final NormalizerCreator blockAnalyzerCreator,
			final IHashCalculator hashCalculator) {
		this.detectedCrds = detectedCrds;
		this.detectedFragments = detectedFragments;
		this.targetFiles = targetFiles;
		this.index = index;
		this.manager = manager;
		this.revisions = revisions;
		this.blockAnalyzerCreator = blockAnalyzerCreator;
		this.granularity = granularity;
		this.hashCalculator = hashCalculator;
	}

	@Override
	public void run() {
		while (true) {
			final int currentIndex = index.getAndIncrement();

			if (currentIndex >= targetFiles.length) {
				break;
			}

			final DBFileInfo targetFile = targetFiles[currentIndex];
			MessagePrinter.println("\t[" + (currentIndex + 1) + "/"
					+ targetFiles.length + "] processing "
					+ targetFile.getPath());

			final String startRevision = revisions.get(targetFile
					.getStartCombinedRevisionId());
			try {
				final String src = manager.getFileContents(startRevision,
						targetFile.getPath());
				final CompilationUnit root = ASTCreator.createAST(src);

				final CodeFragmentDetector detector = new CodeFragmentDetector(
						targetFile.getId(), targetFile.getStartCombinedRevisionId(),
						targetFile.getCombinedEndRevisionId(), hashCalculator, root,
						granularity, blockAnalyzerCreator);

				root.accept(detector);

				this.detectedCrds.putAll(detector.getDetectedCrds());
				this.detectedFragments.putAll(detector.getDetectedFragments());

			} catch (Exception e) {
				MessagePrinter.ePrintln("something is wrong in processing "
						+ targetFile.getPath() + " at revision "
						+ startRevision);
			}
		}
	}
}
