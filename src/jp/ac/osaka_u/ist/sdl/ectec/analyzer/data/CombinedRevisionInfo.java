package jp.ac.osaka_u.ist.sdl.ectec.analyzer.data;

import java.util.Collections;
import java.util.List;

/**
 * A class that represents combined revision
 * 
 * @author k-hotta
 * 
 */
public class CombinedRevisionInfo extends AbstractElement implements
		Comparable<CombinedRevisionInfo> {

	private final List<RevisionInfo> originalRevisions;

	public CombinedRevisionInfo(final long id,
			final List<RevisionInfo> originalRevisions) {
		super(id);
		this.originalRevisions = originalRevisions;
	}

	@Override
	public int compareTo(CombinedRevisionInfo another) {
		return ((Long) this.id).compareTo(another.getId());
	}

	public final List<RevisionInfo> getOriginalRevisions() {
		return Collections.unmodifiableList(originalRevisions);
	}

}
