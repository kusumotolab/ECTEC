package jp.ac.osaka_u.ist.sdl.ectec.main;

/**
 * An exception class that represents the cases where illegal setting values are
 * specified
 * 
 * @author k-hotta
 * 
 */
public class IllegalSettingValueException extends Exception {

	public IllegalSettingValueException() {
		super();
	}
	
	public IllegalSettingValueException(final String msg) {
		super(msg);
	}
	
}
