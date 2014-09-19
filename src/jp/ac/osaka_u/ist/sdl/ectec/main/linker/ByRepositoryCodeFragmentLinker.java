package jp.ac.osaka_u.ist.sdl.ectec.main.linker;

import java.util.Collections;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCrdInfo;
import jp.ac.osaka_u.ist.sdl.ectec.main.linker.similarity.ICRDSimilarityCalculator;

/**
 * A class to detect code fragment links without cross-project ones
 * 
 * @author k-hotta
 * 
 */
public class ByRepositoryCodeFragmentLinker implements ICodeFragmentLinker {

	/**
	 * the linker
	 */
	private final ICodeFragmentLinker linker;

	public ByRepositoryCodeFragmentLinker(final ICodeFragmentLinker linker) {
		this.linker = linker;
	}

	public final Map<Long, DBCodeFragmentLinkInfo> detectFragmentPairs(
			final Map<Long, DBCodeFragmentInfo> beforeFragments,
			final Map<Long, DBCodeFragmentInfo> afterFragments,
			final ICRDSimilarityCalculator similarityCalculator,
			final double similarityThreshold, final Map<Long, DBCrdInfo> crds,
			final long beforeRevisionId, final long afterRevisionId,
			boolean onlyFragmentInClonesInBeforeRevision,
			Map<Long, DBCloneSetInfo> clonesInBeforeRevision) {
		final Map<Long, DBCodeFragmentLinkInfo> result = new TreeMap<Long, DBCodeFragmentLinkInfo>();

		final Map<Long, Map<Long, DBCodeFragmentInfo>> beforeFragmentsByRepository = getCodeFragmentsByRepository(beforeFragments);
		final Map<Long, Map<Long, DBCodeFragmentInfo>> afterFragmentsByRepository = getCodeFragmentsByRepository(afterFragments);

		final SortedSet<Long> repositoryIds = new TreeSet<Long>();
		repositoryIds.addAll(beforeFragmentsByRepository.keySet());
		repositoryIds.addAll(afterFragmentsByRepository.keySet());

		for (final long repositoryId : repositoryIds) {
			final Map<Long, DBCodeFragmentInfo> beforeFragmentsInRepository = beforeFragmentsByRepository
					.get(repositoryId);
			if (beforeFragmentsInRepository == null) {
				continue;
			}

			final Map<Long, DBCodeFragmentInfo> afterFragmentsInRepository = afterFragmentsByRepository
					.get(repositoryId);
			if (afterFragmentsInRepository == null) {
				continue;
			}

			result.putAll(linker.detectFragmentPairs(
					beforeFragmentsInRepository, afterFragmentsInRepository,
					similarityCalculator, similarityThreshold, crds,
					beforeRevisionId, afterRevisionId,
					onlyFragmentInClonesInBeforeRevision,
					clonesInBeforeRevision));
		}

		return Collections.unmodifiableMap(result);
	}

	/**
	 * divide the given map based on the id of owner repositories
	 * 
	 * @param codeFragments
	 * @return
	 */
	private Map<Long, Map<Long, DBCodeFragmentInfo>> getCodeFragmentsByRepository(
			final Map<Long, DBCodeFragmentInfo> codeFragments) {
		// the key is the id of repositories
		final Map<Long, Map<Long, DBCodeFragmentInfo>> codeFragmentsByRepository = new TreeMap<Long, Map<Long, DBCodeFragmentInfo>>();

		for (final Map.Entry<Long, DBCodeFragmentInfo> codeFragmentEntry : codeFragments
				.entrySet()) {
			final DBCodeFragmentInfo codeFragment = codeFragmentEntry
					.getValue();
			final long repositoryId = codeFragment.getOwnerRepositoryId();

			if (codeFragmentsByRepository.containsKey(repositoryId)) {
				codeFragmentsByRepository.get(repositoryId).put(
						codeFragmentEntry.getKey(), codeFragment);
			} else {
				final Map<Long, DBCodeFragmentInfo> newMap = new TreeMap<Long, DBCodeFragmentInfo>();
				newMap.put(codeFragmentEntry.getKey(), codeFragment);
				codeFragmentsByRepository.put(repositoryId, newMap);
			}
		}
		return codeFragmentsByRepository;
	}

}
