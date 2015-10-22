package jp.ac.osaka_u.ist.sdl.ectec.vcs.git;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

import jp.ac.osaka_u.ist.sdl.ectec.settings.Language;
import jp.ac.osaka_u.ist.sdl.ectec.vcs.AbstractRepositoryManager;
import jp.ac.osaka_u.ist.sdl.ectec.vcs.AbstractTargetRevisionDetector;
import jp.ac.osaka_u.ist.sdl.ectec.vcs.IChangedFilesDetector;

/**
 * A class to manage a git repository
 *
 * @author (please write your name here)
 *
 */
public class GitRepositoryManager extends AbstractRepositoryManager {

	private Repository repository;


	public GitRepositoryManager(String rootUrl, String additionalUrl,
			String userName, String passwd, String repositoryName,
			long repositoryId) throws IOException{
		super(rootUrl, additionalUrl, userName, passwd, repositoryName,
				repositoryId);

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		this.repository = builder.setGitDir(new File(rootUrl))
				.readEnvironment().findGitDir().build();

	}

	@Override
	public AbstractTargetRevisionDetector<?> createTargetRevisionDetector() {
		return new GitTargetRevisionDetector(this);
	}

	@Override
	public IChangedFilesDetector createChangedFilesDetector() {
		return new GitChangedFilesDetector(this);
	}

	/**
	 * get the repository as SVNRepository
	 *
	 * @return
	 */
	public Repository getRepository() {
		return this.repository;
	}

	@Override
	public String getFileContents(String revisionIdentifier, String path)
			throws Exception {
		// TODO implement me

		/*
		 * return the content of the file in the specified revision as a string
		 * value
		 */
		final StringBuilder builder = new StringBuilder();
		String contentsLine;
		final ObjectId specificCommitId = this.repository.resolve(revisionIdentifier);

		RevWalk revWalk = new RevWalk(this.repository);
		RevCommit revCommit = revWalk.parseCommit(specificCommitId);
		RevTree revTree = revCommit.getTree();

		TreeWalk treeWalk = new TreeWalk(this.repository);
		treeWalk.addTree(revTree);
		treeWalk.setRecursive(true);
		treeWalk.setFilter(PathFilter.create(path));
		if(!treeWalk.next()){
			throw new IllegalStateException(
					"Did not find excepted file '" + path + "'");
		}

		ObjectId objectId = treeWalk.getObjectId(0);
		ObjectLoader loader = repository.open(objectId);

		final BufferedReader br = new BufferedReader(
				new InputStreamReader(loader.openStream()));

		while((contentsLine = br.readLine()) != null){
			builder.append(contentsLine);
		}

		revWalk.dispose();

		return builder.toString();
	}

	/**
	 * get the list of paths of all the source files in the given revision
	 *
	 * @param revisionNum
	 * @param lang
	 * @return
	 * @throws Exception
	 */
	public synchronized List<String> getListOfSourceFiles(
			final String revisionIdentifier, final Language lang) throws Exception {
		final String nullStr = null;
		return getListOfSourceFiles(revisionIdentifier, lang, nullStr);
	}

	@Override
	public synchronized List<String> getListOfSourceFiles(String revisionIdentifier,
			Language language, Collection<String> targets) throws Exception {
		// TODO implement me

		/*
		 * return a list of string whose elements are paths of files in the
		 * given revision and written in the given language the list must
		 * include only paths that start with any of strings in the given
		 * collection of string if targets == null, then this method returns all
		 * the paths of source files in the given revision
		 */
		final List<String> pathList = new ArrayList<String>();
		for(String target : targets){
			pathList.addAll(getListOfSourceFiles(revisionIdentifier, language, target));
		}
		return Collections.unmodifiableList(pathList);
	}

	public synchronized List<String> getListOfSourceFiles(String revisionIdentifier,
			Language language, String target) throws Exception{
		final Repository repository = this.repository;
		final List<String> pathList = new ArrayList<String>();
		final ObjectId specificCommitId = repository.resolve(revisionIdentifier);

		RevWalk revWalk = new RevWalk(repository);
		RevCommit revCommit = revWalk.parseCommit(specificCommitId);
		RevTree revTree = revCommit.getTree();

		TreeWalk treeWalk = new TreeWalk(repository);
		treeWalk.addTree(revTree);
		treeWalk.setRecursive(true);
		if(target == null){
			while(treeWalk.next()){
					if(language.isTarget(treeWalk.getPathString())){
						pathList.add(treeWalk.getPathString());
					}
			}
		} else{
			while(treeWalk.next()){
				if(treeWalk.getPathString().startsWith(target)
						&& language.isTarget(treeWalk.getPathString())){
					pathList.add(treeWalk.getPathString());
				}
			}
		}

		revWalk.dispose();
		return Collections.unmodifiableList(pathList);
	}

}
