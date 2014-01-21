package jp.ac.osaka_u.ist.sdl.ectec.information_retriever.fragment_genealogy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.BlockType;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCrdInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBFileInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.detector.vcs.svn.SVNRepositoryManager;

public class FragmentGenealogyPrinter {

	private static final String LINE_SEPARATOR = "\n";

	public static void main(final String[] args) {
		DBConnectionManager dbManager = null;
		SVNRepositoryManager repoManager = null;

		try {
			final String dbPath = args[0];
			final String repositoryPath = args[1];
			final String workingDir = args[2];

			repoManager = new SVNRepositoryManager(repositoryPath, null, null,
					null);

			dbManager = new DBConnectionManager(dbPath, 10000);

			final Map<Long, DBCodeFragmentGenealogyInfo> fragmentGenealogies = dbManager
					.getFragmentGenealogyRetriever().retrieveAll();

			int index = 1;

			for (final Map.Entry<Long, DBCodeFragmentGenealogyInfo> entry : fragmentGenealogies
					.entrySet()) {
				final DBCodeFragmentGenealogyInfo genealogy = entry.getValue();

				if (genealogy.getChangedCount() == 0) {
					continue;
				}

				final Map<Long, DBCodeFragmentLinkInfo> links = dbManager
						.getFragmentLinkRetriever().retrieveWithIds(
								genealogy.getLinks());
				final Map<Long, DBRevisionInfo> revisions = dbManager
						.getRevisionRetriever().retrieveAll();

				final StringBuilder builder = new StringBuilder();

				for (final DBCodeFragmentLinkInfo link : links.values()) {
					if (link.isChanged()) {

						final Map<Long, DBCodeFragmentInfo> fragments = dbManager
								.getFragmentRetriever().retrieveWithIds(
										link.getBeforeElementId(),
										link.getAfterElementId());

						final DBCodeFragmentInfo beforeFragment = fragments
								.get(link.getBeforeElementId());
						final DBCodeFragmentInfo afterFragment = fragments
								.get(link.getAfterElementId());

						final Map<Long, DBFileInfo> files = dbManager
								.getFileRetriever().retrieveWithIds(
										beforeFragment.getOwnerFileId(),
										afterFragment.getOwnerFileId());
						final Map<Long, DBCrdInfo> crds = dbManager
								.getCrdRetriever().retrieveWithIds(
										beforeFragment.getCrdId(),
										afterFragment.getCrdId());

						final DBCrdInfo bCrd = crds.get(beforeFragment
								.getCrdId());
						final DBCrdInfo aCrd = crds.get(afterFragment
								.getCrdId());
						if (bCrd.getType() != BlockType.METHOD
								|| aCrd.getType() != BlockType.METHOD) {
							continue;
						}

						final String bRev = revisions.get(
								link.getBeforeRevisionId()).getIdentifier();
						final String bFile = files.get(
								beforeFragment.getOwnerFileId()).getPath();
						final String bMethod = crds.get(
								beforeFragment.getCrdId()).getAnchor();
						final String bMethodName = bMethod.substring(0,
								bMethod.indexOf("("));
						final int bStart = beforeFragment.getStartLine();
						final int bEnd = beforeFragment.getEndLine();

						final String aRev = revisions.get(
								link.getAfterRevisionId()).getIdentifier();
						final String aFile = files.get(
								afterFragment.getOwnerFileId()).getPath();
						final String aMethod = crds.get(
								afterFragment.getCrdId()).getAnchor();
						final String aMethodName = aMethod.substring(0,
								aMethod.indexOf("("));
						final int aStart = afterFragment.getStartLine();
						final int aEnd = afterFragment.getEndLine();

						if (!(bMethodName.equals("equals") && aMethodName
								.equals("equals"))) {
							//continue;
						}

						try {
							final SortedMap<Integer, String> beforeFileContents = getSrcLines(
									repoManager, bRev, bFile);
							final SortedMap<Integer, String> afterFileContents = getSrcLines(
									repoManager, aRev, aFile);

							final String bMethodContent = getMethodContent(
									beforeFileContents, bStart, bEnd);
							final String aMethodContent = getMethodContent(
									afterFileContents, aStart, aEnd);

							builder.append("BEFORE_METHOD\n");
							builder.append("rev:" + bRev + ", " + bFile + "\n");
							builder.append("--\n");
							builder.append(bMethodContent + "\n");
							builder.append("--\n");
							builder.append("\n");
							builder.append("AFTER_METHOD\n");
							builder.append("rev:" + aRev + ", " + aFile + "\n");
							builder.append("--\n");
							builder.append(aMethodContent + "\n");
							builder.append("--\n");
							builder.append("\n");

						} catch (Exception e) {
							// ignore
						}

					}
				}

				if (builder.length() > 0) {
					final String outputFile = workingDir + File.separator
							+ index + ".txt";
					final PrintWriter pw2 = new PrintWriter(new BufferedWriter(
							new FileWriter(new File(outputFile))));

					pw2.println(builder.toString());

					pw2.close();
					
					index++;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dbManager != null) {
				dbManager.close();
			}
		}
	}

	private static SortedMap<Integer, String> getSrcLines(
			final SVNRepositoryManager repoManager, final String rev,
			final String file) throws Exception {
		final String src = repoManager.getFileContents(Long.parseLong(rev),
				file);
		final SortedMap<Integer, String> srcLines = new TreeMap<Integer, String>();
		int count = 1;
		for (final String str : src.split(LINE_SEPARATOR)) {
			srcLines.put(count++, str + LINE_SEPARATOR);
		}
		return srcLines;
	}

	private static String getMethodContent(
			final SortedMap<Integer, String> fileContents, final int start,
			final int end) {
		final StringBuilder builder = new StringBuilder();

		for (int i = start; i <= end; i++) {
			builder.append(fileContents.get(i));
		}

		return builder.toString();
	}

}
