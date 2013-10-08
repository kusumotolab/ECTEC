package jp.ac.osaka_u.ist.sdl.ectec.detector.sourceanalyzer.crd;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.WhileStatement;

/**
 * A class for calculating metrics
 * 
 * @author k-hotta
 * 
 */
public class MetricsCalculator extends ASTVisitor {

	/**
	 * cyclomatic complexity := the number of branches + 1
	 */
	private int cc;

	/**
	 * the hash values calculated from the name of an invoked method
	 */
	private final Set<Integer> invokedMethodNames;

	public MetricsCalculator() {
		this.cc = 0;
		this.invokedMethodNames = new HashSet<Integer>();
	}

	/**
	 * get the cyclomatic complexity
	 * 
	 * @return
	 */
	public final int getCC() {
		return cc;
	}

	/**
	 * get fan-out
	 * 
	 * @return
	 */
	public final int getFO() {
		return invokedMethodNames.size();
	}

	/**
	 * store the name of the invoked method
	 */
	@Override
	public boolean visit(MethodInvocation node) {
		invokedMethodNames.add(node.getName().toString().hashCode());
		return true;
	}

	/*
	 * incrementing cyclomatic complexity when the following nodes are visited
	 * if, for, for-each, while, do-while, switch-case
	 */

	@Override
	public boolean visit(IfStatement node) {
		this.cc++;
		return true;
	}

	@Override
	public boolean visit(ForStatement node) {
		this.cc++;
		return true;
	}

	@Override
	public boolean visit(EnhancedForStatement node) {
		this.cc++;
		return true;
	}

	@Override
	public boolean visit(WhileStatement node) {
		this.cc++;
		return true;
	}

	@Override
	public boolean visit(DoStatement node) {
		this.cc++;
		return true;
	}

	@Override
	public boolean visit(SwitchCase node) {
		this.cc++;
		return true;
	}

}
