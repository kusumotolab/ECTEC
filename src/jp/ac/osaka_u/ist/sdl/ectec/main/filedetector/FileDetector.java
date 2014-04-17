package jp.ac.osaka_u.ist.sdl.ectec.main.filedetector;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCombinedCommitInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCommitInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBFileInfo;
import jp.ac.osaka_u.ist.sdl.ectec.main.IllegalStateException;
import jp.ac.osaka_u.ist.sdl.ectec.main.vcs.IChangedFilesDetector;
import jp.ac.osaka_u.ist.sdl.ectec.main.vcs.IRepositoryManager;
import jp.ac.osaka_u.ist.sdl.ectec.main.vcs.RepositoryManagerManager;

import org.apache.log4j.Logger;

public class FileDetector {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggingManager
			.getLogger(FileDetector.class.getName());

	/**
	 * the settings
	 */
	private final FileDetectorMainSettings settings;

	/**
	 * the manager of db
	 */
	private final DBConnectionManager dbManager;

	/**
	 * the manager of repository managers
	 */
	private final RepositoryManagerManager repositoryManagerManager;

	public FileDetector(final FileDetectorMainSettings settings,
			final DBConnectionManager dbManager,
			final RepositoryManagerManager repositoryManagerManager) {
		this.settings = settings;
		this.dbManager = dbManager;
		this.repositoryManagerManager = repositoryManagerManager;
	}

	/**
	 * perform the main process
	 * 
	 * @throws Exception
	 */
	public final void run() throws Exception {
		logger.info("retrieving combined commits ... ");
		final Map<Long, DBCombinedCommitInfo> combinedCommits = dbManager
				.getCombinedCommitRetriever().retrieveAll();
		if (combinedCommits.isEmpty()) {
			throw new IllegalStateException(
					"cannot retrieve any combined commits");
		}
		logger.info(combinedCommits.size()
				+ " combined commits have been retrieved");

		logger.info("retrieving original commits ... ");
		final Map<Long, DBCommitInfo> originalCommits = dbManager
				.getCommitRetriever().retrieveAll();
		if (originalCommits.isEmpty()) {
			throw new IllegalStateException("cannot retrieve any commits");
		}
		logger.info(originalCommits.size() + " commtis have been retrieved");

		logger.info("detecting the latest combined commit ... ");
		final DBCombinedCommitInfo latestCombinedCommit = dbManager
				.getCombinedCommitRetriever().getLatestCombinedCommit();
		if (latestCombinedCommit == null) {
			throw new IllegalStateException("cannot find the latest commit");
		}
		logger.info("combined commit " + latestCombinedCommit.getId()
				+ " was identified as the latest combined commit");

		logger.info("creating changed files detectors ... ");
		final ConcurrentMap<Long, IChangedFilesDetector> changedFilesDetectors = new ConcurrentHashMap<Long, IChangedFilesDetector>();
		final ConcurrentMap<Long, IRepositoryManager> repositoryManagers = repositoryManagerManager
				.getRepositoryManagers();

		for (final Map.Entry<Long, IRepositoryManager> entry : repositoryManagers
				.entrySet()) {
			changedFilesDetectors.put(entry.getKey(), entry.getValue()
					.createChangedFilesDetector());
		}
		logger.info(changedFilesDetectors.size()
				+ " changed files detectors have been created");

		logger.info("identifing changed files ... ");
		final ChangedFilesIdentifier identifier = new ChangedFilesIdentifier(
				changedFilesDetectors, settings.getLanguage(),
				settings.getThreads());
		final Map<Long, DBFileInfo> files = identifier.detect(combinedCommits,
				originalCommits, latestCombinedCommit);
		logger.info(files.size() + " files have been detected");

		logger.info("registering detected files ... ");
		dbManager.getFileRegisterer().register(files.values());
		logger.info("complete");
	}
}
