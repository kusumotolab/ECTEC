package jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.crd.AbstractCRDCreator;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.crd.CatchClauseCRDCreator;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.crd.ClassCRDCreator;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.crd.DoStatementCRDCreator;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.crd.ElseStatementCRDCreator;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.crd.EnhancedForStatementCRDCreator;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.crd.FinallyBlockCRDCreator;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.crd.ForStatementCRDCreator;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.crd.IfStatementCRDCreator;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.crd.MethodCRDCreator;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.crd.SwitchStatementCRDCreator;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.crd.SynchronizedStatementCRDCreator;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.crd.TryStatementCRDCreator;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.crd.WhileStatementCRDCreator;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.hash.IHashCalculator;
import jp.ac.osaka_u.ist.sdl.ectec.data.CRD;
import jp.ac.osaka_u.ist.sdl.ectec.data.CodeFragmentInfo;

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
 * A class that parses the given ast and detect crds and code fragments included
 * in it
 * 
 * @author k-hotta
 * 
 */
public class CodeFragmentDetector extends ASTVisitor {

	/**
	 * detected crds
	 */
	private final Map<Long, CRD> detectedCrds;

	/**
	 * detected fragments
	 */
	private final Map<Long, CodeFragmentInfo> detectedFragments;

	/**
	 * the id of the owner file
	 */
	private final long ownerFileId;

	/**
	 * the id of the start revision
	 */
	private final long startRevisionId;

	/**
	 * the id of the end revision
	 */
	private final long endRevisionId;

	/**
	 * crds for out blocks of the current block under processed
	 */
	private final Stack<CRD> parentCrds;

	/**
	 * the root node of the processed ast
	 */
	private final CompilationUnit root;

	/**
	 * a map having finally blocks temporary
	 */
	private final Map<TryStatement, Block> optionalFinallyBlocks;

	/**
	 * a map having else blocks temporary
	 */
	private final Map<IfStatement, Block> optionalElseBlocks;

	/**
	 * the calculator for hash values
	 */
	private final IHashCalculator hashCalculator;

	public CodeFragmentDetector(final long ownerFileId,
			final long startRevisionId, final long endRevisionId,
			final CompilationUnit root, final IHashCalculator hashCalculator) {
		this.detectedCrds = new TreeMap<Long, CRD>();
		this.detectedFragments = new TreeMap<Long, CodeFragmentInfo>();
		this.ownerFileId = ownerFileId;
		this.startRevisionId = startRevisionId;
		this.endRevisionId = endRevisionId;
		this.parentCrds = new Stack<CRD>();
		this.root = root;
		this.optionalFinallyBlocks = new HashMap<TryStatement, Block>();
		this.optionalElseBlocks = new HashMap<IfStatement, Block>();
		this.hashCalculator = hashCalculator;
	}

	/**
	 * get all the detected crds
	 * 
	 * @return
	 */
	public final Map<Long, CRD> getDetectedCrds() {
		return Collections.unmodifiableMap(detectedCrds);
	}

	/**
	 * get all the detected fragments
	 * 
	 * @return
	 */
	public final Map<Long, CodeFragmentInfo> getDetectedFragments() {
		return Collections.unmodifiableMap(detectedFragments);
	}

	/**
	 * get the start line of the given node
	 * 
	 * @param node
	 * @return
	 */
	private final int getStartLine(final ASTNode node) {
		return root.getLineNumber(node.getStartPosition());
	}

	/**
	 * get the end line of the given node
	 * 
	 * @param node
	 * @return
	 */
	private final int getEndLine(final ASTNode node) {
		return root.getLineNumber(node.getStartPosition() + node.getLength());
	}

	/**
	 * peek the stack
	 * 
	 * @return the top element on the stack, null if the stack is empty
	 */
	private final CRD peekCrdStack() {
		if (parentCrds.isEmpty()) {
			return null;
		} else {
			return parentCrds.peek();
		}
	}

	/**
	 * create an instance of CodeFragmentInfo for the given node
	 * 
	 * @param node
	 * @param crdId
	 * @return
	 */
	private final CodeFragmentInfo createCodeFragment(final ASTNode node,
			final long crdId) {
		final int startLine = getStartLine(node);
		final int endLine = getEndLine(node);
		final long hash = hashCalculator.getHashValue(node);

		return new CodeFragmentInfo(ownerFileId, crdId, startRevisionId,
				endRevisionId, hash, startLine, endLine);
	}

	/**
	 * detect and store both crd and fragment
	 * 
	 * @param node
	 * @param creator
	 * @return the crd
	 */
	private final CRD detectCrd(final ASTNode node,
			final AbstractCRDCreator<?> creator) {
		final CRD crd = creator.createCrd();
		detectedCrds.put(crd.getId(), crd);

		final CodeFragmentInfo fragment = createCodeFragment(node, crd.getId());
		detectedFragments.put(fragment.getId(), fragment);

		return crd;
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		// do nothing and do not parse the children of this node any longer
		// if the node declares an interface
		if (node.isInterface()) {
			return false;
		}

		final CRD crd = detectCrd(node, new ClassCRDCreator(node,
				peekCrdStack()));
		parentCrds.push(crd);

		// visit the children
		return true;
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		final CRD crd = detectCrd(node, new MethodCRDCreator(node,
				peekCrdStack()));
		parentCrds.push(crd);

		// visit the children
		return true;
	}

	@Override
	public void endVisit(MethodDeclaration node) {
		parentCrds.pop();
	}

	@Override
	public boolean visit(EnhancedForStatement node) {
		final CRD crd = detectCrd(node, new EnhancedForStatementCRDCreator(
				node, peekCrdStack()));
		parentCrds.push(crd);

		// visit the children
		return true;
	}

	@Override
	public void endVisit(EnhancedForStatement node) {
		parentCrds.pop();
	}

	@Override
	public boolean visit(ForStatement node) {
		final CRD crd = detectCrd(node, new ForStatementCRDCreator(node,
				peekCrdStack()));
		parentCrds.push(crd);

		// visit the children
		return true;
	}

	@Override
	public void endVisit(ForStatement node) {
		parentCrds.pop();
	}

	@Override
	public boolean visit(WhileStatement node) {
		final CRD crd = detectCrd(node, new WhileStatementCRDCreator(node,
				peekCrdStack()));
		parentCrds.push(crd);

		// visit the children
		return true;
	}

	@Override
	public void endVisit(WhileStatement node) {
		parentCrds.pop();
	}

	@Override
	public boolean visit(DoStatement node) {
		final CRD crd = detectCrd(node, new DoStatementCRDCreator(node,
				peekCrdStack()));
		parentCrds.push(crd);

		// visit the children
		return true;
	}

	@Override
	public void endVisit(DoStatement node) {
		parentCrds.pop();
	}

	@Override
	public boolean visit(SwitchStatement node) {
		final CRD crd = detectCrd(node, new SwitchStatementCRDCreator(node,
				peekCrdStack()));
		parentCrds.push(crd);

		// visit the children
		return true;
	}

	@Override
	public void endVisit(SwitchStatement node) {
		parentCrds.pop();
	}

	@Override
	public boolean visit(SynchronizedStatement node) {
		final CRD crd = detectCrd(node, new SynchronizedStatementCRDCreator(
				node, peekCrdStack()));
		parentCrds.push(crd);

		// visit the children
		return true;
	}

	@Override
	public void endVisit(SynchronizedStatement node) {
		parentCrds.pop();
	}

	@Override
	public boolean visit(TryStatement node) {
		final CRD crd = detectCrd(node, new TryStatementCRDCreator(node,
				peekCrdStack()));
		parentCrds.push(crd);

		// keep the finally block if it exists
		final Block finallyBlock = node.getFinally();
		if (finallyBlock != null) {
			this.optionalFinallyBlocks.put(node, finallyBlock);
		}

		// visit the children
		return true;
	}

	@Override
	public void endVisit(TryStatement node) {
		parentCrds.pop();

		// remove the temporary stored finally block
		if (this.optionalFinallyBlocks.containsKey(node)) {
			this.optionalFinallyBlocks.remove(node);
		}
	}

	@Override
	public boolean visit(CatchClause node) {
		final CRD crd = detectCrd(node, new CatchClauseCRDCreator(node,
				peekCrdStack()));
		parentCrds.push(crd);

		// visit the children
		return true;
	}

	@Override
	public void endVisit(CatchClause node) {
		parentCrds.pop();
	}

	@Override
	public boolean visit(IfStatement node) {
		final CRD crd = detectCrd(node, new IfStatementCRDCreator(node,
				peekCrdStack()));
		parentCrds.push(crd);

		// keep the else block if it exists
		final Statement elseStatement = node.getElseStatement();
		if (elseStatement != null) {
			if (!(elseStatement instanceof IfStatement)) {
				if (elseStatement instanceof Block) {
					final Block elseBlock = (Block) elseStatement;
					this.optionalElseBlocks.put(node, elseBlock);
				}
			}
		}

		// visit the children
		return true;
	}

	@Override
	public void endVisit(IfStatement node) {
		parentCrds.pop();

		// remove the temporary stored else block
		if (this.optionalElseBlocks.containsKey(node)) {
			this.optionalElseBlocks.remove(node);
		}
	}

	/**
	 * this works only in the case where the block is finally or else
	 */
	@Override
	public boolean visit(Block node) {
		// finally 節
		if (this.optionalFinallyBlocks.containsValue(node)) {
			final CRD crd = detectCrd(node, new FinallyBlockCRDCreator(node,
					peekCrdStack()));
			parentCrds.push(crd);
		}

		// else 節
		else if (this.optionalElseBlocks.containsValue(node)) {
			final CRD crd = detectCrd(node, new ElseStatementCRDCreator(node,
					peekCrdStack()));
			parentCrds.push(crd);
		}

		return true;
	}

	@Override
	public void endVisit(Block node) {
		// finally 節
		if (this.optionalFinallyBlocks.containsValue(node)) {
			parentCrds.pop();
		}

		// else 節
		else if (this.optionalElseBlocks.containsValue(node)) {
			parentCrds.pop();
		}
	}

}
