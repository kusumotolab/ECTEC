package jp.ac.osaka_u.ist.sdl.ectec.main.vcs.svn;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;

/**
 * A class to create a repository whose url starts with "http://" or "https://"
 * 
 * @author k-hotta
 * 
 */
public class WebDAVRepositoryCreator extends RemoteRepositoryCreator {

	@Override
	protected void setup() {
		DAVRepositoryFactory.setup();
	}

	@Override
	protected SVNRepository create(SVNURL url) throws SVNException {
		return DAVRepositoryFactory.create(url);
	}

}
