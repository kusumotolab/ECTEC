package jp.ac.osaka_u.ist.sdl.ectec.vcs.git;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.settings.Language;
import jp.ac.osaka_u.ist.sdl.ectec.vcs.AbstractTargetRevisionDetector;

import org.apache.log4j.Logger;

/**
 * A class to detect target revisions and commits of git repositories
 * 
 * @author (please append your name)
 * 
 */
public class GitTargetRevisionDetector extends
		AbstractTargetRevisionDetector<GitRepositoryManager> {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggingManager
			.getLogger(GitTargetRevisionDetector.class.getName());

	/**
	 * the logger for errors
	 */
	private static final Logger eLogger = LoggingManager.getLogger("error");

	public GitTargetRevisionDetector(final GitRepositoryManager manager) {
		super(manager);
	}

	@Override
	protected Map<String, Date> detectRevisionsAfterTargetCommits(
			final Language language, final Set<String> ignoredList)
			throws Exception {
		// TODO implement me

		/*
		 * please return a sorted map whose keys are identifiers of AFTER
		 * revisions of commits that added/deleted/changed at least one source
		 * file whose values are dates of the commits
		 */

		return null;
	}

}
