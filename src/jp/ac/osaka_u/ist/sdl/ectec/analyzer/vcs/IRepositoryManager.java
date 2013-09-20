package jp.ac.osaka_u.ist.sdl.ectec.analyzer.vcs;

import java.util.Collection;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.ectec.settings.Language;

/**
 * An interface for managers of repositories
 * 
 * @author k-hotta
 * 
 */
public interface IRepositoryManager {

	/**
	 * get the target revisions detector corresponding to each version control
	 * system
	 * 
	 * @return
	 */
	public TargetRevisionDetector getTargetRevisionDetector();

	/**
	 * get the identifier of the latest revision
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getLatestRevision() throws Exception;

	/**
	 * get the file contents
	 * 
	 * @param revisionIdentifier
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public String getFileContents(final String revisionIdentifier,
			final String path) throws Exception;

	/**
	 * get the list of source files
	 * 
	 * @param revisionIdentifier
	 * @param language
	 * @return
	 * @throws Exception
	 */
	public List<String> getListOfSourceFiles(final String revisionIdentifier,
			final Language language) throws Exception;

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
	public List<String> getListOfSourceFiles(final String revisionIdentifier,
			final Language language, final Collection<String> targets)
			throws Exception;

	/**
	 * run diff
	 * 
	 * @param beforeRevisionIdentifier
	 * @param afterRevisionIdentifier
	 * @return
	 * @throws Exception
	 */
	public String doDiff(final String beforeRevisionIdentifier,
			final String afterRevisionIdentifier) throws Exception;

}
