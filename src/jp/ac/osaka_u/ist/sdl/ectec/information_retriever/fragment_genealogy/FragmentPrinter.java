package jp.ac.osaka_u.ist.sdl.ectec.information_retriever.fragment_genealogy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCrdInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBFileInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.detector.vcs.svn.SVNRepositoryManager;

public class FragmentPrinter {

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

			final Map<Long, DBRevisionInfo> revisions = dbManager
					.getRevisionRetriever().retrieveAll();

			for (final Map.Entry<Long, DBCodeFragmentGenealogyInfo> entry : fragmentGenealogies
					.entrySet()) {
				final DBCodeFragmentGenealogyInfo genealogy = entry.getValue();

				if (genealogy.getChangedCount() == 0) {
					continue;
				}

				final Map<Long, DBCodeFragmentInfo> fragments = dbManager
						.getFragmentRetriever().retrieveWithIds(
								genealogy.getElements());

				final List<Long> fileIds = new ArrayList<Long>();
				final List<Long> crdIds = new ArrayList<Long>();
				for (final Map.Entry<Long, DBCodeFragmentInfo> entry2 : fragments
						.entrySet()) {
					final long fileId = entry2.getValue().getOwnerFileId();
					final long crdId = entry2.getValue().getCrdId();
					fileIds.add(fileId);
					crdIds.add(crdId);
				}

				final Map<Long, DBFileInfo> files = dbManager
						.getFileRetriever().retrieveWithIds(fileIds);
				final Map<Long, DBCrdInfo> crds = dbManager.getCrdRetriever()
						.retrieveWithIds(crdIds);

				final PrintWriter pw2 = new PrintWriter(new BufferedWriter(
						new FileWriter(new File(workingDir + File.separator
								+ index + ".txt"))));

				final SortedMap<Long, DBCodeFragmentLinkInfo> links = dbManager
						.getFragmentLinkRetriever().retrieveWithIds(
								genealogy.getLinks());

				final SortedMap<Long, String> toBePrinted = new TreeMap<Long, String>();

				boolean first = true;
				for (final SortedMap.Entry<Long, DBCodeFragmentLinkInfo> entry2 : links
						.entrySet()) {
					final DBCodeFragmentLinkInfo link = entry2.getValue();

					if (first) {
						final DBCodeFragmentInfo fragment = fragments.get(link
								.getBeforeElementId());

						final String str = printFragment(fragment,
								revisions.get(link.getBeforeRevisionId()),
								files.get(fragment.getOwnerFileId()),
								crds.get(fragment.getCrdId()), repoManager);

						toBePrinted.put(Long.parseLong(revisions.get(
								link.getBeforeRevisionId()).getIdentifier()),
								str);

						first = false;
					}

					if (link.isChanged()) {
						final DBCodeFragmentInfo fragment = fragments.get(link
								.getAfterElementId());

						final String str = printFragment(fragment,
								revisions.get(link.getAfterRevisionId()),
								files.get(fragment.getOwnerFileId()),
								crds.get(fragment.getCrdId()), repoManager);

						toBePrinted.put(Long.parseLong(revisions.get(
								link.getAfterRevisionId()).getIdentifier()),
								str);
					}

				}

				for (final Map.Entry<Long, String> entry3 : toBePrinted
						.entrySet()) {
					pw2.println(entry3.getValue());
				}

				pw2.close();

				index++;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dbManager != null) {
				dbManager.close();
			}
		}
	}

	private static String printFragment(final DBCodeFragmentInfo fragment,
			final DBRevisionInfo revision, final DBFileInfo file,
			final DBCrdInfo crd, final SVNRepositoryManager repoManager)
			throws Exception {
		final String rev = revision.getIdentifier();
		final String filePath = file.getPath();
		final String method = crd.getAnchor();
		final String methodName = method.substring(0, method.indexOf("("));
		final int start = fragment.getStartLine();
		final int end = fragment.getEndLine();

		final SortedMap<Integer, String> fileContents = getSrcLines(
				repoManager, rev, filePath);
		final String methodContent = getMethodContent(fileContents, start, end);

		StringBuilder builder = new StringBuilder();
		builder.append("rev: " + rev + ", " + filePath + ", " + start + "-"
				+ end + "\n");
		builder.append("method name: " + methodName + "\n");
		builder.append("--" + "\n");
		builder.append(methodContent + "\n");
		builder.append("--" + "\n");
		return builder.toString();
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
