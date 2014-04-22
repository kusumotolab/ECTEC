package jp.ac.osaka_u.ist.sdl.ectec.vcs;

import java.util.Collection;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.ectec.settings.Language;

/**
 * An abstract class to represent managers of repositories
 * 
 * @author k-hotta
 * 
 */
public abstract class AbstractRepositoryManager {

	/**
	 * the user name which is used to access the repository
	 */
	protected final String userName;

	/**
	 * the password which is used to access the repository
	 */
	protected final String passwd;

	/**
	 * the name of the repository
	 */
	protected final String repositoryName;

	/**
	 * the id of the repository
	 */
	protected final long repositoryId;

	public AbstractRepositoryManager(final String userName,
			final String passwd, final String repositoryName,
			final long repositoryId) {
		this.userName = userName;
		this.passwd = passwd;
		this.repositoryName = repositoryName;
		this.repositoryId = repositoryId;
	}

	public final String getUserName() {
		return userName;
	}

	public final String getPasswd() {
		return passwd;
	}

	public final String getRepositoryName() {
		return repositoryName;
	}

	public final long getRepositoryId() {
		return repositoryId;
	}

	/**
	 * create target revisions detector corresponding to each version control
	 * system
	 * 
	 * @return
	 */
	public abstract ITargetRevisionDetector createTargetRevisionDetector();

	/**
	 * create a new detector for changed files
	 * 
	 * @return
	 */
	public abstract IChangedFilesDetector createChangedFilesDetector();

	/**
	 * get the file contents
	 * 
	 * @param revisionIdentifier
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public abstract String getFileContents(final String revisionIdentifier,
			final String path) throws Exception;

	/**
	 * get the list of paths of all the source files in the given revision which
	 * is included in the given collection of strings
	 * 
	 * @param revisionIdentifier
	 * @param language
	 * @param targets
	 * @return
	 * @throws Exception
	 */
	public abstract List<String> getListOfSourceFiles(
			final String revisionIdentifier, final Language language,
			final Collection<String> targets) throws Exception;

}
