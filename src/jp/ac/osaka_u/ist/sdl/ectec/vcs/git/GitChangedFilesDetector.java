package jp.ac.osaka_u.ist.sdl.ectec.vcs.git;

import java.util.Map;

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

		return null;
	}

}
