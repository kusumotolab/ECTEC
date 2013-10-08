package jp.ac.osaka_u.ist.sdl.ectec.detector.vcs;

/**
 * An exception class to represent the repository is not initialized
 * 
 * @author k-hotta
 * 
 */
public class RepositoryNotInitializedException extends Exception {

	public RepositoryNotInitializedException(final String message) {
		super(message);
	}

}
