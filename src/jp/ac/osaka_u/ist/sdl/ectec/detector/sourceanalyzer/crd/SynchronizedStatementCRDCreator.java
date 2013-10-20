package jp.ac.osaka_u.ist.sdl.ectec.detector.sourceanalyzer.crd;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.BlockType;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCrdInfo;
import jp.ac.osaka_u.ist.sdl.ectec.detector.sourceanalyzer.normalizer.NormalizedStringCreator;
import jp.ac.osaka_u.ist.sdl.ectec.detector.sourceanalyzer.normalizer.StringCreateVisitor;

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
			DBCrdInfo parent, StringCreateVisitor visitor) {
		super(node, parent, BlockType.SYNCHRONIZED, visitor);
	}

	/**
	 * get the anchor (the expression)
	 */
	@Override
	protected String getAnchor() {
		return getAnchor(node);
	}

	public static String getAnchor(final SynchronizedStatement node) {
		return node.getExpression().toString();
	}

	@Override
	protected String getNormalizedAnchor() {
		final NormalizedStringCreator anchorNormalizer = new NormalizedStringCreator();
		node.getExpression().accept(anchorNormalizer);
		return anchorNormalizer.getString();
	}

}
