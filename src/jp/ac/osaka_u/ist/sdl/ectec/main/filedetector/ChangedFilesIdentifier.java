package jp.ac.osaka_u.ist.sdl.ectec.main.filedetector;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCombinedCommitInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCommitInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBFileInfo;
import jp.ac.osaka_u.ist.sdl.ectec.settings.Language;
import jp.ac.osaka_u.ist.sdl.ectec.vcs.IChangedFilesDetector;

import org.apache.log4j.Logger;

/**
 * A class to detect and register changed files
 * 
 * @author k-hotta
 * 
 */
public class ChangedFilesIdentifier {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggingManager
			.getLogger(ChangedFilesIdentifier.class.getName());

	/**
	 * the detectors
	 */
	private final ConcurrentMap<Long, IChangedFilesDetector> changedFilesDetectors;

	/**
	 * the target language
	 */
	private final Language language;

	/**
	 * the number of threads
	 */
	private final int threadsCount;

	/**
	 * the constructor
	 * 
	 * @param repositoryManagerManager
	 * @param language
	 * @param threadsCount
	 */
	public ChangedFilesIdentifier(
			final ConcurrentMap<Long, IChangedFilesDetector> changedFilesDetectors,
			final Language language, final int threadsCount) {
		this.changedFilesDetectors = changedFilesDetectors;
		this.language = language;
		this.threadsCount = threadsCount;
	}

	/**
	 * detect files that exist in the specified revisions and register them into
	 * the db
	 * 
	 * @param targetRevisions
	 * @throws SQLException
	 */
	public Map<Long, DBFileInfo> detect(
			final Map<Long, DBCombinedCommitInfo> combinedCommits,
			final Map<Long, DBCommitInfo> originalCommits,
			final DBCombinedCommitInfo latestCombinedCommit)
			throws SQLException {
		logger.info("detecting changed files in each revision ... ");
		final ConcurrentMap<String, SortedSet<ChangeOnFile>> changedFiles = detectChangedFiles(
				combinedCommits.values(), originalCommits);

		logger.info("creating  instances of files ... ");
		final Map<Long, DBFileInfo> fileInstances = createFileInstances(
				changedFiles,
				latestCombinedCommit.getAfterCombinedRevisionId(),
				combinedCommits);

		return Collections.unmodifiableMap(fileInstances);
	}

	/**
	 * detects changed files in each of target revisions
	 * 
	 * @param targetRevisions
	 * @return
	 */
	private ConcurrentMap<String, SortedSet<ChangeOnFile>> detectChangedFiles(
			final Collection<DBCombinedCommitInfo> combinedCommits,
			final Map<Long, DBCommitInfo> originalCommits) {
		final Thread[] threads = new Thread[threadsCount];
		final ChangedFilesDetectingThread[] detectingThreads = new ChangedFilesDetectingThread[threadsCount];

		final AtomicInteger index = new AtomicInteger(0);

		final DBCombinedCommitInfo[] commitsAsArray = combinedCommits
				.toArray(new DBCombinedCommitInfo[0]);

		final ConcurrentMap<Long, DBCommitInfo> originalCommitsMap = new ConcurrentHashMap<Long, DBCommitInfo>();
		originalCommitsMap.putAll(originalCommits);

		for (int i = 0; i < threadsCount; i++) {
			final ChangedFilesDetectingThread detectingThread = new ChangedFilesDetectingThread(
					changedFilesDetectors, language, commitsAsArray,
					originalCommitsMap, index);
			detectingThreads[i] = detectingThread;
			threads[i] = new Thread(detectingThread);
			threads[i].start();
		}

		for (final Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		final ConcurrentMap<String, SortedSet<ChangeOnFile>> result = new ConcurrentHashMap<String, SortedSet<ChangeOnFile>>();

		for (final ChangedFilesDetectingThread detectingThread : detectingThreads) {
			final Map<String, SortedSet<ChangeOnFile>> changedFiles = detectingThread
					.getChangedFiles();
			for (final Map.Entry<String, SortedSet<ChangeOnFile>> entry : changedFiles
					.entrySet()) {
				if (result.containsKey(entry.getKey())) {
					result.get(entry.getKey()).addAll(entry.getValue());
				} else {
					final SortedSet<ChangeOnFile> newSet = new TreeSet<ChangeOnFile>();
					newSet.addAll(entry.getValue());
					result.put(entry.getKey(), newSet);
				}
			}
		}

		return result;
	}

	/**
	 * create FileInfo instances from the information of changes on files
	 * 
	 * @param changes
	 * @param lastRevisionId
	 * @return
	 */
	private ConcurrentMap<Long, DBFileInfo> createFileInstances(
			final ConcurrentMap<String, SortedSet<ChangeOnFile>> changes,
			final long lastRevisionId,
			final Map<Long, DBCombinedCommitInfo> commits) {
		final Thread[] threads = new Thread[threadsCount];

		final ConcurrentMap<Long, DBFileInfo> fileInstances = new ConcurrentHashMap<Long, DBFileInfo>();
		final AtomicInteger index = new AtomicInteger(0);

		final String[] targetPathsAsArray = changes.keySet().toArray(
				new String[0]);

		final ConcurrentMap<Long, DBCombinedCommitInfo> concurrentCommits = new ConcurrentHashMap<Long, DBCombinedCommitInfo>();
		concurrentCommits.putAll(commits);

		for (int i = 0; i < threadsCount; i++) {
			threads[i] = new Thread(new FileInfoInstancesCreatingThread(
					changes, concurrentCommits, fileInstances,
					targetPathsAsArray, index, lastRevisionId));
			threads[i].start();
		}

		for (final Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return fileInstances;
	}

}
