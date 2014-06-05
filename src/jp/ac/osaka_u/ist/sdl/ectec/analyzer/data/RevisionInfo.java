package jp.ac.osaka_u.ist.sdl.ectec.analyzer.data;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.ElementVisitor;

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

	/**
	 * the owner repository of this revision
	 */
	private final RepositoryInfo repository;

	public RevisionInfo(final long id, final String identifier,
			final RepositoryInfo repository) {
		super(id);
		this.identifier = identifier;
		this.repository = repository;
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

	/**
	 * get the owner repository of this revision
	 * 
	 * @return
	 */
	public final RepositoryInfo getRepository() {
		return this.repository;
	}
	
	@Override
	public void accept(final ElementVisitor visitor) {
		visitor.visit(this);
	}
	
}
