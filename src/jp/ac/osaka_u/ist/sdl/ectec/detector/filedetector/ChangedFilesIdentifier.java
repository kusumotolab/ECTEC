package jp.ac.osaka_u.ist.sdl.ectec.detector.filedetector;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCommitInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBFileInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer.FileRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.detector.vcs.IRepositoryManager;
import jp.ac.osaka_u.ist.sdl.ectec.settings.Language;
import jp.ac.osaka_u.ist.sdl.ectec.settings.MessagePrinter;

/**
 * A class to detect and register changed files
 * 
 * @author k-hotta
 * 
 */
public class ChangedFilesIdentifier {

	/**
	 * the repository manager
	 */
	private final IRepositoryManager manager;

	/**
	 * the registerer for files
	 */
	private final FileRegisterer registerer;

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
	 * @param detector
	 * @param registerer
	 */
	public ChangedFilesIdentifier(final IRepositoryManager manager,
			final FileRegisterer registerer, final Language language,
			final int threadsCount) {
		this.manager = manager;
		this.registerer = registerer;
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
	public void detectAndRegister(final Map<Long, DBCommitInfo> commits)
			throws SQLException {
		final SortedSet<Long> revisionsAsSet = new TreeSet<Long>();
		for (final Map.Entry<Long, DBCommitInfo> entry : commits.entrySet()) {
			revisionsAsSet.add(entry.getValue().getAfterRevisionId());
		}

		MessagePrinter
				.stronglyPrintln("detecting changed files in each revision ... ");
		final ConcurrentMap<String, SortedSet<ChangeOnFile>> changedFiles = detectChangedFiles(commits
				.values());
		MessagePrinter.stronglyPrintln();

		MessagePrinter.stronglyPrintln("creating  instances of files ... ");
		final ConcurrentMap<Long, DBFileInfo> fileInstances = createFileInstances(
				changedFiles, revisionsAsSet.last(), commits);
		MessagePrinter.stronglyPrintln();

		MessagePrinter.stronglyPrintln("registering files ... ");
		registerer.register(fileInstances.values());
		MessagePrinter.stronglyPrintln("\tOK");
		MessagePrinter.stronglyPrintln();
	}

	/**
	 * detects changed files in each of target revisions
	 * 
	 * @param targetRevisions
	 * @return
	 */
	private ConcurrentMap<String, SortedSet<ChangeOnFile>> detectChangedFiles(
			final Collection<DBCommitInfo> commits) {
		final Thread[] threads = new Thread[threadsCount];

		final ConcurrentMap<String, SortedSet<ChangeOnFile>> changedFiles = new ConcurrentHashMap<String, SortedSet<ChangeOnFile>>();
		final AtomicInteger index = new AtomicInteger(0);

		final DBCommitInfo[] commitsAsArray = commits.toArray(new DBCommitInfo[0]);

		for (int i = 0; i < threadsCount; i++) {
			threads[i] = new Thread(new ChangedFilesDetectingThread(
					manager.createChangedFilesDetector(), changedFiles,
					language, commitsAsArray, index));
			threads[i].start();
		}

		for (final Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return changedFiles;
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
			final long lastRevisionId, final Map<Long, DBCommitInfo> commits) {
		final Thread[] threads = new Thread[threadsCount];

		final ConcurrentMap<Long, DBFileInfo> fileInstances = new ConcurrentHashMap<Long, DBFileInfo>();
		final AtomicInteger index = new AtomicInteger(0);

		final String[] targetPathsAsArray = changes.keySet().toArray(
				new String[0]);

		final ConcurrentMap<Long, DBCommitInfo> concurrentCommits = new ConcurrentHashMap<Long, DBCommitInfo>();
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
