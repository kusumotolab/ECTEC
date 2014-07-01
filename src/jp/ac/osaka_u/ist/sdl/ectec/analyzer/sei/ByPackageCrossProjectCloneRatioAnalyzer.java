package jp.ac.osaka_u.ist.sdl.ectec.analyzer.sei;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCombinedCommitInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCombinedRevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCommitInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBFileInfo;

import org.apache.log4j.Logger;

public class ByPackageCrossProjectCloneRatioAnalyzer {

	private static final Logger logger = LoggingManager
			.getLogger(ByPackageCrossProjectCloneRatioAnalyzer.class.getName());

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			final String dbPath = args[0];
			final String outputFilePath = args[1];

			final PrintWriter pw = new PrintWriter(new BufferedWriter(
					new FileWriter(new File(outputFilePath))));

			final DBConnectionManager dbManager = new DBConnectionManager(
					dbPath, 100000);

			final SortedMap<Long, DBCombinedCommitInfo> combinedCommits = dbManager
					.getCombinedCommitRetriever().retrieveAll();
			final SortedMap<Long, DBCombinedRevisionInfo> combinedRevisions = dbManager
					.getCombinedRevisionRetriever().retrieveAll();
			final Map<Long, DBCommitInfo> commits = dbManager
					.getCommitRetriever().retrieveAll();
			final Map<Long, DBFileInfo> files = dbManager.getFileRetriever()
					.retrieveAll();

			pw.print("REPOSITORY_ID,PATH");
			for (final Map.Entry<Long, DBCombinedCommitInfo> combinedCommitEntry : combinedCommits
					.entrySet()) {
				pw.print(","
						+ combinedCommitEntry.getValue()
								.getAfterCombinedRevisionId());
			}
			pw.println();

			pw.print(" , ");
			for (final Map.Entry<Long, DBCombinedCommitInfo> combinedCommitEntry : combinedCommits
					.entrySet()) {
				final DBCommitInfo originalCommit = commits
						.get(combinedCommitEntry.getValue()
								.getOriginalCommitId());

				final Date date = originalCommit.getDate();
				final Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				final int year = cal.get(Calendar.YEAR);
				final int month = cal.get(Calendar.MONTH) + 1;
				final int day = cal.get(Calendar.DAY_OF_MONTH);
				final int hour = cal.get(Calendar.HOUR_OF_DAY);
				final int minute = cal.get(Calendar.MINUTE);
				final int second = cal.get(Calendar.SECOND);
				pw.print("," + year + "/" + month + "/" + day + " " + hour
						+ ":" + minute + ":" + second);
			}
			pw.println();

			final Map<Long, Map<String, Map<Long, Integer>>> clonedLinesByPackage = new TreeMap<Long, Map<String, Map<Long, Integer>>>();
			final Map<Long, Map<String, Map<Long, Integer>>> locByPackage = new TreeMap<Long, Map<String, Map<Long, Integer>>>();

			int count = 0;

			for (final Map.Entry<Long, DBCombinedCommitInfo> entry : combinedCommits
					.entrySet()) {
				final long combinedRevisionId = entry.getValue()
						.getAfterCombinedRevisionId();

				logger.info("[" + (++count) + "/" + combinedCommits.size()
						+ "] analyzing combined commit " + entry.getKey());

				final Map<Long, DBCloneSetInfo> clones = dbManager
						.getCloneRetriever()
						.retrieveElementsInSpecifiedRevision(combinedRevisionId);
				logger.debug(clones.size() + " clones");

				final Map<Long, DBCodeFragmentInfo> fragments = dbManager
						.getFragmentRetriever()
						.retrieveElementsInSpecifiedCombinedRevision(
								combinedRevisionId);
				logger.debug(fragments.size() + " fragments");

				final Map<Long, DBCloneSetInfo> crossProjectClones = getCrossProjectClones(
						clones, fragments);

				final Map<Long, Map<Long, Integer>> loc = getLoc(fragments);
				final Map<Long, Map<Long, Set<Integer>>> clonedLines = getClonedLines(
						crossProjectClones, fragments);

				detectPackageLoc(files, locByPackage, combinedRevisionId, loc);
				detectPackageClonedLines(files, clonedLinesByPackage,
						combinedRevisionId, clonedLines);
			}

			for (final Map.Entry<Long, Map<String, Map<Long, Integer>>> entry1 : locByPackage
					.entrySet()) {
				final long repositoryId = entry1.getKey();

				for (final Map.Entry<String, Map<Long, Integer>> entry2 : entry1
						.getValue().entrySet()) {
					final Map<Long, Integer> tmpLoc = entry2.getValue();
					final Map<Long, Integer> tmpClonedLines = clonedLinesByPackage
							.get(repositoryId).get(entry2.getKey());

					pw.print(repositoryId + "," + entry2.getKey());

					for (final Map.Entry<Long, DBCombinedCommitInfo> combinedCommitEntry : combinedCommits
							.entrySet()) {
						final long combinedRevisionId = combinedCommitEntry
								.getValue().getAfterCombinedRevisionId();
						final int tmpLocInRev = (tmpLoc
								.containsKey(combinedRevisionId)) ? tmpLoc
								.get(combinedRevisionId) : 0;
						final int tmpClonedLineInRev = (tmpClonedLines != null && tmpClonedLines
								.containsKey(combinedRevisionId)) ? tmpClonedLines
								.get(combinedRevisionId) : 0;

						final double ratio = (tmpLocInRev == 0) ? 0
								: (double) tmpClonedLineInRev
										/ (double) tmpLocInRev;
						pw.print("," + ratio);
					}

					pw.println();
				}
			}

			pw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void detectPackageClonedLines(
			final Map<Long, DBFileInfo> files,
			final Map<Long, Map<String, Map<Long, Integer>>> clonedLinesByPackage,
			final long combinedRevisionId,
			final Map<Long, Map<Long, Set<Integer>>> clonedLines) {
		for (final Map.Entry<Long, Map<Long, Set<Integer>>> clonedLineEntry1 : clonedLines
				.entrySet()) {
			for (final Map.Entry<Long, Set<Integer>> clonedLineEntry2 : clonedLineEntry1
					.getValue().entrySet()) {
				final long fileId = clonedLineEntry2.getKey();
				final DBFileInfo file = files.get(fileId);

				final List<String> packageStrs = getAllPackages(file.getPath());

				if (!clonedLinesByPackage.containsKey(file
						.getOwnerRepositoryId())) {
					clonedLinesByPackage.put(file.getOwnerRepositoryId(),
							new TreeMap<String, Map<Long, Integer>>());
				}

				final Map<String, Map<Long, Integer>> clonedLinesByPackageInRepo = clonedLinesByPackage
						.get(file.getOwnerRepositoryId());

				for (final String packageStr : packageStrs) {
					if (!clonedLinesByPackageInRepo.containsKey(packageStr)) {
						clonedLinesByPackageInRepo.put(packageStr,
								new TreeMap<Long, Integer>());
					}

					final Map<Long, Integer> packageClonedLines = clonedLinesByPackageInRepo
							.get(packageStr);

					if (!packageClonedLines.containsKey(combinedRevisionId)) {
						packageClonedLines.put(combinedRevisionId, 0);
					}

					final int currentValue = packageClonedLines
							.get(combinedRevisionId);
					packageClonedLines.remove(combinedRevisionId);
					packageClonedLines.put(combinedRevisionId, currentValue
							+ clonedLineEntry2.getValue().size());
				}
			}
		}
	}

	private static void detectPackageLoc(final Map<Long, DBFileInfo> files,
			final Map<Long, Map<String, Map<Long, Integer>>> locByPackage,
			final long combinedRevisionId,
			final Map<Long, Map<Long, Integer>> loc) {
		for (final Map.Entry<Long, Map<Long, Integer>> locEntry1 : loc
				.entrySet()) {
			for (final Map.Entry<Long, Integer> locEntry2 : locEntry1
					.getValue().entrySet()) {
				final long fileId = locEntry2.getKey();
				final DBFileInfo file = files.get(fileId);

				final List<String> packageStrs = getAllPackages(file.getPath());

				if (!locByPackage.containsKey(file.getOwnerRepositoryId())) {
					locByPackage.put(file.getOwnerRepositoryId(),
							new TreeMap<String, Map<Long, Integer>>());
				}

				final Map<String, Map<Long, Integer>> locByPackageInRepo = locByPackage
						.get(file.getOwnerRepositoryId());

				for (final String packageStr : packageStrs) {
					if (!locByPackageInRepo.containsKey(packageStr)) {
						locByPackageInRepo.put(packageStr,
								new TreeMap<Long, Integer>());
					}

					final Map<Long, Integer> packageLoc = locByPackageInRepo
							.get(packageStr);

					if (!packageLoc.containsKey(combinedRevisionId)) {
						packageLoc.put(combinedRevisionId, 0);
					}

					final int currentValue = packageLoc.get(combinedRevisionId);
					packageLoc.remove(combinedRevisionId);
					packageLoc.put(combinedRevisionId,
							currentValue + locEntry2.getValue());
				}
			}
		}
	}

	private static Map<Long, DBCloneSetInfo> getCrossProjectClones(
			final Map<Long, DBCloneSetInfo> clones,
			final Map<Long, DBCodeFragmentInfo> fragments) {
		final Map<Long, DBCloneSetInfo> result = new TreeMap<Long, DBCloneSetInfo>();

		for (final Map.Entry<Long, DBCloneSetInfo> entry : clones.entrySet()) {
			final DBCloneSetInfo clone = entry.getValue();
			long repositoryId = -1;
			boolean crossProject = false;

			for (final long fragmentId : clone.getElements()) {
				final DBCodeFragmentInfo fragment = fragments.get(fragmentId);
				if (repositoryId == -1) {
					repositoryId = fragment.getOwnerRepositoryId();
				} else if (repositoryId != fragment.getOwnerRepositoryId()) {
					crossProject = true;
					break;
				}
			}

			if (crossProject) {
				result.put(entry.getKey(), entry.getValue());
			}
		}

		return Collections.unmodifiableMap(result);
	}

	private static final Map<Long, Map<Long, Integer>> getLoc(
			final Map<Long, DBCodeFragmentInfo> fragments) {
		final Map<Long, Map<Long, Integer>> result = new TreeMap<Long, Map<Long, Integer>>();

		for (final Map.Entry<Long, DBCodeFragmentInfo> fragmentEntry : fragments
				.entrySet()) {
			final DBCodeFragmentInfo fragment = fragmentEntry.getValue();

			final long repositoryId = fragment.getOwnerRepositoryId();
			final long fileId = fragment.getOwnerFileId();
			final int endLine = fragment.getEndLine();

			if (!result.containsKey(repositoryId)) {
				result.put(repositoryId, new TreeMap<Long, Integer>());
			}

			final Map<Long, Integer> repositoryFiles = result.get(repositoryId);
			if (!repositoryFiles.containsKey(fileId)) {
				repositoryFiles.put(fileId, -1);
			}

			final int registeredLoc = repositoryFiles.get(fileId);
			if (endLine > registeredLoc) {
				repositoryFiles.remove(fileId);
				repositoryFiles.put(fileId, endLine);
			}
		}

		return Collections.unmodifiableMap(result);
	}

	private static final Map<Long, Map<Long, Set<Integer>>> getClonedLines(
			final Map<Long, DBCloneSetInfo> clones,
			final Map<Long, DBCodeFragmentInfo> fragments) {
		final Map<Long, Map<Long, Set<Integer>>> result = new TreeMap<Long, Map<Long, Set<Integer>>>();

		for (final Map.Entry<Long, DBCloneSetInfo> cloneEntry : clones
				.entrySet()) {
			final DBCloneSetInfo clone = cloneEntry.getValue();
			for (final long fragmentId : clone.getElements()) {
				final DBCodeFragmentInfo fragment = fragments.get(fragmentId);
				final long repositoryId = fragment.getOwnerRepositoryId();
				final long fileId = fragment.getOwnerFileId();
				final int start = fragment.getStartLine();
				final int end = fragment.getEndLine();

				if (!result.containsKey(repositoryId)) {
					result.put(repositoryId, new TreeMap<Long, Set<Integer>>());
				}

				final Map<Long, Set<Integer>> repositoryFiles = result
						.get(repositoryId);
				if (!repositoryFiles.containsKey(fileId)) {
					repositoryFiles.put(fileId, new TreeSet<Integer>());
				}
				final Set<Integer> fileLines = repositoryFiles.get(fileId);
				for (int i = start; i <= end; i++) {
					fileLines.add(i);
				}

			}
		}

		return Collections.unmodifiableMap(result);
	}

	private static List<String> getAllPackages(final String path) {
		final List<String> result = new ArrayList<String>();

		int i = 0;
		while (true) {
			i = path.indexOf("/", i);
			if (i == -1) {
				break;
			}
			
			result.add(path.substring(0, i) + "/");
			i++;
		}

		return Collections.unmodifiableList(result);
	}

}
