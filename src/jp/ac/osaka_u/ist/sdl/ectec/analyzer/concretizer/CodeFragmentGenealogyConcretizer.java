package jp.ac.osaka_u.ist.sdl.ectec.analyzer.concretizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CodeFragmentGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CodeFragmentLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CombinedRevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentGenealogyInfo;

/**
 * A class for concretizing code fragment genealogies
 * 
 * @author k-hotta
 * 
 */
public class CodeFragmentGenealogyConcretizer {

	public CodeFragmentGenealogyInfo concretize(
			final DBCodeFragmentGenealogyInfo dbGenealogy,
			final Map<Long, CombinedRevisionInfo> combinedRevisions,
			final Map<Long, CodeFragmentInfo> fragments,
			final Map<Long, CodeFragmentLinkInfo> fragmentLinks) {
		final long id = dbGenealogy.getId();
		final CombinedRevisionInfo startCombinedRevision = combinedRevisions
				.get(dbGenealogy.getStartCombinedRevisionId());
		final CombinedRevisionInfo endCombinedRevision = combinedRevisions
				.get(dbGenealogy.getEndCombinedRevisionId());

		final List<CodeFragmentInfo> fragmentsList = new ArrayList<CodeFragmentInfo>();
		final List<Long> fragmentIds = dbGenealogy.getElements();
		for (final long fragmentId : fragmentIds) {
			fragmentsList.add(fragments.get(fragmentId));
		}

		final List<CodeFragmentLinkInfo> fragmentLinksList = new ArrayList<CodeFragmentLinkInfo>();
		final List<Long> fragmentLinkIds = dbGenealogy.getLinks();
		for (final long fragmentLinkId : fragmentLinkIds) {
			fragmentLinksList.add(fragmentLinks.get(fragmentLinkId));
		}

		int changeCount = 0;
		for (final CodeFragmentLinkInfo fragmentLink : fragmentLinksList) {
			if (fragmentLink.isChanged()) {
				changeCount++;
			}
		}

		return new CodeFragmentGenealogyInfo(id, startCombinedRevision,
				endCombinedRevision, fragmentsList, fragmentLinksList,
				changeCount);
	}

	public Map<Long, CodeFragmentGenealogyInfo> concretizeAll(
			final Collection<DBCodeFragmentGenealogyInfo> dbGenealogies,
			final Map<Long, CombinedRevisionInfo> combinedRevisions,
			final Map<Long, CodeFragmentInfo> fragments,
			final Map<Long, CodeFragmentLinkInfo> fragmentLinks) {
		final Map<Long, CodeFragmentGenealogyInfo> result = new TreeMap<Long, CodeFragmentGenealogyInfo>();

		for (final DBCodeFragmentGenealogyInfo dbGenealogy : dbGenealogies) {
			final CodeFragmentGenealogyInfo genealogy = concretize(dbGenealogy,
					combinedRevisions, fragments, fragmentLinks);
			result.put(genealogy.getId(), genealogy);
		}

		return Collections.unmodifiableMap(result);
	}

	public Map<Long, CodeFragmentGenealogyInfo> concretizeAll(
			final Map<Long, DBCodeFragmentGenealogyInfo> dbGenealogies,
			final Map<Long, CombinedRevisionInfo> combinedRevisions,
			final Map<Long, CodeFragmentInfo> fragments,
			final Map<Long, CodeFragmentLinkInfo> fragmentLinks) {
		return concretizeAll(dbGenealogies.values(), combinedRevisions,
				fragments, fragmentLinks);
	}

}
