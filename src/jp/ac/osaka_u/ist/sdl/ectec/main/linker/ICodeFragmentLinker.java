package jp.ac.osaka_u.ist.sdl.ectec.main.linker;

import java.util.Map;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetInfo;
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
	 * @param onlyFragmentInClonesInBeforeRevision
	 * @param clonesInBeforeRevision
	 * @return
	 */
	public Map<Long, DBCodeFragmentLinkInfo> detectFragmentPairs(
			final Map<Long, DBCodeFragmentInfo> beforeBlocks,
			final Map<Long, DBCodeFragmentInfo> afterBlocks,
			final ICRDSimilarityCalculator similarityCalculator,
			final double similarityThreshold, final Map<Long, DBCrdInfo> crds,
			final long beforeRevisionId, final long afterRevisionId,
			final boolean onlyFragmentInClonesInBeforeRevision,
			final Map<Long, DBCloneSetInfo> clonesInBeforeRevision);
}
