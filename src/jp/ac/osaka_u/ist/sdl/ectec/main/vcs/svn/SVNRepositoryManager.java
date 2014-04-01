package jp.ac.osaka_u.ist.sdl.ectec.main.vcs.svn;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.ectec.main.vcs.IChangedFilesDetector;
import jp.ac.osaka_u.ist.sdl.ectec.main.vcs.IRepositoryManager;
import jp.ac.osaka_u.ist.sdl.ectec.main.vcs.ITargetRevisionDetector;
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
public class SVNRepositoryManager implements IRepositoryManager {

	/**
	 * the target revision detector
	 */
	private final SVNTargetRevisionDetector targetRevisionDetector;

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

	public SVNRepositoryManager(final String urlRoot, final String userName,
			final String passwd, final String additionalUrl) throws Exception {
		this.targetRevisionDetector = new SVNTargetRevisionDetector(this);

		final String urlStr = (additionalUrl == null) ? urlRoot : urlRoot
				+ additionalUrl;
		this.url = SVNURL.parseURIDecoded(urlStr);

		final RepositoryCreator creator = RepositoryCreator
				.getCorrespondingInstance(urlStr);

		this.repository = creator.create(url, userName, passwd);

		this.urlStr = urlStr;
		this.userName = userName;
		this.passwd = passwd;
		this.additionalUrl = additionalUrl;
	}

	/**
	 * get the target revisions detector corresponding to each version control
	 * system
	 * 
	 * @return
	 */
	@Override
	public ITargetRevisionDetector getTargetRevisionDetector() {
		return this.targetRevisionDetector;
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
	 * get the additional url
	 * 
	 * @return
	 */
	public String getAdditionalUrl() {
		return additionalUrl;
	}

	/**
	 * get the first revision number (always equals to 1)
	 */
	@Override
	public String getFirstRevision() throws Exception {
		return ((Long) (long) 1).toString();
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
	@Override
	public String getLatestRevision() throws SVNException {
		return ((Long) this.repository.getLatestRevision()).toString();
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
		final String normalizedPath = (path == null) ? "" : path;

		String targetPath = null;
		if (path != null && this.additionalUrl != null) {
			targetPath = normalizedPath.substring(this.additionalUrl.length());
		} else {
			targetPath = path;
		}

		final SVNURL target = this.url.appendPath(targetPath, false);
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
	 * get the list of source files
	 * 
	 * @param revisionIdentifier
	 * @param language
	 * @return
	 * @throws Exception
	 */
	@Override
	public synchronized List<String> getListOfSourceFiles(
			final String revisionIdentifier, final Language lang)
			throws Exception {
		return getListOfSourceFiles(Long.parseLong(revisionIdentifier), lang);
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
		// final SVNLogClient logClient = SVNClientManager.newInstance(null,
		// this.userName, this.passwd).getLogClient();
		//
		// final String additionalUrl = this.additionalUrl;
		//
		// final List<String> result = new ArrayList<String>();
		// logClient.doList(this.url, SVNRevision.create(revisionNum),
		// SVNRevision.create(revisionNum), true, SVNDepth.INFINITY,
		// SVNDirEntry.DIRENT_ALL, new ISVNDirEntryHandler() {
		//
		// @Override
		// public void handleDirEntry(SVNDirEntry dirEntry)
		// throws SVNException {
		// final String path = (additionalUrl == null) ? dirEntry
		// .getRelativePath() : additionalUrl
		// + dirEntry.getRelativePath();
		//
		// if (lang.isTarget(path)) {
		// if (targets == null) {
		// result.add(dirEntry.getRelativePath());
		// } else {
		// for (final String target : targets) {
		// if (path.contains(target)) {
		// result.add(dirEntry.getRelativePath());
		// break;
		// }
		// }
		// }
		// }
		// }
		//
		// });
		//
		// return Collections.unmodifiableList(result);
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
		final SVNLogClient logClient = SVNClientManager.newInstance(null,
				this.userName, this.passwd).getLogClient();

		final String additionalUrl = this.additionalUrl;

		final SVNURL url = (target == null) ? this.url : this.url.appendPath(
				target, false);

		final List<String> result = new ArrayList<String>();
		logClient.doList(url, SVNRevision.create(revisionNum),
				SVNRevision.create(revisionNum), true, SVNDepth.INFINITY,
				SVNDirEntry.DIRENT_ALL, new ISVNDirEntryHandler() {

					@Override
					public void handleDirEntry(SVNDirEntry dirEntry)
							throws SVNException {
						final String path = (additionalUrl == null) ? dirEntry
								.getRelativePath() : additionalUrl
								+ dirEntry.getRelativePath();

						if (lang.isTarget(path)) {
							result.add(dirEntry.getRelativePath());
						}
					}

				});

		return Collections.unmodifiableList(result);
	}

	/**
	 * run diff
	 * 
	 * @param beforeRevisionIdentifier
	 * @param afterRevisionIdentifier
	 * @return
	 * @throws Exception
	 */
	public String doDiff(final String beforeRevisionIdentifier,
			final String afterRevisionIdentifier) throws Exception {
		return doDiff(Long.parseLong(beforeRevisionIdentifier),
				Long.parseLong(afterRevisionIdentifier));
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
		diffClient.doDiff(this.url, SVNRevision.create(beforeRevNum), this.url,
				SVNRevision.create(afterRevNum), SVNDepth.INFINITY, true,
				new OutputStream() {
					@Override
					public void write(int arg0) throws IOException {
						diffText.append((char) arg0);
					}
				});

		return diffText.toString();
	}

}
