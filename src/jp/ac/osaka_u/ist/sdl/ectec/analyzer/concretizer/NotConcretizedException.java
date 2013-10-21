package jp.ac.osaka_u.ist.sdl.ectec.analyzer.concretizer;

/**
 * An exception that is thrown when a concretizing operation fail to complete
 * 
 * @author k-hotta
 * 
 */
public class NotConcretizedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6505711336003469L;

	public NotConcretizedException(Exception e) {
		super(e);
	}
	
	public NotConcretizedException(String msg) {
		super(msg);
	}

}
