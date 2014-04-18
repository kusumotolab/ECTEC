package jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.ast.NodeCountVisitor;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.BlockType;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCrdInfo;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.crd.AbstractBlockAnalyzer;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.crd.CatchClauseCRDCreator;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.crd.ClassCRDCreator;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.crd.DoStatementCRDCreator;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.crd.ElseStatementCRDCreator;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.crd.EnhancedForStatementCRDCreator;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.crd.FinallyBlockCRDCreator;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.crd.ForStatementCRDCreator;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.crd.IfStatementCRDCreator;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.crd.MethodCRDCreator;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.crd.SwitchStatementCRDCreator;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.crd.SynchronizedStatementCRDCreator;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.crd.TryStatementCRDCreator;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.crd.WhileStatementCRDCreator;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.hash.IHashCalculator;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.normalizer.NormalizerCreator;
import jp.ac.osaka_u.ist.sdl.ectec.settings.AnalyzeGranularity;

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
	private final Map<Long, DBCrdInfo> detectedCrds;

	/**
	 * detected fragments
	 */
	private final Map<Long, DBCodeFragmentInfo> detectedFragments;

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
	private final Stack<DBCrdInfo> parentCrds;

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

	/**
	 * the calculator for hash values for clone detection
	 */
	private final NormalizerCreator cloneHashCalculatorCreator;

	/**
	 * the granularity of the analysis
	 */
	private final AnalyzeGranularity granularity;

	public CodeFragmentDetector(final long ownerFileId,
			final long startRevisionId, final long endRevisionId,
			final IHashCalculator hashCalculator, final CompilationUnit root,
			final AnalyzeGranularity granularity,
			final NormalizerCreator cloneHashCalculatorCreator) {
		this.detectedCrds = new TreeMap<Long, DBCrdInfo>();
		this.detectedFragments = new TreeMap<Long, DBCodeFragmentInfo>();
		this.ownerFileId = ownerFileId;
		this.startRevisionId = startRevisionId;
		this.endRevisionId = endRevisionId;
		this.parentCrds = new Stack<DBCrdInfo>();
		this.root = root;
		this.optionalFinallyBlocks = new HashMap<TryStatement, Block>();
		this.optionalElseBlocks = new HashMap<IfStatement, Block>();
		this.hashCalculator = hashCalculator;
		this.cloneHashCalculatorCreator = cloneHashCalculatorCreator;
		this.granularity = granularity;
	}

	/**
	 * get all the detected crds
	 * 
	 * @return
	 */
	public final Map<Long, DBCrdInfo> getDetectedCrds() {
		return Collections.unmodifiableMap(detectedCrds);
	}

	/**
	 * get all the detected fragments
	 * 
	 * @return
	 */
	public final Map<Long, DBCodeFragmentInfo> getDetectedFragments() {
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
	private final DBCrdInfo peekCrdStack() {
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
	private final DBCodeFragmentInfo createCodeFragment(final int startLine,
			final int endLine, final String rawStr, final int size,
			final long crdId, final String strForClone) {
		final long hash = hashCalculator.calcHashValue(rawStr.toString());
		final long hashForClone = hashCalculator.calcHashValue(strForClone);

		return new DBCodeFragmentInfo(ownerFileId, crdId, startRevisionId,
				endRevisionId, hash, hashForClone, startLine, endLine, size);
	}

	/**
	 * get the number of nodes in the subtree of the given node
	 * 
	 * @param node
	 * @return
	 */
	private int countSize(final ASTNode node) {
		final NodeCountVisitor visitor = new NodeCountVisitor();
		node.accept(visitor);
		return visitor.getNodeCount();
	}

	/**
	 * detect and store both crd and fragment
	 * 
	 * @param node
	 * @param analyzer
	 * @return the crd
	 */
	private final DBCrdInfo detectCrd(final int startLine, final int endLine,
			final String rawStr, final int size,
			final AbstractBlockAnalyzer<?> analyzer) {
		analyzer.analyze();
		final DBCrdInfo crd = analyzer.getCreatedCrd();
		detectedCrds.put(crd.getId(), crd);

		final BlockType bType = crd.getType();
		if (bType.isInterested(granularity)) {
			final DBCodeFragmentInfo fragment = createCodeFragment(startLine,
					endLine, rawStr, size, crd.getId(),
					analyzer.getStringForCloneDetection());
			detectedFragments.put(fragment.getId(), fragment);
		}

		return crd;
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		// do nothing and do not parse the children of this node any longer
		// if the node declares an interface
		if (node.isInterface()) {
			return false;
		}

		final DBCrdInfo crd = detectCrd(getStartLine(node), getEndLine(node),
				node.toString(), countSize(node),
				new ClassCRDCreator(node, peekCrdStack(),
						cloneHashCalculatorCreator.createNewCalculator()));
		parentCrds.push(crd);

		// visit the children
		return true;
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		final DBCrdInfo crd = detectCrd(getStartLine(node), getEndLine(node),
				node.toString(), countSize(node),
				new MethodCRDCreator(node, peekCrdStack(),
						cloneHashCalculatorCreator.createNewCalculator()));
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
		final DBCrdInfo crd = detectCrd(getStartLine(node), getEndLine(node),
				node.toString(), countSize(node),
				new EnhancedForStatementCRDCreator(node, peekCrdStack(),
						cloneHashCalculatorCreator.createNewCalculator()));
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
		final DBCrdInfo crd = detectCrd(getStartLine(node), getEndLine(node),
				node.toString(), countSize(node),
				new ForStatementCRDCreator(node, peekCrdStack(),
						cloneHashCalculatorCreator.createNewCalculator()));
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
		final DBCrdInfo crd = detectCrd(getStartLine(node), getEndLine(node),
				node.toString(), countSize(node),
				new WhileStatementCRDCreator(node, peekCrdStack(),
						cloneHashCalculatorCreator.createNewCalculator()));
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
		final DBCrdInfo crd = detectCrd(getStartLine(node), getEndLine(node),
				node.toString(), countSize(node),
				new DoStatementCRDCreator(node, peekCrdStack(),
						cloneHashCalculatorCreator.createNewCalculator()));
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
		final DBCrdInfo crd = detectCrd(getStartLine(node), getEndLine(node),
				node.toString(), countSize(node),
				new SwitchStatementCRDCreator(node, peekCrdStack(),
						cloneHashCalculatorCreator.createNewCalculator()));
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
		final DBCrdInfo crd = detectCrd(getStartLine(node), getEndLine(node),
				node.toString(), countSize(node),
				new SynchronizedStatementCRDCreator(node, peekCrdStack(),
						cloneHashCalculatorCreator.createNewCalculator()));
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
		// +1 for the size goes to "try" itself
		final DBCrdInfo crd = detectCrd(getStartLine(node),
				getEndLine(node.getBody()), node.getBody().toString(),
				countSize(node.getBody()) + 1,
				new TryStatementCRDCreator(node, peekCrdStack(),
						cloneHashCalculatorCreator.createNewCalculator()));
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
		final DBCrdInfo crd = detectCrd(getStartLine(node), getEndLine(node),
				node.toString(), countSize(node),
				new CatchClauseCRDCreator(node, peekCrdStack(),
						cloneHashCalculatorCreator.createNewCalculator()));
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
		// +1 for the size goes to "try" itself
		final DBCrdInfo crd = detectCrd(
				getStartLine(node),
				getEndLine(node.getThenStatement()),
				node.toString(),
				countSize(node.getThenStatement())
						+ countSize(node.getExpression()) + 1,
				new IfStatementCRDCreator(node, peekCrdStack(),
						cloneHashCalculatorCreator.createNewCalculator()));
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
		// finally
		if (this.optionalFinallyBlocks.containsValue(node)) {
			final DBCrdInfo crd = detectCrd(getStartLine(node),
					getEndLine(node), node.toString(), countSize(node),
					new FinallyBlockCRDCreator(node, peekCrdStack(),
							cloneHashCalculatorCreator.createNewCalculator()));
			parentCrds.push(crd);
		}

		// else
		else if (this.optionalElseBlocks.containsValue(node)) {
			final DBCrdInfo crd = detectCrd(getStartLine(node),
					getEndLine(node), node.toString(), countSize(node),
					new ElseStatementCRDCreator(node, peekCrdStack(),
							cloneHashCalculatorCreator.createNewCalculator()));
			parentCrds.push(crd);
		}

		return true;
	}

	@Override
	public void endVisit(Block node) {
		// finally
		if (this.optionalFinallyBlocks.containsValue(node)) {
			parentCrds.pop();
		}

		// else
		else if (this.optionalElseBlocks.containsValue(node)) {
			parentCrds.pop();
		}
	}

}
