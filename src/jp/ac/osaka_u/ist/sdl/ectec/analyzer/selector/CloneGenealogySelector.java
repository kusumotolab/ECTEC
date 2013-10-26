package jp.ac.osaka_u.ist.sdl.ectec.analyzer.selector;

import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneGenealogyInfo;

/**
 * A class for choosing clone genealogies to be concretized from db
 * 
 * @author k-hotta
 * 
 */
public class CloneGenealogySelector {

	/**
	 * the db manager
	 */
	private final DBConnectionManager dbManager;

	/**
	 * the constraint
	 */
	private final IConstraint constraint;

	/**
	 * A constructor
	 * 
	 * @param dbManager
	 *            the manager of db connection
	 * @param constraint
	 *            the constraint, null if no constraint is needed
	 */
	public CloneGenealogySelector(final DBConnectionManager dbManager,
			final IConstraint constraint) {
		this.dbManager = dbManager;
		if (constraint != null) {
			this.constraint = constraint;
		} else {
			this.constraint = new LaxConstraint();
		}
	}

	public CloneGenealogySelector(final DBConnectionManager dbManager) {
		this(dbManager, null);
	}

	public SortedMap<Long, DBCloneGenealogyInfo> select() {
		try {
			final SortedMap<Long, DBCloneGenealogyInfo> allGenealogies = dbManager
					.getCloneGenealogyRetriever().retrieveAll();

			final SortedMap<Long, DBCloneGenealogyInfo> result = new TreeMap<Long, DBCloneGenealogyInfo>();

			for (final Map.Entry<Long, DBCloneGenealogyInfo> entry : allGenealogies
					.entrySet()) {
				if (constraint.satisfy(entry.getValue())) {
					result.put(entry.getKey(), entry.getValue());
				}
			}

			return Collections.unmodifiableSortedMap(result);

		} catch (Exception e) {
			return new TreeMap<Long, DBCloneGenealogyInfo>();
		}
	}

}
