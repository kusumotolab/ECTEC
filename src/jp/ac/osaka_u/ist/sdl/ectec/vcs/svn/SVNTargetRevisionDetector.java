package jp.ac.osaka_u.ist.sdl.ectec.vcs.svn;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.settings.Language;
import jp.ac.osaka_u.ist.sdl.ectec.vcs.AbstractTargetRevisionDetector;

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
public class SVNTargetRevisionDetector extends
		AbstractTargetRevisionDetector<SVNRepositoryManager> {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggingManager
			.getLogger(SVNTargetRevisionDetector.class.getName());

	/**
	 * the logger for errors
	 */
	private static final Logger eLogger = LoggingManager.getLogger("error");

	public SVNTargetRevisionDetector(final SVNRepositoryManager manager) {
		super(manager);
	}

	@Override
	protected Map<String, Date> detectRevisionsAfterTargetCommits(
			final Language language, final Set<String> ignoredList)
			throws Exception {
		final SVNRepository repository = manager.getRepository();

		final long latestRevisionNum = repository.getLatestRevision();

		final Map<String, Date> revisions = new HashMap<String, Date>();
		final ISVNLogEntryHandler handler = new ISVNLogEntryHandler() {
			public void handleLogEntry(SVNLogEntry logEntry)
					throws SVNException {

				for (final Map.Entry<String, SVNLogEntryPath> entry : logEntry
						.getChangedPaths().entrySet()) {

					// in the case that a target source file was
					// changed
					if (language.isTarget(entry.getKey())) {
						final String revision = ((Long) logEntry.getRevision())
								.toString();
						revisions.put(revision, logEntry.getDate());
						logger.debug("\t[" + manager.getRepositoryName()
								+ "] revision " + revision
								+ " was identified as a target revision");
						break;
					}

					// in the case that a directory might be deleted
					else if (('D' == entry.getValue().getType())
							|| ('R' == entry.getValue().getType())) {
						final String revision = ((Long) logEntry.getRevision())
								.toString();
						revisions.put(revision, logEntry.getDate());
						logger.debug("\t[" + manager.getRepositoryName()
								+ "] revision " + revision
								+ " was identified as a target revision");
						break;
					}
				}
			}
		};

		final Set<Long> ignoredRevisionNums = new TreeSet<Long>();
		for (final String ignoredRevision : ignoredList) {
			ignoredRevisionNums.add(Long.parseLong(ignoredRevision));
		}

		for (long currentRevisionNum = 1; currentRevisionNum <= latestRevisionNum; currentRevisionNum++) {
			try {
				if (ignoredRevisionNums.contains(currentRevisionNum)) {
					logger.debug("\t["
							+ manager.getRepositoryName()
							+ "] revision "
							+ currentRevisionNum
							+ " was ignored because it is included in the ignored list");
					continue;
				}
				repository.log(null, currentRevisionNum, currentRevisionNum,
						true, false, handler);
			} catch (Exception e) {
				eLogger.warn("\t[" + manager.getRepositoryName()
						+ "] revision " + currentRevisionNum
						+ " was ignored due to an error");
			}
		}

		return Collections.unmodifiableMap(revisions);
	}

}
