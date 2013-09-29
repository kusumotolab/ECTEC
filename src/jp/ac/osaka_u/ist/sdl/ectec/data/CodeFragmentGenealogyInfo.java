package jp.ac.osaka_u.ist.sdl.ectec.data;

import java.util.List;

/**
 * A class that represents a genealogy of code fragments
 * 
 * @author k-hotta
 * 
 */
public class CodeFragmentGenealogyInfo extends
		AbstractGenealogyInfo<CodeFragmentInfo, CodeFragmentLinkInfo> {

	public CodeFragmentGenealogyInfo(final long id, final long startRevisionId,
			final long endRevisionId, final List<Long> elements,
			final List<Long> links) {
		super(id, startRevisionId, endRevisionId, elements, links);
	}

}
