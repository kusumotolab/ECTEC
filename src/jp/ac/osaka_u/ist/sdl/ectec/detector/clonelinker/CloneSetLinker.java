package jp.ac.osaka_u.ist.sdl.ectec.detector.clonelinker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.data.CloneSetInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.CloneSetLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.CodeFragmentLinkInfo;

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
	public Map<Long, CloneSetLinkInfo> detectCloneSetLinks(
			final Collection<CloneSetInfo> beforeClones,
			final Collection<CloneSetInfo> afterClones,
			final Map<Long, CodeFragmentLinkInfo> fragmentLinks,
			final long beforeRevisionId, final long afterRevisionId) {
		final Map<Long, CloneSetLinkInfo> result = new TreeMap<Long, CloneSetLinkInfo>();
		final Map<Long, CodeFragmentLinkInfo> fragmentLinksCopy = new HashMap<Long, CodeFragmentLinkInfo>();
		fragmentLinksCopy.putAll(fragmentLinks);

		for (final CloneSetInfo beforeClone : beforeClones) {
			for (final CloneSetInfo afterClone : afterClones) {
				final List<Long> relatedLinks = getRelatedFragmentLinks(
						beforeClone, afterClone, fragmentLinksCopy);
				final List<Long> unchangedFragmentIds = getUnchangedFragmentIds(
						beforeClone, afterClone);

				if (!relatedLinks.isEmpty() || !unchangedFragmentIds.isEmpty()) {
					final CloneSetLinkInfo cloneLink = createLinkInstance(
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
	private List<Long> getRelatedFragmentLinks(final CloneSetInfo beforeClone,
			final CloneSetInfo afterClone,
			final Map<Long, CodeFragmentLinkInfo> fragmentLinksCopy) {
		final List<Long> beforeFragments = beforeClone.getElements();
		final List<Long> afterFragments = afterClone.getElements();

		final List<Long> result = new ArrayList<Long>();

		for (final Map.Entry<Long, CodeFragmentLinkInfo> entry : fragmentLinksCopy
				.entrySet()) {
			final CodeFragmentLinkInfo link = entry.getValue();
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
	private List<Long> getUnchangedFragmentIds(final CloneSetInfo beforeClone,
			final CloneSetInfo afterClone) {
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
	private CloneSetLinkInfo createLinkInstance(final long beforeRevisionId,
			final long afterRevisionId,
			final Map<Long, CodeFragmentLinkInfo> fragmentLinksCopy,
			final CloneSetInfo beforeClone, final CloneSetInfo afterClone,
			final List<Long> relatedLinks, final List<Long> unchangedFragments) {
		int numberOfChangedElements = 0;
		int numberOfCoChangedElements = 0;

		for (final long linkId : relatedLinks) {
			final CodeFragmentLinkInfo link = fragmentLinksCopy.get(linkId);
			if (link.isChanged()) {
				numberOfChangedElements++;
				numberOfCoChangedElements++;
			}
		}

		final int numberOfAddedElements = afterClone.getElements().size()
				- relatedLinks.size() - unchangedFragments.size();
		final int numberOfDeletedElements = beforeClone.getElements().size()
				- relatedLinks.size() - unchangedFragments.size();

		final CloneSetLinkInfo cloneLink = new CloneSetLinkInfo(
				beforeClone.getId(), afterClone.getId(), beforeRevisionId,
				afterRevisionId, numberOfChangedElements,
				numberOfAddedElements, numberOfDeletedElements,
				numberOfCoChangedElements, relatedLinks);

		return cloneLink;
	}

}
