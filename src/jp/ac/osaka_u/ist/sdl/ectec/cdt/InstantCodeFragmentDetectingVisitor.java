package jp.ac.osaka_u.ist.sdl.ectec.cdt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTVisitor;

public class InstantCodeFragmentDetectingVisitor extends ASTVisitor {

	private final Map<Long, InstantCodeFragmentInfo> detectedFragments;

	private final List<Token> tokens;

	private final String filePath;

	public InstantCodeFragmentDetectingVisitor(final List<Token> tokens,
			final String filePath) {
		this.detectedFragments = new HashMap<Long, InstantCodeFragmentInfo>();
		this.tokens = tokens;
		this.filePath = filePath;
	}

}
