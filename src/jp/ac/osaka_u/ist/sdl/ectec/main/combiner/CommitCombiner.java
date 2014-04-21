package jp.ac.osaka_u.ist.sdl.ectec.main.combiner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCombinedCommitInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCombinedRevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCommitInfo;

import org.apache.log4j.Logger;

/**
 * A class to make combined revisions
 * 
 * @author k-hotta
 * 
 */
public class CommitCombiner {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggingManager
			.getLogger(CommitCombiner.class.getName());

	/**
	 * the logger for errors
	 */
	private static final Logger eLogger = LoggingManager.getLogger("error");

	/**
	 * the commits <br>
	 * a key is an id of repositories, and a value is a list of commits in the
	 * repository <br>
	 * the list of commits MUST be sorted by the ascending order of their dates
	 */
	private final Map<Long, LinkedList<DBCommitInfo>> commits;

	/**
	 * the combined revisions <br>
	 * this field will be filled after calling
	 * {@link CommitCombiner#detectCombinedRevisions()}
	 */
	private final Map<Long, DBCombinedRevisionInfo> combinedRevisions;

	/**
	 * the combined commits <br>
	 * this field will be filled after calling
	 * {@link CommitCombiner#detectCombinedRevisions()}
	 */
	private final Map<Long, DBCombinedCommitInfo> combinedCommits;

	/**
	 * the number of given original commits
	 */
	private final int numberOfOriginalCommits;

	public CommitCombiner(final Map<Long, LinkedList<DBCommitInfo>> commits) {
		this.commits = commits;

		int numberOfOriginalCommits = 0;
		for (final Map.Entry<Long, LinkedList<DBCommitInfo>> entry : commits
				.entrySet()) {
			numberOfOriginalCommits += entry.getValue().size();
		}

		this.numberOfOriginalCommits = numberOfOriginalCommits;

		this.combinedRevisions = new TreeMap<Long, DBCombinedRevisionInfo>();
		this.combinedCommits = new TreeMap<Long, DBCombinedCommitInfo>();
	}

	/**
	 * get detected combined revisions
	 * 
	 * @return
	 */
	public final Map<Long, DBCombinedRevisionInfo> getCombinedRevisions() {
		return Collections.unmodifiableMap(combinedRevisions);
	}

	/**
	 * get detected combined commits
	 * 
	 * @return
	 */
	public final Map<Long, DBCombinedCommitInfo> getCombinedCommits() {
		return Collections.unmodifiableMap(combinedCommits);
	}

	/**
	 * detect combined revisions and combined commits <br>
	 * the results will be stored the fields of this object
	 */
	public void detectCombinedRevisionsAndCommits() {
		final Map<Long, DBCommitInfo> currentCommits = new TreeMap<Long, DBCommitInfo>();
		final Map<Long, Long> currentRevisionIds = new TreeMap<Long, Long>();

		initializeCurrentCommits(currentCommits);

		DBCombinedRevisionInfo previousCombinedRevision = getInitialCombinedRevision();
		combinedRevisions.put(previousCombinedRevision.getId(),
				previousCombinedRevision);

		int numberOfProcessedCommits = 0;

		while (true) {
			final Map.Entry<Long, DBCommitInfo> oldestCommitEntry = getOldestCommit(currentCommits);

			if (oldestCommitEntry == null) {
				logger.info("all the commits have been processed");
				break;
			}

			final long oldestCommitRepositoryId = oldestCommitEntry.getKey();
			final DBCommitInfo oldestCommit = oldestCommitEntry.getValue();
			numberOfProcessedCommits++;

			logger.info("[" + numberOfProcessedCommits + "/"
					+ numberOfOriginalCommits + "] processing the commit "
					+ oldestCommit.getId() + " in repository "
					+ oldestCommitRepositoryId);

			currentRevisionIds.put(oldestCommitRepositoryId,
					oldestCommit.getAfterRevisionId());

			final List<Long> currentRevisionsList = new ArrayList<Long>();
			currentRevisionsList.addAll(currentRevisionIds.values());

			final DBCombinedRevisionInfo newCombinedRevision = new DBCombinedRevisionInfo(
					currentRevisionsList);
			final DBCombinedCommitInfo newCombinedCommit = new DBCombinedCommitInfo(
					previousCombinedRevision.getId(),
					newCombinedRevision.getId(), oldestCommit.getId());

			combinedRevisions.put(newCombinedRevision.getId(),
					newCombinedRevision);
			combinedCommits.put(newCombinedCommit.getId(), newCombinedCommit);

			final DBCommitInfo nextCommit = pollQueue(oldestCommitRepositoryId);
			if (nextCommit == null) {
				currentCommits.remove(oldestCommitRepositoryId);
			} else {
				currentCommits.put(oldestCommitRepositoryId, nextCommit);
			}

			previousCombinedRevision = newCombinedRevision;
		}
	}

	/**
	 * initialize current commits with the oldest commits
	 * 
	 * @param currentCommits
	 */
	private void initializeCurrentCommits(
			final Map<Long, DBCommitInfo> currentCommits) {
		for (final long repositoryId : commits.keySet()) {
			final LinkedList<DBCommitInfo> commitsInARepository = commits
					.get(repositoryId);
			if (commitsInARepository == null) {
				eLogger.fatal("cannot find the list of commits in repository "
						+ repositoryId);
			} else if (commitsInARepository.isEmpty()) {
				eLogger.warn("the list of commits in repository "
						+ repositoryId + " is empty");
				eLogger.warn("this repository will be ignored");
			}

			// put the first element into currentCommits
			// pollQueue are never null because of the above statements
			currentCommits.put(repositoryId, pollQueue(repositoryId));
		}
	}

	/**
	 * get the initial combined revision
	 */
	private DBCombinedRevisionInfo getInitialCombinedRevision() {
		final List<Long> placeboIdList = new ArrayList<Long>();
		placeboIdList.add((long) -1);
		final DBCombinedRevisionInfo initialCombinedRevision = new DBCombinedRevisionInfo(
				-1, placeboIdList);

		return initialCombinedRevision;
	}

	/**
	 * get the oldest commit <br>
	 * (entry of the given map between repository ids and commits)
	 * 
	 * @param currentCommits
	 * @return
	 */
	private Map.Entry<Long, DBCommitInfo> getOldestCommit(
			final Map<Long, DBCommitInfo> currentCommits) {
		Map.Entry<Long, DBCommitInfo> currentResult = null;

		for (final Map.Entry<Long, DBCommitInfo> entry : currentCommits
				.entrySet()) {
			if (currentResult == null) {
				currentResult = entry;
				continue;
			}

			final Date currentResultDate = currentResult.getValue().getDate();
			final Date challengerDate = entry.getValue().getDate();

			if (currentResultDate.compareTo(challengerDate) > 0) {
				currentResult = entry;
			}
		}

		return currentResult;
	}

	/**
	 * poll the queue of the repository whose id is the given long value
	 * 
	 * @param repositoryId
	 * @return
	 */
	private DBCommitInfo pollQueue(final long repositoryId) {
		final DBCommitInfo commit = commits.get(repositoryId).poll();

		if (commit == null) {
			logger.debug("the queue of repository " + repositoryId
					+ " is empty");
		} else {
			logger.debug("\tpoll the queue of repository " + repositoryId
					+ " and get the commit " + commit.getId());
		}

		return commit;
	}

}
