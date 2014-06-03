package jp.ac.osaka_u.ist.sdl.ectec.analyzer.concretizer;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.RepositoryInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRepositoryInfo;
import jp.ac.osaka_u.ist.sdl.ectec.settings.VersionControlSystem;

/**
 * A class for concretizing repositories
 * 
 * @author k-hotta
 * 
 */
public final class RepositoryConcretizer {

	public RepositoryInfo concretize(final DBRepositoryInfo dbRepository) {
		final long id = dbRepository.getId();
		final String name = dbRepository.getName();
		final String url = dbRepository.getUrl();
		final VersionControlSystem vcs = dbRepository.getManagingVcs();
		final String userName = dbRepository.getUserName();
		final String passwd = dbRepository.getPasswd();

		return new RepositoryInfo(id, name, url, vcs, userName, passwd);
	}

	public Map<Long, RepositoryInfo> concretizeAll(
			final Collection<DBRepositoryInfo> dbRepositories) {
		final Map<Long, RepositoryInfo> result = new TreeMap<Long, RepositoryInfo>();

		for (final DBRepositoryInfo dbRepository : dbRepositories) {
			final RepositoryInfo concretizedRepository = concretize(dbRepository);
			result.put(concretizedRepository.getId(), concretizedRepository);
		}

		return Collections.unmodifiableMap(result);
	}

	public Map<Long, RepositoryInfo> concretizeAll(
			final Map<Long, DBRepositoryInfo> dbRepositories) {
		return concretizeAll(dbRepositories.values());
	}

}
