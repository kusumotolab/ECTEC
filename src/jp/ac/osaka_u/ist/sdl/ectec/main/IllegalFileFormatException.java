package jp.ac.osaka_u.ist.sdl.ectec.main;

/**
 * An exception class that represents illegal file formats
 * 
 * @author k-hotta
 * 
 */
public class IllegalFileFormatException extends Exception {

	public IllegalFileFormatException() {
		super();
	}
	
	public IllegalFileFormatException(final String msg) {
		super(msg);
	}
	
}
