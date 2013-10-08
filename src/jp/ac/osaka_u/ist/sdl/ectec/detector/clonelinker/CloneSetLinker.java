package jp.ac.osaka_u.ist.sdl.ectec.detector.clonelinker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentLinkInfo;

/**
 * A class for linking clone sets between two revisions
 * 
 * @author k-hotta
 * 
 */
public class CloneSetLinker {

	/**
	 * detect links
	 * 
	 * @param beforeClones
	 * @param afterClones
	 * @param fragmentLinks
	 * @param beforeRevisionId
	 * @param afterRevisionId
	 * @return
	 */
	public Map<Long, DBCloneSetLinkInfo> detectCloneSetLinks(
			final Collection<DBCloneSetInfo> beforeClones,
			final Collection<DBCloneSetInfo> afterClones,
			final Map<Long, DBCodeFragmentLinkInfo> fragmentLinks,
			final long beforeRevisionId, final long afterRevisionId) {
		final Map<Long, DBCloneSetLinkInfo> result = new TreeMap<Long, DBCloneSetLinkInfo>();
		final Map<Long, DBCodeFragmentLinkInfo> fragmentLinksCopy = new HashMap<Long, DBCodeFragmentLinkInfo>();
		fragmentLinksCopy.putAll(fragmentLinks);

		for (final DBCloneSetInfo beforeClone : beforeClones) {
			for (final DBCloneSetInfo afterClone : afterClones) {
				final List<Long> relatedLinks = getRelatedFragmentLinks(
						beforeClone, afterClone, fragmentLinksCopy);
				final List<Long> unchangedFragmentIds = getUnchangedFragmentIds(
						beforeClone, afterClone);

				if (!relatedLinks.isEmpty() || !unchangedFragmentIds.isEmpty()) {
					final DBCloneSetLinkInfo cloneLink = createLinkInstance(
							beforeRevisionId, afterRevisionId,
							fragmentLinksCopy, beforeClone, afterClone,
							relatedLinks, unchangedFragmentIds);

					result.put(cloneLink.getId(), cloneLink);
				}
			}
		}

		return Collections.unmodifiableMap(result);
	}

	/**
	 * get the list of ids whose corresponding links are related to the given
	 * pair of clone sets
	 * 
	 * @param beforeClone
	 * @param afterClone
	 * @param fragmentLinksCopy
	 * @return
	 */
	private List<Long> getRelatedFragmentLinks(final DBCloneSetInfo beforeClone,
			final DBCloneSetInfo afterClone,
			final Map<Long, DBCodeFragmentLinkInfo> fragmentLinksCopy) {
		final List<Long> beforeFragments = beforeClone.getElements();
		final List<Long> afterFragments = afterClone.getElements();

		final List<Long> result = new ArrayList<Long>();

		for (final Map.Entry<Long, DBCodeFragmentLinkInfo> entry : fragmentLinksCopy
				.entrySet()) {
			final DBCodeFragmentLinkInfo link = entry.getValue();
			final long beforeFragmentId = link.getBeforeElementId();
			final long afterFragmentId = link.getAfterElementId();

			if (beforeFragments.contains(beforeFragmentId)
					&& afterFragments.contains(afterFragmentId)) {
				result.add(link.getId());
			}
		}

		return result;
	}

	/**
	 * get the list of ids of elements that are included both in the before and
	 * after clones
	 * 
	 * @param beforeClone
	 * @param afterClone
	 * @return
	 */
	private List<Long> getUnchangedFragmentIds(final DBCloneSetInfo beforeClone,
			final DBCloneSetInfo afterClone) {
		final List<Long> beforeFragments = beforeClone.getElements();
		final List<Long> afterFragments = afterClone.getElements();

		final List<Long> result = new ArrayList<Long>();

		for (final long beforeFragment : beforeFragments) {
			for (final long afterFragment : afterFragments) {
				if (beforeFragment == afterFragment) {
					result.add(beforeFragment);
				}
			}
		}

		return result;
	}

	/**
	 * create an instance of clone set link
	 * 
	 * @param beforeRevisionId
	 * @param afterRevisionId
	 * @param fragmentLinksCopy
	 * @param beforeClone
	 * @param afterClone
	 * @param relatedLinks
	 * @return
	 */
	private DBCloneSetLinkInfo createLinkInstance(final long beforeRevisionId,
			final long afterRevisionId,
			final Map<Long, DBCodeFragmentLinkInfo> fragmentLinksCopy,
			final DBCloneSetInfo beforeClone, final DBCloneSetInfo afterClone,
			final List<Long> relatedLinks, final List<Long> unchangedFragments) {
		int numberOfChangedElements = 0;
		int numberOfCoChangedElements = 0;

		for (final long linkId : relatedLinks) {
			final DBCodeFragmentLinkInfo link = fragmentLinksCopy.get(linkId);
			if (link.isChanged()) {
				numberOfChangedElements++;
				numberOfCoChangedElements++;
			}
		}

		final int numberOfAddedElements = afterClone.getElements().size()
				- relatedLinks.size() - unchangedFragments.size();
		final int numberOfDeletedElements = beforeClone.getElements().size()
				- relatedLinks.size() - unchangedFragments.size();

		final DBCloneSetLinkInfo cloneLink = new DBCloneSetLinkInfo(
				beforeClone.getId(), afterClone.getId(), beforeRevisionId,
				afterRevisionId, numberOfChangedElements,
				numberOfAddedElements, numberOfDeletedElements,
				numberOfCoChangedElements, relatedLinks);

		return cloneLink;
	}

}
