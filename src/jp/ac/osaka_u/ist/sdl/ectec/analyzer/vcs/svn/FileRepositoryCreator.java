package jp.ac.osaka_u.ist.sdl.ectec.analyzer.vcs.svn;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;

/**
 * A class to create repository whose url starts "files://"
 * 
 * @author k-hotta
 * 
 */
public class FileRepositoryCreator extends RepositoryCreator {

	@Override
	public SVNRepository create(SVNURL url, String name, String passwd)
			throws SVNException {
		FSRepositoryFactory.setup();
		return FSRepositoryFactory.create(url);
	}

}
