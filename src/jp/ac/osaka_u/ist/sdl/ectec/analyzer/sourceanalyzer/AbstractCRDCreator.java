package jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer;

import java.util.List;

import jp.ac.osaka_u.ist.sdl.ectec.data.CRD;

import org.eclipse.jdt.core.dom.ASTNode;

/**
 * A class to create a crd for a given node
 * 
 * @author k-hotta
 * 
 */
public abstract class AbstractCRDCreator<T extends ASTNode> {

	/**
	 * the node to be analyzed
	 */
	protected final T node;

	/**
	 * the crds for the ancestors of this node
	 */
	protected final List<CRD> ancestors;

	public AbstractCRDCreator(final T node, final List<CRD> ancestors) {
		this.node = node;
		this.ancestors = ancestors;
	}
	
	public abstract CRD createCrd();

}
