package jp.ac.osaka_u.ist.sdl.ectec.analyzer.vcs.svn;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * An abstract class to create repository that may need authentication
 * 
 * @author k-hotta
 * 
 */
public abstract class RemoteRepositoryCreator extends RepositoryCreator {

	@Override
	public SVNRepository create(SVNURL url, String name, String passwd)
			throws SVNException {
		setup();
		final SVNRepository repository = create(url);

		if (name != null && passwd != null) {
			ISVNAuthenticationManager authManager = SVNWCUtil
					.createDefaultAuthenticationManager(name, passwd);
			repository.setAuthenticationManager(authManager);
		}

		return repository;
	}

	protected abstract void setup();

	protected abstract SVNRepository create(SVNURL url) throws SVNException;

}
