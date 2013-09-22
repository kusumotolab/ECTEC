package jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.crd;

import java.util.LinkedList;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.ectec.data.BlockType;
import jp.ac.osaka_u.ist.sdl.ectec.data.CRD;
import jp.ac.osaka_u.ist.sdl.ectec.settings.Constants;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.Statement;

/**
 * A crd creator for else statements
 * 
 * @author k-hotta
 * 
 */
public class ElseStatementCRDCreator extends AbstractCRDCreator<Statement> {

	public ElseStatementCRDCreator(Statement node, List<CRD> ancestors) {
		super(node, ancestors, BlockType.ELSE);
	}

	/**
	 * get the anchor (the predicates of the outer if statements connected into
	 * a string)
	 */
	@Override
	protected String getAnchor() {
		final StringBuilder builder = new StringBuilder();

		final List<String> predicates = detectElsePredicates(node);
		for (final String predicate : predicates) {
			builder.append(predicate + Constants.PREDICATE_DIVIDER);
		}

		builder.delete(builder.length() - Constants.PREDICATE_DIVIDER.length(),
				builder.length());

		return builder.toString();
	}

	private List<String> detectElsePredicates(Statement elseStatement) {
		List<String> predicates = new LinkedList<String>();

		detectPredicates(elseStatement.getParent(), predicates);

		return predicates;
	}

	private void detectPredicates(ASTNode node, List<String> predicates) {
		if (!(node instanceof IfStatement)) {
			return;
		}

		detectPredicates(node.getParent(), predicates);

		IfStatement ifStatement = (IfStatement) node;
		predicates.add(ifStatement.getExpression().toString());
	}

}
