package jp.ac.osaka_u.ist.sdl.ectec.main.filedetector;

import java.util.SortedSet;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCombinedCommitInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBFileInfo;

import org.apache.log4j.Logger;

/**
 * A thread class to create instances of FileInfo
 * 
 * @author k-hotta
 * 
 */
public class FileInfoInstancesCreatingThread implements Runnable {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggingManager
			.getLogger(FileInfoInstancesCreatingThread.class.getName());

	/**
	 * the logger for errors
	 */
	private static final Logger eLogger = LoggingManager.getLogger("error");

	/**
	 * the file path and when it was changed
	 */
	private final ConcurrentMap<String, SortedSet<ChangeOnFile>> changedFiles;

	/**
	 * the combined commits
	 */
	private final ConcurrentMap<Long, DBCombinedCommitInfo> combinedCommits;

	/**
	 * the instances of FileInfo
	 */
	private final ConcurrentMap<Long, DBFileInfo> files;

	/**
	 * the file paths to be analyzed
	 */
	private final String[] targetPaths;

	/**
	 * the index that points current processing state
	 */
	private final AtomicInteger index;

	/**
	 * the id of the latest revision
	 */
	private final long lastRevisionId;

	public FileInfoInstancesCreatingThread(
			final ConcurrentMap<String, SortedSet<ChangeOnFile>> changedFiles,
			final ConcurrentMap<Long, DBCombinedCommitInfo> combinedCommits,
			final ConcurrentMap<Long, DBFileInfo> files,
			final String[] targetPaths, final AtomicInteger index,
			final long lastRevisionId) {
		this.changedFiles = changedFiles;
		this.combinedCommits = combinedCommits;
		this.files = files;
		this.targetPaths = targetPaths;
		this.index = index;
		this.lastRevisionId = lastRevisionId;
	}

	@Override
	public void run() {
		while (true) {
			final int currentIndex = index.getAndIncrement();

			if (currentIndex >= targetPaths.length) {
				break;
			}

			final String path = targetPaths[currentIndex];

			logger.info("[" + (currentIndex + 1) + "/" + targetPaths.length
					+ "] analyzing " + path);

			// get the ids of revisions where this file was updated
			final SortedSet<ChangeOnFile> changes = changedFiles.get(path);
			if (changes == null) {
				eLogger.warn("something is wrong when analyzing " + path);
				continue;
			}

			// create new instances of FileInfo
			ChangeOnFile currentChange = null;

			for (final ChangeOnFile nextChange : changes) {
				if (currentChange == null) {
					currentChange = nextChange;
					continue;
				}

				final DBCombinedCommitInfo currentChangeCombinedCommit = combinedCommits
						.get(currentChange.getCombinedCommitId());

				final DBCombinedCommitInfo nextChangeCombinedCommit = combinedCommits
						.get(nextChange.getCombinedCommitId());

				final long previousCombinedRevisionId = nextChangeCombinedCommit
						.getBeforeCombinedRevisionId();

				switch (currentChange.getChangeType()) {
				case ADD:
					final DBFileInfo addedFile = new DBFileInfo(
							currentChange.getRepositoryId(), path,
							currentChangeCombinedCommit
									.getAfterCombinedRevisionId(),
							previousCombinedRevisionId);
					files.put(addedFile.getId(), addedFile);
					break;

				case CHANGE:
					final DBFileInfo changedFile = new DBFileInfo(
							currentChange.getRepositoryId(), path,
							currentChangeCombinedCommit
									.getAfterCombinedRevisionId(),
							previousCombinedRevisionId);
					files.put(changedFile.getId(), changedFile);
					break;

				case DELETE:
					break;
				}

				currentChange = nextChange;

			}

			final DBCombinedCommitInfo currentChangeCombinedCommit = combinedCommits
					.get(currentChange.getCombinedCommitId());

			// treat the last change
			switch (currentChange.getChangeType()) {
			case ADD:
				final DBFileInfo addedFile = new DBFileInfo(
						currentChange.getRepositoryId(), path,
						currentChangeCombinedCommit
								.getAfterCombinedRevisionId(), lastRevisionId);
				files.put(addedFile.getId(), addedFile);
				break;

			case CHANGE:
				final DBFileInfo changedFile = new DBFileInfo(
						currentChange.getRepositoryId(), path,
						currentChangeCombinedCommit
								.getAfterCombinedRevisionId(), lastRevisionId);
				files.put(changedFile.getId(), changedFile);
				break;

			case DELETE:
				break;
			}

		}
	}
}
