package jp.ac.osaka_u.ist.sdl.ectec.analyzer.sei;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
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
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCommitInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRepositoryInfo;

import org.apache.log4j.Logger;

public class CrossProjectCloneRatioTransitionAnalyzer {

	private static final Logger logger = LoggingManager
			.getLogger(CrossProjectCloneRatioTransitionAnalyzer.class.getName());

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

			final Map<Long, DBRepositoryInfo> repositories = dbManager
					.getRepositoryRetriever().retrieveAll();

			pw.println(getHeader(repositories));

			final SortedMap<Long, DBCombinedCommitInfo> combinedCommits = dbManager
					.getCombinedCommitRetriever().retrieveAll();
			final Map<Long, DBCommitInfo> commits = dbManager.getCommitRetriever().retrieveAll();
			
			int count = 0;

			for (final Map.Entry<Long, DBCombinedCommitInfo> entry : combinedCommits
					.entrySet()) {
				final long combinedRevisionId = entry.getValue().getAfterCombinedRevisionId();
				final DBCommitInfo originalCommit = commits.get(entry.getValue().getOriginalCommitId());
				
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

				final StringBuilder builder = new StringBuilder();

				builder.append(combinedRevisionId + ",");
				
				final Date date = originalCommit.getDate();
				final Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				final int year = cal.get(Calendar.YEAR);
				final int month = cal.get(Calendar.MONTH) + 1;
				final int day = cal.get(Calendar.DAY_OF_MONTH);
				final int hour = cal.get(Calendar.HOUR_OF_DAY);
				final int minute = cal.get(Calendar.MINUTE);
				final int second = cal.get(Calendar.SECOND);
				builder.append(year + "/" + month + "/" + day + " " + hour + ":" + minute + ":" + second + ",");
				
				for (final Map.Entry<Long, DBRepositoryInfo> repositoryEntry : repositories
						.entrySet()) {
					builder.append(repositoryEntry.getKey() + ",");
				}
				for (final Map.Entry<Long, DBRepositoryInfo> repositoryEntry : repositories
						.entrySet()) {
					final Map<Long, Integer> repositoryLoc = loc
							.get(repositoryEntry.getKey());
					final Map<Long, Set<Integer>> repositoryClonedLines = clonedLines
							.get(repositoryEntry.getKey());

					int totalLoc = 0;
					int totalCloneLoc = 0;

					if (repositoryLoc != null) {
						for (final Map.Entry<Long, Integer> fileEntry : repositoryLoc
								.entrySet()) {
							totalLoc += fileEntry.getValue();
							if ((repositoryClonedLines != null)
									&& (repositoryClonedLines
											.containsKey(fileEntry.getKey()))) {
								totalCloneLoc += repositoryClonedLines.get(
										fileEntry.getKey()).size();
							}
						}
					}

					builder.append(totalLoc + ",");
					builder.append(totalCloneLoc + ",");
				}

				builder.deleteCharAt(builder.length() - 1);
				pw.println(builder.toString());
			}

			pw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getHeader(
			final Map<Long, DBRepositoryInfo> repositories) {
		final StringBuilder builder = new StringBuilder();

		builder.append("COMB_REV_ID,COMMITED_DATE,");

		for (final Map.Entry<Long, DBRepositoryInfo> entry : repositories
				.entrySet()) {
			builder.append("REPO" + entry.getKey() + "_REV_ID,");
		}

		for (final Map.Entry<Long, DBRepositoryInfo> entry : repositories
				.entrySet()) {
			builder.append("REPO" + entry.getKey() + "_LOC,");
			builder.append("REPO" + entry.getKey() + "_CLONE_LOC,");
		}

		builder.deleteCharAt(builder.length() - 1);

		return builder.toString();
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

}
