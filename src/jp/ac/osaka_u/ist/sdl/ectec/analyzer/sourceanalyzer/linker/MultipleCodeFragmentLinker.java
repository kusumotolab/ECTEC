package jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.linker;

import java.util.Collection;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.ectec.data.CodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.CodeFragmentLinkInfo;

public class MultipleCodeFragmentLinker implements ICodeFragmentLinker {

	@Override
	public Map<Long, CodeFragmentLinkInfo> detectFragmentPairs(
			Collection<CodeFragmentInfo> beforeBlocks,
			Collection<CodeFragmentInfo> afterBlocks) {
		// TODO implement
		return null;
	}

}
