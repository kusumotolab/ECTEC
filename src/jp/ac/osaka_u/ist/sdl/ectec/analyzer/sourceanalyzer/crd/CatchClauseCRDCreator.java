package jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.crd;

import java.util.List;

import jp.ac.osaka_u.ist.sdl.ectec.data.BlockType;
import jp.ac.osaka_u.ist.sdl.ectec.data.CRD;

import org.eclipse.jdt.core.dom.CatchClause;

/**
 * A crd creator for catch clauses
 * 
 * @author k-hotta
 * 
 */
public class CatchClauseCRDCreator extends AbstractCRDCreator<CatchClause> {

	public CatchClauseCRDCreator(CatchClause node, List<CRD> ancestors) {
		super(node, ancestors, BlockType.CATCH);
	}

	/**
	 * get the anchor (the type of the exception caught by this clause)
	 */
	@Override
	protected String getAnchor() {
		return node.getException().getType().toString();
	}

}
