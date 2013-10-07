package jp.ac.osaka_u.ist.sdl.ectec.analyzer.vcs.svn;

import java.util.Collections;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.vcs.ITargetRevisionDetector;
import jp.ac.osaka_u.ist.sdl.ectec.data.Commit;
import jp.ac.osaka_u.ist.sdl.ectec.data.RevisionInfo;
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
	private final Map<Long, RevisionInfo> targetRevisions;

	/**
	 * detected commits
	 */
	private final Map<Long, Commit> commits;

	public SVNTargetRevisionDetector(final SVNRepositoryManager manager) {
		this.manager = manager;
		this.targetRevisions = new TreeMap<Long, RevisionInfo>();
		this.commits = new TreeMap<Long, Commit>();
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
		repository.log(null, startRevisionNum, selectedEndRevisionNum, true,
				false, new ISVNLogEntryHandler() {
					public void handleLogEntry(SVNLogEntry logEntry)
							throws SVNException {

						for (final Map.Entry<String, SVNLogEntryPath> entry : logEntry
								.getChangedPaths().entrySet()) {

							// in the case that a target source file was changed
							if (language.isTarget(entry.getKey())) {
								final long revision = logEntry.getRevision();
								revisions.add(revision);
								MessagePrinter
										.println("\trevision "
												+ revision
												+ " was identified as a target revision");
								break;
							}

							// in the case that a directory might be deleted
							else if (('D' == entry.getValue().getType())
									|| ('R' == entry.getValue().getType())) {
								final long revision = logEntry.getRevision();
								revisions.add(revision);
								MessagePrinter
										.println("\trevision "
												+ revision
												+ " was identified as a target revision");
								break;
							}
						}
					}
				});

		RevisionInfo previousRevision = new RevisionInfo(-1, "INITIAL");
		for (final long revision : revisions) {
			final RevisionInfo newRevision = new RevisionInfo(
					((Long) revision).toString());
			targetRevisions.put(newRevision.getId(), newRevision);

			final Commit commit = new Commit(previousRevision.getId(),
					newRevision.getId(), previousRevision.getIdentifier(),
					newRevision.getIdentifier());
			commits.put(commit.getId(), commit);

			previousRevision = newRevision;
		}

	}

	@Override
	public Map<Long, RevisionInfo> getTargetRevisions() {
		return Collections.unmodifiableMap(targetRevisions);
	}

	@Override
	public Map<Long, Commit> getCommits() {
		return Collections.unmodifiableMap(commits);
	}
}
