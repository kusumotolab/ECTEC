package jp.ac.osaka_u.ist.sdl.ectec.detector.sourceanalyzer.crd;

import java.util.ArrayList;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.BlockType;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCrdInfo;
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

	public IfStatementCRDCreator(IfStatement node, DBCrdInfo parent,
			StringCreateVisitor visitor) {
		super(node, parent, BlockType.IF, visitor);
	}

	/**
	 * get the anchor (the conditional expression)
	 */
	@Override
	protected String getAnchor() {
		return getAnchor(node);
	}

	public static String getAnchor(final IfStatement node) {
		return node.getExpression().toString();
	}

	@Override
	protected String getNormalizedAnchor() {
		final NormalizedStringCreator anchorNormalizer = new NormalizedStringCreator();
		node.getExpression().accept(anchorNormalizer);
		return anchorNormalizer.getString();
	}

	@Override
	public void analyze() {
		final String head = bType.getHead();
		final String anchor = getAnchor();
		final String normalizedAnchor = getNormalizedAnchor();

		final List<Long> ancestorIds = new ArrayList<Long>();

		if (parent != null) {
			for (final long ancestorId : parent.getAncestors()) {
				ancestorIds.add(ancestorId);
			}
			ancestorIds.add(parent.getId());
		}

		final MetricsCalculator cmCalculator = new MetricsCalculator();
		node.getThenStatement().accept(cmCalculator);
		node.getExpression().accept(cmCalculator);

		final int cm = cmCalculator.getCC() + cmCalculator.getFO();

		final String thisCrdStr = getStringCrdForThisBlock(head, anchor, cm);
		final String fullText = (parent == null) ? thisCrdStr : parent
				.getFullText() + "\n" + thisCrdStr;

		visitor.getBuffer().append("if ");
		node.getExpression().accept(visitor);
		visitor.getBuffer().append(" ");
		node.getThenStatement().accept(visitor);

		createdCrd = new DBCrdInfo(bType, head, anchor, normalizedAnchor, cm,
				ancestorIds, fullText);
		stringForCloneDetection = visitor.getString();
	}
}
