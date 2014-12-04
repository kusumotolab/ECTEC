package jp.ac.osaka_u.ist.sdl.ectec.main.revisiondetector;

import java.sql.SQLException;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentMap;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCommitInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer.CommitRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer.RevisionRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.settings.Constants;

import org.apache.log4j.Logger;

/**
 * A monitor class for target revisions detect threads
 * 
 * @author k-hotta
 * 
 */
public class RevisionDetectThreadMonitor {

	/**
	 * the logger
	 */
	private final static Logger logger = LoggingManager
			.getLogger(RevisionDetectThreadMonitor.class.getName());

	/**
	 * the logger for errors
	 */
	private final static Logger eLogger = LoggingManager.getLogger("error");

	/**
	 * a map having detected revisions
	 */
	private final ConcurrentMap<Long, DBRevisionInfo> detectedRevisions;

	/**
	 * a map having detected commits
	 */
	private final ConcurrentMap<Long, DBCommitInfo> detectedCommits;
	/**
	 * the registerer for revisions
	 */
	private final RevisionRegisterer revisionRegisterer;

	/**
	 * the registerer for commits
	 */
	private final CommitRegisterer commitRegisterer;

	/**
	 * the array of threads to be monitored
	 */
	private final Thread[] threads;

	public RevisionDetectThreadMonitor(
			final ConcurrentMap<Long, DBRevisionInfo> detectedRevisions,
			final ConcurrentMap<Long, DBCommitInfo> detectedCommits,
			final RevisionRegisterer revisionRegisterer,
			final CommitRegisterer commitRegisterer, final Thread[] threads) {
		this.detectedRevisions = detectedRevisions;
		this.detectedCommits = detectedCommits;
		this.revisionRegisterer = revisionRegisterer;
		this.commitRegisterer = commitRegisterer;
		this.threads = threads;
	}

	public void monitor() throws Exception {
		long numberOfRevisions = 0;
		long numberOfCommits = 0;

		while (true) {

			try {
				Thread.sleep(Constants.MONITORING_INTERVAL);

				synchronized (detectedRevisions) {
					if (detectedRevisions.size() >= Constants.MAX_ELEMENTS_COUNT) {
						final Set<DBRevisionInfo> currentRevisions = new TreeSet<DBRevisionInfo>();
						currentRevisions.addAll(detectedRevisions.values());
						revisionRegisterer.register(currentRevisions);

						logger.info(currentRevisions.size()
								+ " revisions have been registered into db");
						numberOfRevisions += currentRevisions.size();

						for (final DBRevisionInfo revision : currentRevisions) {
							detectedRevisions.remove(revision.getId());
						}
					}
				}

				synchronized (detectedCommits) {
					if (detectedCommits.size() >= Constants.MAX_ELEMENTS_COUNT) {
						final Set<DBCommitInfo> currentCommits = new TreeSet<DBCommitInfo>();
						currentCommits.addAll(detectedCommits.values());
						commitRegisterer.register(currentCommits);

						logger.info(currentCommits.size()
								+ " commits have been registered into db");
						numberOfCommits += currentCommits.size();

						for (final DBCommitInfo commit : currentCommits) {
							detectedCommits.remove(commit.getId());
						}
					}
				}

			} catch (Exception e) {
				eLogger.warn(
						"something is wrong in RevisionDetectThreadMonitor\n",
						e);
				if (e instanceof SQLException) {
					final SQLException se = (SQLException) e;
					eLogger.warn("error code: " + se.getErrorCode());

					SQLException ne = null;
					while ((ne = se.getNextException()) != null) {
						eLogger.warn("next exception: ", ne);
					}
				}
			}

			boolean allThreadDead = true;
			for (final Thread thread : threads) {
				if (thread.isAlive()) {
					allThreadDead = false;
					break;
				}
			}

			if (allThreadDead) {
				break;
			}

		}

		revisionRegisterer.register(detectedRevisions.values());
		commitRegisterer.register(detectedCommits.values());

		logger.info(detectedRevisions.size()
				+ " revisions have been registered into db");
		logger.info(detectedCommits.size()
				+ " commits have been registered into db");

		numberOfRevisions += detectedRevisions.size();
		numberOfCommits += detectedCommits.size();

		logger.info("total revisions: " + numberOfRevisions);
		logger.info("total commits: " + numberOfCommits);

	}
}
