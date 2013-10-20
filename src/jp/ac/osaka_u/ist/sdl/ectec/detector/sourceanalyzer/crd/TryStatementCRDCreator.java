package jp.ac.osaka_u.ist.sdl.ectec.detector.sourceanalyzer.crd;

import java.util.ArrayList;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.BlockType;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCrdInfo;
import jp.ac.osaka_u.ist.sdl.ectec.detector.sourceanalyzer.normalizer.StringCreateVisitor;
import jp.ac.osaka_u.ist.sdl.ectec.settings.Constants;

import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.TryStatement;

/**
 * A crd creator for try statements
 * 
 * @author k-hotta
 * 
 */
public class TryStatementCRDCreator extends AbstractBlockAnalyzer<TryStatement> {

	public TryStatementCRDCreator(TryStatement node, DBCrdInfo parent,
			StringCreateVisitor visitor) {
		super(node, parent, BlockType.TRY, visitor);
	}

	/**
	 * get the anchor (caught exceptions)
	 */
	@Override
	protected String getAnchor() {
		return getAnchor(node);
	}

	public static String getAnchor(final TryStatement node) {
		final StringBuilder builder = new StringBuilder();

		@SuppressWarnings("rawtypes")
		List catchClauses = node.catchClauses();

		boolean catchAnyException = false;

		for (Object obj : catchClauses) {
			final CatchClause catchClause = (CatchClause) obj;
			final String caughtExceptionType = catchClause.getException()
					.getType().toString();
			builder.append(caughtExceptionType + Constants.PREDICATE_DIVIDER);
			catchAnyException = true;
		}

		if (catchAnyException) {
			builder.delete(
					builder.length() - Constants.PREDICATE_DIVIDER.length(),
					builder.length());
		}

		return builder.toString();
	}

	@Override
	protected String getNormalizedAnchor() {
		return getAnchor();
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
		node.getBody().accept(cmCalculator);
		final int cm = cmCalculator.getCC() + cmCalculator.getFO();

		final String thisCrdStr = getStringCrdForThisBlock(head, anchor, cm);
		final String fullText = (parent == null) ? thisCrdStr : parent
				.getFullText() + "\n" + thisCrdStr;

		node.getBody().accept(visitor);

		createdCrd = new DBCrdInfo(bType, head, anchor, normalizedAnchor, cm,
				ancestorIds, fullText);
		stringForCloneDetection = "try " + visitor.getString();
	}

}
