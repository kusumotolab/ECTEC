package jp.ac.osaka_u.ist.sdl.ectec.vcs.git;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCommitInfo;
import jp.ac.osaka_u.ist.sdl.ectec.settings.Language;
import jp.ac.osaka_u.ist.sdl.ectec.vcs.IChangedFilesDetector;

/**
 * A class to detect files changed in commits of git repositories
 *
 * @author (please append your name)
 *
 */
public class GitChangedFilesDetector implements IChangedFilesDetector {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggingManager
			.getLogger(GitTargetRevisionDetector.class.getName());
	/**
	 * the repository manager
	 */
	private final GitRepositoryManager manager;

	public GitChangedFilesDetector(final GitRepositoryManager manager) {
		this.manager = manager;
	}

	@Override
	public Map<String, Character> detectChangedFiles(DBCommitInfo commit,
			Language language) throws Exception {
		// TODO implement me

		/*
		 * please return a map
		 * whose keys are file paths changed in the given commit and written in the specified language
		 * whose values are types of changes as a character (A, D, or M)
		 * A means an addition of a file
		 * D means a deletion of a file
		 * M means a modification of a file
		 */

		final String beforeRevision = commit.getBeforeRevisionIdentifier();

		final String afterRevision = commit.getAfterRevisionIdentifier();

		logger.debug("beforeRevision: " + beforeRevision
				+ " afterRevision: " + afterRevision
				+ " beforeRevisionId: " + commit.getBeforeRevisionId());
		// a special treat for the initial commit
		if (commit.getBeforeRevisionId() == -1) {
			final List<String> allFilesInAfterRev = manager
					.getListOfSourceFiles(afterRevision, language);

			final Map<String, Character> result = new HashMap<String, Character>();

			for (final String file : allFilesInAfterRev) {
					result.put(file, 'A');
					logger.debug("combined commit: " + commit.getId()
					+ " detectFile: " + file
					+ " changeType: " + "A");
			}
			return Collections.unmodifiableMap(result);
		}

		final Map<String, Character> result = new HashMap<String, Character>();

		final Repository repository = manager.getRepository();

		RevWalk revWalk = new RevWalk(repository);

		ObjectId oldHead = revWalk.parseCommit(repository.resolve(beforeRevision))
				.getTree()
				.getId();

		ObjectId head = revWalk.parseCommit(repository.resolve(afterRevision))
				.getTree()
				.getId();

		ObjectReader reader = repository.newObjectReader();
		CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
		oldTreeIter.reset(reader, oldHead);
		CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
		newTreeIter.reset(reader, head);

		Git git = new Git(repository);
		List<DiffEntry> diffs= git.diff()
    		                    .setNewTree(newTreeIter)
    		                    .setOldTree(oldTreeIter)
    		                    .call();
		for (DiffEntry entry : diffs) {
			Character changeType = null;
			String filePath = null;
			if(entry.getChangeType().toString().equals("ADD")){
				changeType = 'A';
				filePath = entry.getNewPath();
			}else if(entry.getChangeType().toString().equals("DELETE")){
				changeType = 'D';
				filePath = entry.getOldPath();
			}else if(entry.getChangeType().toString().equals("MODIFY")){
				changeType = 'M';
				filePath = entry.getNewPath();
			}

			if(language.isTarget(filePath)){
				result.put(filePath, changeType);
				logger.debug("combined commit: " + commit.getId()
				+ " detectFile: " + filePath
				+ " changeType: " + changeType);
			}
		}

		return Collections.unmodifiableMap(result);
	}

}
