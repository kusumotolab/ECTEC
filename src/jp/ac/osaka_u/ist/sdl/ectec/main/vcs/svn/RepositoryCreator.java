package jp.ac.osaka_u.ist.sdl.ectec.main.vcs.svn;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.SVNRepository;

/**
 * A class to create SVN repository
 * 
 * @author k-hotta
 * 
 */
public abstract class RepositoryCreator {

	public static RepositoryCreator getCorrespondingInstance(final String url)
			throws IllegalSVNURLException {
		if (url.startsWith("file:///")) {
			return new FileRepositoryCreator();
		} else if (url.startsWith("svn://") || url.startsWith("svn+")) {
			return new CustomSvnRepositoryCreator();
		} else if (url.startsWith("http://") || url.startsWith("https://")) {
			return new WebDAVRepositoryCreator();
		} else {
			throw new IllegalSVNURLException(
					"URL must be starts with \"file:///\", \"svn://\", \"svn+xxx://\", \"http://\", or \"https://\"");
		}
	}

	public abstract SVNRepository create(final SVNURL url, final String name,
			final String passwd) throws SVNException;

}
