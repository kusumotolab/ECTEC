package jp.ac.osaka_u.ist.sdl.ectec.main.clonedetector;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;

public class ByRepositoryFragmentComparator {

	private final FragmentComparator comparator;

	public ByRepositoryFragmentComparator(final FragmentComparator comparator) {
		this.comparator = comparator;
	}

	/**
	 * detect clones (without detecting cross-project clones)
	 * 
	 * @param codeFragments
	 * @return
	 */
	public final Map<Long, DBCloneSetInfo> detectClones(
			final Map<Long, DBCodeFragmentInfo> codeFragments) {
		final Map<Long, Map<Long, DBCodeFragmentInfo>> codeFragmentsByRepository = getCodeFragmentsByRepository(codeFragments);

		final Map<Long, DBCloneSetInfo> result = new TreeMap<Long, DBCloneSetInfo>();

		for (final Map.Entry<Long, Map<Long, DBCodeFragmentInfo>> entry : codeFragmentsByRepository
				.entrySet()) {
			result.putAll(comparator.detectClones(entry.getValue()));
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
