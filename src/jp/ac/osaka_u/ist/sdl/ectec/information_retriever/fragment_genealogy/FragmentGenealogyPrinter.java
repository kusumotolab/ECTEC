package jp.ac.osaka_u.ist.sdl.ectec.information_retriever.fragment_genealogy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.ectec.data.CRD;
import jp.ac.osaka_u.ist.sdl.ectec.data.CodeFragmentGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.CodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.FileInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.RevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;

public class FragmentGenealogyPrinter {

	public static void main(final String[] args) {
		DBConnectionManager dbManager = null;
		PrintWriter pw = null;

		try {
			final String dbPath = args[0];
			pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(
					args[1]))));

			pw.println("B_Rev,B_File,B_Method,B_Start,B_End,A_Rev,A_File,A_Method,A_Start,A_End");

			dbManager = new DBConnectionManager(dbPath, 10000);

			final Map<Long, CodeFragmentGenealogyInfo> fragmentGenealogies = dbManager
					.getFragmentGenealogyRetriever().retrieveAll();

			for (final Map.Entry<Long, CodeFragmentGenealogyInfo> entry : fragmentGenealogies
					.entrySet()) {
				final CodeFragmentGenealogyInfo genealogy = entry.getValue();

				final long startRevisionId = genealogy.getStartRevisionId();
				final long endRevisionId = genealogy.getEndRevisionId();

				final Map<Long, RevisionInfo> revisions = dbManager
						.getRevisionRetriever().retrieveWithIds(
								startRevisionId, endRevisionId);
				final Map<Long, CodeFragmentInfo> fragments = dbManager
						.getFragmentRetriever().retrieveWithIds(
								genealogy.getElements());

				CodeFragmentInfo startFragment = null;
				CodeFragmentInfo endFragment = null;
				for (final CodeFragmentInfo fragment : fragments.values()) {
					if (fragment.getStartRevisionId() == startRevisionId) {
						startFragment = fragment;
					}
					if (fragment.getEndRevisionId() == endRevisionId) {
						endFragment = fragment;
					}
				}

				final Map<Long, FileInfo> files = dbManager.getFileRetriever()
						.retrieveWithIds(startFragment.getOwnerFileId(),
								endFragment.getOwnerFileId());
				final Map<Long, CRD> crds = dbManager.getCrdRetriever()
						.retrieveWithIds(startFragment.getCrdId(),
								endFragment.getCrdId());

				final String bRev = revisions.get(startRevisionId)
						.getIdentifier();
				final String bFile = files.get(startFragment.getOwnerFileId())
						.getPath();
				final String bMethod = crds.get(startFragment.getCrdId())
						.getAnchor();
				final String bMethodName = bMethod.substring(0,
						bMethod.indexOf("("));
				final int bStart = startFragment.getStartLine();
				final int bEnd = startFragment.getEndLine();

				final String aRev = revisions.get(endRevisionId)
						.getIdentifier();
				final String aFile = files.get(endFragment.getOwnerFileId())
						.getPath();
				final String aMethod = crds.get(endFragment.getCrdId())
						.getAnchor();
				final String aMethodName = aMethod.substring(0,
						aMethod.indexOf("("));
				final int aStart = endFragment.getStartLine();
				final int aEnd = endFragment.getEndLine();

				pw.println(bRev + "," + bFile + "," + bMethodName + ","
						+ bStart + "," + bEnd + "," + aRev + "," + aFile + ","
						+ aMethodName + "," + aStart + "," + aEnd);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dbManager != null) {
				dbManager.close();
			}
			if (pw != null) {
				pw.close();
			}
		}
	}
}
