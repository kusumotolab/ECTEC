package jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.ast.ASTCreator;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCombinedRevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCrdInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBFileInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.hash.IHashCalculator;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.normalizer.NormalizerCreator;
import jp.ac.osaka_u.ist.sdl.ectec.settings.AnalyzeGranularity;
import jp.ac.osaka_u.ist.sdl.ectec.vcs.AbstractRepositoryManager;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.CompilationUnit;

/**
 * A thread class for detecting code fragments and crds
 * 
 * @author k-hotta
 * 
 */
public class CodeFragmentDetectingThread implements Runnable {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggingManager
			.getLogger(CodeFragmentDetectingThread.class.getName());

	/**
	 * the logger for errors
	 */
	private static final Logger eLogger = LoggingManager.getLogger("error");

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
	 * the repository managers
	 */
	private final ConcurrentMap<Long, AbstractRepositoryManager> repositoryManagers;

	/**
	 * a map having target revisions
	 */
	private final ConcurrentMap<Long, DBRevisionInfo> originalRevisions;

	/**
	 * a map having target combined revisions
	 */
	private final ConcurrentMap<Long, DBCombinedRevisionInfo> combinedRevisions;

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

	/**
	 * the lowest value of size to be considered
	 */
	private final int fragmentSizeThreshold;

	public CodeFragmentDetectingThread(
			final ConcurrentMap<Long, DBCrdInfo> detectedCrds,
			final ConcurrentMap<Long, DBCodeFragmentInfo> detectedFragments,
			final DBFileInfo[] targetFiles,
			final AtomicInteger index,
			final ConcurrentMap<Long, AbstractRepositoryManager> repositoryManagers,
			final ConcurrentMap<Long, DBRevisionInfo> originalRevisions,
			final ConcurrentMap<Long, DBCombinedRevisionInfo> combinedRevisions,
			final AnalyzeGranularity granularity,
			final NormalizerCreator blockAnalyzerCreator,
			final IHashCalculator hashCalculator,
			final int fragmentSizeThreshold) {
		this.detectedCrds = detectedCrds;
		this.detectedFragments = detectedFragments;
		this.targetFiles = targetFiles;
		this.index = index;
		this.repositoryManagers = repositoryManagers;
		this.originalRevisions = originalRevisions;
		this.combinedRevisions = combinedRevisions;
		this.blockAnalyzerCreator = blockAnalyzerCreator;
		this.granularity = granularity;
		this.hashCalculator = hashCalculator;
		this.fragmentSizeThreshold = fragmentSizeThreshold;
	}

	@Override
	public void run() {
		while (true) {
			final int currentIndex = index.getAndIncrement();

			if (currentIndex >= targetFiles.length) {
				break;
			}

			final DBFileInfo targetFile = targetFiles[currentIndex];
			logger.info("[" + (currentIndex + 1) + "/" + targetFiles.length
					+ "] processing " + targetFile.getPath());

			final long repositoryId = targetFile.getOwnerRepositoryId();
			final DBCombinedRevisionInfo startCombinedRevision = combinedRevisions
					.get(targetFile.getStartCombinedRevisionId());
			try {
				final AbstractRepositoryManager repositoryManager = repositoryManagers
						.get(repositoryId);

				if (repositoryManager == null) {
					throw new IllegalStateException(
							"repository manager of repository " + repositoryId
									+ " is null");
				}

				final DBRevisionInfo originalRevision = getCorrespondingOriginalRevision(
						startCombinedRevision, repositoryId);

				final String src = repositoryManager.getFileContents(
						originalRevision.getIdentifier(), targetFile.getPath());
				final CompilationUnit root = ASTCreator.createAST(src);

				final ASTParser parser = new ASTParser(targetFile.getId(),
						repositoryId, targetFile.getStartCombinedRevisionId(),
						targetFile.getCombinedEndRevisionId(), hashCalculator,
						root, granularity, blockAnalyzerCreator,
						fragmentSizeThreshold, targetFile.isAddedAtStart(),
						targetFile.isDeletedAtEnd());

				root.accept(parser);

				this.detectedCrds.putAll(parser.getDetectedCrds());
				this.detectedFragments.putAll(parser.getDetectedFragments());

			} catch (Exception e) {
				eLogger.warn("something is wrong in processing "
						+ targetFile.getPath() + " in repository "
						+ repositoryId + " at combined revision "
						+ startCombinedRevision.getId());
			}
		}
	}

	private DBRevisionInfo getCorrespondingOriginalRevision(
			final DBCombinedRevisionInfo combinedRevision,
			final long repositoryId) throws IllegalStateException {
		DBRevisionInfo result = null;
		for (final long candidateId : combinedRevision.getOriginalRevisions()) {
			final DBRevisionInfo candidateOriginalRevision = originalRevisions
					.get(candidateId);
			if (candidateOriginalRevision.getRepositoryId() == repositoryId) {
				if (result == null) {
					result = candidateOriginalRevision;
				} else {
					throw new IllegalStateException(
							"duplicate repository in the combined revision "
									+ combinedRevision.getId());
				}
			}
		}

		if (result == null) {
			throw new IllegalStateException(
					"cannot find corresponding original revision for repository "
							+ repositoryId + " for combined revision "
							+ combinedRevision.getId());
		}

		return result;
	}

}
