package jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.linker;

import java.util.Collection;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.similarity.ICRDSimilarityCalculator;
import jp.ac.osaka_u.ist.sdl.ectec.data.CodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.CodeFragmentLinkInfo;

/**
 * A class to detect 1 by 1 links of code fragments
 * 
 * @author k-hotta
 * 
 */
public class SingleCodeFragmentLinker implements ICodeFragmentLinker {
	
	@Override
	public Map<Long, CodeFragmentLinkInfo> detectFragmentPairs(
			Collection<CodeFragmentInfo> beforeBlocks,
			Collection<CodeFragmentInfo> afterBlocks,
			ICRDSimilarityCalculator similarityCalculator) {
		// TODO implement
		return null;
	}

}
