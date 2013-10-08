package jp.ac.osaka_u.ist.sdl.ectec.db.data;

/**
 * A class to represent any elements to be registered into the db
 * 
 * @author k-hotta
 * 
 */
public abstract class AbstractDBElement {

	/**
	 * ID
	 */
	protected final long id;

	public AbstractDBElement(final long id) {
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
