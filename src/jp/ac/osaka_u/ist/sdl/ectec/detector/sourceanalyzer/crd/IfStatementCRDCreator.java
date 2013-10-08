package jp.ac.osaka_u.ist.sdl.ectec.detector.sourceanalyzer.crd;

import jp.ac.osaka_u.ist.sdl.ectec.data.BlockType;
import jp.ac.osaka_u.ist.sdl.ectec.data.CRD;
import jp.ac.osaka_u.ist.sdl.ectec.detector.sourceanalyzer.normalizer.NormalizedStringCreator;
import jp.ac.osaka_u.ist.sdl.ectec.detector.sourceanalyzer.normalizer.StringCreateVisitor;

import org.eclipse.jdt.core.dom.IfStatement;

/**
 * A crd creator for if statements
 * 
 * @author k-hotta
 * 
 */
public class IfStatementCRDCreator extends AbstractBlockAnalyzer<IfStatement> {

	public IfStatementCRDCreator(IfStatement node, CRD parent,
			StringCreateVisitor visitor) {
		super(node, parent, BlockType.IF, visitor);
	}

	/**
	 * get the anchor (the conditional expression)
	 */
	@Override
	protected String getAnchor() {
		return node.getExpression().toString();
	}

	@Override
	protected String getNormalizedAnchor() {
		final NormalizedStringCreator anchorNormalizer = new NormalizedStringCreator();
		node.getExpression().accept(anchorNormalizer);
		return anchorNormalizer.getString();
	}

}
