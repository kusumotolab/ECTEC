package jp.ac.osaka_u.ist.sdl.ectec.analyzer.data;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.ElementVisitor;

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
		return id;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this.getClass() != obj.getClass()) {
			return false;
		}

		final AbstractElement another = (AbstractElement) obj;

		return this.id == another.getId();
	}

	public abstract void accept(final ElementVisitor visitor);
	
}
