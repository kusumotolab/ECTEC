package jp.ac.osaka_u.ist.sdl.ectec.util;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.BlockType;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.crd.CatchClauseCRDCreator;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.crd.ClassCRDCreator;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.crd.DoStatementCRDCreator;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.crd.ElseStatementCRDCreator;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.crd.EnhancedForStatementCRDCreator;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.crd.FinallyBlockCRDCreator;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.crd.ForStatementCRDCreator;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.crd.IfStatementCRDCreator;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.crd.MethodCRDCreator;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.crd.MetricsCalculator;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.crd.SwitchStatementCRDCreator;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.crd.SynchronizedStatementCRDCreator;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.crd.TryStatementCRDCreator;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.crd.WhileStatementCRDCreator;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
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
 * A private class to detect the node corresponding to the target block
 * 
 * @author k-hotta
 * 
 */
public class BlockDetectingVisitor extends ASTVisitor {

	private final BlockType targetBlockType;

	private final String targetAnchor;

	private final int targetCm;

	private final int targetStartLine;

	private final int targetEndLine;

	private final CompilationUnit root;

	private ASTNode result = null;

	public BlockDetectingVisitor(final BlockType targetBlockType,
			final String targetAnchor, final int targetCm,
			final int targetStartLine, final int targetEndLine,
			final CompilationUnit root) {
		this.targetBlockType = targetBlockType;
		this.targetAnchor = targetAnchor;
		this.targetCm = targetCm;
		this.targetStartLine = targetStartLine;
		this.targetEndLine = targetEndLine;
		this.root = root;
	}

	public ASTNode getResult() {
		return result;
	}

	private final boolean satisfyConditions(final int startLine,
			final int endLine, final int cm, final String anchor) {
		// NOTE: block type is (must be) already checked
		if (startLine != targetStartLine) {
			return false;
		}

		if (endLine != targetEndLine) {
			return false;
		}

		if (cm != targetCm) {
			return false;
		}

		if (!anchor.equals(targetAnchor)) {
			return false;
		}

		return true;
	}

	private boolean judge(ASTNode node, final String anchor) {
		final int startLine = root.getLineNumber(node.getStartPosition());
		final int endLine = root.getLineNumber(node.getStartPosition()
				+ node.getLength());
		final MetricsCalculator cmCalculator = new MetricsCalculator();
		node.accept(cmCalculator);
		final int cm = cmCalculator.getCC() + cmCalculator.getFO();

		return satisfyConditions(startLine, endLine, cm, anchor);
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		if (targetBlockType != BlockType.CLASS) {
			return true;
		}

		final String anchor = ClassCRDCreator.getAnchor(node);

		if (judge(node, anchor)) {
			result = node;
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		if (targetBlockType != BlockType.METHOD) {
			return true;
		}

		final String anchor = MethodCRDCreator.getAnchor(node);

		if (judge(node, anchor)) {
			result = node;
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean visit(CatchClause node) {
		if (targetBlockType != BlockType.CATCH) {
			return true;
		}

		final String anchor = CatchClauseCRDCreator.getAnchor(node);

		if (judge(node, anchor)) {
			result = node;
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean visit(DoStatement node) {
		if (targetBlockType != BlockType.DO) {
			return true;
		}

		final String anchor = DoStatementCRDCreator.getAnchor(node);

		if (judge(node, anchor)) {
			result = node;
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean visit(IfStatement node) {
		if (targetBlockType == BlockType.IF) {
			return processIfStatement(node);
		} else if (targetBlockType == BlockType.ELSE) {
			return processElseStatement(node);
		} else {
			return true;
		}
	}

	private boolean processIfStatement(final IfStatement node) {
		final String anchor = IfStatementCRDCreator.getAnchor(node);

		final int startLine = root.getLineNumber(node.getStartPosition());
		final int endLine = root.getLineNumber(node.getThenStatement()
				.getStartPosition() + node.getThenStatement().getLength());
		final MetricsCalculator cmCalculator = new MetricsCalculator();
		node.getThenStatement().accept(cmCalculator);
		node.getExpression().accept(cmCalculator);
		final int cm = cmCalculator.getCC() + cmCalculator.getFO();

		final boolean satisfy = satisfyConditions(startLine, endLine, cm,
				anchor);

		if (satisfy) {
			result = node;
		}

		return !satisfy;
	}

	private boolean processElseStatement(final IfStatement node) {
		final Statement elseStatement = node.getElseStatement();
		if (elseStatement == null) {
			return true;
		}

		final String anchor = ElseStatementCRDCreator.getAnchor(elseStatement);

		final boolean judge = judge(elseStatement, anchor);

		if (judge) {
			result = node;
		}

		return !judge;
	}

	@Override
	public boolean visit(EnhancedForStatement node) {
		if (targetBlockType != BlockType.ENHANCED_FOR) {
			return true;
		}

		final String anchor = EnhancedForStatementCRDCreator.getAnchor(node);

		if (judge(node, anchor)) {
			result = node;
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean visit(ForStatement node) {
		if (targetBlockType != BlockType.FOR) {
			return true;
		}

		final String anchor = ForStatementCRDCreator.getAnchor(node);

		if (judge(node, anchor)) {
			result = node;
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean visit(TryStatement node) {
		if (targetBlockType == BlockType.TRY) {
			return processTryStatement(node);
		} else if (targetBlockType == BlockType.FINALLY) {
			return processFinallyBlock(node);
		} else {
			return true;
		}
	}

	private boolean processTryStatement(final TryStatement node) {
		final String anchor = TryStatementCRDCreator.getAnchor(node);
		final int startLine = root.getLineNumber(node.getStartPosition());
		final int endLine = root.getLineNumber(node.getBody()
				.getStartPosition() + node.getBody().getLength());
		final MetricsCalculator cmCalculator = new MetricsCalculator();
		node.getBody().accept(cmCalculator);
		final int cm = cmCalculator.getCC() + cmCalculator.getFO();

		final boolean satisfy = satisfyConditions(startLine, endLine, cm,
				anchor);

		if (satisfy) {
			result = node;
		}

		return !satisfy;
	}

	private boolean processFinallyBlock(final TryStatement node) {
		final Block finallyBlock = node.getFinally();
		if (finallyBlock == null) {
			return true;
		}

		final String anchor = FinallyBlockCRDCreator.getAnchor(finallyBlock);

		final boolean judge = judge(finallyBlock, anchor);

		if (judge) {
			result = node.getFinally();
		}

		return !judge;
	}

	@Override
	public boolean visit(SwitchStatement node) {
		if (targetBlockType != BlockType.SWITCH) {
			return true;
		}

		final String anchor = SwitchStatementCRDCreator.getAnchor(node);

		if (judge(node, anchor)) {
			result = node;
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean visit(SynchronizedStatement node) {
		if (targetBlockType != BlockType.SYNCHRONIZED) {
			return true;
		}

		final String anchor = SynchronizedStatementCRDCreator.getAnchor(node);

		if (judge(node, anchor)) {
			result = node;
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean visit(WhileStatement node) {
		if (targetBlockType != BlockType.WHILE) {
			return true;
		}

		final String anchor = WhileStatementCRDCreator.getAnchor(node);

		if (judge(node, anchor)) {
			result = node;
			return false;
		} else {
			return true;
		}
	}

}
