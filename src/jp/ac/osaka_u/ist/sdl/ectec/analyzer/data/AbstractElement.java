package jp.ac.osaka_u.ist.sdl.ectec.analyzer.data;

/**
 * A class that represents a data element
 * 
 * @author k-hotta
 * 
 */
public abstract class AbstractElement {

	/**
	 * the id
	 */
	private final long id;

	public AbstractElement(final long id) {
		this.id = id;
	}

	/**
	 * get the id of this element
	 * 
	 * @return
	 */
	public final long getId() {
		return id;
	}

}
