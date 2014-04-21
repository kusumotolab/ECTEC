package jp.ac.osaka_u.ist.sdl.ectec.main.filedetector;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCombinedCommitInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCommitInfo;
import jp.ac.osaka_u.ist.sdl.ectec.main.IllegalStateException;
import jp.ac.osaka_u.ist.sdl.ectec.settings.Language;
import jp.ac.osaka_u.ist.sdl.ectec.vcs.IChangedFilesDetector;

import org.apache.log4j.Logger;

/**
 * A thread class that detects changed files with specified detector
 * 
 * @author k-hotta
 * 
 */
public class ChangedFilesDetectingThread implements Runnable {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggingManager
			.getLogger(ChangedFilesDetectingThread.class.getName());

	/**
	 * the logger for errors
	 */
	private static final Logger eLogger = LoggingManager.getLogger("error");

	/**
	 * detectors
	 */
	private final ConcurrentMap<Long, IChangedFilesDetector> detectors;

	/**
	 * the map contains the result of analysis <br>
	 * the key is the path of a file and the value is a set of ids of revisions
	 * where the file whose path is specified with the key is changed
	 */
	private final Map<String, SortedSet<ChangeOnFile>> changedFiles;

	/**
	 * the target language
	 */
	private final Language language;

	/**
	 * the target combined commits as array
	 */
	private final DBCombinedCommitInfo[] combinedCommits;

	/**
	 * the original commits
	 */
	private final ConcurrentMap<Long, DBCommitInfo> originalCommits;

	/**
	 * the counter that points which revision is the next target
	 */
	private final AtomicInteger index;

	public ChangedFilesDetectingThread(
			final ConcurrentMap<Long, IChangedFilesDetector> detectors,
			final Language language,
			final DBCombinedCommitInfo[] combinedCommits,
			final ConcurrentMap<Long, DBCommitInfo> originalCommits,
			final AtomicInteger index) {
		this.detectors = detectors;
		this.changedFiles = new HashMap<String, SortedSet<ChangeOnFile>>();
		this.language = language;
		this.combinedCommits = combinedCommits;
		this.originalCommits = originalCommits;
		this.index = index;
	}

	public final Map<String, SortedSet<ChangeOnFile>> getChangedFiles() {
		return Collections.unmodifiableMap(changedFiles);
	}

	@Override
	public void run() {
		while (true) {
			final int currentIndex = index.getAndIncrement();

			if (currentIndex >= combinedCommits.length) {
				break;
			}

			final DBCombinedCommitInfo targetCombinedCommit = combinedCommits[currentIndex];
			final DBCommitInfo targetOriginalCommit = originalCommits
					.get(targetCombinedCommit.getOriginalCommitId());

			try {

				if (targetOriginalCommit == null) {
					throw new IllegalStateException(
							"cannot find corresponding original commit to combined commit "
									+ targetCombinedCommit.getId());
				}

				logger.info("[" + (currentIndex + 1) + "/"
						+ combinedCommits.length
						+ "] analyzing combined commit "
						+ targetCombinedCommit.getId()
						+ " (original commit : commit on repository "
						+ targetOriginalCommit.getRepositoryId()
						+ " from revision "
						+ targetOriginalCommit.getBeforeRevisionIdentifier()
						+ " to revision "
						+ targetOriginalCommit.getAfterRevisionIdentifier());

				// detect changed files in this revision
				final IChangedFilesDetector detector = detectors
						.get(targetOriginalCommit.getRepositoryId());
				final Map<String, Character> changedFilesInThisRevision = detector
						.detectChangedFiles(targetOriginalCommit, language);

				// store paths detected the above step into the map
				for (final Map.Entry<String, Character> entry : changedFilesInThisRevision
						.entrySet()) {
					// the path of a changed file
					final String path = entry.getKey();

					/*
					 * put a new entry into the map if the path is not contained
					 * in it as a key
					 */
					if (!changedFiles.containsKey(path)) {
						changedFiles.put(path, new TreeSet<ChangeOnFile>());
					}

					final ChangeOnFile changeOnFile = new ChangeOnFile(
							targetOriginalCommit.getRepositoryId(), path,
							targetCombinedCommit.getId(),
							targetOriginalCommit.getDate(),
							ChangeTypeOnFile.getCorrespondingChangeType(entry
									.getValue()));
					changedFiles.get(path).add(changeOnFile);
				}

			} catch (Exception e) {
				eLogger.warn("something is wrong when processing combined commit "
						+ targetCombinedCommit.getId() + "\n" + e.toString());
			}
		}
	}
}
