package jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.crd;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.normalizer.NormalizedStringCreator;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.normalizer.StringCreateVisitor;
import jp.ac.osaka_u.ist.sdl.ectec.data.BlockType;
import jp.ac.osaka_u.ist.sdl.ectec.data.CRD;

import org.eclipse.jdt.core.dom.SynchronizedStatement;

/**
 * A crd creator for synchronized statements
 * 
 * @author k-hotta
 * 
 */
public class SynchronizedStatementCRDCreator extends
		AbstractBlockAnalyzer<SynchronizedStatement> {

	public SynchronizedStatementCRDCreator(SynchronizedStatement node,
			CRD parent, StringCreateVisitor visitor) {
		super(node, parent, BlockType.SYNCHRONIZED, visitor);
	}

	/**
	 * get the anchor (the expression)
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
