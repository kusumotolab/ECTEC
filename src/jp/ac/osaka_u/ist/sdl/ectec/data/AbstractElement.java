package jp.ac.osaka_u.ist.sdl.ectec.data;

/**
 * A class to represent any elements to be registered into the db
 * 
 * @author k-hotta
 * 
 */
public abstract class AbstractElement {

	/**
	 * ID
	 */
	protected final long id;

	public AbstractElement(final long id) {
		this.id = id;
	}

	/**
	 * get the id of this element
	 * 
	 * @return
	 */
	public final long getId() {
		return this.id;
	}

}
