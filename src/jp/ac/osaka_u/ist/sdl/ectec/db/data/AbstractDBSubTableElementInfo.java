package jp.ac.osaka_u.ist.sdl.ectec.db.data;

/**
 * A class that represents elements in sub tables
 * 
 * @author k-hotta
 * 
 */
public abstract class AbstractDBSubTableElementInfo extends AbstractDBElement {

	private final long subElementId;

	public AbstractDBSubTableElementInfo(final long mainElementId,
			final long subElementId) {
		super(mainElementId);
		this.subElementId = subElementId;
	}

	public final long getMainElementId() {
		return this.id;
	}

	public final long getSubElementId() {
		return this.subElementId;
	}

	@Override
	public final boolean equals(final Object o) {
		if (this == o) {
			return true;
		}

		if (o == null) {
			return false;
		}

		if (this.getClass() != o.getClass()) {
			return false;
		}

		final AbstractDBSubTableElementInfo another = (AbstractDBSubTableElementInfo) o;

		return this.getMainElementId() == another.getMainElementId()
				&& this.getSubElementId() == another.getSubElementId();
	}

}
