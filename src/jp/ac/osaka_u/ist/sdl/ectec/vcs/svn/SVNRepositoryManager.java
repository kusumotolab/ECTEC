package jp.ac.osaka_u.ist.sdl.ectec.vcs.svn;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.ectec.settings.Language;
import jp.ac.osaka_u.ist.sdl.ectec.vcs.AbstractRepositoryManager;
import jp.ac.osaka_u.ist.sdl.ectec.vcs.AbstractTargetRevisionDetector;
import jp.ac.osaka_u.ist.sdl.ectec.vcs.IChangedFilesDetector;

import org.tmatesoft.svn.core.ISVNDirEntryHandler;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCClient;

/**
 * A class for managing the svn repository
 * 
 * @author k-hotta
 * 
 */
public class SVNRepositoryManager extends AbstractRepositoryManager {

	/**
	 * the URL of the repository
	 */
	private final SVNURL url;

	/**
	 * the repository under managed by this manager
	 */
	private final SVNRepository repository;

	public SVNRepositoryManager(final String rootUrl,
			final String additionalUrl, final String userName,
			final String passwd, final String repositoryName,
			final long repositoryId) throws Exception {
		super(rootUrl, additionalUrl, userName, passwd, repositoryName,
				repositoryId);

		final String urlStr = (additionalUrl == null) ? rootUrl : rootUrl
				+ additionalUrl;

		this.url = SVNURL.parseURIDecoded(urlStr);

		final RepositoryCreator creator = RepositoryCreator
				.getCorrespondingInstance(urlStr);

		this.repository = creator.create(url, userName, passwd);
	}

	/**
	 * get the target revisions detector corresponding to each version control
	 * system
	 * 
	 * @return
	 */
	@Override
	public AbstractTargetRevisionDetector<?> createTargetRevisionDetector() {
		return new SVNTargetRevisionDetector(this);
	}

	/**
	 * create a new changed files detector
	 */
	@Override
	public IChangedFilesDetector createChangedFilesDetector() {
		return new SVNChangedFilesDetector(this);
	}

	/**
	 * get the repository as SVNRepository
	 * 
	 * @return
	 */
	public SVNRepository getRepository() {
		return this.repository;
	}

	/**
	 * get the url of the repository as SVNURL
	 * 
	 * @return
	 */
	public SVNURL getURL() {
		return this.url;
	}

	/**
	 * get the file contents
	 * 
	 * @param revisionIdentifier
	 * @param path
	 * @return
	 * @throws SVNException
	 */
	@Override
	public String getFileContents(final String revisionIdentifier,
			final String path) throws SVNException {
		return getFileContents(Long.parseLong(revisionIdentifier), path);
	}

	/**
	 * get the contents of the given file as String
	 * 
	 * @param revisionNum
	 * @param path
	 * @return
	 * @throws SVNException
	 */
	public synchronized String getFileContents(final long revisionNum,
			final String path) throws SVNException {
		
		final StringBuilder builder = new StringBuilder();

		final SVNURL target = this.url.appendPath(path, false);
		final SVNClientManager clientManager = SVNClientManager.newInstance(null,
				this.userName, this.passwd);
		final SVNWCClient wcClient = clientManager.getWCClient();
		wcClient.doGetFileContents(target, SVNRevision.create(revisionNum),
				SVNRevision.create(revisionNum), false, new OutputStream() {

					@Override
					public void write(int b) throws IOException {
						builder.append((char) b);
					}

				});
		
		clientManager.dispose();
		
		return builder.toString();
	}

	/**
	 * get the list of paths of all the source files in the given revision
	 * 
	 * @param revisionNum
	 * @param lang
	 * @return
	 * @throws SVNException
	 */
	public synchronized List<String> getListOfSourceFiles(
			final long revisionNum, final Language lang) throws SVNException {
		final String nullStr = null;
		return getListOfSourceFiles(revisionNum, lang, nullStr);
	}

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
	@Override
	public synchronized List<String> getListOfSourceFiles(
			final String revisionIdentifier, final Language language,
			final Collection<String> targets) throws Exception {
		return getListOfSourceFiles(Long.parseLong(revisionIdentifier),
				language, targets);
	}

	/**
	 * get the list of paths of all the source files in the given revision which
	 * is included in the given collection of strings
	 * 
	 * @param revisionNum
	 * @param lang
	 * @param targets
	 * @return
	 * @throws SVNException
	 */
	public synchronized List<String> getListOfSourceFiles(
			final long revisionNum, final Language lang,
			final Collection<String> targets) throws SVNException {
		final List<String> result = new ArrayList<String>();
		for (final String target : targets) {
			try {
				result.addAll(getListOfSourceFiles(revisionNum, lang, target));
			} catch (SVNException e) {
				// ignore
			}
		}
		return Collections.unmodifiableList(result);
	}

	public synchronized List<String> getListOfSourceFiles(
			final long revisionNum, final Language lang, final String target)
			throws SVNException {
		final SVNClientManager clientManager = SVNClientManager.newInstance(null,
				this.userName, this.passwd);
		final SVNLogClient logClient = clientManager.getLogClient();

		final SVNURL url = (target == null) ? this.url : this.url.appendPath(
				target, false);

		final List<String> result = new ArrayList<String>();
		logClient.doList(url, SVNRevision.create(revisionNum),
				SVNRevision.create(revisionNum), true, SVNDepth.INFINITY,
				SVNDirEntry.DIRENT_ALL, new ISVNDirEntryHandler() {

					@Override
					public void handleDirEntry(SVNDirEntry dirEntry)
							throws SVNException {
						final String path = dirEntry.getRelativePath();

						if (lang.isTarget(path)) {
							result.add(dirEntry.getRelativePath());
						}
					}

				});
		
		clientManager.dispose();

		return Collections.unmodifiableList(result);
	}

}
