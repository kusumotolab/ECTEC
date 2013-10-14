package jp.ac.osaka_u.ist.sdl.ectec.cdt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import jp.ac.osaka_u.ist.sdl.ectec.detector.sourceanalyzer.hash.IHashCalculator;
import jp.ac.osaka_u.ist.sdl.ectec.detector.sourceanalyzer.normalizer.NormalizerCreator;
import jp.ac.osaka_u.ist.sdl.ectec.detector.sourceanalyzer.normalizer.StringCreateVisitor;
import jp.ac.osaka_u.ist.sdl.ectec.settings.AnalyzeGranularity;
import jp.ac.osaka_u.ist.sdl.ectec.settings.MessagePrinter;

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

public class InstantCodeFragmentDetectingVisitor extends ASTVisitor {

	private final List<InstantCodeFragmentInfo> detectedFragments;

	private final List<Token> tokens;

	private final String filePath;

	private CompilationUnit root = null;

	private final IHashCalculator hashCalculator;

	private final NormalizerCreator normalizerCreator;

	private final Set<Block> processedBlocks;

	private final int threshold;

	private final AnalyzeGranularity granularity;

	public InstantCodeFragmentDetectingVisitor(final List<Token> tokens,
			final String filePath, final IHashCalculator hashCalculator,
			final NormalizerCreator normalizerCreator, final int threshold,
			final AnalyzeGranularity granularity) {
		this.detectedFragments = new ArrayList<InstantCodeFragmentInfo>();
		this.tokens = tokens;
		this.filePath = filePath;
		this.hashCalculator = hashCalculator;
		this.normalizerCreator = normalizerCreator;
		this.processedBlocks = new HashSet<Block>();
		this.threshold = threshold;
		this.granularity = granularity;
	}

	/**
	 * get the result
	 * 
	 * @return
	 */
	public final List<InstantCodeFragmentInfo> getDetectedFragments() {
		return Collections.unmodifiableList(detectedFragments);
	}

	/**
	 * get the token that locates just after the given position
	 * 
	 * @param position
	 * @return
	 */
	private final Token getHeadToken(final int position) {
		final int startLine = root.getLineNumber(position);
		// + 1 is needed to fix mismatch between positions of ast and java lexer
		final int startColumn = root.getColumnNumber(position) + 1;

		for (final Token token : tokens) {
			if (token.getLine() > startLine) {
				return token;
			}

			if (token.getLine() == startLine
					&& token.getColumn() >= startColumn) {
				return token;
			}
		}

		return null; // no token has been found
	}

	/**
	 * get the token that locates just before the given position
	 * 
	 * @param position
	 * @return
	 */
	private final Token getTailToken(final int position) {
		final int endLine = root.getLineNumber(position);
		// + 1 is needed to fix mismatch between positions of ast and java lexer
		final int endColumn = root.getColumnNumber(position) + 1;

		int size = tokens.size();
		ListIterator<Token> iter = tokens.listIterator(size);
		while (iter.hasPrevious()) {
			final Token token = iter.previous();

			if (token.getLine() < endLine) {
				return token;
			}

			if (token.getLine() == endLine && token.getColumn() <= endColumn) {
				return token;
			}
		}

		return null; // no token has been found
	}

	@Override
	public boolean visit(CompilationUnit node) {
		if (root == null) {
			root = node;
		}

		return true;
	}

	/**
	 * create a fragment from the given node and register it
	 * 
	 * @param node
	 */
	private final void processBlock(final ASTNode node) {
		final InstantCodeFragmentInfo fragment = createFragmentInstance(node);

		if (fragment == null) {
			return;
		}

		register(fragment);
	}

	/**
	 * create an instance of block from the given node
	 * 
	 * @param node
	 * @return
	 */
	private InstantCodeFragmentInfo createFragmentInstance(final ASTNode node) {
		final Token headToken = getHeadToken(node.getStartPosition());
		final Token tailToken = getTailToken(node.getStartPosition()
				+ node.getLength());

		final int size = tokens.indexOf(tailToken) - tokens.indexOf(headToken)
				+ 1;

		if (headToken == null || tailToken == null) {
			MessagePrinter.ePrintln("cannot find corresponding token to "
					+ node.getClass().getName() + " in position "
					+ node.getStartPosition() + " of " + filePath);
			return null;
		}

		if (size < threshold) {
			return null;
		}

		final StringCreateVisitor normalizer = normalizerCreator
				.createNewCalculator();
		node.accept(normalizer);
		final String normalizedStr = normalizer.getResult();

		return new InstantCodeFragmentInfo(filePath, headToken.getLine(),
				headToken.getColumn(), tailToken.getLine(),
				tailToken.getColumn(),
				hashCalculator.calcHashValue(normalizedStr), size);
	}

	private void register(final InstantCodeFragmentInfo fragment) {
		detectedFragments.add(fragment);
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		if (granularity == AnalyzeGranularity.METHOD) {
			return true;
		}

		processBlock(node);
		return true;
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		if (granularity == AnalyzeGranularity.CLASS) {
			return true;
		}

		processBlock(node);
		processedBlocks.add(node.getBody());
		return true;
	}

	@Override
	public boolean visit(CatchClause node) {
		if (granularity != AnalyzeGranularity.ALL) {
			return true;
		}

		processBlock(node);
		processedBlocks.add(node.getBody());
		return true;
	}

	@Override
	public boolean visit(DoStatement node) {
		if (granularity != AnalyzeGranularity.ALL) {
			return true;
		}

		processBlock(node);
		if (node.getBody() instanceof Block) {
			processedBlocks.add((Block) node.getBody());
		}
		return true;
	}

	@Override
	public boolean visit(EnhancedForStatement node) {
		if (granularity != AnalyzeGranularity.ALL) {
			return true;
		}

		processBlock(node);
		if (node.getBody() instanceof Block) {
			processedBlocks.add((Block) node.getBody());
		}
		return true;
	}

	@Override
	public boolean visit(ForStatement node) {
		if (granularity != AnalyzeGranularity.ALL) {
			return true;
		}

		processBlock(node);
		if (node.getBody() instanceof Block) {
			processedBlocks.add((Block) node.getBody());
		}
		return true;
	}

	@Override
	public boolean visit(IfStatement node) {
		if (granularity != AnalyzeGranularity.ALL) {
			return true;
		}

		final Token headToken = getHeadToken(node.getStartPosition());
		final Token tailToken = getTailToken(node.getStartPosition()
				+ node.getThenStatement().getLength());

		final int size = tokens.indexOf(tailToken) - tokens.indexOf(headToken)
				+ 1;

		if (headToken == null || tailToken == null) {
			MessagePrinter.ePrintln("cannot find corresponding token to "
					+ node.getClass().getName() + " in position "
					+ node.getStartPosition() + " of " + filePath);
			return true;
		}

		if (size < threshold) {
			return true;
		}

		final StringCreateVisitor normalizer = normalizerCreator
				.createNewCalculator();
		normalizer.getBuffer().append("if ");
		node.getExpression().accept(normalizer);
		normalizer.getBuffer().append(" ");
		node.getThenStatement().accept(normalizer);
		final String normalizedStr = normalizer.getResult();

		final InstantCodeFragmentInfo fragment = new InstantCodeFragmentInfo(
				filePath, headToken.getLine(), headToken.getColumn(),
				tailToken.getLine(), tailToken.getColumn(),
				hashCalculator.calcHashValue(normalizedStr), size);

		register(fragment);

		if (node.getThenStatement() instanceof Block) {
			processedBlocks.add((Block) node.getThenStatement());
		}
		return true;
	}

	@Override
	public boolean visit(SwitchStatement node) {
		if (granularity != AnalyzeGranularity.ALL) {
			return true;
		}

		processBlock(node);
		for (Object obj : node.statements()) {
			final Statement statement = (Statement) obj;
			if (statement instanceof Block) {
				processedBlocks.add((Block) statement);
			}
		}
		return true;
	}

	@Override
	public boolean visit(TryStatement node) {
		if (granularity != AnalyzeGranularity.ALL) {
			return true;
		}

		final Token headToken = getHeadToken(node.getStartPosition());
		final Token tailToken = getTailToken(node.getStartPosition()
				+ node.getBody().getLength());

		if (headToken == null || tailToken == null) {
			MessagePrinter.ePrintln("cannot find corresponding token to "
					+ node.getClass().getName() + " in position "
					+ node.getStartPosition() + " of " + filePath);
			return true;
		}

		final int size = tokens.indexOf(tailToken) - tokens.indexOf(headToken)
				+ 1;

		if (size < threshold) {
			return true;
		}

		final StringCreateVisitor normalizer = normalizerCreator
				.createNewCalculator();
		normalizer.getBuffer().append("try ");
		node.accept(normalizer);
		final String normalizedStr = normalizer.getResult();

		final InstantCodeFragmentInfo fragment = new InstantCodeFragmentInfo(
				filePath, headToken.getLine(), headToken.getColumn(),
				tailToken.getLine(), tailToken.getColumn(),
				hashCalculator.calcHashValue(normalizedStr), size);

		register(fragment);

		processedBlocks.add(node.getBody());
		return true;
	}

	@Override
	public boolean visit(SynchronizedStatement node) {
		if (granularity != AnalyzeGranularity.ALL) {
			return true;
		}

		processBlock(node);
		processedBlocks.add(node.getBody());
		return true;
	}

	@Override
	public boolean visit(WhileStatement node) {
		if (granularity != AnalyzeGranularity.ALL) {
			return true;
		}

		processBlock(node);
		if (node.getBody() instanceof Block) {
			processedBlocks.add((Block) node.getBody());
		}
		return true;
	}

	@Override
	public boolean visit(Block node) {
		if (processedBlocks.contains(node)) {
			return true;
		}
		if (granularity != AnalyzeGranularity.ALL) {
			return true;
		}

		processBlock(node);
		processedBlocks.add(node);
		return true;
	}

}
