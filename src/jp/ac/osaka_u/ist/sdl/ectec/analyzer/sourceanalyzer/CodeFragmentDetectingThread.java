package jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.hash.IHashCalculator;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.vcs.IRepositoryManager;
import jp.ac.osaka_u.ist.sdl.ectec.data.CRD;
import jp.ac.osaka_u.ist.sdl.ectec.data.CodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.FileInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.RevisionInfo;
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
	private final ConcurrentMap<Long, CRD> detectedCrds;

	/**
	 * a map having detected fragments
	 */
	private final ConcurrentMap<Long, CodeFragmentInfo> detectedFragments;

	/**
	 * a array of target files
	 */
	private final FileInfo[] targetFiles;

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
	private final ConcurrentMap<Long, RevisionInfo> revisions;

	/**
	 * the hash calculator
	 */
	private final IHashCalculator hashCalculator;

	public CodeFragmentDetectingThread(
			final ConcurrentMap<Long, CRD> detectedCrds,
			final ConcurrentMap<Long, CodeFragmentInfo> detectedFragments,
			final FileInfo[] targetFiles, final AtomicInteger index,
			final IRepositoryManager manager,
			final ConcurrentMap<Long, RevisionInfo> revisions,
			final IHashCalculator hashCalculator) {
		this.detectedCrds = detectedCrds;
		this.detectedFragments = detectedFragments;
		this.targetFiles = targetFiles;
		this.index = index;
		this.manager = manager;
		this.revisions = revisions;
		this.hashCalculator = hashCalculator;
	}

	@Override
	public void run() {
		while (true) {
			final int currentIndex = index.getAndIncrement();

			if (currentIndex >= targetFiles.length) {
				break;
			}

			final FileInfo targetFile = targetFiles[currentIndex];
			MessagePrinter.println("\t[" + (currentIndex + 1) + "/"
					+ targetFiles.length + "] processing "
					+ targetFile.getPath());

			final String startRevision = revisions.get(
					targetFile.getStartRevisionId()).getIdentifier();
			try {
				final String src = manager.getFileContents(startRevision,
						targetFile.getPath());
				final CompilationUnit root = ASTCreator.createAST(src);

				final CodeFragmentDetector detector = new CodeFragmentDetector(
						targetFile.getId(), targetFile.getStartRevisionId(),
						targetFile.getEndRevisionId(), root, hashCalculator);

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
