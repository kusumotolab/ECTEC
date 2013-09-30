package jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.crd;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.normalizer.StringCreateVisitor;
import jp.ac.osaka_u.ist.sdl.ectec.data.BlockType;
import jp.ac.osaka_u.ist.sdl.ectec.data.CRD;

import org.eclipse.jdt.core.dom.CatchClause;

/**
 * A crd creator for catch clauses
 * 
 * @author k-hotta
 * 
 */
public class CatchClauseCRDCreator extends AbstractBlockAnalyzer<CatchClause> {

	public CatchClauseCRDCreator(CatchClause node, CRD parent,
			StringCreateVisitor visitor) {
		super(node, parent, BlockType.CATCH, visitor);
	}

	/**
	 * get the anchor (the type of the exception caught by this clause)
	 */
	@Override
	protected String getAnchor() {
		return node.getException().getType().toString();
	}

	@Override
	protected String getNormalizedAnchor() {
		return node.getException().getType().toString();
	}

}
