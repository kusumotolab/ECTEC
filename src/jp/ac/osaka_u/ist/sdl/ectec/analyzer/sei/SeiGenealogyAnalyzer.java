package jp.ac.osaka_u.ist.sdl.ectec.analyzer.sei;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.SQLiteDBConfig;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentLinkInfo;

import org.apache.log4j.Logger;

public class SeiGenealogyAnalyzer {

	private static final Logger logger = LoggingManager
			.getLogger(SeiGenealogyAnalyzer.class.getName());

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			final String dbPath = args[0];
			final String outputFilePath = args[1];

			final PrintWriter pw = new PrintWriter(new BufferedWriter(
					new FileWriter(new File(outputFilePath))));
			pw.println(getHeader());

			final DBConnectionManager dbManager = new DBConnectionManager(
					new SQLiteDBConfig(dbPath), 100000);

			final Map<Long, DBCodeFragmentGenealogyInfo> genealogies = dbManager
					.getFragmentGenealogyRetriever().retrieveAll();
			logger.info(genealogies.size() + " genealogies have been retrieved");

			int count = 0;
			for (final DBCodeFragmentGenealogyInfo genealogy : genealogies
					.values()) {
				logger.debug("[" + (++count) + "/" + genealogies.size()
						+ "] processing genealogy " + genealogy.getId());
				final List<Long> elementIds = genealogy.getElements();
				final List<Long> linkIds = genealogy.getLinks();

				final Map<Long, DBCodeFragmentInfo> elements = dbManager
						.getFragmentRetriever().retrieveWithIds(elementIds);
				final Map<Long, DBCodeFragmentLinkInfo> links = dbManager
						.getFragmentLinkRetriever().retrieveWithIds(linkIds);

				final long branchedCombinedRevisionId = getBranchedCombinedRevisionId(elements
						.values());

				int elementsBeforeBranched = 0;
				int elementsAfterBranchedInOrigin = 0;
				int elementsAfterBranchedInDest = 0;
				int modBeforeBranched = 0;
				int modAfterBranchedInOrigin = 0;
				int modAfterBranchedInDest = 0;

				for (final DBCodeFragmentInfo fragment : elements.values()) {
					for (long combinedRevisionId = fragment
							.getStartCombinedRevisionId(); combinedRevisionId <= fragment
							.getEndCombinedRevisionId(); combinedRevisionId++) {
						if (combinedRevisionId < branchedCombinedRevisionId) {
							elementsBeforeBranched++;
						} else {
							if (fragment.getOwnerRepositoryId() == 0) {
								elementsAfterBranchedInOrigin++;
							} else {
								elementsAfterBranchedInDest++;
							}
						}
					}
				}

				for (final DBCodeFragmentLinkInfo link : links.values()) {
					if (link.isChanged()) {
						if (link.getAfterCombinedRevisionId() < branchedCombinedRevisionId) {
							modBeforeBranched++;
						} else {
							final DBCodeFragmentInfo beforeFragment = elements
									.get(link.getBeforeElementId());
							final DBCodeFragmentInfo afterFragment = elements
									.get(link.getAfterElementId());

							if (beforeFragment.getOwnerRepositoryId() == 0
									&& afterFragment.getOwnerRepositoryId() == 0) {
								modAfterBranchedInOrigin++;
							} else {
								modAfterBranchedInDest++;
							}
						}
					}
				}

				if (elementsBeforeBranched != 0
						&& (elementsAfterBranchedInDest != 0 || elementsAfterBranchedInOrigin != 0)) {
					pw.println(genealogy.getId() + ","
							+ genealogy.getStartCombinedRevisionId() + ","
							+ genealogy.getEndCombinedRevisionId() + ","
							+ elementsBeforeBranched + ","
							+ elementsAfterBranchedInOrigin + ","
							+ elementsAfterBranchedInDest + ","
							+ modBeforeBranched + ","
							+ modAfterBranchedInOrigin + ","
							+ modAfterBranchedInDest);
				}
			}

			pw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static final String getHeader() {
		return "GENEALOGY_ID,START_COMBINED_REVISION,END_COMBINED_REVISION,ELEMENTS_BEFORE_BRANCHED,ELEMENT_AFTER_BRANCHED_IN_ORIGIN,ELEMENTS_AFTER_BRANCHED_IN_DEST,MOD_BEFORE_BRANCHED,MOD_AFTER_BRANCHED_IN_ORIGIN,MOD_AFTER_BRANCHED_IN_DEST";
	}

	private static final long getBranchedCombinedRevisionId(
			final Collection<DBCodeFragmentInfo> elements) {
		long result = Long.MAX_VALUE;

		for (final DBCodeFragmentInfo element : elements) {
			final long combinedRevisionId = element
					.getStartCombinedRevisionId();
			if (element.getOwnerRepositoryId() != 0
					&& combinedRevisionId < result) {
				result = combinedRevisionId;
			}
		}

		return result;
	}

}
