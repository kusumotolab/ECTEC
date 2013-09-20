package jp.ac.osaka_u.ist.sdl.ectec.analyzer.vcs.svn;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.vcs.RepositoryNotInitializedException;
import jp.ac.osaka_u.ist.sdl.ectec.settings.Language;

import org.tmatesoft.svn.core.ISVNDirEntryHandler;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNDiffClient;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCClient;

/**
 * A class for managing the svn repository
 * 
 * @author k-hotta
 * 
 */
public class SVNRepositoryManager {

	/**
	 * the URL of the repository
	 */
	private final SVNURL url;

	/**
	 * the repository under managed by this manager
	 */
	private final SVNRepository repository;

	/**
	 * a string representation of the URL of the repository
	 */
	private final String urlStr;

	/**
	 * the user name which is used to access the repository
	 */
	private final String userName;

	/**
	 * the password which is used to access the repository
	 */
	private final String passwd;

	/**
	 * the additional URL
	 */
	private final String additionalUrl;

	/**
	 * the singleton object
	 */
	private static SVNRepositoryManager SINGLETON = null;

	private SVNRepositoryManager(final SVNURL url,
			final SVNRepository repository, final String urlStr,
			final String userName, final String passwd,
			final String additionalUrl) {
		this.url = url;
		this.repository = repository;
		this.urlStr = urlStr;
		this.userName = userName;
		this.passwd = passwd;
		this.additionalUrl = additionalUrl;
	}

	/**
	 * set up the repository
	 * 
	 * @param urlRoot
	 * @param additionalUrl
	 * @param userName
	 * @param passwd
	 * @return
	 * @throws Exception
	 */
	public static SVNRepositoryManager setup(final String urlRoot,
			final String additionalUrl, final String userName,
			final String passwd) throws Exception {
		if (SINGLETON != null) {
			return SINGLETON;
		}

		final String urlStr = (additionalUrl == null) ? urlRoot : urlRoot
				+ additionalUrl;
		final SVNURL url = SVNURL.parseURIDecoded(urlStr);

		final RepositoryCreator creator = RepositoryCreator
				.getCorrespondingInstance(urlStr);

		final SVNRepository repository = creator.create(url, userName, passwd);

		SINGLETON = new SVNRepositoryManager(url, repository, urlStr, userName,
				passwd, additionalUrl);

		return SINGLETON;
	}

	/**
	 * set up the repository without using user names and passwords
	 * 
	 * @param urlStr
	 * @return
	 * @throws Exception
	 */
	public static SVNRepositoryManager setup(final String urlStr)
			throws Exception {
		return setup(urlStr, null, null, null);
	}

	/**
	 * get the instance of SVNRepositoryManager
	 * 
	 * @return
	 * @throws RepositoryNotInitializedException
	 */
	public static SVNRepositoryManager getInstance()
			throws RepositoryNotInitializedException {
		if (SINGLETON == null) {
			throw new RepositoryNotInitializedException(
					"repository is not initialized");
		}

		return SINGLETON;
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
	 * get the revision number of the latest revision
	 * 
	 * @return
	 * @throws SVNException
	 */
	public long getLatestRevisionAsLong() throws SVNException {
		return this.repository.getLatestRevision();
	}

	/**
	 * get the revision number of the latest revision
	 * 
	 * @return
	 * @throws SVNException
	 */
	public String getLatestRevision() throws SVNException {
		return ((Long) this.repository.getLatestRevision()).toString();
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
		final String normalizedPath = (path == null) ? "" : path;
		final SVNURL target = this.url.appendPath(normalizedPath, false);
		final SVNWCClient wcClient = SVNClientManager.newInstance(null,
				this.userName, this.passwd).getWCClient();
		wcClient.doGetFileContents(target, SVNRevision.create(revisionNum),
				SVNRevision.create(revisionNum), false, new OutputStream() {

					@Override
					public void write(int b) throws IOException {
						builder.append((char) b);
					}

				});

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
		return getListOfSourceFiles(revisionNum, lang, null);
	}

	/**
	 * get the list of paths of all the source files in the given revision which
	 * is included int the given collection of strings
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
		final SVNLogClient logClient = SVNClientManager.newInstance(null,
				this.userName, this.passwd).getLogClient();

		final String additionalUrl = this.additionalUrl;

		final List<String> result = new ArrayList<String>();
		logClient.doList(this.url, SVNRevision.create(revisionNum),
				SVNRevision.create(revisionNum), true, SVNDepth.INFINITY,
				SVNDirEntry.DIRENT_ALL, new ISVNDirEntryHandler() {

					@Override
					public void handleDirEntry(SVNDirEntry dirEntry)
							throws SVNException {
						final String path = (additionalUrl == null) ? dirEntry
								.getRelativePath() : additionalUrl
								+ dirEntry.getRelativePath();

						if (lang.isTarget(path)) {
							if (targets == null) {
								result.add(dirEntry.getRelativePath());
							} else {
								for (final String target : targets) {
									if (path.contains(target)) {
										result.add(dirEntry.getRelativePath());
										break;
									}
								}
							}
						}
					}

				});

		return Collections.unmodifiableList(result);
	}

	/**
	 * run diff
	 * 
	 * @param beforeRevNum
	 * @param afterRevNum
	 * @return
	 * @throws SVNException
	 */
	public String doDiff(final long beforeRevNum, final long afterRevNum)
			throws SVNException {
		final SVNDiffClient diffClient = SVNClientManager.newInstance(null,
				this.userName, this.passwd).getDiffClient();
		final StringBuilder diffText = new StringBuilder();
		diffClient.doDiff(SINGLETON.url, SVNRevision.create(beforeRevNum),
				this.url, SVNRevision.create(afterRevNum), SVNDepth.INFINITY,
				true, new OutputStream() {
					@Override
					public void write(int arg0) throws IOException {
						diffText.append((char) arg0);
					}
				});

		return diffText.toString();
	}

}
