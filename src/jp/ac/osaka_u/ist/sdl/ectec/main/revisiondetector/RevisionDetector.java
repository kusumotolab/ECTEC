package jp.ac.osaka_u.ist.sdl.ectec.main.revisiondetector;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCommitInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRepositoryInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.vcs.RepositoryManagerManager;

import org.apache.log4j.Logger;

/**
 * A class that performs the main processing of detecting target revisions
 * 
 * @author k-hotta
 * 
 */
public class RevisionDetector {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggingManager
			.getLogger(RevisionDetector.class.getName());

	/**
	 * the settings
	 */
	private final RevisionDetectorMainSettings settings;

	/**
	 * the manager of db
	 */
	private final DBConnectionManager dbManager;

	/**
	 * the manager of repository managers
	 */
	private final RepositoryManagerManager repositoryManagerManager;

	/**
	 * the target repositories
	 */
	private final Map<Long, DBRepositoryInfo> targetRepositories;

	public RevisionDetector(final RevisionDetectorMainSettings settings,
			final DBConnectionManager dbManager,
			final RepositoryManagerManager repositoryManagerManager,
			final Map<Long, DBRepositoryInfo> targetRepositories) {
		this.settings = settings;
		this.dbManager = dbManager;
		this.repositoryManagerManager = repositoryManagerManager;
		this.targetRepositories = targetRepositories;
	}

	/**
	 * perform the main process
	 * 
	 * @throws Exception
	 */
	public final void run() throws Exception {
		// the minimum number of thread is 2
		final int threadsCount = Math.max(
				Math.min(targetRepositories.size(), settings.getThreads()), 2);

		final Thread[] threads = new Thread[threadsCount - 1];

		final AtomicInteger index = new AtomicInteger(0);
		final DBRepositoryInfo[] repositories = targetRepositories.values()
				.toArray(new DBRepositoryInfo[] {});
		final ConcurrentMap<Long, DBRevisionInfo> revisions = new ConcurrentHashMap<Long, DBRevisionInfo>();
		final ConcurrentMap<Long, DBCommitInfo> commits = new ConcurrentHashMap<Long, DBCommitInfo>();

		for (int i = 0; i < threadsCount - 1; i++) {
			threads[i] = new Thread(new RevisionDetectThread(index,
					repositories, repositoryManagerManager,
					settings.getLanguage(), settings.getIgnoredRevisions(),
					revisions, commits));
			threads[i].start();
			logger.info("thread " + threads[i].getName() + " started");
		}

		final RevisionDetectThreadMonitor monitor = new RevisionDetectThreadMonitor(
				revisions, commits, dbManager.getRevisionRegisterer(),
				dbManager.getCommitRegisterer(), threads);
		logger.info("monitoring thread started");
		monitor.monitor();

	}
}
