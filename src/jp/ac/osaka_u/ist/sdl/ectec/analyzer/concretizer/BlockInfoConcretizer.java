package jp.ac.osaka_u.ist.sdl.ectec.analyzer.concretizer;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.BlockInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CRD;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CatchClauseInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.ClassInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CombinedRevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.DoStatementInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.ElseStatementInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.EnhancedForStatementInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.FileInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.FinallyBlockInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.ForStatementInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.IfStatementInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.MethodInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.SwitchStatementInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.SynchronizedStatementInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.TryStatementInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.WhileStatementInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.BlockType;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.util.BlockDetectingVisitor;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.WhileStatement;

/**
 * A class for concretizing blocks
 * 
 * @author k-hotta
 * 
 */
public class BlockInfoConcretizer {

	public BlockInfo<?> concretize(final DBCodeFragmentInfo dbFragment,
			final Map<Long, CombinedRevisionInfo> combinedRevisions,
			final Map<Long, FileInfo> files, final Map<Long, CRD> crds) {
		final long id = dbFragment.getId();
		final FileInfo ownerFile = files.get(dbFragment.getOwnerFileId());
		final CRD crd = crds.get(dbFragment.getCrdId());
		final CombinedRevisionInfo startCombinedRevision = combinedRevisions
				.get(dbFragment.getStartCombinedRevisionId());
		final CombinedRevisionInfo endCombinedRevision = combinedRevisions
				.get(dbFragment.getEndCombinedRevisionId());
		final int startLine = dbFragment.getStartLine();
		final int endLine = dbFragment.getEndLine();
		final int size = dbFragment.getSize();

		final BlockType bType = crd.getBlockType();
		final CompilationUnit root = ownerFile.getNode();

		final BlockDetectingVisitor blockDetectVisitor = new BlockDetectingVisitor(
				bType, crd.getAnchor(), crd.getCm(), startLine, endLine, root);
		root.accept(blockDetectVisitor);
		final ASTNode node = blockDetectVisitor.getResult();

		switch (bType) {
		case CLASS:
			return new ClassInfo(id, ownerFile, crd, startCombinedRevision,
					endCombinedRevision, startLine, endLine, size,
					(TypeDeclaration) node);

		case METHOD:
			return new MethodInfo(id, ownerFile, crd, startCombinedRevision,
					endCombinedRevision, startLine, endLine, size,
					(MethodDeclaration) node);

		case CATCH:
			return new CatchClauseInfo(id, ownerFile, crd,
					startCombinedRevision, endCombinedRevision, startLine,
					endLine, size, (CatchClause) node);

		case DO:
			return new DoStatementInfo(id, ownerFile, crd,
					startCombinedRevision, endCombinedRevision, startLine,
					endLine, size, (DoStatement) node);

		case ELSE:
			return new ElseStatementInfo(id, ownerFile, crd,
					startCombinedRevision, endCombinedRevision, startLine,
					endLine, size, (Statement) node);

		case ENHANCED_FOR:
			return new EnhancedForStatementInfo(id, ownerFile, crd,
					startCombinedRevision, endCombinedRevision, startLine,
					endLine, size, (EnhancedForStatement) node);

		case FINALLY:
			return new FinallyBlockInfo(id, ownerFile, crd,
					startCombinedRevision, endCombinedRevision, startLine,
					endLine, size, (Block) node);

		case FOR:
			return new ForStatementInfo(id, ownerFile, crd,
					startCombinedRevision, endCombinedRevision, startLine,
					endLine, size, (ForStatement) node);

		case IF:
			return new IfStatementInfo(id, ownerFile, crd,
					startCombinedRevision, endCombinedRevision, startLine,
					endLine, size, (IfStatement) node);

		case SWITCH:
			return new SwitchStatementInfo(id, ownerFile, crd,
					startCombinedRevision, endCombinedRevision, startLine,
					endLine, size, (SwitchStatement) node);

		case SYNCHRONIZED:
			return new SynchronizedStatementInfo(id, ownerFile, crd,
					startCombinedRevision, endCombinedRevision, startLine,
					endLine, size, (SynchronizedStatement) node);

		case TRY:
			return new TryStatementInfo(id, ownerFile, crd,
					startCombinedRevision, endCombinedRevision, startLine,
					endLine, size, (TryStatement) node);

		case WHILE:
			return new WhileStatementInfo(id, ownerFile, crd,
					startCombinedRevision, endCombinedRevision, startLine,
					endLine, size, (WhileStatement) node);

		default:
			return null;
		}
	}

	public Map<Long, BlockInfo<?>> concretizeAll(
			final Collection<DBCodeFragmentInfo> dbFragments,
			final Map<Long, CombinedRevisionInfo> combinedRevisions,
			final Map<Long, FileInfo> files, final Map<Long, CRD> crds) {
		final Map<Long, BlockInfo<?>> result = new TreeMap<Long, BlockInfo<?>>();

		for (final DBCodeFragmentInfo dbFragment : dbFragments) {
			final BlockInfo<?> block = concretize(dbFragment,
					combinedRevisions, files, crds);
			result.put(block.getId(), block);
		}

		return Collections.unmodifiableMap(result);
	}

	public Map<Long, BlockInfo<?>> concretizeAll(
			final Map<Long, DBCodeFragmentInfo> dbFragments,
			final Map<Long, CombinedRevisionInfo> combinedRevisions,
			final Map<Long, FileInfo> files, final Map<Long, CRD> crds) {
		return concretizeAll(dbFragments.values(), combinedRevisions, files,
				crds);
	}

}
