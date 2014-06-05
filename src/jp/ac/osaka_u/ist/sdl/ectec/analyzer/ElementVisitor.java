package jp.ac.osaka_u.ist.sdl.ectec.analyzer;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CRD;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CloneGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CloneSetInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CloneSetLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CodeFragmentGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CodeFragmentLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CombinedRevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.FileInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.RepositoryInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.RevisionInfo;

/**
 * An abstract class to visit AbstractElement
 * 
 * @author k-hotta
 * 
 */
public abstract class ElementVisitor {

	public void visit(final CloneGenealogyInfo element) {
		return;
	}

	public void visit(final CloneSetInfo element) {
		return;
	}

	public void visit(final CloneSetLinkInfo element) {
		return;
	}

	public void visit(final CodeFragmentGenealogyInfo element) {
		return;
	}

	public void visit(final CodeFragmentInfo element) {
		return;
	}

	public void visit(final CodeFragmentLinkInfo element) {
		return;
	}

	public void visit(final CombinedRevisionInfo element) {
		return;
	}

	public void visit(final CRD element) {
		return;
	}

	public void visit(final FileInfo element) {
		return;
	}

	public void visit(final RepositoryInfo element) {
		return;
	}

	public void visit(final RevisionInfo element) {
		return;
	}

}
