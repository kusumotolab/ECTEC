package jp.ac.osaka_u.ist.sdl.ectec.analyzer.concretizer;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CodeFragmentLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CombinedRevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentLinkInfo;

/**
 * A class for concretizing code fragment links
 * 
 * @author k-hotta
 * 
 */
public class CodeFragmentLinkConcretizer {

	public CodeFragmentLinkInfo concretize(
			final DBCodeFragmentLinkInfo dbFragmentLink,
			final Map<Long, CombinedRevisionInfo> combinedRevisions,
			final Map<Long, CodeFragmentInfo> fragments) {
		final long id = dbFragmentLink.getId();
		final CombinedRevisionInfo beforeCombinedRevision = combinedRevisions
				.get(dbFragmentLink.getBeforeCombinedRevisionId());
		final CombinedRevisionInfo afterCombinedRevision = combinedRevisions
				.get(dbFragmentLink.getAfterCombinedRevisionId());
		final CodeFragmentInfo beforeFragment = fragments.get(dbFragmentLink
				.getBeforeElementId());
		final CodeFragmentInfo afterFragment = fragments.get(dbFragmentLink
				.getAfterElementId());
		final boolean changed = dbFragmentLink.isChanged();

		return new CodeFragmentLinkInfo(id, beforeCombinedRevision,
				afterCombinedRevision, beforeFragment, afterFragment, changed);
	}

	public Map<Long, CodeFragmentLinkInfo> concretizeAll(
			final Collection<DBCodeFragmentLinkInfo> dbFragmentLinks,
			final Map<Long, CombinedRevisionInfo> combinedRevisions,
			final Map<Long, CodeFragmentInfo> fragments) {
		final Map<Long, CodeFragmentLinkInfo> result = new TreeMap<Long, CodeFragmentLinkInfo>();

		for (final DBCodeFragmentLinkInfo dbFragmentLink : dbFragmentLinks) {
			final CodeFragmentLinkInfo fragmentLink = concretize(
					dbFragmentLink, combinedRevisions, fragments);
			result.put(fragmentLink.getId(), fragmentLink);
		}

		return Collections.unmodifiableMap(result);
	}

	public Map<Long, CodeFragmentLinkInfo> concretizeAll(
			final Map<Long, DBCodeFragmentLinkInfo> dbFragmentLinks,
			final Map<Long, CombinedRevisionInfo> combinedRevisions,
			final Map<Long, CodeFragmentInfo> fragments) {
		return concretizeAll(dbFragmentLinks.values(), combinedRevisions,
				fragments);
	}

}
