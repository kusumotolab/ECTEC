package jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.crd;

import java.util.List;

import jp.ac.osaka_u.ist.sdl.ectec.data.BlockType;
import jp.ac.osaka_u.ist.sdl.ectec.data.CRD;
import jp.ac.osaka_u.ist.sdl.ectec.settings.Constants;

import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.TryStatement;

/**
 * A crd creator for try statements
 * 
 * @author k-hotta
 * 
 */
public class TryStatementCRDCreator extends AbstractCRDCreator<TryStatement> {

	public TryStatementCRDCreator(TryStatement node, CRD parent) {
		super(node, parent, BlockType.TRY);
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

}
