package jp.ac.osaka_u.ist.sdl.ectec.analyzer.concretizer;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CloneGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.manager.DBDataManagerManager;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.manager.DataManagerManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.detector.vcs.IRepositoryManager;

/**
 * A class for controlling the concretizer
 * 
 * @author k-hotta
 * 
 */
public class ConcretizerController {

	/**
	 * the concretizer under control
	 */
	private final Concretizer concretizer;

	public ConcretizerController(final DataManagerManager dataManagerManager,
			final DBDataManagerManager dbDataManagerManager,
			final DBConnectionManager dbManager,
			final IRepositoryManager repositoryManager,
			final boolean isBlockMode) {
		this.concretizer = new Concretizer(dataManagerManager,
				dbDataManagerManager, dbManager, repositoryManager, isBlockMode);
	}

	/**
	 * concretize a clone genealogy
	 * 
	 * @param genealogyId
	 * @return
	 * @throws NotConcretizedException
	 */
	public CloneGenealogyInfo concretizeCloneGenealogy(final long genealogyId)
			throws NotConcretizedException {
		return concretizer.concretizeCloneGenealogy(genealogyId);
	}

	/**
	 * concretize clone genealogies
	 * 
	 * @param genealogyIds
	 * @return
	 * @throws NotConcretizedException
	 */
	public Map<Long, CloneGenealogyInfo> concretizeCloneGenealogies(
			final Collection<Long> genealogyIds) throws NotConcretizedException {
		final Map<Long, CloneGenealogyInfo> result = new TreeMap<Long, CloneGenealogyInfo>();

		for (final long genealogyId : genealogyIds) {
			result.put(genealogyId,
					concretizer.concretizeCloneGenealogy(genealogyId));
		}

		return Collections.unmodifiableMap(result);
	}

}
