package jp.ac.osaka_u.ist.sdl.ectec.analyzer.data;

/**
 * A class that represents revision
 * 
 * @author k-hotta
 * 
 */
public class RevisionInfo extends AbstractElement implements
		Comparable<RevisionInfo> {

	/**
	 * the identifier of this revision
	 */
	private final String identifier;

	public RevisionInfo(final long id, final String identifier) {
		super(id);
		this.identifier = identifier;
	}

	@Override
	public int compareTo(RevisionInfo another) {
		return ((Long) this.id).compareTo(another.getId());
	}

	/**
	 * get the identifier of this revision
	 * 
	 * @return
	 */
	public final String getIdentifier() {
		return this.identifier;
	}

}
