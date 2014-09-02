package jp.ac.osaka_u.ist.sdl.ectec.vcs.git;

import java.util.Collection;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.ectec.settings.Language;
import jp.ac.osaka_u.ist.sdl.ectec.vcs.AbstractRepositoryManager;
import jp.ac.osaka_u.ist.sdl.ectec.vcs.AbstractTargetRevisionDetector;
import jp.ac.osaka_u.ist.sdl.ectec.vcs.IChangedFilesDetector;

/**
 * A class to manage a git repository
 * 
 * @author (please write your name here)
 * 
 */
public class GitRepositoryManager extends AbstractRepositoryManager {

	public GitRepositoryManager(String rootUrl, String additionalUrl,
			String userName, String passwd, String repositoryName,
			long repositoryId) {
		super(rootUrl, additionalUrl, userName, passwd, repositoryName,
				repositoryId);
	}

	@Override
	public AbstractTargetRevisionDetector<?> createTargetRevisionDetector() {
		return new GitTargetRevisionDetector(this);
	}

	@Override
	public IChangedFilesDetector createChangedFilesDetector() {
		return new GitChangedFilesDetector(this);
	}

	@Override
	public String getFileContents(String revisionIdentifier, String path)
			throws Exception {
		// TODO implement me

		/*
		 * return the content of the file in the specified revision as a string
		 * value
		 */

		return null;
	}

	@Override
	public List<String> getListOfSourceFiles(String revisionIdentifier,
			Language language, Collection<String> targets) throws Exception {
		// TODO implement me

		/*
		 * return a list of string whose elements are paths of files in the
		 * given revision and written in the given language the list must
		 * include only paths that start with any of strings in the given
		 * collection of string if targets == null, then this method returns all
		 * the paths of source files in the given revision
		 */

		return null;
	}

}
