package jp.ac.osaka_u.ist.sdl.ectec.vcs.svn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCommitInfo;
import jp.ac.osaka_u.ist.sdl.ectec.settings.Language;
import jp.ac.osaka_u.ist.sdl.ectec.vcs.IChangedFilesDetector;

import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.io.SVNRepository;

/**
 * A class that detects added/changed/deleted files from the given svn
 * repository
 * 
 * @author k-hotta
 * 
 */
public class SVNChangedFilesDetector implements IChangedFilesDetector {

	/**
	 * the manager of the target repository
	 */
	private final SVNRepositoryManager manager;

	public SVNChangedFilesDetector(final SVNRepositoryManager manager) {
		this.manager = manager;
	}

	@Override
	public Map<String, Character> detectChangedFiles(final DBCommitInfo commit,
			final Language language) throws Exception {
		final long afterRevision = Long.parseLong(commit
				.getAfterRevisionIdentifier());

		// a special treat for the initial commit
		if (commit.getBeforeRevisionId() == -1) {
			final List<String> allFilesInAfterRev = manager
					.getListOfSourceFiles(afterRevision, language);

			final Map<String, Character> result = new HashMap<String, Character>();

			for (final String file : allFilesInAfterRev) {
				result.put(file, 'A');
			}
			return Collections.unmodifiableMap(result);
		}

		final Map<String, Character> result = new HashMap<String, Character>();

		final List<String> deleted = new ArrayList<String>();

		final SVNRepository repository = manager.getRepository();

		repository.log(null, afterRevision, afterRevision, true, false,
				new ISVNLogEntryHandler() {
					public void handleLogEntry(SVNLogEntry logEntry)
							throws SVNException {

						for (final Map.Entry<String, SVNLogEntryPath> entry : logEntry
								.getChangedPaths().entrySet()) {

							final String targetStr;
							if (manager.getAdditionalUrl() != null) {
								targetStr = entry.getKey()
										.substring(
												manager.getAdditionalUrl()
														.length() + 1);
							} else {
								targetStr = entry.getKey().substring(1);
							}

							String copyStr = entry.getValue().getCopyPath();
							if (copyStr != null) {
								if (manager.getAdditionalUrl() != null) {
									copyStr = copyStr.substring(manager
											.getAdditionalUrl().length() + 1);
								} else {
									copyStr = copyStr.substring(1);
								}
							}
							// for (final String sourceFilePath :
							// allFilesInAfterRev) {
							// if (targetStr.endsWith(sourceFilePath)) {
							// targetStr = sourceFilePath;
							// break;
							// }
							// }

							final char type = entry.getValue().getType();

							// in the case that source files are updated
							if (language.isTarget(entry.getKey())) {
								if (copyStr != null && type == 'R') {
									if (language.isTarget(copyStr)) {
										result.put(copyStr, 'D');
									}
									result.put(targetStr, 'A');
								} else {
									result.put(targetStr, type);
								}
							}

							// in the case that directories are deleted
							else if ('D' == entry.getValue().getType()) {
								deleted.add(targetStr);
							}
						}
					}
				});

		if (!deleted.isEmpty()) {
			final List<String> sourceFilesInDeletedDir = manager
					.getListOfSourceFiles(Long.parseLong(commit
							.getBeforeRevisionIdentifier()), language, deleted);

			for (final String deletedFile : sourceFilesInDeletedDir) {
				if (!result.containsKey(deletedFile)) {
					result.put(deletedFile, 'D');
				}
			}

			// for (final String deletedDir : deleted) {
			// for (final String deletedFile : sourceFilesInBeforeRev) {
			// final String dirName = deletedFile.substring(0,
			// deletedFile.lastIndexOf("/"));
			// if (deletedDir.endsWith(dirName)) {
			// result.put(deletedFile, 'D');
			// break;
			// }
			// }
			// }

		}

		return Collections.unmodifiableMap(result);
	}

}
