package jp.ac.osaka_u.ist.sdl.ectec.analyzer.concretizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CRD;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.BlockType;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCrdInfo;

/**
 * A class for concretizing crds
 * 
 * @author k-hotta
 * 
 */
public class CRDConcretizer {

	public Map<Long, CRD> concretizeAll(final DBCrdInfo dbCrd,
			final Map<Long, DBCrdInfo> dbCrds,
			final Map<Long, CRD> alreadyConcretized) {
		final Map<Long, CRD> result = new TreeMap<Long, CRD>();

		final List<CRD> ancestors = new ArrayList<CRD>();
		final List<Long> ancestorIds = dbCrd.getAncestors();
		for (final long ancestorId : ancestorIds) {
			if (alreadyConcretized.containsKey(ancestorId)) {
				ancestors.add(alreadyConcretized.get(ancestorId));
			} else {
				final CRD ancestor = concretizeAll(dbCrds.get(ancestorId),
						dbCrds, alreadyConcretized).get(ancestorId);
				ancestors.add(ancestor);
			}
		}

		final long id = dbCrd.getId();
		final BlockType bType = dbCrd.getType();
		final String head = dbCrd.getHead();
		final String anchor = dbCrd.getAnchor();
		final String normalizedAnchor = dbCrd.getNormalizedAnchor();
		final int cm = dbCrd.getCm();
		final String fullText = dbCrd.getFullText();

		final CRD newCrd = new CRD(id, bType, head, anchor, normalizedAnchor,
				cm, ancestors, fullText);
		result.put(newCrd.getId(), newCrd);
		alreadyConcretized.putAll(result);

		return Collections.unmodifiableMap(result);
	}

	public Map<Long, CRD> concretizeAll(Map<Long, DBCrdInfo> dbCrds) {
		final Map<Long, CRD> result = new TreeMap<Long, CRD>();

		for (final Map.Entry<Long, DBCrdInfo> entry : dbCrds.entrySet()) {
			final DBCrdInfo dbCrd = entry.getValue();
			result.putAll(concretizeAll(dbCrd, dbCrds, result));
		}

		return Collections.unmodifiableMap(result);
	}

}
