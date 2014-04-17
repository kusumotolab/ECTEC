package jp.ac.osaka_u.ist.sdl.ectec.main.combiner;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCombinedCommitInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCombinedRevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCommitInfo;

import org.apache.log4j.Logger;

/**
 * A class that performs the main process of combining revisions and commits
 * 
 * @author k-hotta
 * 
 */
public class Combiner {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggingManager
			.getLogger(Combiner.class.getName());

	/**
	 * the db manager
	 */
	private final DBConnectionManager dbManager;

	public Combiner(final DBConnectionManager dbManager) {
		this.dbManager = dbManager;
	}

	public void perform() throws Exception {
		logger.info("retrieving commits ... ");
		final Map<Long, DBCommitInfo> commits = dbManager.getCommitRetriever()
				.retrieveAll();
		logger.info(commits.size() + " commits have been retrieved");

		logger.info("sorting retrieved commits ... ");
		final Map<Long, LinkedList<DBCommitInfo>> sortedCommits = sortCommits(commits);
		logger.info("sorting completed");

		logger.info("detecting combined revisions and combined commits ... ");
		final CommitCombiner commitCombiner = new CommitCombiner(sortedCommits);
		commitCombiner.detectCombinedRevisionsAndCommits();

		final Map<Long, DBCombinedRevisionInfo> combinedRevisions = commitCombiner
				.getCombinedRevisions();
		final Map<Long, DBCombinedCommitInfo> combinedCommits = commitCombiner
				.getCombinedCommits();

		logger.info("complete");
		logger.info(combinedRevisions.size()
				+ " combined revisions have been detected");
		logger.info(combinedCommits.size()
				+ " combined commits have been detected");

		logger.info("registering results ... ");
		dbManager.getCombinedRevisionRegisterer().register(
				combinedRevisions.values());
		dbManager.getCombinedCommitRegisterer().register(
				combinedCommits.values());
		logger.info("complete");
	}

	private final Map<Long, LinkedList<DBCommitInfo>> sortCommits(
			final Map<Long, DBCommitInfo> commits) {
		final Map<Long, SortedSet<DBCommitInfo>> resultWithSortedSet = new TreeMap<Long, SortedSet<DBCommitInfo>>();

		int processedCommits = 0;
		for (final Map.Entry<Long, DBCommitInfo> commitEntry : commits
				.entrySet()) {
			final DBCommitInfo commit = commitEntry.getValue();
			final long repositoryId = commit.getRepositoryId();

			logger.debug("[" + (processedCommits++) + "/" + commits.size()
					+ "] processing commit " + commit.getId()
					+ " in repository " + repositoryId);

			if (resultWithSortedSet.containsKey(repositoryId)) {
				resultWithSortedSet.get(repositoryId).add(commit);
			} else {
				final SortedSet<DBCommitInfo> newSet = new TreeSet<DBCommitInfo>(
						new Comparator<DBCommitInfo>() {
							@Override
							public int compare(DBCommitInfo arg0,
									DBCommitInfo arg1) {
								final int basedOnDate = arg0.getDate()
										.compareTo(arg1.getDate());
								if (basedOnDate != 0) {
									return basedOnDate;
								} else {
									return arg0.compareTo(arg1);
								}
							}
						});
				newSet.add(commit);
				resultWithSortedSet.put(repositoryId, newSet);
			}

		}

		final Map<Long, LinkedList<DBCommitInfo>> result = new TreeMap<Long, LinkedList<DBCommitInfo>>();
		for (final Map.Entry<Long, SortedSet<DBCommitInfo>> entry : resultWithSortedSet
				.entrySet()) {
			final long repositoryId = entry.getKey();
			final LinkedList<DBCommitInfo> linkedList = new LinkedList<DBCommitInfo>();
			linkedList.addAll(entry.getValue());

			result.put(repositoryId, linkedList);
		}

		return Collections.unmodifiableMap(result);
	}

}
