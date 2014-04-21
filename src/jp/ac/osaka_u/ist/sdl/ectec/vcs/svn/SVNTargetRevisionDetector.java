package jp.ac.osaka_u.ist.sdl.ectec.vcs.svn;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCommitInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.settings.Language;
import jp.ac.osaka_u.ist.sdl.ectec.vcs.ITargetRevisionDetector;

import org.apache.log4j.Logger;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.io.SVNRepository;

/**
 * A target revision detector for a SVN repository
 * 
 * @author k-hotta
 * 
 */
public class SVNTargetRevisionDetector implements ITargetRevisionDetector {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggingManager
			.getLogger(SVNTargetRevisionDetector.class.getName());

	/**
	 * the logger for errors
	 */
	private static final Logger eLogger = LoggingManager.getLogger("error");

	/**
	 * the repository manager
	 */
	private final SVNRepositoryManager manager;

	/**
	 * detected target revisions
	 */
	private final Map<Long, DBRevisionInfo> targetRevisions;

	/**
	 * detected commits
	 */
	private final Map<Long, DBCommitInfo> commits;

	public SVNTargetRevisionDetector(final SVNRepositoryManager manager) {
		this.manager = manager;
		this.targetRevisions = new TreeMap<Long, DBRevisionInfo>();
		this.commits = new TreeMap<Long, DBCommitInfo>();
	}

	@Override
	public void detect(final Language language) throws Exception {
		final SVNRepository repository = manager.getRepository();

		final long latestRevisionNum = repository.getLatestRevision();

		final SortedMap<Long, Date> revisions = new TreeMap<Long, Date>();
		final ISVNLogEntryHandler handler = new ISVNLogEntryHandler() {
			public void handleLogEntry(SVNLogEntry logEntry)
					throws SVNException {

				for (final Map.Entry<String, SVNLogEntryPath> entry : logEntry
						.getChangedPaths().entrySet()) {

					// in the case that a target source file was
					// changed
					if (language.isTarget(entry.getKey())) {
						final long revision = logEntry.getRevision();
						revisions.put(revision, logEntry.getDate());
						logger.debug("\t[" + manager.getRepositoryName()
								+ "] revision " + revision
								+ " was identified as a target revision");
						break;
					}

					// in the case that a directory might be deleted
					else if (('D' == entry.getValue().getType())
							|| ('R' == entry.getValue().getType())) {
						final long revision = logEntry.getRevision();
						revisions.put(revision, logEntry.getDate());
						logger.debug("\t[" + manager.getRepositoryName()
								+ "] revision " + revision
								+ " was identified as a target revision");
						break;
					}
				}
			}
		};

		for (long currentRevisionNum = 1; currentRevisionNum <= latestRevisionNum; currentRevisionNum++) {
			try {
				repository.log(null, currentRevisionNum, currentRevisionNum,
						true, false, handler);
			} catch (Exception e) {
				eLogger.warn("\t[" + manager.getRepositoryName()
						+ "] revision " + currentRevisionNum
						+ " was ignored due to an error");
			}
		}

		final long repositoryId = manager.getRepositoryId();

		DBRevisionInfo previousRevision = new DBRevisionInfo(-1, "INITIAL",
				repositoryId);
		for (final Map.Entry<Long, Date> entry : revisions.entrySet()) {
			final DBRevisionInfo newRevision = new DBRevisionInfo(
					((Long) entry.getKey()).toString(), repositoryId);
			targetRevisions.put(newRevision.getId(), newRevision);

			final DBCommitInfo commit = new DBCommitInfo(repositoryId,
					previousRevision.getId(), newRevision.getId(),
					previousRevision.getIdentifier(),
					newRevision.getIdentifier(), entry.getValue());
			commits.put(commit.getId(), commit);

			previousRevision = newRevision;
		}

	}

	@Override
	public Map<Long, DBRevisionInfo> getTargetRevisions() {
		return Collections.unmodifiableMap(targetRevisions);
	}

	@Override
	public Map<Long, DBCommitInfo> getCommits() {
		return Collections.unmodifiableMap(commits);
	}
}
