package jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentGenealogyElementInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentGenealogyLinkElementInfo;

/**
 * A class that represents a registerer for genealogies of code fragments
 * 
 * @author k-hotta
 * 
 */
public class CodeFragmentGenealogyRegisterer extends
		AbstractWithSubTableElementRegisterer<DBCodeFragmentGenealogyInfo> {

	private static final String subElementName = DBCodeFragmentGenealogyElementInfo.class
			.getSimpleName();

	private static final String subElementLinkName = DBCodeFragmentGenealogyLinkElementInfo.class
			.getSimpleName();

	private static List<String> subElements;

	private static Map<String, String> preparedStatementQueries;

	static {
		subElements = new ArrayList<String>();
		subElements.add(subElementName);
		subElements.add(subElementLinkName);

		preparedStatementQueries = new HashMap<String, String>();
		preparedStatementQueries.put(subElementName,
				"insert into CODE_FRAGMENT_GENEALOGY_ELEMENT values (?,?)");
		preparedStatementQueries
				.put(subElementLinkName,
						"insert into CODE_FRAGMENT_GENEALOGY_LINK_ELEMENT values (?,?)");
	}

	public CodeFragmentGenealogyRegisterer(DBConnectionManager dbManager,
			int maxBatchCount) {
		super(dbManager, maxBatchCount, subElements, preparedStatementQueries);
	}

	@Override
	protected String createMainPreparedStatementQuery() {
		return "insert into CODE_FRAGMENT_GENEALOGY values (?,?,?)";
	}

	@Override
	protected void fillMainElementPreparedStatement(
			PreparedStatement mainPstmt, DBCodeFragmentGenealogyInfo element)
			throws SQLException {
		int column = 0;

		mainPstmt.setLong(++column, element.getId());
		mainPstmt.setLong(++column, element.getStartCombinedRevisionId());
		mainPstmt.setLong(++column, element.getEndCombinedRevisionId());

		mainPstmt.addBatch();
	}

	@Override
	protected Map<String, Integer> fillSubElementPreparedStatements(
			DBCodeFragmentGenealogyInfo element,
			Map<String, PreparedStatement> subPstmts,
			Map<String, Integer> currentCount) throws SQLException {
		final long genealogyId = element.getId();
		final List<Long> elements = element.getElements();
		final List<Long> links = element.getLinks();

		final PreparedStatement elementPstmt = subPstmts.get(subElementName);

		for (final long elementId : elements) {
			int column = 0;
			elementPstmt.setLong(++column, genealogyId);
			elementPstmt.setLong(++column, elementId);

			elementPstmt.addBatch();
		}

		final int beforeElementCount = currentCount.get(subElementName);
		currentCount.put(subElementName, beforeElementCount + elements.size());

		final PreparedStatement linkPstmt = subPstmts.get(subElementLinkName);

		for (final long linkId : links) {
			int column = 0;
			linkPstmt.setLong(++column, genealogyId);
			linkPstmt.setLong(++column, linkId);

			linkPstmt.addBatch();
		}

		final int beforeLinkCount = currentCount.get(subElementLinkName);
		currentCount.put(subElementLinkName, beforeLinkCount + links.size());

		return currentCount;
	}

}
