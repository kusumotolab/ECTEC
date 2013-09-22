package jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.crd;

import java.util.List;

import jp.ac.osaka_u.ist.sdl.ectec.data.BlockType;
import jp.ac.osaka_u.ist.sdl.ectec.data.CRD;
import jp.ac.osaka_u.ist.sdl.ectec.settings.Constants;

import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.TryStatement;

/**
 * A crd creator for finally blocks
 * 
 * @author k-hotta
 * 
 */
public class FinallyBlockCRDCreator extends AbstractCRDCreator<Block> {

	public FinallyBlockCRDCreator(Block node, List<CRD> ancestors) {
		super(node, ancestors, BlockType.FINALLY);
	}

	/**
	 * get the anchor (the string created by connecting exceptions caught by the
	 * parent try statement
	 */
	@Override
	protected String getAnchor() {
		final StringBuilder builder = new StringBuilder();

		final TryStatement parentTryStatement = (TryStatement) node.getParent();

		@SuppressWarnings("rawtypes")
		List catchClauses = parentTryStatement.catchClauses();

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
