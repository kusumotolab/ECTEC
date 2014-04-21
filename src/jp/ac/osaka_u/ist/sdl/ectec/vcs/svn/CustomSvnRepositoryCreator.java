package jp.ac.osaka_u.ist.sdl.ectec.vcs.svn;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;

/**
 * A class to create a repository whose url starts with "svn://" or "svn+xxx://"
 * ("svn+ssh://" in particular)
 * 
 * @author k-hotta
 * 
 */
public class CustomSvnRepositoryCreator extends RemoteRepositoryCreator {

	@Override
	protected void setup() {
		SVNRepositoryFactoryImpl.setup();
	}

	@Override
	protected SVNRepository create(SVNURL url) throws SVNException {
		return SVNRepositoryFactoryImpl.create(url);
	}

}
