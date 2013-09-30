package jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.hash;

import org.eclipse.jdt.core.dom.ASTNode;

/**
 * A default hash calculator which uses String.hashCode()
 * 
 * @author k-hotta
 * 
 */
public class ExactHashCalculator extends IHashCalculator {

	@Override
	public long getHashValue(final ASTNode node) {
		final String str = node.toString();
		final StringBuilder builder = new StringBuilder();

		for (final String splitStr : str.split("\n")) {
			builder.append(splitStr.trim() + "\n");
		}

		return (long) builder.toString().hashCode();
	}

}
