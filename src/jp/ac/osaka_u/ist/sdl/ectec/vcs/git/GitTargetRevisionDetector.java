package jp.ac.osaka_u.ist.sdl.ectec.vcs.git;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.settings.Language;
import jp.ac.osaka_u.ist.sdl.ectec.vcs.AbstractTargetRevisionDetector;

/**
 * A class to detect target revisions and commits of git repositories
 *
 * @author (please append your name)
 *
 */
public class GitTargetRevisionDetector extends
		AbstractTargetRevisionDetector<GitRepositoryManager> {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggingManager
			.getLogger(GitTargetRevisionDetector.class.getName());

	/**
	 * the logger for errors
	 */
	private static final Logger eLogger = LoggingManager.getLogger("error");


	public GitTargetRevisionDetector(final GitRepositoryManager manager) {
		super(manager);
	}

	@Override
	protected Map<String, Date> detectRevisionsAfterTargetCommits(
			final Language language, final Set<String> ignoredList)
			throws Exception {
		// TODO implement me

		/*
		 * please return a sorted map whose keys are identifiers of AFTER
		 * revisions of commits that added/deleted/changed at least one source
		 * file whose values are dates of the commits
		 */

		final Repository gitRepository = manager.getRepository();
		final Map<String, Date> revisions = new HashMap<String, Date>();
		final List<ObjectId> commitId = new ArrayList<ObjectId>();
		final List<Date> commitDate = new ArrayList<Date>();

		Git git = new Git(gitRepository);
		RevWalk revWalk = new RevWalk(gitRepository);
		Iterable<RevCommit> logs = git.log().call();

		for(RevCommit rev : logs){
			//新しいコミットから順にListに格納．ただし，ignoredListに含まれるコミットはListに格納しない．
			if(ignoredList.contains(rev.getName())){
				logger.debug("\t["
						+ manager.getRepositoryName()
						+ "] revision "
						+ rev.getName()
						+ " was ignored because it is included in the ignored list");
				continue;
			}
			commitId.add(rev.getId());
			commitDate.add(rev.getAuthorIdent().getWhen());
			//System.out.println("Commit: " + rev  + ", name: " + rev.getName() + ", id: " + rev.getId().getName());
		}

		for(int currentNum = 0; currentNum < commitId.size() - 1; currentNum++){
			try{
				ObjectId newHead = revWalk.parseCommit(commitId.get(currentNum))
						.getTree()
						.getId();

				ObjectId oldHead = revWalk.parseCommit(commitId.get(currentNum + 1))
						.getTree()
						.getId();

				ObjectReader reader = gitRepository.newObjectReader();
				CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
				newTreeIter.reset(reader, newHead);
				CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
				oldTreeIter.reset(reader, oldHead);
				List<DiffEntry> diffs= git.diff()
	                    .setNewTree(newTreeIter)
	                    .setOldTree(oldTreeIter)
	                    .call();
				for (DiffEntry entry : diffs) {
					if(language.isTarget(entry.getOldPath())
							|| language.isTarget(entry.getNewPath())){
						revisions.put(commitId.get(currentNum).name(),
								commitDate.get(currentNum));
						logger.debug("\t[" + manager.getRepositoryName()
						+ "] revision " + currentNum + ":"
						+ commitId.get(currentNum).name()
						+ " was identified as a target revision");
						break;
					}
				}
			}catch (Exception e){
				eLogger.warn("\t[" + manager.getRepositoryName()
				+ "] revision " + currentNum + ":"
				+ commitId.get(currentNum).name()
				+ " was ignored due to an error\n"
				+ e.toString());
			}
		}

		//Oldest Commit
		int oldestCommitNum = commitId.size() - 1;
		RevCommit revCommit = revWalk.parseCommit(commitId.get(oldestCommitNum));
		RevTree revTree = revCommit.getTree();
		System.out.println("get RevTree: " + revTree);
		TreeWalk treeWalk = new TreeWalk(gitRepository);
		treeWalk.addTree(revTree);
		treeWalk.setRecursive(true);
		while(treeWalk.next()){
			if(language.isTarget(treeWalk.getPathString())){
				revisions.put(commitId.get(oldestCommitNum).name(),
						commitDate.get(commitId.size() - 1));
				logger.debug("\t[" + manager.getRepositoryName()
				+ "] revision " + oldestCommitNum + ":"
				+ commitId.get(oldestCommitNum).name()
				+ " was identified as a target revision");
				break;
			}
		}

		return Collections.unmodifiableMap(revisions);
	}

}
