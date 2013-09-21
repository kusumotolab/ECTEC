package jp.ac.osaka_u.ist.sdl.ectec.analyzer.filedetector;

import java.util.Collection;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.vcs.IRepositoryManager;
import jp.ac.osaka_u.ist.sdl.ectec.data.FileInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.RevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.registerer.FileRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.settings.Language;

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
	 */
	public void detectAndRegister(final Map<RevisionInfo, Long> targetRevisions) {
		final SortedSet<RevisionInfo> revisionsAsSet = new TreeSet<RevisionInfo>();
		revisionsAsSet.addAll(targetRevisions.keySet());
		final ConcurrentMap<String, SortedSet<ChangeOnFile>> changedFiles = detectChangedFiles(revisionsAsSet);

		final ConcurrentMap<Long, Long> revisionsMap = new ConcurrentHashMap<Long, Long>();
		for (Map.Entry<RevisionInfo, Long> entry : targetRevisions.entrySet()) {
			revisionsMap.put(entry.getKey().getId(), entry.getValue());
		}

		final ConcurrentMap<Long, FileInfo> fileInstances = createFileInstances(
				changedFiles, revisionsAsSet.last().getId(), revisionsMap);
	}

	/**
	 * detects changed files in each of target revisions
	 * 
	 * @param targetRevisions
	 * @return
	 */
	private ConcurrentMap<String, SortedSet<ChangeOnFile>> detectChangedFiles(
			final Collection<RevisionInfo> targetRevisions) {
		final Thread[] threads = new Thread[threadsCount];

		final ConcurrentMap<String, SortedSet<ChangeOnFile>> changedFiles = new ConcurrentHashMap<String, SortedSet<ChangeOnFile>>();
		final AtomicInteger index = new AtomicInteger(0);

		final RevisionInfo[] revisionsAsArray = targetRevisions
				.toArray(new RevisionInfo[0]);

		for (int i = 0; i < threadsCount; i++) {
			threads[i] = new Thread(new ChangedFilesDetectingThread(
					manager.createChangedFilesDetector(), changedFiles,
					language, revisionsAsArray, index));
			threads[i].start();
		}

		for (final Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
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
	private ConcurrentMap<Long, FileInfo> createFileInstances(
			final ConcurrentMap<String, SortedSet<ChangeOnFile>> changes,
			final long lastRevisionId,
			final ConcurrentMap<Long, Long> targetRevisions) {
		final Thread[] threads = new Thread[threadsCount];

		final ConcurrentMap<Long, FileInfo> fileInstances = new ConcurrentHashMap<Long, FileInfo>();
		final AtomicInteger index = new AtomicInteger(0);

		final String[] targetPathsAsArray = changes.keySet().toArray(
				new String[0]);

		for (int i = 0; i < threadsCount; i++) {
			threads[i] = new Thread(new FileInfoInstancesCreatingThread(
					changes, targetRevisions, fileInstances,
					targetPathsAsArray, index, lastRevisionId));
			threads[i].start();
		}

		for (final Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return fileInstances;
	}

}
