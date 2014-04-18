package jp.ac.osaka_u.ist.sdl.ectec.detector.linker;

import java.util.Collection;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCrdInfo;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.similarity.ICRDSimilarityCalculator;

/**
 * An interface to provide a function to detect links of code fragments between
 * two revisions
 * 
 * @author k-hotta
 * 
 */
public interface ICodeFragmentLinker {

	/**
	 * detect pairs of code fragments
	 * 
	 * @param beforeBlocks
	 * @param afterBlocks
	 * @param similarityCalculator
	 * @param similarityThreshold
	 * @param crds
	 * @param beforeRevisionId
	 * @param afterRevisionId
	 * @return
	 */
	public Map<Long, DBCodeFragmentLinkInfo> detectFragmentPairs(
			final Collection<DBCodeFragmentInfo> beforeBlocks,
			final Collection<DBCodeFragmentInfo> afterBlocks,
			final ICRDSimilarityCalculator similarityCalculator,
			final double similarityThreshold, final Map<Long, DBCrdInfo> crds,
			final long beforeRevisionId, final long afterRevisionId);
}
