package jp.ac.osaka_u.ist.sdl.ectec.data.registerer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.ectec.data.CloneSetLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.retriever.LinkElementRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.util.StringUtils;

/**
 * A class for retrieving links of clone sets
 * 
 * @author k-hotta
 * 
 */
public class CloneSetLinkRetriever extends
		LinkElementRetriever<CloneSetLinkInfo> {

	public CloneSetLinkRetriever(DBConnectionManager dbManager) {
		super(dbManager);
	}

	@Override
	protected CloneSetLinkInfo createElement(ResultSet rs) throws SQLException {
		int column = 0;
		final long id = rs.getLong(++column);
		final long beforeElementId = rs.getLong(++column);
		final long afterElementId = rs.getLong(++column);
		final long beforeRevisionId = rs.getLong(++column);
		final long afterRevisionId = rs.getLong(++column);
		final int numberOfChangedElements = rs.getInt(++column);
		final int numberOfAddedElements = rs.getInt(++column);
		final int numberOfDeletedElements = rs.getInt(++column);
		final int numberOfCoChangedElements = rs.getInt(++column);
		final String codeFragmentLinksStr = rs.getString(++column);
		final List<Long> codeFragmentLinks = new ArrayList<Long>();
		StringUtils.convertStringToCollection(codeFragmentLinks,
				codeFragmentLinksStr);

		return new CloneSetLinkInfo(id, beforeElementId, afterElementId,
				beforeRevisionId, afterRevisionId, numberOfChangedElements,
				numberOfAddedElements, numberOfDeletedElements,
				numberOfCoChangedElements, codeFragmentLinks);
	}

	@Override
	protected String getBeforeRevisionIdColumnName() {
		return "BEFORE_REVISION_ID";
	}

	@Override
	protected String getAfterRevisionIdColumnName() {
		return "AFTER_REVISION_ID";
	}

	@Override
	protected String getTableName() {
		return "CLONE_SET_LINK";
	}

	@Override
	protected String getIdColumnName() {
		return "CLONE_SET_LINK_ID";
	}

}
