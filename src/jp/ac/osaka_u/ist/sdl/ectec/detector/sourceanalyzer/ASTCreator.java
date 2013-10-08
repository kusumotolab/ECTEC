package jp.ac.osaka_u.ist.sdl.ectec.detector.sourceanalyzer;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

/**
 * A class to create ASTs from the given source files
 * 
 * @author k-hotta
 * 
 */
public class ASTCreator {

	public static CompilationUnit createAST(final String sourceCode) {
		final ASTParser parser = ASTParser.newParser(AST.JLS4);

		parser.setSource(sourceCode.toCharArray());

		return (CompilationUnit) parser.createAST(new NullProgressMonitor());
	}

}
