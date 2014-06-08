package jp.ac.osaka_u.ist.sdl.ectec.main.linker;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCrdInfo;
import jp.ac.osaka_u.ist.sdl.ectec.main.linker.similarity.ICRDSimilarityCalculator;

public abstract class AbstractLocationLimitedCodeFragmentLinkMaker {

	protected final Map<Long, DBCodeFragmentLinkInfo> links;

	protected final FragmentLinkConditionUmpire umpire;

	protected final ICRDSimilarityCalculator similarityCalculator;

	protected final double similarityThreshold;

	protected final Map<Long, DBCrdInfo> crds;

	protected final long beforeRevisionId;

	protected final long afterRevisionId;

	public AbstractLocationLimitedCodeFragmentLinkMaker(
			FragmentLinkConditionUmpire umpire,
			ICRDSimilarityCalculator similarityCalculator,
			double similarityThreshold, Map<Long, DBCrdInfo> crds,
			long beforeRevisionId, long afterRevisionId) {
		this.links = new TreeMap<Long, DBCodeFragmentLinkInfo>();
		this.umpire = umpire;
		this.similarityCalculator = similarityCalculator;
		this.similarityThreshold = similarityThreshold;
		this.crds = crds;
		this.beforeRevisionId = beforeRevisionId;
		this.afterRevisionId = afterRevisionId;
	}

	public final Map<Long, DBCodeFragmentLinkInfo> getDetectedLinks() {
		return Collections.unmodifiableMap(links);
	}

	public abstract void processFragment(final DBCodeFragmentInfo fragment,
			final Collection<DBCodeFragmentInfo> pairCandidateFragments,
			final boolean reversed);

	protected final boolean match(final DBCrdInfo beforeCrd,
			final DBCodeFragmentInfo beforeFragment, final DBCrdInfo afterCrd,
			final DBCodeFragmentInfo afterFragment) {
		if (beforeCrd.equals(afterCrd)) {
			return false;
		}

		if (!umpire.satisfyCrdConditions(beforeCrd, afterCrd)) {
			return false;
		}

		final double similarity = similarityCalculator.calcSimilarity(
				beforeCrd, afterCrd);

		if (umpire.satisfyAllConditions(beforeCrd, afterCrd, similarity)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * make fragment link instances with the given pairs
	 * 
	 * @param pairs
	 * @param beforeRevisionId
	 * @param afterRevisionId
	 * @return
	 */
	protected final void makeLinkInstances(
			final Map<DBCodeFragmentInfo, DBCodeFragmentInfo> pairs) {
		for (final Map.Entry<DBCodeFragmentInfo, DBCodeFragmentInfo> entry : pairs
				.entrySet()) {
			final DBCodeFragmentInfo beforeFragment = entry.getKey();
			final DBCodeFragmentInfo afterFragment = entry.getValue();
			final DBCodeFragmentLinkInfo link = makeLinkInstance(
					beforeFragment, afterFragment);

			links.put(link.getId(), link);
		}
	}

	protected final DBCodeFragmentLinkInfo makeLinkInstance(
			final DBCodeFragmentInfo beforeFragment,
			final DBCodeFragmentInfo afterFragment) {
		final boolean changed = beforeFragment.getHash() != afterFragment
				.getHash();

		return new DBCodeFragmentLinkInfo(beforeFragment.getId(),
				afterFragment.getId(), beforeRevisionId, afterRevisionId,
				changed);
	}

}
