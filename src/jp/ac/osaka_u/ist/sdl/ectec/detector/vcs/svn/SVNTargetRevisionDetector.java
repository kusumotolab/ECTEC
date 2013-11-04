package jp.ac.osaka_u.ist.sdl.ectec.detector.vcs.svn;

import java.util.Collections;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCommitInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.detector.vcs.ITargetRevisionDetector;
import jp.ac.osaka_u.ist.sdl.ectec.settings.Language;
import jp.ac.osaka_u.ist.sdl.ectec.settings.MessagePrinter;

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
	public void detect(final Language language,
			final String startRevisionIdentifier,
			final String endRevisionIdentifier) throws Exception {
		// startRevisionIdentifier and endRevisionIdentifier must be Long
		final long startRevisionNum = Long.parseLong(startRevisionIdentifier);
		final long endRevisionNum = Long.parseLong(endRevisionIdentifier);

		final SVNRepository repository = manager.getRepository();

		// compare the specified end revision num and the latest revision num
		// and choose the lower one
		final long latestRevisionNum = repository.getLatestRevision();
		final long selectedEndRevisionNum = Math.min(endRevisionNum,
				latestRevisionNum);

		final SortedSet<Long> revisions = new TreeSet<Long>();
		final ISVNLogEntryHandler handler = new ISVNLogEntryHandler() {
			public void handleLogEntry(SVNLogEntry logEntry)
					throws SVNException {

				for (final Map.Entry<String, SVNLogEntryPath> entry : logEntry
						.getChangedPaths().entrySet()) {

					// in the case that a target source file was
					// changed
					if (language.isTarget(entry.getKey())) {
						final long revision = logEntry.getRevision();
						revisions.add(revision);
						MessagePrinter.println("\trevision " + revision
								+ " was identified as a target revision");
						break;
					}

					// in the case that a directory might be deleted
					else if (('D' == entry.getValue().getType())
							|| ('R' == entry.getValue().getType())) {
						final long revision = logEntry.getRevision();
						revisions.add(revision);
						MessagePrinter.println("\trevision " + revision
								+ " was identified as a target revision");
						break;
					}
				}
			}
		};

		for (long currentRevisionNum = startRevisionNum; currentRevisionNum <= selectedEndRevisionNum; currentRevisionNum++) {
			try {
				repository.log(null, currentRevisionNum, currentRevisionNum,
						true, false, handler);
			} catch (Exception e) {
				MessagePrinter.ePrintln("\trevision " + currentRevisionNum
						+ " was ignored due to an error");
			}
		}

		DBRevisionInfo previousRevision = new DBRevisionInfo(-1, "INITIAL");
		for (final long revision : revisions) {
			final DBRevisionInfo newRevision = new DBRevisionInfo(
					((Long) revision).toString());
			targetRevisions.put(newRevision.getId(), newRevision);

			final DBCommitInfo commit = new DBCommitInfo(
					previousRevision.getId(), newRevision.getId(),
					previousRevision.getIdentifier(),
					newRevision.getIdentifier());
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
