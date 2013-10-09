package jp.ac.osaka_u.ist.sdl.ectec.detector.sourceanalyzer.crd;

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

}