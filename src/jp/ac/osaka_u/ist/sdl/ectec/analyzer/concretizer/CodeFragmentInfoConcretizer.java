package jp.ac.osaka_u.ist.sdl.ectec.analyzer.concretizer;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CRD;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CombinedRevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.FileInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;

/**
 * A class for concretizing code fragments
 * 
 * @author k-hotta
 * 
 */
public class CodeFragmentInfoConcretizer {

	public CodeFragmentInfo concretize(final DBCodeFragmentInfo dbFragment,
			final Map<Long, CombinedRevisionInfo> combinedRevisions,
			final Map<Long, FileInfo> files, final Map<Long, CRD> crds) {
		final long id = dbFragment.getId();
		final FileInfo ownerFile = files.get(dbFragment.getOwnerFileId());
		final CRD crd = crds.get(dbFragment.getCrdId());
		final CombinedRevisionInfo startCombinedRevision = combinedRevisions
				.get(dbFragment.getStartCombinedRevisionId());
		final CombinedRevisionInfo endCombinedRevision = combinedRevisions
				.get(dbFragment.getEndCombinedRevisionId());
		final int startLine = dbFragment.getStartLine();
		final int endLine = dbFragment.getEndLine();
		final int size = dbFragment.getSize();

		return new CodeFragmentInfo(id, ownerFile, crd, startCombinedRevision,
				endCombinedRevision, startLine, endLine, size);
	}

	public Map<Long, CodeFragmentInfo> concretizeAll(
			final Collection<DBCodeFragmentInfo> dbFragments,
			final Map<Long, CombinedRevisionInfo> combinedRevisions,
			final Map<Long, FileInfo> files, final Map<Long, CRD> crds) {
		final Map<Long, CodeFragmentInfo> result = new TreeMap<Long, CodeFragmentInfo>();

		for (final DBCodeFragmentInfo dbFragment : dbFragments) {
			final CodeFragmentInfo fragment = concretize(dbFragment,
					combinedRevisions, files, crds);
			result.put(fragment.getId(), fragment);
		}

		return Collections.unmodifiableMap(result);
	}

	public Map<Long, CodeFragmentInfo> concretizeAll(
			final Map<Long, DBCodeFragmentInfo> dbFragments,
			final Map<Long, CombinedRevisionInfo> combinedRevisions,
			final Map<Long, FileInfo> files, final Map<Long, CRD> crds) {
		return concretizeAll(dbFragments.values(), combinedRevisions, files,
				crds);
	}

}
