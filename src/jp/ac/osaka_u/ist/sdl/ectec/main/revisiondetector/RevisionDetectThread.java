package jp.ac.osaka_u.ist.sdl.ectec.main.revisiondetector;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCommitInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRepositoryInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.main.IllegalStateException;
import jp.ac.osaka_u.ist.sdl.ectec.settings.Language;
import jp.ac.osaka_u.ist.sdl.ectec.vcs.AbstractRepositoryManager;
import jp.ac.osaka_u.ist.sdl.ectec.vcs.AbstractTargetRevisionDetector;
import jp.ac.osaka_u.ist.sdl.ectec.vcs.RepositoryManagerManager;

import org.apache.log4j.Logger;

/**
 * A thread class for detecting target revisions
 * 
 * @author k-hotta
 * 
 */
public class RevisionDetectThread implements Runnable {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggingManager
			.getLogger(RevisionDetectThread.class.getName());

	/**
	 * the logger for error
	 */
	private static final Logger eLogger = LoggingManager.getLogger("error");

	/**
	 * the index
	 */
	private final AtomicInteger index;

	/**
	 * target repositories
	 */
	private final DBRepositoryInfo[] repositories;

	/**
	 * the manager for repository managers
	 */
	private final RepositoryManagerManager repositoryManagerManager;

	/**
	 * the target language
	 */
	private final Language language;

	/**
	 * the list of revisions to be ignored
	 */
	private final ConcurrentMap<Long, Set<String>> ignoredList;

	/**
	 * detected revisions
	 */
	private final ConcurrentMap<Long, DBRevisionInfo> revisions;

	/**
	 * detected commits
	 */
	private final ConcurrentMap<Long, DBCommitInfo> commits;

	public RevisionDetectThread(final AtomicInteger index,
			final DBRepositoryInfo[] repositories,
			final RepositoryManagerManager repositoryManagerManager,
			final Language language,
			final ConcurrentMap<Long, Set<String>> ignoredList,
			final ConcurrentMap<Long, DBRevisionInfo> revisions,
			final ConcurrentMap<Long, DBCommitInfo> commits) {
		this.index = index;
		this.repositories = repositories;
		this.repositoryManagerManager = repositoryManagerManager;
		this.language = language;
		this.ignoredList = ignoredList;
		this.revisions = revisions;
		this.commits = commits;
	}

	@Override
	public void run() {
		while (true) {
			final int currentIndex = index.getAndIncrement();

			if (currentIndex >= repositories.length) {
				break;
			}

			final DBRepositoryInfo repository = repositories[currentIndex];
			logger.info("[" + (currentIndex + 1) + "/" + repositories.length
					+ "] processing the repository of " + repository.getName()
					+ " (ID = " + repository.getId() + ")");
			try {
				final AbstractRepositoryManager repositoryManager = repositoryManagerManager
						.getRepositoryManager(repository.getId());
				if (repositoryManager == null) {
					throw new IllegalStateException(
							"cannot find the repository manager for repository "
									+ repository.getId());
				}

				final AbstractTargetRevisionDetector<?> detector = repositoryManager
						.createTargetRevisionDetector();
				final Set<String> ignoredListForThisRepo = (ignoredList
						.containsKey(repository.getId())) ? ignoredList
						.get(repository.getId()) : new HashSet<String>();
				detector.detect(language, ignoredListForThisRepo);

				revisions.putAll(detector.getTargetRevisions());
				commits.putAll(detector.getCommits());

			} catch (Exception e) {
				eLogger.warn("something is wrong when processing "
						+ repository.getName()
						+ " (this repository will be ignored)\n" + e.toString());
			}
		}
	}
}
