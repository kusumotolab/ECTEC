package jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetLinkFragmentLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetLinkInfo;

/**
 * A class that represents a registerer for links of clone sets
 * 
 * @author k-hotta
 * 
 */
public class CloneSetLinkRegisterer extends
		AbstractWithSubTableElementRegisterer<DBCloneSetLinkInfo> {

	private static final String subElementName = DBCloneSetLinkFragmentLinkInfo.class
			.getSimpleName();

	private static List<String> subElements;

	private static Map<String, String> preparedStatementQueries;

	static {
		subElements = new ArrayList<String>();
		subElements.add(subElementName);

		preparedStatementQueries = new HashMap<String, String>();
		preparedStatementQueries.put(subElementName,
				"insert into CLONE_SET_LINK_FRAGMENT_LINK values(?,?)");
	}

	/**
	 * the constructor
	 * 
	 * @param dbManager
	 * @param maxBatchCount
	 */
	public CloneSetLinkRegisterer(DBConnectionManager dbManager,
			int maxBatchCount) {
		super(dbManager, maxBatchCount, subElements, preparedStatementQueries);
	}

	@Override
	protected String createMainPreparedStatementQuery() {
		return "insert into CLONE_SET_LINK values(?,?,?,?,?)";
	}

	@Override
	protected void fillMainElementPreparedStatement(
			PreparedStatement mainPstmt, DBCloneSetLinkInfo element)
			throws SQLException {
		int column = 0;
		mainPstmt.setLong(++column, element.getId());
		mainPstmt.setLong(++column, element.getBeforeElementId());
		mainPstmt.setLong(++column, element.getAfterElementId());
		mainPstmt.setLong(++column, element.getBeforeCombinedRevisionId());
		mainPstmt.setLong(++column, element.getAfterCombinedRevisionId());

		mainPstmt.addBatch();
	}

	@Override
	protected Map<String, Integer> fillSubElementPreparedStatements(
			DBCloneSetLinkInfo element,
			Map<String, PreparedStatement> subPstmts,
			Map<String, Integer> currentCount) throws SQLException {
		final long elementId = element.getId();
		final Collection<Long> fragmentLinks = element.getCodeFragmentLinks();

		final PreparedStatement pstmt = subPstmts
				.get(DBCloneSetLinkFragmentLinkInfo.class.getSimpleName());

		for (final long fragmentLink : fragmentLinks) {
			int column = 0;
			pstmt.setLong(++column, elementId);
			pstmt.setLong(++column, fragmentLink);

			pstmt.addBatch();
		}

		final int beforeCount = currentCount.get(subElementName);
		currentCount.put(subElementName, beforeCount + fragmentLinks.size());

		return currentCount;
	}
}
